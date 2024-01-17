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

	public File ruleProbsFileFor(SampleGrammar grammar) {
		return new File(LocalConfig.GIT_ROOT
				+ "/staticmodels/static_probabilities/"
				+ "rule-probs-" + name + "-" + grammar.getName()
				+ "-withNCR-" + grammar.isWithNoncanonicalRules() + ".txt");
	}
	public File ruleCountsFileFor(SampleGrammar grammar){
		return new File(LocalConfig.GIT_ROOT
				+ "/staticmodels/static_counts/"
				+ "rule-counts-" + name + "-" + grammar.getName()
				+ "-withNCR-" + grammar.isWithNoncanonicalRules() + ".txt");
	}
	@Override
	public String toString() {
		return "TrainingDataset(" +
				"name='" + name + '\'' +
				')';
	}
}
