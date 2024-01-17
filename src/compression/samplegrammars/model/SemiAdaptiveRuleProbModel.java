package compression.samplegrammars.model;

import compression.coding.Interval;
import compression.grammar.Category;
import compression.grammar.NonTerminal;
import compression.grammar.RNAGrammar;
import compression.grammar.RNAWithStructure;
import compression.grammar.Rule;
import compression.samplegrammars.RuleProbsForGrammarSemiAdaptive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A rule probability model that uses counts to estimate the probability of a rule from a
 * given RNA sequence.
 */
public class SemiAdaptiveRuleProbModel implements RuleProbModel {
	/**
	 * store right hand side as list of categories
	 */
	private final Map<NonTerminal, Map<List<Category>, Interval>> ruleProbs;

	/** Requires the rules in G and in probs to match in probability! */
	public SemiAdaptiveRuleProbModel(RNAGrammar G, RNAWithStructure rna) {
		// obtains probability for each rule given the particular RNA
		Map<Rule, Double> probs = new RuleProbsForGrammarSemiAdaptive(G, rna).ruleProbs();
		this.ruleProbs = StaticRuleProbModel.computeRuleIntervalMap(G, probs);
	}

	@Override
	public List<Interval> getIntervalList(NonTerminal lhs) {
		return new ArrayList<>(ruleProbs.get(lhs).values());
	}

	public List<Category> getRhsFor(Interval intvl, NonTerminal lhs) {
		for (List<Category> rhs : ruleProbs.get(lhs).keySet()) {
			if (intvl.equals(ruleProbs.get(lhs).get(rhs))) {
				return rhs;
			}
		}
		return null;
	}

	@Override
	public Interval getIntervalFor(final Rule rule) {
		return ruleProbs.get(rule.getLeft()).get(Arrays.asList(rule.getRight()));
	}


}
