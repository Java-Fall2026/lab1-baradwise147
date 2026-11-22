package ua.beauty.exception;

/**
 * Checked exception thrown when input data from an external file or stream is invalid or malformed.
 */
public class InvalidDataException extends Exception {

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
