package ua.beauty.repository;

/**
 * Functional interface that extracts a unique string identity from an item.
 * Implementations are typically provided as lambdas — no separate class needed.
 *
 * @param <T> the type of item this extractor operates on
 */
@FunctionalInterface
public interface IdentityExtractor<T> {

    /**
     * Returns the unique identity string of the given item.
     *
     * @param item the item to extract identity from
     * @return non-null, non-blank string that uniquely identifies {@code item}
     */
    String identityOf(T item);
}
