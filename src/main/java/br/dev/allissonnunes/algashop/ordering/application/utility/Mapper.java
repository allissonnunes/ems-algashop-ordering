package br.dev.allissonnunes.algashop.ordering.application.utility;

import org.jspecify.annotations.Nullable;

public interface Mapper {

    <T> @Nullable T convert(@Nullable Object source, Class<T> targetType);

}
