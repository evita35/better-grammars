package compression.parser;

import compression.grammar.Rule;
import compression.grammar.Terminal;
import compression.grammargenerator.UnparsableException;

import java.util.List;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public interface StochasticParser<T> extends Parser<T> {

	List<Rule> mostLikelyLeftmostDerivationFor(List<Terminal<T>> word) throws UnparsableException;

	double logProbabilityOf(List<Terminal<T>> word) throws UnparsableException;

}
