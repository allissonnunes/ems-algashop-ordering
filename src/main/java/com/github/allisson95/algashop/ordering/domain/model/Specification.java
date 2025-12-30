package com.github.allisson95.algashop.ordering.domain.model;

import static java.util.Objects.requireNonNull;

@FunctionalInterface
public interface Specification<T> {

    boolean isSatisfiedBy(T t);

    static <T> Specification<T> where(Specification<T> spec) {
        requireNonNull(spec, "spec cannot be null");
        return spec;
    }

    default Specification<T> and(Specification<T> other) {
        requireNonNull(other, "other specification cannot be null");
        return t -> isSatisfiedBy(t) && other.isSatisfiedBy(t);
    }

    default Specification<T> or(Specification<T> other) {
        requireNonNull(other, "other specification cannot be null");
        return t -> isSatisfiedBy(t) || other.isSatisfiedBy(t);
    }

    static <T> Specification<T> not(Specification<T> spec) {
        requireNonNull(spec, "spec cannot be null");
        return t -> !spec.isSatisfiedBy(t);
    }

}
