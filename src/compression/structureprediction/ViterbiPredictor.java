package compression.structureprediction;

import compression.Compressions;
import compression.data.FolderBasedDataset;
import compression.parser.CYKParser;
import compression.parser.Parser;
import compression.samplegrammars.SampleGrammar;
import compression.data.TrainingDataset;
import compression.grammar.IgnoringSecondPartPairOfChar;
import compression.grammar.RNAWithStructure;
import compression.util.AllGrammars;
import compression.util.CSVFile;
import compression.grammar.Terminal;
import compression.grammar.Grammar;
import compression.grammar.Rule;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 *
 * DO NOT USE, UNFINISHED.
 *
 */
public class ViterbiPredictor {


	public static void main(String[] args) throws Exception{
		Map<String, SampleGrammar> ALL_GRAMMARS;
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

		ALL_GRAMMARS = AllGrammars.allGrammars(withNonCanonicalRules);
		System.out.println("GOT HERE!!");

		List<SampleGrammar> listOfGrammars = new ArrayList<>();
		System.out.println(Arrays.toString(args));

		System.out.println("GOT HERE!!");

		if ("ALL".equalsIgnoreCase(args[3])) {
			listOfGrammars.addAll(ALL_GRAMMARS.values());
		} else {
			for (int i = 3; i < args.length; i++) {
				final String arg = args[i];
				if (ALL_GRAMMARS.containsKey(arg)) {
					listOfGrammars.add(ALL_GRAMMARS.get(arg));
				} else {
					System.err.println("Don't know grammar " + arg);
				}
			}
		}
		System.out.println("GOT HERE!!");

		if (listOfGrammars.size() == 0) {
			System.out.println("No valid grammars given");
			System.exit(97);
		}


		System.out.println("dataset = " + dataset);
		System.out.println("trainingDataset = " + trainingDataset);
		System.out.println("listOfGrammars = " + listOfGrammars.stream().map(G -> G.getName()).collect(Collectors.joining(", ")));
		System.out.println("withNonCanonicalRules = " + withNonCanonicalRules);


		List<String> grammarHeader = new ArrayList<>();
		//List<String> modelsHeader = new ArrayList<>();
		grammarHeader.add("File Name");

		for (SampleGrammar grammar : listOfGrammars) {
			grammarHeader.add(grammar.getName()+" No Of Pairs in Real Structure");
			grammarHeader.add(grammar.getName()+" No Of Pairs in Predicted Structure");
			grammarHeader.add(grammar.getName()+" false Positive");
			grammarHeader.add(grammar.getName()+" false Negative");

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

			List<Terminal<IgnoringSecondPartPairOfChar>> terminals = PredictionWriter.convertTerminals(RNAWS.asTerminals());

			for (SampleGrammar G : listOfGrammars) {
//				System.gc();
				//System.out.println("String Before Encoding");
				//System.out.println(RNAWS);
				//compression using static prob model
				Map<Rule, Double> ruleProbs = G.readRuleProbs(trainingDataset.ruleProbsFileFor(G));
				//System.out.println("Assign probabilities is: "+ G.getAssignProbs());

				Grammar<IgnoringSecondPartPairOfChar> predictionGrammar = PredictionWriter.convertSampleGrammar(G);
				Parser<IgnoringSecondPartPairOfChar> parser = new CYKParser<>(predictionGrammar);
//				System.out.println("predictionGrammar = " + predictionGrammar);
				//System.out.println("tokens = " + tokens+ "start ="+ startSymbol);
				List<Rule> der = parser.leftmostDerivationFor( terminals);
				//TODO fix in PredictionWriter

				String predictedSecondaryStructure = "";//TODO complete the implementation

				CreateRNAFile RF= new CreateRNAFile("C:\\Users\\USER\\Documents\\GitHub\\clone\\clone\\datasets",RNAWS.name);
				RF.writeToFile(RNAWS.primaryStructure,predictedSecondaryStructure);

				PPVNSensitivity ppvnSensitivity= new PPVNSensitivity(RNAWS, predictedSecondaryStructure);
				cells.add(ppvnSensitivity.getNumberOfPairsReal()+"");
				cells.add(ppvnSensitivity.getNumberOfPairsPredicted()+"");
				cells.add(ppvnSensitivity.getFalsePositive()+"");
				cells.add(ppvnSensitivity.getFalseNegative()+"");
				System.out.println("RNA.primaryStructure        = " + RNAWS.primaryStructure);
				System.out.println("predictedSecondaryStructure = " + predictedSecondaryStructure);
				System.out.println("RNA.secondaryStructure      = " + RNAWS.secondaryStructure);

				System.out.println("number of Pairs in Real Structure      = " + ppvnSensitivity.getNumberOfPairsReal());
				System.out.println("number of Pairs in predicted Structure      = " + ppvnSensitivity.getNumberOfPairsPredicted());
				System.out.println("false Positve      = " + ppvnSensitivity.getFalsePositive());
				System.out.println("false negative      = " + ppvnSensitivity.getFalseNegative());
				System.out.println("common pairs " + ppvnSensitivity.returnCommonPairs());

				System.out.println("LENGTH OF CELLS IS: "+ cells.size());
			}

			System.out.println("LENGTH OF CELLS IS: "+ cells.size());
			out.appendRow(cells);
			System.out.println();
		}

	}
}
