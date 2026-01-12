package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PhoneTest {

    @Test
    void shouldCreatePhone() {
        final Phone phone = new Phone("478-256-2504");
        assertWith(phone,
                p -> assertThat(p.value()).isEqualTo("478-256-2504"),
                p -> assertThat(p.toString()).isEqualTo("478-256-2504"));
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreatePhoneWithNullValue() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Phone(null))
                .withMessage("phone cannot be null");
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreatePhoneWithEmptyValue() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Phone(""))
                .withMessage("phone cannot be blank");
    }

}