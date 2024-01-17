package compression.structureprediction;

import compression.Compressions;
import compression.LocalConfig;
import compression.data.FolderBasedDataset;
import compression.data.TrainingDataset;
import compression.grammar.*;
import compression.parser.CYKParser;
import compression.parser.StochasticParser;
import compression.samplegrammars.SampleGrammar;
import compression.util.AllGrammars;
import compression.util.CSVFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 *
 *
 * DO NOT USE, UNFINISHED.
 *
 */
public class PredictionWriter {

    public static Grammar<IgnoringSecondPartPairOfChar> convertSampleGrammar(SampleGrammar sampleGrammar) {
        // Use same nonterminals, but map terminals
        Grammar<PairOfChar> G = sampleGrammar.getGrammar();
        Map<Terminal<PairOfChar>, Terminal<IgnoringSecondPartPairOfChar>> terminalMap = new HashMap<>();
        for (Terminal<PairOfChar> terminal : G.terminals) {
            PairOfCharTerminal t = (PairOfCharTerminal) terminal;
            terminalMap.put(terminal, new IgnoringSecondPartPairOfChar(t.getChars().getPry(), t.getChars().getSec()).asTerminal());
        }
        Grammar.Builder<IgnoringSecondPartPairOfChar> builder = new Grammar.Builder<>(G.name, G.getStartSymbol());
        for (Rule rule : G.getAllRules()) {
            builder.addRule(rule.left,
                    Arrays.stream(rule.right)
                            .map(category -> {
                                if (Category.isTerminal(category)) {
                                    //noinspection unchecked
                                    return terminalMap.get((Terminal<PairOfChar>) category);
                                } else {
                                    return category;
                                }
                            }).toArray(Category[]::new));
        }
        return builder.build();
    }

    public static List<Terminal<IgnoringSecondPartPairOfChar>> convertTerminals(
            List<Terminal<PairOfChar>> terminals) {
        return terminals.stream().map(
                        pairOfCharToken ->
                                new IgnoringSecondPartPairOfChar(
                                        pairOfCharToken.getChars().getPry(),
                                        pairOfCharToken.getChars().getSec()
                                ).asTerminal())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws Exception {

        System.out.println(Arrays.toString(args));
        if (args.length < 4) {
            System.out.println("ViterbiPredictor <dataset> <training-dataset> <with-NCR> <grammar1> <grammar2> ...");
            System.out.println("\t where <dataset> is the name of a subfolder in datasets ");
            System.out.println("\t where <training-dataset> is the name for the rule probabilities for the static model ");
            System.out.println("\t where <with-NCR> [true|false] whether or not to include rules for noncanonical base pairs ");
            System.out.println("\t where <grammar1>, ... is a list of grammars from " + AllGrammars.allGrammarNames());
            System.exit(98);
        }

        FolderBasedDataset dataset = new FolderBasedDataset(args[0]);
        TrainingDataset trainingDataset = new TrainingDataset(args[1]);
        boolean withNonCanonicalRules = Boolean.parseBoolean(args[2]);
        List<SampleGrammar> listOfGrammars = AllGrammars.getGrammarsFromCmdLine(args[3], withNonCanonicalRules);

        System.out.println("dataset = " + dataset);
        System.out.println("trainingDataset = " + trainingDataset);
        System.out.println("listOfGrammars = " + listOfGrammars.stream().map(G -> G.getName()).collect(Collectors.joining(", ")));
        System.out.println("withNonCanonicalRules = " + withNonCanonicalRules);


        List<String> grammarHeader = new ArrayList<>();
        //List<String> modelsHeader = new ArrayList<>();
        grammarHeader.add("File Name");

        for (SampleGrammar grammar : listOfGrammars) {
            grammarHeader.add(grammar.getName() + " No Of Pairs in Real Structure");
            grammarHeader.add(grammar.getName() + " No Of Pairs in Predicted Structure");
            grammarHeader.add(grammar.getName() + " false Positive");
            grammarHeader.add(grammar.getName() + " false Negative");

            //modelsHeader.add("STATIC MODEL");
            //modelsHeader.add("ADAPTIVE MODEL");
        }

        CSVFile out = new CSVFile(
                new File("ppv-sensitivity-"
                        + dataset.name
                        + "-training-data-" + trainingDataset.name
                        + "-withNCR-" + withNonCanonicalRules
                        + "-" + Compressions.DATE_TIME_FOMATTER.format(LocalDateTime.now()) + ".csv"),
                grammarHeader);


        for (RNAWithStructure RNAWS : dataset) {


            List<String> cells = new ArrayList<>();
            System.out.println(RNAWS.name);
            cells.add(RNAWS.name);//name of file

            List<Terminal<IgnoringSecondPartPairOfChar>> terminals = convertTerminals(RNAWS.asTerminals());

            for (SampleGrammar G : listOfGrammars) {
//				System.gc();
                //System.out.println("String Before Encoding");
                //System.out.println(RNAWS);
                //compression using static prob model
                Map<Rule, Double> ruleProbs = G.readRuleProbs(trainingDataset.ruleProbsFileFor(G));
                //System.out.println("Assign probabilities is: "+ G.getAssignProbs());

                Grammar<IgnoringSecondPartPairOfChar> predictionGrammar = convertSampleGrammar(G);
                StochasticParser<IgnoringSecondPartPairOfChar> parser = new CYKParser<>(predictionGrammar);
                //System.out.println("terminals = " + terminals+ "start ="+ startSymbol);
                List<Rule> der = parser.mostLikelyLeftmostDerivationFor(terminals);

                final StringBuilder builder = new StringBuilder();
                for (Rule rule : der) {
                    // TODO need to get the terminals in the order of the final produced string
                    // which is no longer just the order in which they appear
                    // S -> (A) -> (.)
                    // S -> (A) , A -> .
                    // want (.)
                    // 1st rule: ()
                    // 2nd rule: .
                    // easiest option: produce the list of terminals from derivation,
                    // then read off secondary structure.
                    // append to string builder
                }

                String predictedSecondaryStructure = builder.toString();//

                CreateRNAFile RF = new CreateRNAFile(LocalConfig.GIT_ROOT + "/datasets/Earley-SecStrucPrediction-BenchMarkDataSet-rule-probs-dowell-mixed80-G6Bound-withNCR-true", RNAWS.name);
                RF.writeToFile(RNAWS.primaryStructure, predictedSecondaryStructure);


            }

        }
    }
}
