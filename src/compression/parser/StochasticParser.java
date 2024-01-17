package compression.parser;

import compression.grammar.Rule;
import compression.grammar.Terminal;
import compression.grammargenerator.UnparsableException;

import java.util.List;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public interface StochasticParser<T> extends Parser<T> {

	/**
	 * @return the most likely leftmost derivation for the given word
	 * @throws UnparsableException if word is not in the language of the grammar
	 * */
	List<Rule> mostLikelyLeftmostDerivationFor(List<Terminal<T>> word) throws UnparsableException;

	/** @return ln(p) for p the probability of the most likely leftmost derivation for the given word */
	double logProbabilityOf(List<Terminal<T>> word) throws UnparsableException;

}
