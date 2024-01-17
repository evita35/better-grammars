


package compression.samplegrammars;

import compression.grammar.*;

public class DowellGrammar5Bound extends AbstractBuiltinGrammar {

	public DowellGrammar5Bound(boolean withNonCanonicalRules) {
		super(withNonCanonicalRules, buildSecondaryStructureGrammar().convertToSRF());
	}

	public static SecondaryStructureGrammar buildSecondaryStructureGrammar() {
		NonTerminal S = new NonTerminal("S");
		NonTerminal U = new NonTerminal("$U");
		NonTerminal B = new NonTerminal("B");

		CharTerminal o = new CharTerminal('(');
		CharTerminal c = new CharTerminal(')');
		CharTerminal u = new CharTerminal('.');


		Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("DowellGrammar5Bound", S)
				.addRule(U, u)
				.addRule(B, o, S, c)
				.addRule(S, U)
				.addRule(S, B)
				.addRule(S, U, S)
				.addRule(S, B, S);
		return SecondaryStructureGrammar.from(Gb.build());
	}

}
