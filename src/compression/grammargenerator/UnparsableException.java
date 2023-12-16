package compression.grammargenerator;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class UnparsableException extends Exception {
	public UnparsableException() {
	}

	public UnparsableException(final String message) {
		super(message);
	}

	public UnparsableException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnparsableException(final Throwable cause) {
		super(cause);
	}

	public UnparsableException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
