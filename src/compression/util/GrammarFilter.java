package compression.util;

import compression.LocalConfig;
import compression.RuleProbType;
import compression.data.CachedDataset;
import compression.data.Dataset;
import compression.data.FolderBasedDataset;
import compression.grammar.GrammarFolder;
import compression.grammar.RNAWithStructure;
import compression.grammar.SecondaryStructureGrammar;
import compression.grammargenerator.AbstractGrammarExplorer;
import compression.grammargenerator.RandomGrammarExplorer;
import compression.parser.GrammarReaderNWriter;
import compression.parser.SRFParser;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class GrammarFilter {
	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			System.out.println("Take the best k grammars from a folder of grammars.");
			System.out.println("Usage: RandomGrammarExplorer input-grammars nBestGrammarsToKeep full-dataset [small-dataset] [rule-prob-model]");
			System.out.println("\t rule-prob-model: one of 'static', 'semi-adaptive', 'adaptive'");
			System.exit(1);
		}
		GrammarFolder inputGrammarsFolder = new GrammarFolder(args[0]);
		int nBestGrammarsToKeep = Integer.parseInt(args[1]);
		Dataset fullDataset = new CachedDataset(new FolderBasedDataset(args[2]));
		Dataset smallDataset = new CachedDataset(new FolderBasedDataset(args.length > 3 ? args[3] : "small-dataset"));
		Dataset parsableDataset = new CachedDataset(new FolderBasedDataset("minimal-parsable"));
		RuleProbType ruleProbType = args.length > 4 ? RuleProbType.fromString(args[4]) : RuleProbType.ADAPTIVE;

		// TODO generalize?
		if (ruleProbType == RuleProbType.STATIC) {
			System.out.println("Only adaptive / semi-adaptive rule probability model is supported at the moment. Sorry.");
			System.exit(1);
		}

		File outputGrammarsFolder = new File(LocalConfig.GIT_ROOT + "/grammars/" +
				inputGrammarsFolder.folderName +
				"-best-" + nBestGrammarsToKeep +
				"-" + ruleProbType +
				"-on-" + fullDataset.getName());
		if (!outputGrammarsFolder.mkdirs()) {
			System.out.println("Could not create output folder " + outputGrammarsFolder);
			System.out.println("Does it already exist?");
			throw new IOException("Could not create output folder " + outputGrammarsFolder);
		}


		System.out.println("inputGrammarsFolder = " + inputGrammarsFolder);
		System.out.println("outputGrammarsFolder = " + outputGrammarsFolder);
		System.out.println("nBestGrammarsToKeep = " + nBestGrammarsToKeep);
		System.out.println("fullDataset = " + fullDataset);
		System.out.println("smallDataset = " + smallDataset);
		System.out.println("parsableDataset = " + parsableDataset);
		System.out.println("ruleProbType = " + ruleProbType);

		SortedSet<RandomGrammarExplorer.GrammarWithScore> bestGrammars = new TreeSet<>();
		bestGrammars.add(new RandomGrammarExplorer.GrammarWithScore(null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));

		long nGrammars = -1;
		double worstBitsPerBaseSmallDataset = Double.POSITIVE_INFINITY;
		next_grammar:
		for (SecondaryStructureGrammar grammar : inputGrammarsFolder) {
			++nGrammars;
			// measure elapsed time
			long startTime = System.currentTimeMillis();
			try {
				// Level 1 check: parses minimal-parsable?
				SRFParser<Character> ssParser = new SRFParser<>(grammar);
				for (RNAWithStructure rna : parsableDataset) {
					if (!ssParser.parsable(rna.secondaryStructureAsTerminals()))
						continue next_grammar; // ignore this grammar
				}
				// Passed level 1
				System.out.println("Grammar " + nGrammars + " (" + grammar.name + ")");
				System.out.println("\tpassed level 1 (" + (System.currentTimeMillis() - startTime) + " ms)");

				// Level 2: determine bits per base compression ratio on small dataset
				double avgBitsPerBaseSmallDataset = AbstractGrammarExplorer.getBitsPerBase(smallDataset, ruleProbType, grammar, false);
				System.out.println("\tavgBitsPerBaseSmallDataset = " + avgBitsPerBaseSmallDataset);
				System.out.println("\tworstBitsPerBaseSmallDataset = " + worstBitsPerBaseSmallDataset);
				// if good enough, keep it and go to level 3
				if (avgBitsPerBaseSmallDataset > worstBitsPerBaseSmallDataset) {
					// ignore this grammar
					continue;
				}
				System.out.println("\tpassed level 2 (" + (System.currentTimeMillis() - startTime) + " ms)");

				// Level 3: determine bits per base compression ratio on full dataset
				double avgBitsPerBaseFullDataset = AbstractGrammarExplorer.getBitsPerBase(fullDataset, ruleProbType, grammar, true);
				System.out.println("\tavgBitsPerBaseFullDataset = " + avgBitsPerBaseFullDataset);
				RandomGrammarExplorer.GrammarWithScore e = new RandomGrammarExplorer.GrammarWithScore(grammar, avgBitsPerBaseFullDataset, avgBitsPerBaseSmallDataset);
				bestGrammars.add(e);

				System.out.println("\tpassed level 3  (" + (System.currentTimeMillis() - startTime) + " ms)");
				if (bestGrammars.size() > nBestGrammarsToKeep) {
					bestGrammars.remove(bestGrammars.last());
				}
				worstBitsPerBaseSmallDataset = bestGrammars.stream().max(Comparator.comparingDouble(
						a -> a.avgBitsPerBaseSmallDataset)).get().avgBitsPerBaseSmallDataset;
				System.out.println("\tbestGrammars.size() = " + bestGrammars.size());
				System.out.println(bestGrammars.stream().mapToDouble(a -> a.avgBitsPerBase).summaryStatistics());
			} catch (Exception e) {
				System.err.println("Didn't except this: " + e);
				System.err.println("Grammar " + grammar.name + " is invalid?");
				System.err.println(grammar);
				e.printStackTrace();
				System.out.println("Skipping grammar " + grammar.name);
			}
		}
		System.out.println("Writing best grammars to " + outputGrammarsFolder);
		for (RandomGrammarExplorer.GrammarWithScore grammarWithScore : bestGrammars) {
			SecondaryStructureGrammar grammar = grammarWithScore.grammar;
			GrammarReaderNWriter grammarWriter = new GrammarReaderNWriter(outputGrammarsFolder, grammar);
			grammarWriter.writeGrammarToFile(grammar);
		}
	}
}
