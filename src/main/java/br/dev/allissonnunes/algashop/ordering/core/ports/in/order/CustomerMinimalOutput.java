package br.dev.allissonnunes.algashop.ordering.core.ports.in.order;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerMinimalOutput(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String document,
        String phone
) {

}
