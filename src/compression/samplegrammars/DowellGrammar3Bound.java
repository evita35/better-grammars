
package compression.samplegrammars;
import compression.grammar.*;

public class DowellGrammar3Bound implements SampleGrammar {

    private boolean withNonCanonicalRules;
    private RNAGrammar G;


    public DowellGrammar3Bound(boolean withNonCanonicalRules) {
        this.withNonCanonicalRules= withNonCanonicalRules;
        SecondaryStructureGrammar SSG = buildSecondaryStructureGrammar().convertToSRF();
        SSG=SSG.convertToSRF();
        G = RNAGrammar.from(SSG, withNonCanonicalRules);
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


        Grammar.Builder<Character> Gb = new Grammar.Builder<Character>("DowellGrammar3",S)
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
                .addRule(L, B)
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





