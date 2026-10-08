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
}
