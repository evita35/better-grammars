package compression.samplegrammars.model;

import compression.coding.BigDecimalInterval;
import compression.coding.Interval;
import compression.grammar.Category;
import compression.grammar.Grammar;
import compression.grammar.Rule;
import compression.grammar.NonTerminal;

import java.math.BigDecimal;
import java.util.*;

/**
 * Interface for our general abstraction of a rule probability model.
 * <p>
 * The model is responsible for computing the probability of each rule and
 * for selecting the right-hand side of a rule given the left-hand side and
 * a subinterval of [0,1).
 */
public interface RuleProbModel {

	/**
     * Compute the probability for the given rule and use it as the length of
     * the interval; the starting point of the interval is the total probability
     * of all rules preceding this rule.
     * <p>
     * If the model adapts to the input, this method will change future probabilities.
     *
     * @param rule
     * @return
     */
    Interval getIntervalFor(final Rule rule);

    /**
     * Compute the list of Intervals for all rules with the given left-hand side.
     * <p>
     * This method does not change future probabilities.
     *
     * @param lhs
     * @return
     */
    List<Interval> getIntervalList(NonTerminal lhs);

    /**
     * Selects the right-hand side of the rule with given left-hand side (lhs)
     * whose interval is the given interval.
     * <p>
     * If the model adapts to the input, this method will change future probabilities.
     *
     * @param interval
     * @param lhs
     * @return
     */
    List<Category> getRhsFor(Interval interval, NonTerminal lhs);


	/** Helper method to convert from rule counts to rule probabilities */
	static Map<Rule, Double> computeRuleProbs(Grammar<?> grammar, Map<Rule, Long> ruleCounts) {
		Map<Rule, Double> rulesToProbs = new HashMap<>();
		Map<NonTerminal, Long> nonTerminalFreq = new HashMap<>();
		Set<Rule> rules = ruleCounts.keySet();

		for (NonTerminal NT : grammar.getNonTerminals()) nonTerminalFreq.put(NT, 0L);

		for (Rule rule : rules) {
			nonTerminalFreq.replace(rule.left, nonTerminalFreq.get(rule.left) + ruleCounts.get(rule));
		}

		for (Rule rule : rules) {
			rulesToProbs.put(rule,
					nonTerminalFreq.get(rule.left) == 0 ?
							Double.NaN :
							((double) ruleCounts.get(rule)) / nonTerminalFreq.get(rule.left));
		}
		return rulesToProbs;
	}

	/**
	 * A dummy rule probability model that returns the unit interval for every rule,
	 * useful for parsing when an arbitrary derivation is acceptable.
	 * <p>
	 * Does not support getIntervalList and getRhsFor.
	 */
	RuleProbModel DONT_CARE = new RuleProbModel() {

		@Override
		public Interval getIntervalFor(final Rule rule) {
			return BigDecimalInterval.UNIT_INTERVAL;
		}

		@Override
		public List<Interval> getIntervalList(final NonTerminal lhs) {
			throw new UnsupportedOperationException("Not implemented.");
		}

		@Override
		public List<Category> getRhsFor(final Interval interval, final NonTerminal lhs) {
			throw new UnsupportedOperationException("Not implemented.");
		}
	};

	/**
	 * A dummy rule probability model that returns [0,0.5) for every rule,
	 * useful for parsing when an arbitrary derivation is acceptable,
	 * but shortest derivations are sought.
	 * <p>
	 * Does not support getIntervalList and getRhsFor.
	 */
	RuleProbModel HALF_EACH = new RuleProbModel() {

		private final BigDecimalInterval zeroToHalf = new BigDecimalInterval(BigDecimal.ZERO, BigDecimal.valueOf(0.5));

		@Override
		public Interval getIntervalFor(final Rule rule) {
			return zeroToHalf;
		}

		@Override
		public List<Interval> getIntervalList(final NonTerminal lhs) {
			throw new UnsupportedOperationException("Not implemented.");
		}

		@Override
		public List<Category> getRhsFor(final Interval interval, final NonTerminal lhs) {
			throw new UnsupportedOperationException("Not implemented.");
		}
	};

}
