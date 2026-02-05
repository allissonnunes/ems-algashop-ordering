package br.dev.allissonnunes.algashop.ordering.utils.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.text.MatchesPattern;

class MatchesUUID extends TypeSafeMatcher<String> {

    private static final Matcher<String> UUID_PATTERN_MATCHER
            = MatchesPattern.matchesPattern("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");

    @Override
    protected boolean matchesSafely(final String item) {
        return UUID_PATTERN_MATCHER.matches(item);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("a valid UUID");
    }

    public static Matcher<String> uuid() {
        return new MatchesUUID();
    }

}
