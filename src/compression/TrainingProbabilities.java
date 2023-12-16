package compression;

import compression.data.TrainingDataset;
import compression.grammar.GrammarFolder;
import compression.grammar.RNAGrammar;
import compression.grammar.SecondaryStructureGrammar;
import compression.samplegrammars.SampleGrammar;
import compression.util.AllGrammars;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Main class for computing rule probabilities from rule counts.
 */
public class TrainingProbabilities {

	public static void main(String[] args) throws Exception {

		// TODO properly document API
		System.out.println(Arrays.toString(args));
		if (args.length < 4) {
			System.out.println("TrainingProbabilities <training-data> <with-NCR> <grammar-folder> ...");
			System.out.println("\t where <training-dataset> is the name for the rule probabilities ");
			System.out.println("\t where <with-NCR> [true|false] whether or not to include rules for noncanonical base pairs ");
			System.out.println("\t where <grammar-folder> grammars folder ");
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
				//System.out.println(G.name);
				//System.out.println("Grammar is: " + G.getAllRules());
				File file = trainingDataset.getRuleProbsForAutoGenGrammar(G);
				G.writeRuleProbs(file,
						G.computeRulesToProbs(
								G.readRuleCounts(
										trainingDataset.getRuleCountForAutoGenGrammar(G)
								)
						)
				);
				System.out.println("Finished writing " + file);
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
				File file = trainingDataset.getRuleProbsForAutoGenGrammar(rnaGrammar);
				rnaGrammar.writeRuleProbs(file,
						rnaGrammar.computeRulesToProbs(
								rnaGrammar.readRuleCounts(
										trainingDataset.getRuleCountForAutoGenGrammar(rnaGrammar)
								)
						)
				);
				System.out.println("Finished writing " + file);
			}
		}


	}
}
