package br.dev.allissonnunes.algashop.ordering.utils.hamcrest;

import org.hamcrest.Matcher;
import org.springframework.http.MediaType;

public final class AlgaShopMatchers {

    private AlgaShopMatchers() {
    }

    public static Matcher<String> uuid() {
        return MatchesUUID.uuid();
    }

    public static Matcher<String> contentType(final MediaType mediaType) {
        return MatchesContentType.contentType(mediaType);
    }

}
