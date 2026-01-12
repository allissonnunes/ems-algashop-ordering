package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DocumentTest {

    @Test
    void shouldCreateDocument() {
        final Document document = new Document("255-08-0578");
        assertWith(document,
                d -> assertThat(d.value()).isEqualTo("255-08-0578"),
                d -> assertThat(d.toString()).isEqualTo("255-08-0578"));
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateDocumentWithNullValue() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Document(null))
                .withMessage("document cannot be null");
    }

    @Test
    void shouldThrowsExceptionWhenTryToCreateDocumentWithEmptyValue() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Document(""))
                .withMessage("document cannot be blank");
    }

}