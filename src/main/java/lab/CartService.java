package lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public void addItem(String productId, String name, BigDecimal price, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        CartItem existing = items.get(productId);
        if (existing != null) {
            int newQuantity = existing.quantity() + quantity;
            items.put(productId, new CartItem(productId, name, price, newQuantity));
            log.info("Updated existing item in cart: {} new quantity: {}", productId, newQuantity);
        } else {
            items.put(productId, new CartItem(productId, name, price, quantity));
            log.info("Added new item to cart: {} x{} @ {}", productId, quantity, price);
        }
    }

    public void removeItem(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID must not be null or blank");
        }
        if (!items.containsKey(productId)) {
            throw new IllegalArgumentException("Product not found in cart: " + productId);
        }
        items.remove(productId);
        log.info("Removed item from cart: {}", productId);
    }

    public void updateQuantity(String productId, int newQuantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID must not be null or blank");
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        CartItem existing = items.get(productId);
        if (existing == null) {
            throw new IllegalArgumentException("Product not found in cart: " + productId);
        }
        items.put(productId, new CartItem(existing.productId(), existing.name(), existing.price(), newQuantity));
        log.info("Updated quantity for product: {} to {}", productId, newQuantity);
    }

    public BigDecimal total() {
        return items.values().stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clear() {
        items.clear();
        log.info("Cleared cart");
    }

    public List<CartItem> getItems() {
        return List.copyOf(items.values());
    }

    public record CartItem(String productId, String name, BigDecimal price, int quantity) {}
}
