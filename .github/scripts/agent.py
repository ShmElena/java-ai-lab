import os
import json
import re
import time
from google import genai
from google.genai import types
from github import Github, Auth, GithubException

GITHUB_TOKEN = os.environ["GITHUB_TOKEN"]
REPO_NAME = os.environ["GITHUB_REPOSITORY"]
ISSUE_NUMBER = int(os.environ["ISSUE_NUMBER"])
EVENT_NAME = os.environ["EVENT_NAME"]
BOT_LOGIN = "github-actions[bot]"

gh = Github(auth=Auth.Token(GITHUB_TOKEN))
repo = gh.get_repo(REPO_NAME)
issue = repo.get_issue(ISSUE_NUMBER)

client = genai.Client(api_key=os.environ["GEMINI_API_KEY"])

SYSTEM_PROMPT = """You are a Java coding assistant for a Java 21 Maven project.

Your behavior depends on the conversation stage:

STAGE 1 — New issue, no previous bot comments:
- Read the issue carefully
- Propose a clear implementation plan:
  * Exact method signatures
  * Validation rules and edge cases
  * List of tests you will write
- End your response with exactly: "Shall I proceed? Reply **@ai go** to implement."

STAGE 2 — User gave feedback on the plan:
- Update the plan based on feedback
- Ask for confirmation again

STAGE 3 — User wrote "@ai go" (or "go ahead" / "implement" / "yes, proceed"):
- Respond ONLY with a raw JSON object, no markdown, no explanation:
{"action":"implement","files":[{"path":"src/main/java/lab/ClassName.java","content":"...full file content..."}]}

STAGE 4 — Comment contains "CI tests failed on branch `ai/issue-N`":
- Analyze the test failure output carefully
- Find the root cause
- Fix the code (and tests if needed)
- Respond ONLY with a raw JSON object:
{"action":"fix","branch":"ai/issue-N","files":[{"path":"...","content":"...full fixed file content..."}]}
The branch name must be extracted exactly from the failure comment.

STAGE 5 — PR review comment on a specific file and line:
- You are given: the file path, line number, reviewer's comment, and the full file content
- Apply the requested change to the file
- Respond ONLY with a raw JSON object:
{"action":"fix","branch":"BRANCH_NAME","files":[{"path":"...","content":"...full updated file content..."}]}
The branch name is provided in the context.

Always follow coding conventions from AGENT.md.
"""


def get_file(path, ref=None):
    try:
        kwargs = {"ref": ref} if ref else {}
        return repo.get_contents(path, **kwargs).decoded_content.decode("utf-8")
    except GithubException:
        return None


def get_java_sources(ref=None):
    result = ""
    try:
        kwargs = {"ref": ref} if ref else {}
        for f in repo.get_contents("src/main/java/lab", **kwargs):
            if f.name.endswith(".java"):
                result += f"\n\n### {f.path}\n```java\n{f.decoded_content.decode()}\n```"
        for f in repo.get_contents("src/test/java/lab", **kwargs):
            if f.name.endswith(".java"):
                result += f"\n\n### {f.path}\n```java\n{f.decoded_content.decode()}\n```"
    except GithubException:
        pass
    return result


def build_history():
    history = []

    agent_md = get_file("AGENT.md")
    if agent_md:
        history.append(types.Content(role="user", parts=[types.Part(text=f"Project instructions (AGENT.md):\n{agent_md}")]))
        history.append(types.Content(role="model", parts=[types.Part(text="Understood, I will follow these instructions.")]))

    if EVENT_NAME == "pull_request_review_comment":
        return build_review_comment_history(history)

    sources = get_java_sources()
    if sources:
        history.append(types.Content(role="user", parts=[types.Part(text=f"Current codebase:{sources}")]))
        history.append(types.Content(role="model", parts=[types.Part(text="I have reviewed the existing code.")]))

    history.append(types.Content(
        role="user",
        parts=[types.Part(text=f"**Issue #{issue.number}: {issue.title}**\n\n{issue.body or '(no description)'}")]
    ))

    for comment in issue.get_comments():
        role = "model" if comment.user.login == BOT_LOGIN else "user"
        history.append(types.Content(role=role, parts=[types.Part(text=comment.body)]))

    return history


def build_review_comment_history(history):
    review_file = os.environ.get("REVIEW_FILE", "")
    review_line = os.environ.get("REVIEW_LINE", "")
    review_body = os.environ.get("REVIEW_BODY", "")
    pr_branch = os.environ.get("PR_BRANCH", "")

    file_content = get_file(review_file, ref=pr_branch) or "(could not read file)"

    sources = get_java_sources(ref=pr_branch)
    if sources:
        history.append(types.Content(role="user", parts=[types.Part(text=f"Current codebase on branch `{pr_branch}`:{sources}")]))
        history.append(types.Content(role="model", parts=[types.Part(text="I have reviewed the existing code.")]))

    message = (
        f"PR review comment on branch `{pr_branch}`:\n\n"
        f"**File:** `{review_file}`\n"
        f"**Line:** {review_line}\n"
        f"**Comment:** {review_body}\n\n"
        f"**Full file content:**\n```java\n{file_content}\n```"
    )
    history.append(types.Content(role="user", parts=[types.Part(text=message)]))

    return history


def extract_json(text):
    match = re.search(r'```(?:json)?\s*(\{.*?\})\s*```', text, re.DOTALL)
    if match:
        return match.group(1)
    if text.strip().startswith("{"):
        return text.strip()
    return None


def write_files(files, branch, commit_msg):
    for f in files:
        try:
            existing = repo.get_contents(f["path"], ref=branch)
            repo.update_file(f["path"], commit_msg, f["content"], existing.sha, branch=branch)
        except GithubException:
            repo.create_file(f["path"], commit_msg, f["content"], branch=branch)


def implement(data):
    branch = f"ai/issue-{issue.number}"
    default = repo.default_branch
    sha = repo.get_git_ref(f"heads/{default}").object.sha

    try:
        repo.create_git_ref(f"refs/heads/{branch}", sha)
    except GithubException:
        pass

    write_files(data["files"], branch, f"ai: implement #{issue.number}")

    pr = repo.create_pull(
        title=f"AI: {issue.title}",
        body=f"Closes #{issue.number}\n\nGenerated by AI agent.",
        head=branch,
        base=default
    )
    issue.create_comment(f"Done! PR opened: {pr.html_url}\n\nCI will run tests automatically.")


def fix_on_branch(data):
    branch = data["branch"]
    write_files(data["files"], branch, f"ai: fix #{issue.number}")
    issue.create_comment(f"Fixed! Updated branch `{branch}` — CI will re-run automatically.")


def call_model(history):
    last = history.pop()
    for attempt in range(4):
        try:
            chat = client.chats.create(
                model="gemini-3.8-flash",
                config=types.GenerateContentConfig(
                    system_instruction=SYSTEM_PROMPT,
                    temperature=0.2,
                ),
                history=history,
            )
            return chat.send_message(last.parts[0].text).text.strip()
        except genai.errors.ServerError as e:
            if attempt == 3:
                raise
            wait = 15 * (attempt + 1)
            print(f"Model unavailable, retrying in {wait}s... ({e})")
            time.sleep(wait)


def main():
    history = build_history()
    reply = call_model(history)

    json_str = extract_json(reply)
    if json_str:
        try:
            data = json.loads(json_str)
            if data.get("action") == "implement":
                implement(data)
                return
            if data.get("action") == "fix":
                fix_on_branch(data)
                return
        except json.JSONDecodeError:
            pass

    issue.create_comment(reply)


if __name__ == "__main__":
    main()
