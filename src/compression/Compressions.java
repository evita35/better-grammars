package compression;


import compression.coding.ArithmeticEncoder;
import compression.coding.BitSizeOnlyArithmeticEncoder;
import compression.data.CachedDataset;
import compression.data.Dataset;
import compression.data.FolderBasedDataset;
import compression.data.TrainingDataset;
import compression.grammar.*;
import compression.samplegrammars.model.AdaptiveRuleProbModel;
import compression.samplegrammars.model.RuleProbModel;
import compression.samplegrammars.model.SemiAdaptiveRuleProbModel;
import compression.samplegrammars.model.StaticRuleProbModel;
import compression.util.CSVFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Main class for compression experiments
 */
public class Compressions {


    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd-HH-mm-ss";
    public static final DateTimeFormatter DATE_TIME_FOMATTER
                      = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {


        System.out.println(Arrays.toString(args));
        if (args.length < 4) {
             // just to have names
            System.out.println("Compressions <dataset> <grammar-folder> <with-NCR> <rule-probability-model> [<training-dataset>] ");
            System.out.println("\t where <dataset> is the name of a subfolder in datasets ");
            System.out.println("\t where <grammar-folder> is the folder which contains all the grammars for compression");
            System.out.println("\t where <with-NCR> [true|false] whether or not to include rules for noncanonical base pairs ");
            System.out.println("\t where <rule-probability-model> is one of " + Arrays.toString(RuleProbType.values()));
            System.out.println("\t where <training-dataset> is the name for dataset for the static model ");
            System.exit(98);
        }



        Dataset dataset = new CachedDataset(new FolderBasedDataset(args[0]));
        GrammarFolder grammarFolder= new GrammarFolder(args[1]);
        boolean withNonCanonicalRules = Boolean.parseBoolean(args[2]);
        RuleProbType model = RuleProbType.fromString(args[3]);
        System.out.println("dataset = " + dataset);
        System.out.println("grammarFolder = "+ grammarFolder);
        System.out.println("withNonCanonicalRules = " + withNonCanonicalRules);
        System.out.println("model = "+ model);
        TrainingDataset trainingDataset = null;
        if (model == RuleProbType.STATIC || model == RuleProbType.STATIC_FROM_FILE) {
            if (args.length < 5) {
                System.out.println("Compressions <dataset> <grammar-folder> <with-NCR> <rule-probability-model> <training-dataset>");
                System.out.println("\t where <training-dataset> is the name for the rule probabilities for the static model ");
                System.exit(98);
            }
            trainingDataset = new TrainingDataset(args[4]);
            System.out.println("trainingDataset = " + trainingDataset);
        }


        // Construct CSV headers
        List<String> csvHeader = new ArrayList<>();//column headers are the RNA names
        csvHeader.add("Grammars");

        List<String> firstRow = new ArrayList<>();
        firstRow.add("nBases");
        for (RNAWithStructure rnaWithStructure : dataset) {
            csvHeader.add(rnaWithStructure.name);
            firstRow.add(String.format("%d", rnaWithStructure.getNumberOfBases()));
        }


        String filename = "compression-ratios-"
                + dataset.getName()
                + "-grammars-" + grammarFolder.folderName
                + "-withNCR-" + withNonCanonicalRules
                + "-model-" + model
                + (trainingDataset != null ? "-training-data-" + trainingDataset.getName() : "")
                + "-" + DATE_TIME_FOMATTER.format(LocalDateTime.now());
        CSVFile out = new CSVFile(new File(filename + ".csv"), csvHeader);
        // Print all parameters to a file
        PrintWriter paramsOut = new PrintWriter(filename + ".txt");
        // Print actual call
        paramsOut.println(Compressions.class.getName() + " " + String.join(" ", args));
        paramsOut.println();
        paramsOut.println("dataset = " + dataset);
        paramsOut.println("trainingDataset = " + trainingDataset);
        paramsOut.println("withNonCanonicalRules = " + withNonCanonicalRules);
        paramsOut.println("grammars = " + grammarFolder.folderName);
        paramsOut.println("model = "+model);
        paramsOut.close();



       out.appendRow(firstRow);

        for (SecondaryStructureGrammar ssg : grammarFolder) {
            RNAGrammar G = RNAGrammar.from(ssg, withNonCanonicalRules);
            Map<RNAWithStructure, Integer> encodedLengths = getEncodedLengthsParallel(
                    dataset, G, model, trainingDataset);

            List<String> cells = new ArrayList<>();
            cells.add(ssg.name); //name of grammar file
            for (RNAWithStructure rnaWithStructure : dataset) {
                cells.add(encodedLengths.get(rnaWithStructure) + "");
            }
            out.appendRow(cells);
            System.out.println("Grammar " + ssg.name + " done");
            System.out.println("bit per base " + getBitsPerBase(encodedLengths));
            System.out.println();
        }
        out.close();
    }

