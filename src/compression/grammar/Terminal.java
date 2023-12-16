package compression.grammar;

/**
 * Terminal is a wrapper around an object; usually a single character of a string.
 */
public interface Terminal<T> extends Category {

	/**
	 * @return the object wrapped by this terminal
	 */
	T getChars();
}
