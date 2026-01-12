package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateEmail() {
        final Email email = new Email("johndoe@email.com");
        assertWith(email,
                e -> assertThat(e.value()).isEqualTo("johndoe@email.com"),
                e -> assertThat(e.toString()).isEqualTo("johndoe@email.com"));
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateEmailWithNullValue() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Email(null))
                .withMessage("email cannot be null");
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateEmailWithEmptyValue() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Email(""))
                .withMessage("email cannot be blank");
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateEmailWithInvalidValue() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Email("invalid"))
                .withMessage("invalid is not a well-formed email address");
    }

}