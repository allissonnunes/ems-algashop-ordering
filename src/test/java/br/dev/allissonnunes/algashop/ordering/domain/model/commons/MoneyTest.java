package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoney() {
        final Money money = new Money("1499.99");
        assertWith(money,
                m -> assertThat(m).isEqualTo(new Money("1499.99")),
                m -> assertThat(m).hasToString("%,#.2f", money.value()));
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateMoneyWithInvalidValues() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Money((BigDecimal) null))
                .withMessage("money cannot be null");

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Money((String) null))
                .withMessage("money cannot be null");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Money(""))
                .withMessage("money cannot be blank");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Money("-1.0"))
                .withMessage("money cannot be negative");
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1499.99,    1499.99
            899.996,    900.00
            5199.994,   5199.99
            """)
    void shouldRoundMoneyValueTo2DecimalPlacesUsingTheHalfEvenAlgorithm(String value, String expectedValue) {
        assertThat(new Money(value)).isEqualTo(new Money(expectedValue));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            # VALUE,    EQUAL,      GT1,        GT2,        LT1,        LT2
            5,          5,          4,          1,          6,          10
            30.57,      30.57,      30.56,      7.484,      31,         49
            549.98,     549.9823,   549.971,    300.57,     549.99,     899.99
            """)
    void shouldCompareMoney(String value, String equal, String gt1, String gt2, String lt1, String lt2) {
        assertWith(new Money(value),
                m -> assertThatComparable(m).isEqualByComparingTo(new Money(equal)),
                m -> assertThatComparable(m).isGreaterThan(new Money(gt1)),
                m -> assertThatComparable(m).isGreaterThan(new Money(gt2)),
                m -> assertThatComparable(m).isLessThan(new Money(lt1)),
                m -> assertThatComparable(m).isLessThan(new Money(lt2))
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1499.99, 500.00, 1999.99
            845.98, 875.38234, 1721.36
            """)
    void shouldAddMoney(final String moneyValue1, final String moneyValue2, final String expectedMoneyValue) {
        final Money money1 = new Money(moneyValue1);
        final Money money2 = new Money(moneyValue2);

        final Money result = money1.add(money2);

        assertThat(result).isEqualTo(new Money(expectedMoneyValue));
    }

    @Test
    void shouldThrowsExceptionWhenTryToAddNullMoney() {
        final Money money = Money.ZERO;

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> money.add(null))
                .withMessage("moneyToAdd cannot be null");
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1499.99,    1,  1499.99
            845.98,     2,  1691.96
            119.49974,  3,  358.50
            """)
    void shouldMultiplyMoney(final String moneyValue, final int quantity, final String expectedMoneyValue) {
        final Money money = new Money(moneyValue);

        final Money result = money.multiply(new Quantity(quantity));

        assertThat(result).isEqualTo(new Money(expectedMoneyValue));
    }

    @Test
    void shouldThrowsExceptionWhenTryToMultiplyInvalidQuantity() {
        final Money money = new Money("139.90");

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> money.multiply(null))
                .withMessage("quantity cannot be null");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> money.multiply(Quantity.ZERO))
                .withMessage("quantity cannot be negative or zero");
    }

}
