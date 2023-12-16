package compression.parser;

import compression.grammar.Rule;
import compression.grammar.Terminal;
import compression.grammargenerator.UnparsableException;

import java.util.List;

/**
 *
 * A Parser shall be constructed from a Grammar<T>.
 *
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public interface Parser<T> {

	List<Rule> leftmostDerivationFor(List<Terminal<T>> word) throws UnparsableException;

	default boolean parsable(final List<Terminal<T>> word) {
		try {
			leftmostDerivationFor(word);
			return true;
		} catch (final UnparsableException e) {
			return false;
		}
	}

}
