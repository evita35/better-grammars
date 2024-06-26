package compression;


import compression.data.CachedDataset;
import compression.data.Dataset;
import compression.data.FolderBasedDataset;
import compression.data.TrainingDataset;
import compression.grammar.*;
import compression.samplegrammars.SampleGrammar;
import compression.util.AllGrammars;
import compression.util.CSVFile;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Main class for compression experiments with built-in expert grammars.
 */
public class CompressionBuiltinGrammars {


    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd-HH-mm-ss";
    public static final DateTimeFormatter DATE_TIME_FOMATTER
            = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {


        System.out.println(Arrays.toString(args));
        if (args.length < 4) {
            System.out.println("CompressionsBuiltinGrammars <dataset> <grammars> <with-NCR> <rule-probability-model> [<training-dataset>] ");
            System.out.println("\t where <dataset> is the name of a subfolder in datasets ");
            System.out.println("\t where <grammars> is a comma-separated list of built-in grammar names");
            System.out.println("\t where <with-NCR> [true|false] whether or not to include rules for noncanonical base pairs ");
            System.out.println("\t where <rule-probability-model> is either ADAPTIVE , SEMI-ADAPTIVE or STATIC");
            System.out.println("\t where <training-dataset> is the name for the rule probabilities for the static model ");

            System.exit(98);
        }

        Dataset dataset = new CachedDataset(new FolderBasedDataset(args[0]));
        boolean withNonCanonicalRules = Boolean.parseBoolean(args[2]);
        List<SampleGrammar> listOfGrammars =
                AllGrammars.getGrammarsFromCmdLine(args[1], withNonCanonicalRules);
        String grammarNames = args[3];
        RuleProbType model = RuleProbType.fromString(grammarNames);
        TrainingDataset trainingDataset = null;
        if (model == RuleProbType.STATIC || model == RuleProbType.STATIC_FROM_FILE) {
            if (args.length < 5) {
                System.out.println("CompressionsBuiltinGrammars <dataset> <grammars> <with-NCR> <rule-probability-model> <training-dataset>");
                System.out.println("\t where <training-dataset> is the name for the rule probabilities for the static model ");
                System.exit(99);
            }
            trainingDataset = new TrainingDataset(args[4]);
        }

        System.out.println("dataset = " + dataset);
        System.out.println("listOfGrammars = "+ listOfGrammars);
        System.out.println("withNonCanonicalRules = " + withNonCanonicalRules);
        System.out.println("model = "+ model);
        System.out.println("trainingDataset = " + trainingDataset);

        // Construct CSV headers
        List<String> csvHeader = new ArrayList<>();//column headers are the RNA names
        csvHeader.add("Grammars");

        List<String> firstRow=new ArrayList<>();
        firstRow.add("nBases");
        for(RNAWithStructure rnaWithStructure: dataset) {
            csvHeader.add(rnaWithStructure.name);
            firstRow.add(String.format("%d", rnaWithStructure.getNumberOfBases()) + "");
        }


        String filename = "compression-ratios-"
                + dataset.getName()
                + "-grammars-" + grammarNames
                + "-withNCR-" + withNonCanonicalRules
                + "-model-" + model
                + (trainingDataset != null ? "-training-data-" + trainingDataset.getName() : "")
                + "-" + DATE_TIME_FOMATTER.format(LocalDateTime.now());
        CSVFile out = new CSVFile(new File(filename + ".csv"), csvHeader);
        // Print all parameters to a file
        PrintWriter paramsOut = new PrintWriter(filename + ".txt");
        // Print actual call
        paramsOut.println(CompressionBuiltinGrammars.class.getName() + " " + String.join(" ", args));
        paramsOut.println();
        paramsOut.println("dataset = " + dataset);
        paramsOut.println("trainingDataset = " + trainingDataset);
        paramsOut.println("withNonCanonicalRules = " + withNonCanonicalRules);
        paramsOut.println("grammars = " + listOfGrammars);
        paramsOut.println("model= "+model);
        paramsOut.close();


        out.appendRow(firstRow);

        for (SampleGrammar sg : listOfGrammars) {
            RNAGrammar rnaGrammar = sg.getGrammar();
            Map<RNAWithStructure, Integer> encodedLengths = Compressions.getEncodedLengthsParallel(
                    dataset, rnaGrammar, model, trainingDataset);

            List<String> cells = new ArrayList<>();
            cells.add(sg.getName()); //name of grammar file
            for (RNAWithStructure rnaWithStructure : dataset) {
                cells.add(encodedLengths.get(rnaWithStructure) + "");
            }
            out.appendRow(cells);
            System.out.println("Grammar " + sg.getName() + " done");
            System.out.println("bit per base " + Compressions.getBitsPerBase(encodedLengths));
            System.out.println();
        }
        out.close();
    }

}
