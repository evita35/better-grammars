
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.samplegrammars;

        import compression.grammar.*;

public class DowellGrammar6Bound implements SampleGrammar {

    private  RNAGrammar G;

    boolean withNonCanonicalRules;


    public DowellGrammar6Bound(boolean withNonCanonicalRules) {
        this.withNonCanonicalRules= withNonCanonicalRules;
        SecondaryStructureGrammar SSG = buildSecondaryStructureGrammar().convertToSRF();
        SSG=SSG.convertToSRF();
        G = RNAGrammar.from(SSG, withNonCanonicalRules);
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


        Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("DowellGrammar6",S)
                .addRule(U, u)
                .addRule(B, o, M, c)
                .addRule(L, B)
                .addRule(L, U)
                .addRule(M, B)
                .addRule(M, L, S)
                .addRule(M, L)//not in DE04
                .addRule(S, L, S)
                .addRule(S, L)
        ;

        SecondaryStructureGrammar SSG = SecondaryStructureGrammar.from(Gb.build());
        return SSG;
    }


    @Override
    public boolean isWithNoncanonicalRules() {
        return withNonCanonicalRules;
    }


    //public String getFileName(){return fileName;}

    public NonTerminal getStartSymbol() {
        return G.getStartSymbol();
    }

    public RNAGrammar getGrammar() {
        return G;
    }


    public String getName (){
        return G.getName();
    }
}
