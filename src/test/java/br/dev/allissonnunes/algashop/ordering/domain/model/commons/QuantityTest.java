package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class QuantityTest {

    @Test
    void shouldCreateQuantity() {
        final Quantity quantity = new Quantity(5);
        assertWith(quantity,
                q -> assertThat(q).isEqualTo(new Quantity(5)),
                q -> assertThat(q.toString()).isEqualTo("5"));
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateQuantityWithNullValueOrNegativeValue() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Quantity(null))
                .withMessage("quantity cannot be null");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Quantity(-1))
                .withMessage("quantity cannot be negative");
    }

    @Test
    void shouldReturnANewQuantityWithAddedValue() {
        final Quantity q1 = new Quantity(3);
        final Quantity q2 = new Quantity(5);

        final Quantity result = q1.add(q2);

        assertThat(result).isEqualTo(new Quantity(8));
    }

    @Test
    void shouldThrowsExceptionWhenTryToAddNullQuantity() {
        final Quantity q1 = new Quantity(3);

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> q1.add(null))
                .withMessage("quantityToAdd cannot be null");
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            # VALUE,    EQUAL,  GT1,    GT2,    LT1,    LT2
            5,          5,      4,      1,      6,      10
            30,         30,     29,     7,      31,     49
            """)
    void shouldCompareQuantity(int value, int equal, int gt1, int gt2, int lt1, int lt2) {
        assertWith(new Quantity(value),
                q -> assertThatComparable(q).isEqualByComparingTo(new Quantity(equal)),
                q -> assertThatComparable(q).isGreaterThan(new Quantity(gt1)),
                q -> assertThatComparable(q).isGreaterThan(new Quantity(gt2)),
                q -> assertThatComparable(q).isLessThan(new Quantity(lt1)),
                q -> assertThatComparable(q).isLessThan(new Quantity(lt2))
        );
    }

}
