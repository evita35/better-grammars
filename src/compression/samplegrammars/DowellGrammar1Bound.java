
//////////////////////////////////////////
package compression.samplegrammars;

        import compression.grammar.*;

        import java.util.Map;

public class DowellGrammar1Bound implements SampleGrammar {

    private final RNAGrammar G;
    boolean withNonCanonicalRules;

    public DowellGrammar1Bound(boolean withNonCanonicalRules) {
        this.withNonCanonicalRules= withNonCanonicalRules;
        SecondaryStructureGrammar SSG = buildSecondaryStructureGrammar().convertToSRF();
        SSG=SSG.convertToSRF();
        G = RNAGrammar.from(SSG, withNonCanonicalRules);
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
                .addRule(S, U, S, X)
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
        return G.name;
    }
}
