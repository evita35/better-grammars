


package compression.samplegrammars;

import compression.grammar.*;

public class DowellGrammar5Bound implements SampleGrammar {

	private boolean withNonCanonicalRules;
	private RNAGrammar G;


	public DowellGrammar5Bound(boolean withNonCanonicalRules) {
		this.withNonCanonicalRules = withNonCanonicalRules;
		SecondaryStructureGrammar SSG = buildSecondaryStructureGrammar().convertToSRF();
		SSG=SSG.convertToSRF();
		G = RNAGrammar.from(SSG, withNonCanonicalRules);
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
				.addRule(S, B, S)
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

	public String getName() {
		return G.getName();
	}
}
