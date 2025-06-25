package no.ssb.klass.core.util;

import java.util.function.Predicate;

public class PredicateUtils {

    /**
     * Negate the result of another predicate. Useful for method references.
     */
    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
