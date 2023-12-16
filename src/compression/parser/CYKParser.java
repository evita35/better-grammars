package compression.parser;

import compression.coding.BigDecimalInterval;
import compression.coding.Interval;
import compression.grammar.*;
import compression.samplegrammars.model.RuleProbModel;

import java.util.*;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 *
 * TODO: properly throw UnparsableExceptions
 */
public class CYKParser<T> implements StochasticParser<T> {

    private final Grammar<T> grammar;

    private final RuleProbModel ruleProbModel;

    Map<NonTerminal, Integer> integerNonTerminalMap;
    Map<Integer, NonTerminal> nonTerminalIntegerMap;

    List<Rule> ruleList;
    List<Rule> correctlyOrderedRuleList;
    boolean[][][] booleanArray;
    double[][][] logProb;
    Rule[][][] backRule;
    int[][][] backSplit;

    public static final int INVALID_SPLIT = -1;
    private List<Rule> singleTerminalRules;
    private List<Rule> nonTerminalRules;

    public CYKParser(final Grammar<T> grammar) {
        this(grammar, null);
    }

    public CYKParser(final Grammar<T> grammar, final RuleProbModel ruleProbModel) {
        this.grammar = grammar;

        this.ruleProbModel = ruleProbModel != null ? ruleProbModel : new RuleProbModel() {
            @Override
            public Interval getIntervalFor(final Rule rule) {
                return new BigDecimalInterval(0, 1);
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
        Collection<Rule> grammarRules = grammar.getAllRules();
        singleTerminalRules = new ArrayList<>();
        nonTerminalRules = new ArrayList<>();

        for (Rule rule : grammarRules) {
            if (rule.right.length == 1) {
                if (Category.isTerminal(rule.right[0])) {
                    singleTerminalRules.add(rule);
                } else
                    throw new IllegalArgumentException("Error found in rule: " + rule + " rule is not in CNF");

            } else if (rule.right.length == 2) {
                if (!Category.isTerminal(rule.right[0]) && !Category.isTerminal(rule.right[1])) {
                    nonTerminalRules.add(rule);
                } else
                    throw new IllegalArgumentException("Error found in rule: " + rule + " rule is not in CNF");

            } else
                throw new IllegalArgumentException("Error found in rule: " + rule + " rule is not in CNF");

        }

        //TODO SPLIT RULES TO THOSE WITH SINGLE TERMINAL ON RHS
        //TODO SPLIT RULES TO THOSE WITH 2 NONTERMINALS ON RHS
        //TODO RAISE AN EXCEPTION FOR THOSE RULES WHICH DO NOT COMPLY

    }

    @Override
    public List<Rule> leftmostDerivationFor(final List<Terminal<T>> word) {
        return mostLikelyLeftmostDerivationFor(word);
    }

    @Override
    public List<Rule> mostLikelyLeftmostDerivationFor(final List<Terminal<T>> word) {
        if (ruleProbModel == null)
            throw new UnsupportedOperationException("No rule probability model provided.");
        int n = word.size();
        fillTable(word);
        System.out.println("Got here!!!!!!!!!!!!");
        List<Rule> derivation = new ArrayList<>();
        backtraceDerivation(n, 1, grammar.getStartSymbol(), derivation);
        return derivation;
    }

    public List<Terminal<T>> mostLikelyWord(final List<Terminal<T>> word) {
        if (ruleProbModel == null)
            throw new UnsupportedOperationException("No rule probability model provided.");
        int n = word.size();
        fillTable(word);
        List<Terminal<T>> backWord = new ArrayList<>();
        backtraceWord(n, 1, grammar.getStartSymbol(), backWord);
        return backWord;
    }

    public List<Terminal<T>> derivationToWord(List<Rule> derivation){
        List<Terminal<T>>completeWord= new ArrayList<>();

        for (Rule rule: derivation){
            if(rule.right.length==1 && Category.isTerminal(rule.right[0])){
                completeWord.add((Terminal<T>) rule.right[0]);
            }
        }
      return completeWord;
    }

    private void backtraceWord(int l, int s, final NonTerminal NT, final List<Terminal<T>> result) {
        int nt = integerNonTerminalMap.get(NT);
        int p = backSplit[l][s][nt];
        Rule rule = backRule[l][s][nt];
        if (rule.right.length == 2) { // A -> B C
            assert rule.left == NT;
            NonTerminal B = (NonTerminal) rule.right[0];
            NonTerminal C = (NonTerminal) rule.right[1];
            backtraceWord(p, s, B, result);
            backtraceWord(l - p, s + p, C, result);
        } else {
            // A -> a
            //noinspection unchecked
            result.add((Terminal<T>) rule.right[0]);
        }
    }


    private void backtraceDerivation(int l, int s, final NonTerminal NT, final List<Rule> result) {
        int nt = integerNonTerminalMap.get(NT);
        int p = backSplit[l][s][nt];
        Rule rule = backRule[l][s][nt];
        result.add(rule);
        if (rule.right.length == 2) { // A -> B C
            assert rule.left == NT;
            NonTerminal B = (NonTerminal) rule.right[0];
            NonTerminal C = (NonTerminal) rule.right[1];
            backtraceDerivation(p, s, B, result);
            backtraceDerivation(l - p, s + p, C, result);
        } else {
            // A -> a
            return;
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


    private void fillTable(final List<Terminal<T>> word) {
        int n = word.size();
        System.out.println("size of n is:" + n);
        //table = new Cell[n+1][n+1];//we start indices  are 1,1

        ruleList = new ArrayList<>();
        //we need to create a map from Non-terminal to integer indexes 1,2,...
        integerNonTerminalMap = new HashMap<>();
        nonTerminalIntegerMap = new HashMap<>();
        int index = 2;
        //map each nt to an index
        integerNonTerminalMap.put(grammar.getStartSymbol(), 1);//start symbol is placed first in the map
        nonTerminalIntegerMap.put(1, grammar.getStartSymbol());
        for (NonTerminal nt : grammar.getNonTerminals()) {
            if (nt.equals(grammar.startSymbol)) {
                //skip
            } else {
                integerNonTerminalMap.put(nt, index);
                nonTerminalIntegerMap.put(index, nt);
                index++;
            }
        }
        System.out.println(integerNonTerminalMap);

        int noOfNonTerminals = grammar.getNonTerminals().size();
		/*we create a 3-d boolean array of dimension nxnxr, n is the length of
		the word and r is the number of non terminals and the index of
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

        System.out.println(grammar.getAllRules());

        //fills out the boolean array for substrings of length 1
        for (int s = 1; s < n + 1; s++) {
            for (Rule rule : singleTerminalRules) {
                System.out.println(rule.getRight().length);
                if ( rule.right[0].equals(word.get(s - 1))) {
                    int nt = integerNonTerminalMap.get(rule.left);
                    booleanArray[1][s][nt] = true;
                    // here we assume that there are no two identical rules A->a
                    logProb[1][s][nt] = ruleProbModel.getIntervalFor(rule).getLnLength();
                    backRule[1][s][nt] = rule;
                    backSplit[1][s][nt] = INVALID_SPLIT; // A->a
                    ruleList.add(rule);
                    System.out.println(booleanArray[1][s][nt]);//debugging
                }
            }
        }

        for (int l = 2; l <= n; l++) {//l is the span
            for (int s = 1; s <= n - l + 1; s++) {//s is the start of span
                for (int p = 1; p < l; p++) {//p is the partitions of the span
                    for (Rule rule : nonTerminalRules) {
                        int a = integerNonTerminalMap.get(rule.left);
                        int b = integerNonTerminalMap.get(rule.right[0]);
                        int c = integerNonTerminalMap.get(rule.right[1]);
                        if (booleanArray[p][s][b] && booleanArray[l - p][s + p][c]) {
                            System.out.println("Entered the innermost loop");
                            booleanArray[l][s][a] = true;

//								 Vector<Integer> append = new Vector<>(Arrays.asList(new Integer[]{a,b,c}));
//								 System.out.println(append);
                            double prob = ruleProbModel.getIntervalFor(rule).getLnLength()
                                    + logProb[p][s][b] + logProb[l - p][s + p][c];
                            if (prob > logProb[l][s][a]) {
                                backRule[l][s][a] = rule;
                                backSplit[l][s][a] = p;
                                ruleList.add(rule);
                                logProb[l][s][a] = prob;
                            }

                        }
                    }
                }//close partitions
            }//close span start
        }//close span

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

    public void displayBooleanArray(boolean[][][] boolArray) {
        System.out.println("The contents of the boolean-array is: ");
        for (int i = 1; i < boolArray.length; i++) {
            for (int j = 1; j < boolArray[0].length; j++) {
                for (int k = 1; k < boolArray[0][0].length; k++) {

                    System.out.print(boolArray[i][j][k] + "\t");
                }
                System.out.println();
            }
            System.out.println("\n\n");
        }
    }


}



