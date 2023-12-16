package compression;

import compression.data.TrainingDataset;
import compression.grammar.GrammarFolder;
import compression.grammar.RNAGrammar;
import compression.grammar.SecondaryStructureGrammar;
import compression.samplegrammars.SampleGrammar;
import compression.util.AllGrammars;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Main class for computing rule frequencies.
 */
public class Training {

	public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd-HH-mm-ss";
	public static final DateTimeFormatter DATE_TIME_FOMATTER
			= DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(Arrays.toString(args));
		// TODO properly document API
		if (args.length < 4) {
			System.out.println("Training <training-dataset> <with-NCR> <grammar-folder> ...");
			System.out.println("\t where <training-dataset> is the name for the rule probabilities for the static model ");
			System.out.println("\t where <with-NCR> [true|false] whether or not to include rules for noncanonical base pairs ");
			System.out.println("\t where <grammar-folder> grammars folder ");
			System.out.println("\t " + AllGrammars.allGrammarNames());
			System.exit(98);
		}

		TrainingDataset trainingDataset = new TrainingDataset(args[0]);
		boolean withNonCanonicalRules = Boolean.parseBoolean(args[1]);
		boolean useGrammarFolder=Boolean.parseBoolean(args[2]);
		GrammarFolder grammarFolder=null;
		if(useGrammarFolder)
		{
			grammarFolder= new GrammarFolder(args[3]);
			for (SecondaryStructureGrammar ssg : grammarFolder) {
				RNAGrammar G = RNAGrammar.from(ssg, withNonCanonicalRules);
				System.out.println(G.name);
				System.out.println("Grammar is: " + G.getAllRules());
				try {
					G.writeRuleCounts(
							trainingDataset.getRuleCountForAutoGenGrammar(G),
							G.computeRuleCounts(trainingDataset)
					);
				} catch (RuntimeException r) {
					System.out.println("parsing issue with " + G.name);
					continue;
				}
				System.out.println("Displayed SUCCESSFULLY");
			}
		}
		else
		{
			List<SampleGrammar> listOfGrammars =
					AllGrammars.getGrammarsFromCmdLine(args[3], withNonCanonicalRules);
			for (SampleGrammar sg : listOfGrammars) {

				RNAGrammar RFG = sg.getGrammar();
				System.out.println("RFG = " + RFG);
				RNAGrammar rnaGrammar = RFG.convertToSRF();
				System.out.println("rnaGrammar = " + rnaGrammar);
				try {
					rnaGrammar.writeRuleCounts(
							trainingDataset.getRuleCountForAutoGenGrammar(rnaGrammar),
							rnaGrammar.computeRuleCounts(trainingDataset)
					);
				} catch (RuntimeException r) {
					System.out.println("parsing issue with " + rnaGrammar.name);
					continue;
				}
				System.out.println("Displayed SUCCESSFULLY");
			}
		}

		System.out.println("trainingDataset = " + trainingDataset);
		System.out.println("withNonCanonicalRules = " + withNonCanonicalRules);
		System.out.println("grammarFolder = " + grammarFolder);



	}
}
