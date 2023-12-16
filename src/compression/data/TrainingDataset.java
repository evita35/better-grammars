package compression.data;

import compression.LocalConfig;
import compression.samplegrammars.SampleGrammar;

import java.io.File;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class TrainingDataset extends FolderBasedDataset {

	public TrainingDataset(final String name) {
		super(name);
	}


	public File getRuleProbsForGrammar(SampleGrammar grammar) {
		return new File(LocalConfig.GIT_ROOT
				+ "/src/compression/static_probabilities/"
				+ "rule-probs-" + name + "-" + grammar.getName()
				+ "-withNCR-" + grammar.isWithNoncanonicalRules() + ".txt");
	}

	public File getRuleCountsForGrammar(SampleGrammar grammar) {
		return new File(LocalConfig.GIT_ROOT
				+ "/src/compression/static_probabilities/"
				+ "rule-counts-" + name + "-" + grammar.getName()
				+ "-withNCR-" + grammar.isWithNoncanonicalRules() + ".txt");
	}

	public File getRuleProbsForAutoGenGrammar(SampleGrammar autoGengrammar) {
		return new File(LocalConfig.GIT_ROOT
				+ "/staticmodels/static_probabilities/"
				+ "rule-probs-" + name + "-" + autoGengrammar.getName()
				+ "-withNCR-" + autoGengrammar.isWithNoncanonicalRules() + ".txt");
	}
	public File getRuleCountForAutoGenGrammar(SampleGrammar autoGenGrammar){
		return new File(LocalConfig.GIT_ROOT
				+ "/staticmodels/static_counts/"
				+ "rule-counts-" + name + "-" + autoGenGrammar.getName()
				+ "-withNCR-" + autoGenGrammar.isWithNoncanonicalRules() + ".txt");
	}
	@Override
	public String toString() {
		return "TrainingDataset(" +
				"name='" + name + '\'' +
				')';
	}
}