    public static Map<RNAWithStructure, Integer> getEncodedLengthsParallel(
            final Dataset dataset, final RNAGrammar G, final RuleProbType model, final TrainingDataset trainingDataset) {
        Map<RNAWithStructure, Integer> encodedLengths = Collections.synchronizedMap(
                new HashMap<>(dataset.getSize() * 3 / 2));

        // For static models, get rule probabilities once and for all up front
        final Map<Rule, Double> staticRuleProbs;
        try {
            switch (model) {
                case STATIC:
                    // compute rule counts over training dataset
                    Dataset cachedTrainingDataset = new CachedDataset(trainingDataset);
                    Map<Rule, Long> ruleCounts = G.computeRuleCounts(cachedTrainingDataset);
                    staticRuleProbs = Collections.unmodifiableMap(G.computeRulesToProbs(ruleCounts));
                    break;
                case STATIC_FROM_FILE:
                    // load rule probabilities from file
                    staticRuleProbs = Collections.unmodifiableMap(
                            G.readRuleProbs(trainingDataset.ruleProbsFileFor(G)));
                    break;
                default:
                    staticRuleProbs = null;
            }
        } catch (IOException e) {
            if (model == RuleProbType.STATIC_FROM_FILE) {
                System.err.println("Error reading rule probabilities from file.");
                System.err.println("Did you forget to generate them?");
            }
            throw new RuntimeException(e);
        }

        StreamSupport.stream(dataset.spliterator(), true).unordered()
                .forEach((rnaWithStructure) -> {
                    final ArithmeticEncoder arithmeticEncoder = new BitSizeOnlyArithmeticEncoder();
                    final RuleProbModel ruleProbModel;
                    switch (model) {
                        case STATIC:
                        case STATIC_FROM_FILE:
                            ruleProbModel = new StaticRuleProbModel(G.getGrammar(), staticRuleProbs);
                            break;
                        case SEMI_ADAPTIVE:
                            ruleProbModel = new SemiAdaptiveRuleProbModel(G, rnaWithStructure);
                            break;
                        case ADAPTIVE:
                            ruleProbModel = new AdaptiveRuleProbModel(G);
                            break;
                        default:
                            throw new AssertionError();
                    }
                    GenericRNAEncoderForPrecision encoder =
                            new GenericRNAEncoderForPrecision(
                                    ruleProbModel, arithmeticEncoder,
                                    G.getGrammar(), G.getStartSymbol());
                    int encodedLength = encoder.getPrecisionForRNACode(rnaWithStructure);
                    encodedLengths.put(rnaWithStructure, encodedLength);
                });
        return encodedLengths;
    }

    public static double getBitsPerBase(Map<RNAWithStructure, Integer> encodedLengths) {
   		Stream<Double> bitsPerBase = encodedLengths.entrySet().parallelStream().map(e -> {
   			RNAWithStructure rna = e.getKey();
   			int encodedLength = e.getValue();
   			int rnaLength = rna.getNumberOfBases();
   			return ((double) encodedLength) / rnaLength;
   		});
   		return bitsPerBase.unordered().collect(Collectors.averagingDouble(d -> d));
   	}


}
