package lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {

    private OrderProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OrderProcessor();
    }

    @Test
    void totalIsZeroForEmptyOrder() {
        assertEquals(BigDecimal.ZERO, processor.total());
    }

    @Test
    void totalSumsAllItems() {
        processor.addItem("Apple", new BigDecimal("1.50"), 3);
        processor.addItem("Bread", new BigDecimal("2.00"), 1);
        assertEquals(new BigDecimal("6.50"), processor.total());
    }

    @Test
    void addItemRejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> processor.addItem("Bad", new BigDecimal("-1"), 1));
    }

    @Test
    void addItemRejectsZeroQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> processor.addItem("Bad", new BigDecimal("1.00"), 0));
    }

    @Test
    void discountRejectsNegativePercent() {
        assertThrows(IllegalArgumentException.class, () -> processor.discount(-1));
    }

    @Test
    void discountRejectsPercentGreaterThan100() {
        assertThrows(IllegalArgumentException.class, () -> processor.discount(101));
    }

    @Test
    void discountZeroPercentReturnsFullTotal() {
        processor.addItem("Apple", new BigDecimal("10.00"), 1);
        assertEquals(new BigDecimal("10.00"), processor.discount(0));
    }

    @Test
    void discountHundredPercentReturnsZero() {
        processor.addItem("Apple", new BigDecimal("10.00"), 1);
        assertEquals(new BigDecimal("0.00"), processor.discount(100));
    }

    @Test
    void discountAppliesStandardPercentage() {
        processor.addItem("Apple", new BigDecimal("10.00"), 1);
        assertEquals(new BigDecimal("8.00"), processor.discount(20));
    }

    @Test
    void discountRoundsHalfUp() {
        processor.addItem("Item", new BigDecimal("10.55"), 1);
        // 10.55 * 0.85 = 8.9675 -> rounds to 8.97
        assertEquals(new BigDecimal("8.97"), processor.discount(15));
    }

    @Test
    void discountOnEmptyOrderReturnsZero() {
        assertEquals(new BigDecimal("0.00"), processor.discount(20));
    }
}
