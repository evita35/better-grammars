
package compression.grammar;

import compression.util.MyMultimap;

import java.util.*;
import java.util.stream.Collectors;



/**
 * Represents a stochastic context-free grammar (set of production rules with probabilities).
 *
 * Grammars maintain their rules indexed by
 * {@link Rule#getLeft() left side category}. The rule sets contained for
 * any given {@link Category left category} are not guaranteed to be
 * maintained in the order of insertion.
 *
 * Once the Grammar is instantiated, it is immutable.
 */
public class Grammar<T> {
    @SuppressWarnings("WeakerAccess")
    public final String name;
    public final NonTerminal startSymbol;
    public final Set<Terminal<T>> terminals = new HashSet<>();
    protected final MyMultimap<NonTerminal, Rule> rules;


    private final Set<NonTerminal> nonTerminals = new HashSet<>();

    public NonTerminal getStartSymbol() {
        return startSymbol;
    }

    /**
     * Creates a grammar with the given name, and given rules.
     * These restrictions ensure that
     * all nonterminals define probability measures over strings; i.e., P(X ~ x) is a proper distribution over x for all
     * X. Formal definitions of these conditions are given in Appendix A of An Efficient Probabilistic .
     *
     * @param name     The mnemonic name for this grammar.
     * @param rules_   Rules for the grammar
     */
    public Grammar(final String name, final NonTerminal startSymbol, final MyMultimap<NonTerminal, Rule> rules_) {
        this.name = name;
        this.rules = rules_;
        this.startSymbol = startSymbol;

        rules.lock();

        collectTerminalsAndNonTerminals(rules.values());
        // check that start symbol is in nonTerminals and has rules
        if (!nonTerminals.contains(startSymbol)) throw new IllegalArgumentException("Start symbol is not in nonTerminals");
        if (!rules.containsKey(startSymbol)) throw new IllegalArgumentException("Start symbol has no rules");

    }

    @SuppressWarnings("unchecked")
    private void collectTerminalsAndNonTerminals(final Collection<Rule> rules) {
        rules.forEach(rule -> {
            nonTerminals.add(rule.left);
            for (final Category c : rule.right)
                if (c instanceof Terminal) terminals.add((Terminal<T>) c);
                else if (c instanceof NonTerminal) nonTerminals.add((NonTerminal) c);
                else throw new Error("This is a bug");
        });
    }

    /**
     * Tests whether this grammar contains rules for the specified left side
     * category.
     *
     * @param left The left category of the rules to test for.
     * @return <code>true</code> iff this grammar contains rules with the
     * specified category as their {@link Rule#getLeft() left side}.
     */
    public boolean containsRules(final NonTerminal left) {
        return rules.containsKey(left);
    }

    /**
     * Gets the set of rules contained by this grammar with the given left
     * side category.
     *
     * @param LHS The {@link Rule#getLeft() left side} of the rules to find.
     * @return A set containing the rules in this grammar whose
     * {@link Rule#getLeft() left side} is
     * the same as <code>left</code>, or
     * <code>null</code> if no such rules are contained in this grammar. The
     * rule set returned by this method is <em>not</em> guaranteed to contain
     * the rules in the order in which they were {@link Builder#addRule(Rule) added}.
     */
    public Collection<Rule> getRules(final NonTerminal LHS) {
        if (!nonTerminals.contains(LHS)) throw new IllegalArgumentException();
        Collection<Rule> ruleForLHS = rules.get(LHS);
        return ruleForLHS != null ? ruleForLHS : Collections.emptyList();
    }

    /**
     * Gets every rule in this grammar.
     */
    @SuppressWarnings("WeakerAccess")
    public Collection<Rule> getAllRules() {
        return rules.values();
    }


    /**
     * Gets a string representation of this grammar.
     *
     * @return A string listing all of the rules contained by this grammar.
     * @see Rule#toString()
     */
    @Override
    public String toString() {
        return name +
                ": {\n" +
//                Stream.concat(
                rules.values().stream().sorted(Comparator.comparing(Rule::toString))
                        //,lexicalErrorRules.values().stream()
//                )
                        .map(s -> "  " + s)
                        .collect(Collectors.joining(",\n")) +
                "\n  start symbol: " + startSymbol +
                "\n}";
    }

    public int size() {
        return getAllRules().size();
    }

    @SuppressWarnings("unused")
    public Set<NonTerminal> getNonTerminals() {
        return nonTerminals;
    }




    public static class Builder<E> {
        private final MyMultimap<NonTerminal, Rule> rules = new MyMultimap<>();
        private String name;
        private NonTerminal startSymbol;

        public Builder(final String name, NonTerminal startSymbol) {
            this.name = name;
            this.startSymbol = startSymbol;
        }

        public Builder() {
            this.name = null;
        }



        @SuppressWarnings("unused")
        public Builder<E> setName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Adds a production rule.
         *
         * @param rule The rule to add.
         * @throws NullPointerException If <code>rule</code> is <code>null</code>.
         */
        public Builder<E> addRule(final Rule rule) {
            if (rule == null) throw new NullPointerException("null rule");
//            if (rule instanceof LexicalErrorRule) {
//                lexicalErrorRules.put(rule.left, rule);
//            } else {
            rules.put(rule.left, rule);
//            }
            return this;
        }


        public Builder<E> addRule(final NonTerminal left, final Category... right) {
            return addRule(new Rule(left, right));
        }



        public Grammar<E> build() {
            return new Grammar<>(name, startSymbol, rules);
        }

        @SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
        public Builder<E> addRules(final Collection<Rule> rules) {
            rules.forEach(this::addRule);
            return this;
        }
    }
}
