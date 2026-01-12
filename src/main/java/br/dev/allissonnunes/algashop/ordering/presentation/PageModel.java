package br.dev.allissonnunes.algashop.ordering.presentation;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNullElseGet;

@Builder
public record PageModel<T>(
        List<T> content,
        int number,
        int size,
        int totalPages,
        long totalElements
) {

    public PageModel {
        content = requireNonNullElseGet(content, ArrayList::new);
        if (number < 0) {
            throw new IllegalArgumentException("Number must be greater than or equal to zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("Total pages must be greater than or equal to zero");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements must be greater than or equal to zero");
        }
    }

    public static <T> PageModel<T> of(final Page<T> page) {
        return PageModel.<T>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

}
