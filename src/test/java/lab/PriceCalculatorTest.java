package lab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    @Test
    void calculateTaxAmountSuccessfully() {
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal taxPercent = new BigDecimal("15.00");
        BigDecimal tax = calculator.calculateTaxAmount(basePrice, taxPercent);
        assertEquals(new BigDecimal("15.00"), tax);
    }

    @Test
    void calculateWithTaxSuccessfully() {
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal taxPercent = new BigDecimal("15.00");
        BigDecimal total = calculator.calculateWithTax(basePrice, taxPercent);
        assertEquals(new BigDecimal("115.00"), total);
    }

    @Test
    void calculateTaxAmountRoundsHalfUp() {
        BigDecimal basePrice = new BigDecimal("10.25");
        BigDecimal taxPercent = new BigDecimal("8.25"); // 10.25 * 0.0825 = 0.845625 -> 0.85
        BigDecimal tax = calculator.calculateTaxAmount(basePrice, taxPercent);
        assertEquals(new BigDecimal("0.85"), tax);
    }

    @Test
    void calculateTaxAmountRejectsNullBasePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateTaxAmount(null, new BigDecimal("15.00"));
        });
    }

    @Test
    void calculateTaxAmountRejectsNullTaxPercent() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateTaxAmount(new BigDecimal("100.00"), null);
        });
    }

    @Test
    void calculateTaxAmountRejectsNegativeBasePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateTaxAmount(new BigDecimal("-10.00"), new BigDecimal("15.00"));
        });
    }

    @Test
    void calculateTaxAmountRejectsNegativeTaxPercent() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateTaxAmount(new BigDecimal("100.00"), new BigDecimal("-5.00"));
        });
    }

    @Test
    void calculateWithTaxRejectsNullBasePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateWithTax(null, new BigDecimal("15.00"));
        });
    }

    @Test
    void calculateWithTaxRejectsNullTaxPercent() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateWithTax(new BigDecimal("100.00"), null);
        });
    }

    @Test
    void calculateWithTaxRejectsNegativeBasePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateWithTax(new BigDecimal("-10.00"), new BigDecimal("15.00"));
        });
    }

    @Test
    void calculateWithTaxRejectsNegativeTaxPercent() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateWithTax(new BigDecimal("100.00"), new BigDecimal("-5.00"));
        });
    }
}
