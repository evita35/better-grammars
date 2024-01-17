package compression.parser;

import compression.grammar.*;
import compression.grammargenerator.UnparsableException;
import compression.samplegrammars.model.RuleProbModel;

import java.util.*;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 *
 * This class is not thread-safe.
 */

public class SRFParser<T> implements StochasticParser<T> {

    private final Grammar<T> grammar;

    private final RuleProbModel ruleProbModel;

    Map<NonTerminal, Integer> integerNonTerminalMap;
    Map<Integer, NonTerminal> nonTerminalIntegerMap;

    boolean[][][] booleanArray;
    double[][][] logProb;
    Rule[][][] backRule;
    int[][][] backSplit;

    ArrayList<String> sortedNonTerminals;

    public static final int INVALID_SPLIT = -1;
    /**
     * type 1 Ai→ Aj Al
     */
    private final List<Rule> type1Rules;

    private final List<Type1RuleIndices> type1RuleIndices;

    /**
     * type2 Ai → .
     */
    private final List<Rule> type2Rules;
    /**
     * type 3 Ai →(Aj)
     */
    private final List<Rule> type3Rules;
    /**
     * type4 Ai→ Aj    j<i
     */
    private final List<Rule> type4Rules;

    public SRFParser(final Grammar<T> grammar) {
        this(grammar, RuleProbModel.DONT_CARE);
    }

    public SRFParser(final Grammar<T> grammar, final RuleProbModel ruleProbModel) {
        Objects.requireNonNull(grammar);
        Objects.requireNonNull(ruleProbModel);
        this.grammar = grammar;
        this.ruleProbModel = ruleProbModel;

        sortedNonTerminals = new ArrayList<>();
        Collection<Rule> grammarRules = grammar.getAllRules();
        type1Rules = new ArrayList<>();
        type2Rules = new ArrayList<>();
        type3Rules = new ArrayList<>();
        type4Rules = new ArrayList<>();

        for (Rule rule : grammarRules) {
            sortedNonTerminals.add(rule.left.toString()); //adds the string value of the rule's LHS to the sorted tree

            if (rule.right.length == 1) {
                if (Category.isTerminal(rule.right[0])) {
                    type2Rules.add(rule);
                } else
                    type4Rules.add(rule);
            } else if (rule.right.length == 2) {
                if (!Category.isTerminal(rule.right[0]) && !Category.isTerminal(rule.right[1])) {
                    type1Rules.add(rule);
                } else
                    throw new IllegalArgumentException("Error found in rule: " + rule + " rule is not in SRF form");
            } else if (rule.right.length == 3) {
                if (Category.isTerminal(rule.right[0]) && !Category.isTerminal(rule.right[1]) && Category.isTerminal(rule.right[2])) {
                    type3Rules.add(rule);
                } else
                    throw new IllegalArgumentException("Error found in rule: " + rule + " rule is not in SRF form");

            }
        }
        Collections.sort(sortedNonTerminals);//sorts in alphabetical order
        // Sort type 4 rules in order of the LHS
        type4Rules.sort((Rule o1, Rule o2) ->
                Integer.compare(
                        sortedNonTerminals.indexOf(o1.left.toString()),
                        sortedNonTerminals.indexOf(o2.left.toString())));
        for(Rule rule: type4Rules){
            if (sortedNonTerminals.indexOf(rule.left.toString()) <= sortedNonTerminals.indexOf(rule.right[0].toString())) {
                throw new IllegalArgumentException("Error found in type 4 rule: " + rule + " rule is not in SRF form");
            }
        }

        computeNonterminalToIndicesMap();

        // Optimization: cache type1 rule indices
        type1RuleIndices = new ArrayList<>();
        for (Rule rule : type1Rules) {
            //noinspection SuspiciousMethodCalls
            type1RuleIndices.add(new Type1RuleIndices(
                    this.integerNonTerminalMap.get(rule.left),
                    this.integerNonTerminalMap.get(rule.right[0]),
                    this.integerNonTerminalMap.get(rule.right[1]),
                    rule
            ));
        }
    }

