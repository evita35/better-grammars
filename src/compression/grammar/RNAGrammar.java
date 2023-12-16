/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.grammar;

import compression.samplegrammars.SampleGrammar;
import compression.util.MyMultimap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Grammar for compressing RNA primary and secondary structure.
 */
public class RNAGrammar extends Grammar<PairOfChar> implements SampleGrammar {

	public RNAGrammar(final String name, final NonTerminal startSymbol, final MyMultimap<NonTerminal, Rule> rules_) {
		super(name, startSymbol, rules_);
	}

	public static NonTerminal getLeftMostNT(List<Category> sententialForm) {

		for (Category cat : sententialForm) {
			//System.out.println("PRINTING RHS in getLeftMostNT " + cat.toString());
			if (!Category.isTerminal(cat)) {
				return (NonTerminal) cat;
			}
		}
		return null;
	}

	@Override
	public Collection<Rule> getAllRules() {
		return super.getAllRules();
	}

	public static final List<Character> RNA_PRIMARY_STRUCTURE_BASES = List.of('A', 'C', 'G', 'U');
	public static final List<PairOfChar> UNPAIRED_BASES = List.of(
			new PairOfChar('A', '.'),
			new PairOfChar('C', '.'),
			new PairOfChar('G', '.'),
			new PairOfChar('U', '.')
	);
	public static final List<Pair> CANONICAL_BASE_PAIRS = List.of(
			new Pair(new PairOfChar('A', '('), new PairOfChar('U', ')')),
			new Pair(new PairOfChar('U', '('), new PairOfChar('A', ')')),
			new Pair(new PairOfChar('C', '('), new PairOfChar('G', ')')),
			new Pair(new PairOfChar('G', '('), new PairOfChar('C', ')')),
			new Pair(new PairOfChar('U', '('), new PairOfChar('G', ')')),
			new Pair(new PairOfChar('G', '('), new PairOfChar('U', ')'))
	);
	public static final List<Pair> NON_CANONICAL_BASE_PAIRS = List.of(
			new Pair(new PairOfChar('A', '('), new PairOfChar('C', ')')),
			new Pair(new PairOfChar('C', '('), new PairOfChar('A', ')')),
			new Pair(new PairOfChar('A', '('), new PairOfChar('G', ')')),
			new Pair(new PairOfChar('G', '('), new PairOfChar('A', ')')),
			new Pair(new PairOfChar('C', '('), new PairOfChar('U', ')')),
			new Pair(new PairOfChar('U', '('), new PairOfChar('C', ')')),
			new Pair(new PairOfChar('A', '('), new PairOfChar('A', ')')),
			new Pair(new PairOfChar('C', '('), new PairOfChar('C', ')')),
			new Pair(new PairOfChar('G', '('), new PairOfChar('G', ')')),
			new Pair(new PairOfChar('U', '('), new PairOfChar('U', ')'))
	);

	public RNAGrammar convertToSRF() {
		MyMultimap<NonTerminal, Rule> rules = SecondaryStructureGrammar.splitLongRightHandSides(this.getAllRules());
		RNAGrammar G = new RNAGrammar(this.name + "_SRF", this.getStartSymbol(), rules);
		if (!SRFNormalForm.isSRFNormalForm(G))
			throw new IllegalStateException("Grammar is not in SRF normal form; probably type 4 rule violations.)");
		return G;
	}

	@Override
	public String getName() {
		return super.name;
	}

	@Override
	public boolean isWithNoncanonicalRules() {
		return false;
	}

	@Override
	public RNAGrammar getGrammar() {
		return new RNAGrammar(super.name,super.startSymbol,super.rules);
	}

	static class Pair {
		public Pair(final PairOfChar open, final PairOfChar close) {
			this.open = open;
			this.close = close;
		}

		public final PairOfChar open, close;
	}
	/*
	public static RNAGrammar from(SecondaryStructureGrammar G, boolean withNonCanonicalRules, NonTerminal ...N){

	}*/


	/**
	 * Convert a secondary structure grammar in SRF normal form to an RNA grammar.
	 */
	public static RNAGrammar from(SecondaryStructureGrammar G, boolean withNonCanonicalRules) {
		MyMultimap<NonTerminal, Rule> rules = new MyMultimap<>();
		// We want the same rules as in G, but
		// rules of type 2 Ai → . need to be replaced by 4 copies and
		// type 3 rules need to be replaced by 6/16 copies Ai →(Aj)
		for (Rule r : G.getAllRules()) {
			switch (SRFNormalForm.getRuleType(r)) {
				case TYPE_I:
				case TYPE_IV:
					rules.put(r.left, r);
					break;
				case TYPE_II: // Ai → .
					// need to be replaced by 4 copies and
					for (PairOfChar up : UNPAIRED_BASES) {
						rules.put(r.left, new Rule(r.left, new PairOfCharTerminal(up)));
					}
					break;
				case TYPE_III: // Ai → (Aj)
					for (Pair cp : CANONICAL_BASE_PAIRS) {
						rules.put(r.left, new Rule(r.left,
								new PairOfCharTerminal(cp.open), r.right[1], new PairOfCharTerminal(cp.close)));
					}
					if (withNonCanonicalRules) {
						for (Pair cp : NON_CANONICAL_BASE_PAIRS) {
							rules.put(r.left, new Rule(r.left,
									new PairOfCharTerminal(cp.open), r.right[1], new PairOfCharTerminal(cp.close)));
						}
					}
			}
		}
		return new RNAGrammar(G.name, G.getStartSymbol(), rules);
	}

	/** creates a shallow copy of G as SecondaryStructureGrammar */
	public static RNAGrammar fromCheap(Grammar<PairOfChar> G) {
		return new RNAGrammar(G.name, G.startSymbol, G.rules);
	}
}
