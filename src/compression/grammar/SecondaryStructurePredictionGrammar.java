package compression.grammar;

import compression.util.MyMultimap;

/**
 * RNA grammar that ignores the secondary structure, used for predicting a secondary structure
 * when given only a primary structure.
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class SecondaryStructurePredictionGrammar extends Grammar<IgnoringSecondPartPairOfChar> {
	public SecondaryStructurePredictionGrammar(final String name, final NonTerminal startSymbol, final MyMultimap<NonTerminal, Rule> rules_) {
		super(name, startSymbol, rules_);
	}

	// TODO add conversion
}