    @Override
    public Grammar<T> getGrammar() {
        return grammar;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public boolean parsable(final List<Terminal<T>> word) {
        // Can avoid the backtrace in super.parsable()
        int n = word.size();
        fillTableIgnoreProbs(word);
        int S = integerNonTerminalMap.get(grammar.getStartSymbol());
        return booleanArray[n][1][S];
    }

    @Override
    public List<Rule> leftmostDerivationFor(final List<Terminal<T>> word) throws UnparsableException {
        if (ruleProbModel == null)
            throw new UnsupportedOperationException("No rule probability model provided.");
        int n = word.size();
        fillTableIgnoreProbs(word);
        List<Rule> derivation = new ArrayList<>();
        backtraceDerivation(n, 1, grammar.getStartSymbol(), derivation);
        return derivation;
    }


    @Override
    public List<Rule> mostLikelyLeftmostDerivationFor(final List<Terminal<T>> word) throws UnparsableException {
        if (ruleProbModel == null)
            throw new UnsupportedOperationException("No rule probability model provided.");
        int n = word.size();
        fillTable(word);
        List<Rule> derivation = new ArrayList<>();
        backtraceDerivation(n, 1, grammar.getStartSymbol(), derivation);
        return derivation;
    }

    /**
     * @return the word generated by the most likely derivation for the given `word`.
     * @throws UnparsableException if the given `word` is not parsable (i.e. not part of the language of the grammar).
     */
    public List<Terminal<T>> mostLikelyWord(final List<Terminal<T>> word) throws UnparsableException {
        if (ruleProbModel == null)
            throw new UnsupportedOperationException("No rule probability model provided.");
        int n = word.size();
        fillTable(word);
        List<Terminal<T>> backWord = new ArrayList<>();
        backtraceWord(n, 1, grammar.getStartSymbol(), backWord);
        return backWord;
    }



    private void backtraceWord(int l, int s, final NonTerminal NT, final List<Terminal<T>> result) throws UnparsableException {
        int nt = integerNonTerminalMap.get(NT);
        int p = backSplit[l][s][nt];
        Rule rule = backRule[l][s][nt];
        if (rule == null) throw new UnparsableException();
        if (type1Rules.contains(rule)) { // A -> B C
            assert rule.left == NT;
            NonTerminal B = (NonTerminal) rule.right[0];
            NonTerminal C = (NonTerminal) rule.right[1];
            backtraceWord(p, s, B, result);
            backtraceWord(l - p, s + p, C, result);
        } else if(type2Rules.contains(rule)) {
            // A -> a
            //noinspection unchecked
            result.add((Terminal<T>) rule.right[0]);
        }
        else if(type3Rules.contains(rule)){
            // A -> (B)
            NonTerminal B = (NonTerminal) rule.right[1];
            //noinspection unchecked
            result.add((Terminal<T>) rule.right[0]);
            backtraceWord(l-2, s+1, B, result);
            //noinspection unchecked
            result.add((Terminal<T>) rule.right[2]);
        }
        else if(type4Rules.contains(rule)){
            // A -> B
            NonTerminal B = (NonTerminal) rule.right[0];
            backtraceWord(l, s, B, result);
        }
        else {
            throw new IllegalStateException("Unknown rule type: " + rule);
        }
    }

    private void backtraceDerivation(int l, int s, final NonTerminal NT, final List<Rule> result) throws UnparsableException {
        int nt = integerNonTerminalMap.get(NT);
        int p = backSplit[l][s][nt];
        Rule rule = backRule[l][s][nt];
        if (rule == null) throw new UnparsableException();
//        System.out.println(rule);
        result.add(rule);
        if (type1Rules.contains(rule)) { // A -> B C
            //System.out.println("VALUE OF NT IS: "+NT+" VALUE OF RULE.LEFT IS: "+rule.left);
            assert rule.left.equals(NT);
            NonTerminal B = (NonTerminal) rule.right[0];
            NonTerminal C = (NonTerminal) rule.right[1];
            backtraceDerivation(p, s, B, result);
            backtraceDerivation(l - p, s + p, C, result);
        } else if (type2Rules.contains(rule)) {
            // A -> a
            return; // nothing to do
        } else if (type3Rules.contains(rule)) {
            // A -> (B)
            NonTerminal B = (NonTerminal) rule.right[1];
            backtraceDerivation(l - 2, s + 1, B, result);
        } else if (type4Rules.contains(rule)) {
            // A -> B
            NonTerminal B = (NonTerminal) rule.right[0];
            backtraceDerivation(l, s, B, result);
        } else {
            throw new IllegalStateException("Unknown rule type: " + rule);
        }
    }

    @Override
    public double logProbabilityOf(final List<Terminal<T>> word) {
        if (ruleProbModel == null)
            throw new UnsupportedOperationException("No rule probability model provided.");
        int n = word.size();
        fillTable(word);
        return logProb[n][1][integerNonTerminalMap.get(grammar.getStartSymbol())];
    }


    private void fillTableIgnoreProbs(final List<Terminal<T>> word) {
        fillTable(word);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void fillTable(final List<Terminal<T>> word) {
        int n = word.size();

        int noOfNonTerminals = grammar.getNonTerminals().size();
		/*we create a 3-d boolean array of dimension nxnxr, n is the length of
		the word and r is the number of nonterminals and the index of
		r corresponds to the map in integerTerminalMap
		 */
        booleanArray = new boolean[n + 1][n + 1][noOfNonTerminals + 1];//
        logProb = new double[n + 1][n + 1][noOfNonTerminals + 1];//
        backRule = new Rule[n + 1][n + 1][noOfNonTerminals + 1];
        backSplit = new int[n + 1][n + 1][noOfNonTerminals + 1];
        //initialise back and booleanArray
        for (int i = 1; i <= n; i++) {//we ignore indexes with 0
            for (int j = 1; j <= n; j++) {
                for (int k = 1; k < noOfNonTerminals + 1; k++) {
                    booleanArray[i][j][k] = false;
                    backRule[i][j][k] = null;
                    backSplit[i][j][k] = INVALID_SPLIT;
                    logProb[i][j][k] = Double.NEGATIVE_INFINITY; // log(0)
                }
            }
        }

        //fills out the table for substrings of length 1
        for (int s = 1; s < n + 1; s++) {
            for (Rule rule : type2Rules) {
                if (rule.right[0].equals(word.get(s - 1))) {
                    int nt = integerNonTerminalMap.get(rule.left);
                    booleanArray[1][s][nt] = true;
                    // here we assume that there are no two identical rules A->a
                    logProb[1][s][nt] = ruleProbModel.getIntervalFor(rule).getLnLength();
                    backRule[1][s][nt] = rule;
                    backSplit[1][s][nt] = INVALID_SPLIT; // A->a
                }
            }
        }

        for (int l = 1; l <= n; l++) {//l is the span
            for (int s = 1; s <= n - l + 1; s++) {//s is the start of span
                for (int p = 1; p < l; p++) {//p is the partitions of the span
                    /*
                     * type1Rules
                     */
                    // Replaced by cached indices for performance
//                    for (Rule rule : type1Rules) {
//                        int a = integerNonTerminalMap.get(rule.left);
//                        int b = integerNonTerminalMap.get(rule.right[0]);
//                        int c = integerNonTerminalMap.get(rule.right[1]);
                    for (Type1RuleIndices ruleIndices : type1RuleIndices) {
                        int a = ruleIndices.lhs;
                        int b = ruleIndices.rhs1;
                        int c = ruleIndices.rhs2;
                        Rule rule = ruleIndices.rule;
                        if (booleanArray[p][s][b] && booleanArray[l - p][s + p][c]) {
                            //System.out.println("Entered the innermost loop");
                            booleanArray[l][s][a] = true;
                            double prob = ruleProbModel.getIntervalFor(rule).getLnLength()
                                    + logProb[p][s][b] + logProb[l - p][s + p][c];
                            if (prob > logProb[l][s][a]) {
                                backRule[l][s][a] = rule;
                                backSplit[l][s][a] = p;
                                logProb[l][s][a] = prob;
                            }

                        }
                    }
                }//close partitions

                /*
                 * type3Rules Ai->(Aj)
                 */
                if (l >= 2) {
                    for (Rule rule : type3Rules) {
                        int a = integerNonTerminalMap.get(rule.left);
                        int b = integerNonTerminalMap.get(rule.right[1]);// only one non-terminal is found on the right of type 3 rules
                        if (booleanArray[l - 2][s + 1][b]
                                && rule.right[0].equals(word.get(s - 1)) // checks for opening and closing parenthesis
                                && rule.right[2].equals(word.get(s + l - 2))) {
                            booleanArray[l][s][a] = true;                                                                                     // the substring between the parenthesis
                            //there's only one partition for type3 rules so p=1
                            int p = 1;
                            double prob = ruleProbModel.getIntervalFor(rule).getLnLength()
                                    + logProb[l - 2][s + 1][b];
                            if (prob > logProb[l][s][a]) {
                                backRule[l][s][a] = rule;
                                backSplit[l][s][a] = p;
                                logProb[l][s][a] = prob;
                            }

                        }
                    }
                }

                /*
                 * type4Rules
                 */
                for (Rule rule : type4Rules) {
                    int a = integerNonTerminalMap.get(rule.left);
                    int b = integerNonTerminalMap.get(rule.right[0]);//only one non-terminal is found on the right of type 4 rules
                    if (booleanArray[l][s][b]) {//for rules Ai -> Aj checks if j<i
                        booleanArray[l][s][a] = true;
                        //there's only one partition for type3 rules so p=1
                        int p=1;
                        double prob = ruleProbModel.getIntervalFor(rule).getLnLength()
                                + logProb[l][s][b] ;
                        if (prob > logProb[l][s][a]) {
                            backRule[l][s][a] = rule;
                            backSplit[l][s][a] = p;
                            logProb[l][s][a] = prob;
                        }

                    }
                }


            }//close span start
        }//close span

    }

    private void computeNonterminalToIndicesMap() {
        //we need to create a map from Non-terminal to integer indexes 1,2,...
        integerNonTerminalMap = new HashMap<>();
        nonTerminalIntegerMap = new HashMap<>();
        int index = 2;
        //map each nt to an index
        integerNonTerminalMap.put(grammar.getStartSymbol(), 1);//start symbol is placed first in the map
        nonTerminalIntegerMap.put(1, grammar.getStartSymbol());
        for (NonTerminal nt : grammar.getNonTerminals()) {
            //noinspection StatementWithEmptyBody
            if (nt.equals(grammar.startSymbol)) {
                // skip
            } else {
                integerNonTerminalMap.put(nt, index);
                nonTerminalIntegerMap.put(index, nt);
                index++;
            }
        }
    }

    public void displayBackTracingTable(Rule[][][] backtrace) {
        System.out.println("The contents of the CYK-backtracing table are: ");

        for (int i = 1; i < backtrace.length; i++) {
            for (int j = 1; j < backtrace[0].length; j++) {
                for (int k = 1; k < backtrace[0][0].length; k++) {
                    if (backtrace[i][j][k] != null) {
                        //printing the rule
                        System.out.print(backtrace[i][j][k] + "\t\n");
                    }

                }
                System.out.println();
            }
            //System.out.println("\n\n");
        }
    }

    public void displayBooleanArray() {
        System.out.println("The contents of the boolean-array is: ");
        for (int i = 1; i < booleanArray.length; i++) {
            for (int j = 1; j < booleanArray[0].length; j++) {
                for (int k = 1; k < booleanArray[0][0].length; k++) {

                    System.out.print(i+"-"+j+"-"+k+"-"+ booleanArray[i][j][k] + "\t");
                }
                System.out.println();
            }
            System.out.println("\n\n");
        }
    }


    private static final class Type1RuleIndices {

        public Type1RuleIndices(final int lhs, final int rhs1, final int rhs2, final Rule rule) {
            this.lhs = lhs;
            this.rhs1 = rhs1;
            this.rhs2 = rhs2;
            this.rule = rule;
        }

        final int lhs;
        final int rhs1;
        final int rhs2;
        final Rule rule;

    }

}



