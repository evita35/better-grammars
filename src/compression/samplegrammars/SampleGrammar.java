package compression.samplegrammars;

import compression.data.Dataset;
import compression.grammar.*;
import compression.samplegrammars.model.RuleProbModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public interface SampleGrammar {

    String getName();

    boolean isWithNoncanonicalRules();

    NonTerminal getStartSymbol();

    RNAGrammar getGrammar();

    default List<Rule> getGrammarRules() {
        return new ArrayList<>(getGrammar().getAllRules());
    }



    default Map<Rule, Long> computeRuleCounts(Dataset dataset) throws IOException {
	    return new RuleCountsForGrammarLaPlace(this.getGrammar(), dataset).ruleCounts();

    }

    default Map<Rule,Double> computeRulesToProbs(Map<Rule,Long> ruleCounts) {
        Map<Rule, Double> rulesToProbs = RuleProbModel.computeRuleProbs(this.getGrammar(), ruleCounts);

        List<NonTerminal> nonterminals = ruleCounts.keySet().stream().map(Rule::getLeft).collect(Collectors.toList());
        if (!SampleGrammar.checkRulesToProbs(rulesToProbs, nonterminals)) {
            throw new IllegalArgumentException("Probabilities do not sum up to 1");
        }

        return rulesToProbs;
    }

    public static boolean checkRulesToProbs(Map<Rule, Double> r2P) {
        List<NonTerminal> nonterminals = r2P.keySet().stream().map(rule -> rule.getLeft()).collect(Collectors.toList());
        return checkRulesToProbs(r2P, nonterminals);
    }

    public static boolean checkRulesToProbs(Map<Rule, Double> r2P, List<NonTerminal> NT) {
        Map<NonTerminal, Double> sumOfProbabilities = new HashMap<>();

        for (NonTerminal nt : NT)
            sumOfProbabilities.put(nt, 0.0);

        for (Rule rule : r2P.keySet()) {//obtains the sum of probabilities for rules with same LHS
            sumOfProbabilities.replace(rule.left, r2P.get(rule) + sumOfProbabilities.get(rule.left));
        }
//        System.out.println(" SUM OF PROBABILITIES IS: " + sumOfProbabilities);
        for (NonTerminal nt : NT) {
            double sum = sumOfProbabilities.get(nt);
            if (sum < 0.999 || sum > 1.001) {
                System.out.println("SUM OF PROBABILITIES IS: " + Math.round(sum));
                return false;
            }
        }
        return true;
    }

    private static Map.Entry<Rule, Double> parseRuleProbLine(String line) {
        // TODO get rid of nonterminals in CategoryMaps
        CategoryMaps CM = new CategoryMaps();
        NonTerminal nt = CM.stringNonTerminalMap.get(line.substring(0, line.indexOf('\u2192') - 1));
        // obtain the index position after the right arrow character
        int i = line.indexOf('\u2192') + 1;
        ArrayList<Category> rhs= new ArrayList<>();

        double probForRule = 0.0;
        double probForAssign = 0.0;


        while(i<line.length()){
            if(line.charAt(i)=='(')//to read terminal symbols e.g. <A\(>
            {
                probForRule=Double.parseDouble( line.substring(i+1, line.lastIndexOf(')')));
                i=line.lastIndexOf(')');
            }
            else if(line.charAt(i)=='<')
            {
                rhs.add(CM.stringCategoryMap.get(line.substring(i,i+5)));

                i+=5;
            }
            else if(line.charAt(i)==':'){
                probForAssign=Double.parseDouble(line.substring(i+1));
                i=line.length();
                break;
            }
            else if(line.charAt(i)==' ' || line.charAt(i)==')'){
                i++;
            }
            else
            {
                int r=0;


                while(line.charAt(i+r)!=' '){
                    r++;
                    if(i+r>=line.length()||line.charAt(i + r)==':')
                        break;
                }//used to obtain the length of the non-terminal symbol
                rhs.add(CM.stringNonTerminalMap.get(line.substring(i,i+r)));//
                i=i+r;

            }
        }
        Category[] catArray = rhs.toArray(new Category[0]);
        return Map.entry (Rule.create(nt, catArray), probForAssign);

    }


    default public void writeRuleCounts(File file, Map<Rule, Long> ruleCounts ) throws IOException {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            TreeSet<Map.Entry<Rule, Long>> entries = new TreeSet<>(
                    Comparator.comparing(o -> o.getKey().toString()));
            entries.addAll(ruleCounts.entrySet());
            for (Map.Entry<Rule, Long> entry : entries) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }
            bf.flush();
        }
    }

    default public void writeRuleProbs(File file, Map<Rule,Double> assignProbs) throws IOException {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            TreeSet<Map.Entry<Rule, Double>> entries = new TreeSet<>(
                    Comparator.comparing(o -> o.getKey().toString()));
            entries.addAll(assignProbs.entrySet());
            for (Map.Entry<Rule, Double> entry : entries) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }
            bf.flush();
        }
    }



    /**
     * Read rule counts from the text file
     * @param file
     * @return map of rules to counts
     * @throws IOException
     */
    default public Map<Rule,Long> readRuleCounts(File file) throws IOException {//
        Map<Rule, Long> ruleCounts = new HashMap<>();
        try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(new FileInputStream(file), "UTF-8"));){
            String line;
            while ((line = in.readLine()) != null) {
                Map.Entry<Rule, Long> entry = parseRuleCountLine(line);
                ruleCounts.put(entry.getKey(), entry.getValue());
            }
            return ruleCounts;
        }
    }

    private static Map.Entry<Rule, Long> parseRuleCountLine(String line) {
        CategoryMaps CM = new CategoryMaps();
        NonTerminal nt = CM.stringNonTerminalMap.get(line.substring(0, line.indexOf('\u2192') - 1));
        int i = line.indexOf('\u2192') + 1;//obtains the index position after the right arrow character
        ArrayList<Category> rhs = new ArrayList<>();

        long frequencyForCounts = 0L;

        while (i < line.length()) {
            if (line.charAt(i) == '(')//to read terminal symbols e.g. <A\(>
            {
                // old probForRule deleted
                i = line.lastIndexOf(')');
            } else if (line.charAt(i) == '<') {
                rhs.add(CM.stringCategoryMap.get(line.substring(i, i + 5)));
//                System.out.println("category map value is "+CM.stringCategoryMap.get(line.substring(i,i+5)));
                i += 5;
            } else if (line.charAt(i) == ':') {
                frequencyForCounts = Long.parseLong(line.substring(i + 1));
                i = line.length();
                break;
            } else if (line.charAt(i) == ' ' || line.charAt(i) == ')') {
                i++;
            } else {
                int r = 0;
                while (line.charAt(i + r) != ' ') {
//                        System.out.println(line.charAt(i + r));
                    r++;
                    if (i + r >= line.length() || line.charAt(i + r) == ':')
                        break;
                }//used to obtain the length of the non-terminal symbol
                rhs.add(CM.stringNonTerminalMap.get(line.substring(i, i + r)));//
                i = i + r;
            }
        }
        Category[] catArray = rhs.toArray(new Category[0]);
        return Map.entry(Rule.create(nt, catArray), frequencyForCounts);

    }


    /**
     * Reads the rule probabilities from a file.
     *
     * @param file
     * @return a map from rules to their probabilities
     * @throws IOException
     */
    default public Map<Rule, Double> readRuleProbs(File file) throws IOException {
        Map<Rule, Double> ruleProbs = new HashMap<>();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

        String line;
        while ((line = in.readLine()) != null) {
            Map.Entry<Rule, Double> entry = parseRuleProbLine(line);
            ruleProbs.put(entry.getKey(), entry.getValue());
        }
        in.close();
        if (!checkRulesToProbs(ruleProbs)) {
            throw new IllegalArgumentException("Probabilities are not correctly distributed");
        }
        return ruleProbs;
    }


}
