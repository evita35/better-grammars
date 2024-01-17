
package compression.samplegrammars;

import compression.grammar.*;

public class DowellGrammar3Bound extends AbstractBuiltinGrammar {

	public DowellGrammar3Bound(boolean withNonCanonicalRules) {
		super(withNonCanonicalRules, buildSecondaryStructureGrammar().convertToSRF());
	}

	public static SecondaryStructureGrammar buildSecondaryStructureGrammar() {
		NonTerminal S = new NonTerminal("S");
		NonTerminal L = new NonTerminal("L");
		NonTerminal R = new NonTerminal("R");
		NonTerminal B = new NonTerminal("$B");
		NonTerminal U = new NonTerminal("$U");

		CharTerminal o = new CharTerminal('(');
		CharTerminal c = new CharTerminal(')');
		CharTerminal u = new CharTerminal('.');


		Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("DowellGrammar3", S)
				// rules as in DCC23 paper
				.addRule(U, u)
				.addRule(B, o, S, c)
				.addRule(L, B)
				.addRule(L, U, L)
				.addRule(R, U)
				.addRule(R, U, R)
				.addRule(S, B)
				.addRule(S, U, L)
				.addRule(S, R, U)
				.addRule(S, L, S)
				.addRule(S, U)
				.addRule(L, B);
		return SecondaryStructureGrammar.from(Gb.build());
	}


}





