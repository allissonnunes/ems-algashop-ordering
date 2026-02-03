package br.dev.allissonnunes.algashop.ordering.utils;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(JsonFileSource.JsonFileSources.class)
@ArgumentsSource(JsonFileArgumentsProvider.class)
public @interface JsonFileSource {

    String[] resources();

    String encoding() default "UTF-8";

    @Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @interface JsonFileSources {

        /**
         * An array of one or more {@link JsonFileSource @JsonFileSource}
         * annotations.
         */
        JsonFileSource[] value();

    }

}
