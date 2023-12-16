/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.samplegrammars.model;

import compression.coding.BigDecimalInterval;
import compression.coding.Interval;
import compression.grammar.PairOfChar;
import compression.grammar.Category;
import compression.grammar.NonTerminal;
import compression.grammar.Grammar;
import compression.grammar.Rule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A rule probability model that uses adaptive counts to estimate the probability of a
 * rule.
 * <p>
 * The probability of a rule is estimated by the frequency of the rule divided by the sum
 * of the frequencies of all rules with the same left-hand side.
 * <p>
 * The frequency of a rule is initialised to 1, and is incremented by 1 every time the
 * rule is used.
 * <p>
 * The probability of a rule is updated every time the rule is used.
 * <p>
 * When turning counts into probabilities, we use a given precision, i.e., the number of
 * decimal digits in the probability.
 *
 * @author Eva
 */
public class AdaptiveRuleProbModel implements RuleProbModel {

    private Map<NonTerminal, Map<List<Category>, Interval>> ruleProbs;
    private final Grammar<?> grammar;

    private final Map<NonTerminal, Map<List<Category>, Long>> ruleFreq = new HashMap<>();

    /**
     * precision is the scale used for BigDecimalIntervals, i.e., the number of decimal digits in the probability
     */
    private final int precision;

    int counter=0;
    private Map<NonTerminal, Map<List<Category>, Interval>> getRuleProbs() {
        if (ruleProbs == null) {
            fillRuleProbs();

        }
        return ruleProbs;
    }

    public AdaptiveRuleProbModel(final Grammar<?> G, final int precision) {
        this.grammar = G;
        this.precision = precision;
        for (NonTerminal nonTerminal : G.getNonTerminals()) {
            ruleFreq.put(nonTerminal, new HashMap<>());
            for (Rule rule : G.getRules(nonTerminal)) {
                // adaptive count 1 for all frequency at initialisation
                ruleFreq.get(nonTerminal).put(rhsOf(rule), 1L);
            }
        }
        fillRuleProbs();
        //System.out.println(ruleProbs);
        //System.out.println("\n\n");

    }

    public AdaptiveRuleProbModel(Grammar<PairOfChar> G) {
        this(G, 10);
    }

    /**
     * update the Map for non terminals to the Map of rules to
     */
    private void updateRuleFrequency(Rule rule) {
        if (rule.getLeft().toString().compareTo("<start>") == 0)
            return;
        else
            updateRuleFrequency(rule.getLeft(), rhsOf(rule));
    }

    private void updateRuleFrequency(NonTerminal lhs, List<Category> rhs) {
        this.ruleProbs = null; // invalidate old probabilities
        // increment count
        ruleFreq.get(lhs).merge(rhs, 1L, Long::sum);


    }

    private void fillRuleProbs() {
        ruleProbs = new HashMap<>();
        for (NonTerminal lhs : ruleFreq.keySet()) {
            ruleProbs.put(lhs, new HashMap<>());

            Map<List<Category>, Long> rhsFreqsForNT = ruleFreq.get(lhs);
            long leftNumerator = 0;

            // Step 1: sum up all occurrences of a particular nonterminal on the LHS of a production rule
            long totalFreq = 0;
            for (Rule rule : grammar.getRules(lhs)) {
                totalFreq += rhsFreqsForNT.get(rhsOf(rule));
            }
            // Step 2: Compute rule probabilities
            final BigDecimal denominator = BigDecimal.valueOf(totalFreq);
            for (Rule rule : grammar.getRules(lhs)) {
                List<Category> rhs = rhsOf(rule);
//                double p = ((double) rhsFreqsForNT.get(rhs)) / (double) totalFreq;
                long numerator = rhsFreqsForNT.get(rhs);
                BigDecimal p = BigDecimal.valueOf(numerator).divide(denominator, precision, RoundingMode.DOWN);
                BigDecimal left = BigDecimal.valueOf(leftNumerator).divide(denominator, precision, RoundingMode.DOWN);
                ruleProbs.get(lhs).put(rhs, new BigDecimalInterval(left, p));
                leftNumerator += numerator;
            }
        }
    }

    private static List<Category> rhsOf(final Rule rule) {
        return Arrays.asList(rule.getRight());
    }

    @Override
    public Interval getIntervalFor(final Rule rule) {

        // Step 1: Compute the interval using old probs
        final Interval res = getRuleProbs().get(rule.getLeft()).get(rhsOf(rule));
        // Step 2: Update counters
        updateRuleFrequency(rule);
        // Step 3: return computed interval
        return res;

    }

    @Override
    public List<Interval> getIntervalList(NonTerminal lhs) {

        return new ArrayList<>(getRuleProbs().get(lhs).values());
    }

    @Override
    public List<Category> getRhsFor(Interval intvl, NonTerminal lhs) {
        // Step 1: find rule for given intvl
        List<Category> res = null;
        for (List<Category> rhs : getRuleProbs().get(lhs).keySet()) {
            if (intvl.equals(getRuleProbs().get(lhs).get(rhs))) {
                res = rhs;
                break;
            }
        }
        if (res == null) throw new IllegalArgumentException("Did not find any rhs for given lhs and interval");
        // Step 2: Update rule counters
        updateRuleFrequency(lhs, res);
        // Step 3: return rhs
        return res;
    }

}
