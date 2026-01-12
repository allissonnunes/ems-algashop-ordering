package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class RecipientEmbeddable {

    private String firstName;

    private String lastName;

    private String document;

    private String phone;

}
