package compression.samplegrammars;

import compression.grammar.NonTerminal;
import compression.grammar.RNAGrammar;
import compression.grammar.SecondaryStructureGrammar;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public abstract class AbstractBuiltinGrammar implements SampleGrammar {
	protected final boolean withNonCanonicalRules;
	protected final RNAGrammar grammar;

	public AbstractBuiltinGrammar(final boolean withNonCanonicalRules, final RNAGrammar grammar) {
		this.withNonCanonicalRules = withNonCanonicalRules;
		this.grammar = grammar;
	}

	public AbstractBuiltinGrammar(final boolean withNonCanonicalRules, final SecondaryStructureGrammar SSG) {
		this(withNonCanonicalRules, RNAGrammar.from(SSG, withNonCanonicalRules));
	}

	@Override
	public boolean isWithNoncanonicalRules() {
		return withNonCanonicalRules;
	}

	public String getName() {
		return getGrammar().getName();
	}

	@Override
	public String toString() {
		return "BuiltinGrammar(" +
				"name=" + getName() +
				",withNonCanonicalRules=" + withNonCanonicalRules +
				')';
	}

	//public String getFileName(){return fileName;}
	public NonTerminal getStartSymbol() {
		return getGrammar().getStartSymbol();
	}

	@Override
	public RNAGrammar getGrammar() {
		return grammar;
	}


}
