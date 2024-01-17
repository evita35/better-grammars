package compression.samplegrammars.model;

import compression.coding.BigDecimalInterval;
import compression.coding.Interval;
import compression.grammar.PairOfChar;
import compression.grammar.Category;
import compression.grammar.NonTerminal;
import compression.grammar.Grammar;
import compression.grammar.Rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A rule probability model that uses fixed rule probabilities.
 */
public class StaticRuleProbModel implements RuleProbModel {

	/**
	 * store right hand side as list, not entire rule to avoid failed lookups when used
	 * with rules of different probabilities
	 */
	private final Map<NonTerminal, Map<List<Category>, Interval>> ruleProbs;
	Grammar<?> grammar;

	/** Requires the rules in grammar and in probs to match in probability! */
	public StaticRuleProbModel(Grammar<?> grammar, Map<Rule, Double> probs) {
		this.grammar = grammar;
		this.ruleProbs = computeRuleIntervalMap(grammar, probs);
	}

	public static Map<NonTerminal, Map<List<Category>, Interval>> computeRuleIntervalMap(
			final Grammar<?> grammar, final Map<Rule, Double> probs) {
		Map<NonTerminal, Map<List<Category>, Interval>> res = new HashMap<>();
		for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
			res.put(nonTerminal, new HashMap<>());
			//maps each rule to its subinterval of [0, 1)
			BigDecimal left = BigDecimal.ZERO; // left endpoint of interval
			for (Rule rule : grammar.getRules(nonTerminal)) {
				Double pp = probs.get(rule);
				if (!Double.isNaN(pp)) { // don't create entry for rules with NaN prob
					BigDecimal p = BigDecimal.valueOf(pp);//
					ArrayList<Category> rhs = new ArrayList<>(Arrays.asList(rule.getRight()));
					res.get(nonTerminal).put(rhs, new BigDecimalInterval(left, p));
					left = left.add(p);
				}
			}
		}
		return res;
	}

	void printRuleProbs() {
		//System.out.println("PRINTING OUT RULE PROBS");
		for (NonTerminal nt : ruleProbs.keySet()) {
			//System.out.println(nt);
			for (List<Category> catlist : ruleProbs.get(nt).keySet())
				System.out.println(catlist.toString() + " " + ruleProbs.get(nt).get(catlist).toString());
		}
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
		Interval res = ruleProbs.get(rule.getLeft()).get(Arrays.asList(rule.getRight()));
		if (res == null) throw new IllegalArgumentException("rule not found in ruleProbs: " + rule);
		return res;
	}


}
