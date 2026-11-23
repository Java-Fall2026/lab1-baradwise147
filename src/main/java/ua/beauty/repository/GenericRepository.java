package ua.beauty.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.beauty.exception.DuplicateIdentityException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Generic in-memory repository backed by a {@link LinkedHashMap}.
 *
 * <p>LinkedHashMap is chosen deliberately: it preserves insertion order so that
 * {@link #findAll()} always returns items in the order they were added, which makes
 * the output reproducible and easier to reason about during testing and benchmarking.</p>
 *
 * <p>The repository does not require any shared base class or interface from the stored
 * entities — uniqueness is enforced solely through the provided {@link IdentityExtractor}.</p>
 *
 * @param <T> the type of items stored in this repository
 */
public class GenericRepository<T> {

    private static final Logger log = LoggerFactory.getLogger(GenericRepository.class);

    /** Backing store: identity → item. LinkedHashMap preserves insertion order. */
    private final Map<String, T> store = new LinkedHashMap<>();

    private final Class<T> type;
    private final IdentityExtractor<T> extractor;

    /**
     * Creates a new repository for items of the given type.
     *
     * @param type      runtime class of the stored items; used in exception messages
     * @param extractor lambda that maps an item to its unique identity string
     */
    public GenericRepository(Class<T> type, IdentityExtractor<T> extractor) {
        if (type == null || extractor == null) {
            throw new IllegalArgumentException("Type and extractor must not be null");
        }
        this.type = type;
        this.extractor = extractor;
    }

    /**
     * Adds an item to the repository.
     *
     * @param item item to add; must not be null and must have a non-blank identity
     * @throws IllegalArgumentException   if {@code item} is null or its identity is blank
     * @throws DuplicateIdentityException if an item with the same identity already exists
     */
    public void add(T item) {
        String identity = extractIdentity(item);
        if (store.containsKey(identity)) {
            throw new DuplicateIdentityException(identity, type.getSimpleName());
        }
        store.put(identity, item);
        log.info("Added {} with identity '{}'", type.getSimpleName(), identity);
    }

    /**
     * Adds an item only if no item with the same identity already exists.
     *
     * @param item item to add
     * @return {@code true} if the item was added, {@code false} if a duplicate was found
     * @throws IllegalArgumentException if {@code item} is null or its identity is blank
     */
    public boolean addIfAbsent(T item) {
        String identity = extractIdentity(item);
        if (store.containsKey(identity)) {
            log.warn("Skipped duplicate {} with identity '{}'", type.getSimpleName(), identity);
            return false;
        }
        store.put(identity, item);
        log.info("Added {} with identity '{}'", type.getSimpleName(), identity);
        return true;
    }

    /**
     * Adds all items from the collection, skipping any duplicates silently.
     *
     * @param items items to add; null collection is treated as empty
     */
    public void addAll(Collection<? extends T> items) {
        if (items == null) {
            return;
        }
        for (T item : items) {
            addIfAbsent(item);
        }
    }

    /**
     * Removes the item with the given identity.
     *
     * @param identity identity of the item to remove
     * @return {@code true} if the item was removed, {@code false} if it was not found
     */
    public boolean removeByIdentity(String identity) {
        if (store.remove(identity) != null) {
            log.info("Removed {} with identity '{}'", type.getSimpleName(), identity);
            return true;
        }
        log.warn("Cannot remove: no {} found with identity '{}'", type.getSimpleName(), identity);
        return false;
    }

    /**
     * Looks up an item by its identity.
     *
     * @param identity the identity to search for
     * @return an {@link Optional} containing the found item, or empty if not present
     */
    public Optional<T> findByIdentity(String identity) {
        T found = store.get(identity);
        if (found == null) {
            log.warn("Not found: no {} with identity '{}'", type.getSimpleName(), identity);
        }
        return Optional.ofNullable(found);
    }

    /**
     * Returns an unmodifiable snapshot of all stored items in insertion order.
     * External code cannot modify the repository through the returned list.
     *
     * @return unmodifiable list of all items
     */
    public List<T> findAll() {
        return List.copyOf(store.values());
    }

    /**
     * Returns the number of items currently stored.
     *
     * @return item count
     */
    public int size() {
        return store.size();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Extracts and validates the identity of the given item.
     *
     * @param item the item whose identity should be extracted
     * @return non-blank identity string
     * @throws IllegalArgumentException if {@code item} is null or its identity is blank
     */
    private String extractIdentity(T item) {
        if (item == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " item must not be null");
        }
        String identity = extractor.identityOf(item);
        if (identity == null || identity.isBlank()) {
            throw new IllegalArgumentException(
                    type.getSimpleName() + " identity must not be null or blank");
        }
        return identity;
    }
}
