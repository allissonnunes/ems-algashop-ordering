package br.dev.allissonnunes.algashop.ordering.utils;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.support.ParameterDeclarations;
import org.junit.platform.commons.PreconditionViolationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;

class JsonFileArgumentsProvider extends AnnotationBasedArgumentsProvider<JsonFileSource> {

    @Override
    protected @NonNull Stream<? extends Arguments> provideArguments(
            final @NonNull ParameterDeclarations parameters,
            final @NonNull ExtensionContext context,
            final JsonFileSource annotation) {
        final Charset charset = getCharsetFrom(annotation);
        return Stream.of(annotation.resources())
                .map(resourceName ->
                        Arguments.argumentSet(
                                resourceName,
                                readContent(resourceName, charset, context.getRequiredTestClass())));
    }

    private Charset getCharsetFrom(final JsonFileSource jsonFileSource) {
        try {
            return Charset.forName(jsonFileSource.encoding());
        } catch (Exception ex) {
            throw new PreconditionViolationException("The charset supplied in " + jsonFileSource + " is invalid", ex);
        }
    }

    private String readContent(final String resourceName, final Charset charset, final Class<?> baseClass) {
        try (final var is = baseClass.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new RuntimeException(new FileNotFoundException(resourceName));
            }
            return new String(is.readAllBytes(), charset);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
