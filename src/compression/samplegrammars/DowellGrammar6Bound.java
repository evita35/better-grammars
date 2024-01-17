
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.samplegrammars;

import compression.grammar.*;

public class DowellGrammar6Bound extends AbstractBuiltinGrammar {

	public DowellGrammar6Bound(boolean withNonCanonicalRules) {
		super(withNonCanonicalRules, buildSecondaryStructureGrammar().convertToSRF());
	}

	public static SecondaryStructureGrammar buildSecondaryStructureGrammar() {
		NonTerminal S = new NonTerminal("S");
		NonTerminal L = new NonTerminal("L"); // renamed from T
		NonTerminal M = new NonTerminal("M");
		NonTerminal B = new NonTerminal("B");
		NonTerminal U = new NonTerminal("$U");
		// order: M > L, S > L

		CharTerminal o = new CharTerminal('(');
		CharTerminal c = new CharTerminal(')');
		CharTerminal u = new CharTerminal('.');


		Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("DowellGrammar6", S)
				.addRule(U, u)
				.addRule(B, o, M, c)
				.addRule(L, B)
				.addRule(L, U)
				.addRule(M, B)
				.addRule(M, L, S)
				.addRule(M, L)//not in DE04
				.addRule(S, L, S)
				.addRule(S, L);

		return SecondaryStructureGrammar.from(Gb.build());
	}


}
