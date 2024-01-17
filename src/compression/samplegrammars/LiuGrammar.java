/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.samplegrammars;

import compression.grammar.CharTerminal;
import compression.grammar.PairOfChar;
import compression.grammar.PairOfCharTerminal;
import compression.grammar.NonTerminal;
import compression.grammar.Grammar;
import compression.grammar.RNAGrammar;
import compression.grammar.SecondaryStructureGrammar;

/**
 * Grammar from Liu et al. 2008 with epsilon rules removed.
 */
public class LiuGrammar extends AbstractBuiltinGrammar {

	public LiuGrammar(boolean withNonCanonicalRules) {
		super(withNonCanonicalRules, buildSecondaryStructureGrammar());
	}

	public static SecondaryStructureGrammar buildSecondaryStructureGrammar() {
		NonTerminal S = new NonTerminal("S");
		NonTerminal L = new NonTerminal("L");

		CharTerminal o = new CharTerminal('(');
		CharTerminal c = new CharTerminal(')');
		CharTerminal u = new CharTerminal('.');

		Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("LiuGrammar", S)
				.addRule(S, L, S)
				.addRule(S, L)
				.addRule(L, o, S, c)
				.addRule(L, u);

		return SecondaryStructureGrammar.from(Gb.build());
	}

}
