package lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessor.class);

    private final List<OrderItem> items = new ArrayList<>();

    public void addItem(String name, BigDecimal price, int quantity) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        items.add(new OrderItem(name, price, quantity));
        log.info("Added item: {} x{} @ {}", name, quantity, price);
    }

    public BigDecimal total() {
        return items.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public record OrderItem(String name, BigDecimal price, int quantity) {}
}
