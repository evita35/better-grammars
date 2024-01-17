
//////////////////////////////////////////
package compression.samplegrammars;

import compression.grammar.*;

public class DowellGrammar1Bound extends AbstractBuiltinGrammar {

	public DowellGrammar1Bound(boolean withNonCanonicalRules) {
		super(withNonCanonicalRules, buildSecondaryStructureGrammar().convertToSRF());
	}

	public static SecondaryStructureGrammar buildSecondaryStructureGrammar() {
		NonTerminal S = new NonTerminal("S");
		NonTerminal U = new NonTerminal("$U");
		NonTerminal C = new NonTerminal("C");
		NonTerminal X = new NonTerminal("X");
		NonTerminal B = new NonTerminal("B");
		// Order of nonterminals:
		// S > C, C > B, C > U, X > U, X > S

		CharTerminal o = new CharTerminal('(');
		CharTerminal c = new CharTerminal(')');
		CharTerminal u = new CharTerminal('.');

		Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("DowellGrammar1", S)
				// Rules as in DCC23 paper
				.addRule(U, u)
				.addRule(B, o, S, c)

				.addRule(C, B)
				.addRule(C, U)

				.addRule(X, U, X)
				.addRule(X, S, X)
				.addRule(X, U)
				.addRule(X, S)

				.addRule(S, C)
				.addRule(S, C, X)
				.addRule(S, U, S)
				.addRule(S, U, S, X);

		return SecondaryStructureGrammar.from(Gb.build());
	}

	public RNAGrammar getGrammar() {
		return grammar;
	}

}
