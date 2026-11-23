package ua.beauty.exception;

/**
 * Unchecked exception thrown when an item with the same identity already exists
 * in a {@code GenericRepository}.
 *
 * <p>Extends {@link IllegalStateException} so callers are not forced to declare it,
 * but callers that need to handle duplicates explicitly can still catch it by name.</p>
 */
public class DuplicateIdentityException extends IllegalStateException {

    /**
     * @param identity identity value that caused the conflict
     * @param typeName simple name of the entity type (e.g. {@code "BeautyClient"})
     */
    public DuplicateIdentityException(String identity, String typeName) {
        super("Duplicate identity '" + identity + "' already exists in " + typeName + " repository");
    }
}
