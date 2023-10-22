package main;

import java.util.Collection;

import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

public class CollectionEquivalent<TCollection extends Collection<TItem>, TItem>
        implements ArgumentMatcher<TCollection> {

    private final TCollection expected;
    private TCollection actual;

    public CollectionEquivalent(TCollection expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(TCollection argument) {
        this.actual = argument;
        return this.actual.size() == this.expected.size() && this.actual.containsAll(this.expected);
    }

    public String toString() {
        return this.expected.toString();
    }

    public static <TCollection extends Collection<TItem>, TItem> TCollection argThat(TCollection expected) {
        return Mockito.argThat(new CollectionEquivalent<TCollection, TItem>(expected));
    }
}
