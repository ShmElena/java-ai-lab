package lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    @Test
    void addItemSuccessfullyAddsNewItem() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 1);
        List<CartService.CartItem> items = cartService.getItems();
        assertEquals(1, items.size());
        CartService.CartItem item = items.getFirst();
        assertEquals("p1", item.productId());
        assertEquals("Keyboard", item.name());
        assertEquals(new BigDecimal("49.99"), item.price());
        assertEquals(1, item.quantity());
    }

    @Test
    void addItemIncreasesQuantityWhenProductAlreadyExists() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 1);
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 2);
        List<CartService.CartItem> items = cartService.getItems();
        assertEquals(1, items.size());
        assertEquals(3, items.getFirst().quantity());
    }

    @Test
    void addItemRejectsNullOrBlankProductId() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem(null, "Keyboard", new BigDecimal("49.99"), 1));
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem("   ", "Keyboard", new BigDecimal("49.99"), 1));
    }

    @Test
    void addItemRejectsNullOrBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem("p1", null, new BigDecimal("49.99"), 1));
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem("p1", "   ", new BigDecimal("49.99"), 1));
    }

    @Test
    void addItemRejectsNullPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem("p1", "Keyboard", null, 1));
    }

    @Test
    void addItemRejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem("p1", "Keyboard", new BigDecimal("-0.01"), 1));
    }

    @Test
    void addItemRejectsNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 0));
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), -1));
    }

    @Test
    void removeItemSuccessfullyRemovesItem() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 1);
        cartService.removeItem("p1");
        assertTrue(cartService.getItems().isEmpty());
    }

    @Test
    void removeItemThrowsWhenProductNotFound() {
        assertThrows(IllegalArgumentException.class, () -> cartService.removeItem("nonexistent"));
    }

    @Test
    void removeItemRejectsNullOrBlankProductId() {
        assertThrows(IllegalArgumentException.class, () -> cartService.removeItem(null));
        assertThrows(IllegalArgumentException.class, () -> cartService.removeItem("  "));
    }

    @Test
    void updateQuantitySuccessfullyUpdatesItemQuantity() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 1);
        cartService.updateQuantity("p1", 5);
        assertEquals(5, cartService.getItems().getFirst().quantity());
    }

    @Test
    void updateQuantityThrowsWhenProductNotFound() {
        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity("nonexistent", 3));
    }

    @Test
    void updateQuantityRejectsNonPositiveQuantity() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 1);
        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity("p1", 0));
        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity("p1", -2));
    }

    @Test
    void totalReturnsZeroForEmptyCart() {
        assertEquals(BigDecimal.ZERO, cartService.total());
    }

    @Test
    void totalCalculatesCorrectSumForMultipleItems() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 2);
        cartService.addItem("p2", "Mouse", new BigDecimal("25.00"), 1);
        assertEquals(new BigDecimal("124.98"), cartService.total());
    }

    @Test
    void clearEmptiesTheCart() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 1);
        cartService.clear();
        assertTrue(cartService.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cartService.total());
    }

    @Test
    void getItemsReturnsUnmodifiableList() {
        cartService.addItem("p1", "Keyboard", new BigDecimal("49.99"), 1);
        List<CartService.CartItem> items = cartService.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(new CartService.CartItem("p2", "Mouse", BigDecimal.TEN, 1)));
    }
}
