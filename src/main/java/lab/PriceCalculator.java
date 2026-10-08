package lab;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {

    public BigDecimal calculateWithTax(BigDecimal basePrice, BigDecimal taxPercent) {
        if (basePrice == null || taxPercent == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        if (basePrice.compareTo(BigDecimal.ZERO) < 0 || taxPercent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Arguments must be non-negative");
        }
        BigDecimal taxAmount = calculateTaxAmount(basePrice, taxPercent);
        return basePrice.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTaxAmount(BigDecimal basePrice, BigDecimal taxPercent) {
        if (basePrice == null || taxPercent == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        if (basePrice.compareTo(BigDecimal.ZERO) < 0 || taxPercent.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Arguments must be non-negative");
        }
        return basePrice.multiply(taxPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
