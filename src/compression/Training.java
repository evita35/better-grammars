package compression;

import compression.data.TrainingDataset;
import compression.grammar.GrammarFolder;
import compression.grammar.RNAGrammar;
import compression.grammar.SecondaryStructureGrammar;
import compression.samplegrammars.SampleGrammar;
import compression.util.AllGrammars;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Main class for computing rule frequencies.
 */
public class Training {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(Arrays.toString(args));
		if (args.length < 4) {
			System.out.println("Training <training-dataset> <with-NCR> <folder|builtin> <grammar-folder>|<list-of-grammars> ...");
			System.out.println("\t where <training-dataset> is the name for the rule probabilities for the static model ");
			System.out.println("\t where <with-NCR> [true|false] whether or not to include rules for noncanonical base pairs ");
			System.out.println("\t where <folder|builtin> whether or not to use a folder of grammars ('folder') or a list of built-in grammars ('builtin') ");
			System.out.println("\t where <grammar-folder> grammars folder ");
			System.out.println("\t where <list-of-grammars> list of built-in grammars: ");
			System.out.println("\t " + AllGrammars.allGrammarNames());
			System.exit(98);
		}

		TrainingDataset trainingDataset = new TrainingDataset(args[0]);
		boolean withNonCanonicalRules = Boolean.parseBoolean(args[1]);
		boolean useGrammarFolder = Boolean.parseBoolean(args[2]);
		System.out.println("trainingDataset = " + trainingDataset);
		System.out.println("withNonCanonicalRules = " + withNonCanonicalRules);
		final Iterator<RNAGrammar> grammarsIter;
		if (useGrammarFolder) {
			GrammarFolder grammarFolder = new GrammarFolder(args[3]);
			System.out.println("grammarFolder = " + grammarFolder);
			Iterator<SecondaryStructureGrammar> ssgIter = grammarFolder.iterator();
			grammarsIter = new Iterator<>() {
				public boolean hasNext() {
					return ssgIter.hasNext();
				}

				public RNAGrammar next() {
					return RNAGrammar.from(ssgIter.next(), withNonCanonicalRules);
				}
			};
		} else {
			List<SampleGrammar> listOfGrammars =
					AllGrammars.getGrammarsFromCmdLine(args[3], withNonCanonicalRules);
			grammarsIter = listOfGrammars.stream().map(SampleGrammar::getGrammar).iterator();
		}
		while (grammarsIter.hasNext()) {
			RNAGrammar G = grammarsIter.next();
			try {
				System.out.println("Running training for " + G.name + " at " + DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now()));
				G.writeRuleCounts(
						trainingDataset.ruleCountsFileFor(G),
						G.computeRuleCounts(trainingDataset)
				);
				G.writeRuleProbs(trainingDataset.ruleProbsFileFor(G),
						G.computeRulesToProbs(
								G.readRuleCounts(trainingDataset.ruleCountsFileFor(G))
						)
				);
			} catch (RuntimeException e) {
				System.out.println("parsing issue with " + G.name);
				e.printStackTrace();
				continue;
			}
		}


	}
}
