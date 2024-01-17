package compression.samplegrammars;

import compression.grammar.*;

public class DowellGrammar4Bound extends AbstractBuiltinGrammar {

	public DowellGrammar4Bound(boolean withNonCanonicalRules) {
		super(withNonCanonicalRules, buildSecondaryStructureGrammar().convertToSRF());
	}

	public static SecondaryStructureGrammar buildSecondaryStructureGrammar() {
		NonTerminal S = new NonTerminal("S");
		//NonTerminal T = new NonTerminal("T");
		NonTerminal Q = new NonTerminal("Q");//replaced T with Q to maintain lexicographical ordering
		//since Q appears on RHS of some rules in which S is on the LHS
		NonTerminal B = new NonTerminal("B");
		NonTerminal C = new NonTerminal("C");
		NonTerminal D = new NonTerminal("D");
		NonTerminal U = new NonTerminal("$U");

		CharTerminal o = new CharTerminal('(');
		CharTerminal c = new CharTerminal(')');
		CharTerminal u = new CharTerminal('.');


		Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("DowellGrammar4", S)
				// rules as in DCC23 paper
				.addRule(U, u)
				.addRule(B, o, S, c)
				.addRule(C, B)
				.addRule(C, U)
				.addRule(D, C)
				.addRule(D, C, D)
				.addRule(Q, B)
				.addRule(Q, B, D)
				.addRule(S, U)
				.addRule(S, U, S)
				.addRule(S, Q);

		return SecondaryStructureGrammar.from(Gb.build());
	}


}
