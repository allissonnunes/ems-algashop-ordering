package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class BirthDateTest {

    @Test
    void shouldCreateBirthDate() {
        final BirthDate birthDate = new BirthDate(LocalDate.of(1991, 7, 5));
        assertWith(birthDate,
                bd -> assertThat(bd.value()).isEqualTo(LocalDate.of(1991, 7, 5)),
                bd -> assertThat(bd.toString()).isEqualTo("1991-07-05"));
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateBirthDateWithNullValue() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new BirthDate(null))
                .withMessage("birthDate cannot be null");
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateBirthDateWithFutureValue() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BirthDate(LocalDate.now().plusDays(1L)))
                .withMessage("birthDate cannot be in the future");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BirthDate(LocalDate.now().plusMonths(1L)))
                .withMessage("birthDate cannot be in the future");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BirthDate(LocalDate.now().plusYears(1L)))
                .withMessage("birthDate cannot be in the future");
    }

    @Test
    void shouldCalculateAge() {
        final Clock ageCalculationClock = Clock.fixed(LocalDate.of(2021, 1, 1).atStartOfDay(Clock.systemDefaultZone().getZone()).toInstant(), Clock.systemDefaultZone().getZone());
        final BirthDate birthDate = new BirthDate(LocalDate.of(1991, 7, 5));
        assertThat(birthDate.age(ageCalculationClock)).isEqualTo(29);
    }

}