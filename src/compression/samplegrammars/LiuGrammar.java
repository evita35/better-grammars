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
 * Grammar from Liu et al 2008 with epsilon rules removed.
 */
public class LiuGrammar implements SampleGrammar {

    private final RNAGrammar G;
    private final NonTerminal S;
    public String name="LiuGrammar";

    boolean withNonCanonicalRules;

    public LiuGrammar(boolean withNonCanonicalRules) {
        this.withNonCanonicalRules= withNonCanonicalRules;
        SecondaryStructureGrammar SSG = buildSecondaryStructureGrammar();
        G = RNAGrammar.from(SSG, withNonCanonicalRules);
        this.S = G.startSymbol;

    }

    public static SecondaryStructureGrammar buildSecondaryStructureGrammar() {
        NonTerminal S = new NonTerminal("S");
        NonTerminal L = new NonTerminal("L");

        CharTerminal o = new CharTerminal('(');
        CharTerminal c = new CharTerminal(')');
        CharTerminal u = new CharTerminal('.');


        Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("LiuGrammar",S)
                .addRule(S, L, S)
                .addRule(S, L)
                .addRule(L, o, S, c)
                .addRule(L, u);

        SecondaryStructureGrammar SSG = SecondaryStructureGrammar.from(Gb.build());
        return SSG;
    }

    @Override
    public boolean isWithNoncanonicalRules() {
        return withNonCanonicalRules;
    }


    public NonTerminal getStartSymbol() {
        return S;
    }

    public RNAGrammar getGrammar() {
        return G;
    }

    @Override
    public String getName() {
        return name;
    }
}
