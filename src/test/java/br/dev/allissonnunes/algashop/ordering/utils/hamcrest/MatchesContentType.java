package br.dev.allissonnunes.algashop.ordering.utils.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.MediaType;

class MatchesContentType extends TypeSafeMatcher<String> {

    private final MediaType mediaType;

    MatchesContentType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    protected boolean matchesSafely(final String item) {
        return this.mediaType.isCompatibleWith(MediaType.parseMediaType(item));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("a valid " + this.mediaType);
    }

    public static Matcher<String> contentType(final MediaType mediaType) {
        return new MatchesContentType(mediaType);
    }

}
