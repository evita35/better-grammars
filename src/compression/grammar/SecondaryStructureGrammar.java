package compression.grammar;

import compression.samplegrammars.*;
import compression.util.MyMultimap;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class SecondaryStructureGrammar extends Grammar<Character> {

	public SecondaryStructureGrammar(final String name, final NonTerminal startSymbol, final MyMultimap<NonTerminal, Rule> rules_) {
		super(name, startSymbol, rules_);
	}


	public static SecondaryStructureGrammar from(Grammar<Character> G) {
		MyMultimap<NonTerminal, Rule> rules = new MyMultimap<>();
		for (NonTerminal lhs : G.getNonTerminals()) {
			for (Rule r : G.getRules(lhs)) {
				rules.put(lhs, new Rule(lhs, r.right));
			}
		}
		return new SecondaryStructureGrammar(G.name, G.startSymbol, rules);
	}

	/** creates a shallow copy of G as SecondaryStructureGrammar */
	public static SecondaryStructureGrammar fromCheap(Grammar<Character> G) {
		return new SecondaryStructureGrammar(G.name, G.startSymbol, G.rules);
	}

	/**
	 * Attempts to convers this grammar to SRF normal form.
	 * Currently this only mean splitting rules with long right-hand sides into a sequence of rules.
	 * Nonterminals must already have the right ordering for type 4 rules.
	 */
	public SecondaryStructureGrammar convertToSRF() {
		MyMultimap<NonTerminal, Rule> rules = splitLongRightHandSides(this.getAllRules());
		SecondaryStructureGrammar G = new SecondaryStructureGrammar(this.name + "_SRF", this.getStartSymbol(), rules);
		if (!SRFNormalForm.isSRFNormalForm(G))
			throw new IllegalStateException("Grammar is not in SRF normal form; probably type 4 rule violations.)");
		return G;
	}

	/**
	 * Split all right-hand sides of rules with 3 or more nonterminals; any other rules
	 * are copied unchanged.
	 */
	public static MyMultimap<NonTerminal, Rule> splitLongRightHandSides(final Collection<Rule> rules) {
		MyMultimap<NonTerminal, Rule> res = new MyMultimap<>();
		int ruleIndex = -1;
		for (Rule rule : rules) {
			NonTerminal A = rule.left;
			Category[] rhs = rule.right.clone();
			++ruleIndex;
			try {
				SRFNormalForm.getRuleType(rule);
				// Could try to fix type 4 violations here ... or just let it bubble up later
				res.put(A, new Rule(A, rhs));
				continue;
			} catch (IllegalArgumentException e) {
				// no problem here (yet); rule just is not already in SRF normal form
				// --> do conversion
			}
			// only other supported rule form is A -> B1 B2 B3 ... Bk
			if (!Arrays.stream(rhs).allMatch(Category::isNonTerminal) || rhs.length < 3) {
				throw new IllegalArgumentException("Rule " + rule + " is not in SRF form and not a sequence of nonterminals on rhs.");
			}
			// A -> B0 B1 B2 ... Bk-1 will be split into a sequence of res
			// 0: A  -> B0 A1
			// 1: A1 -> B1 A2
			// ...
			// k-2: Ak-2 -> Bk-2 Bk-1
			int k = rhs.length;
			NonTerminal Ai = A;
			for (int i = 0; i < k-1; i++) {
				NonTerminal Bi = (NonTerminal) rhs[i];
				NonTerminal Aip1 =
						i == k - 2 ?
								(NonTerminal) rhs[i+1] :
								new NonTerminal(A.name + "_R" + ruleIndex + "_" + (i + 1));
				res.put(Ai, new Rule(Ai, Bi, Aip1));
				Ai = Aip1;
			}
		}
		return res;
	}

	public static void main(String[] args) {
		if (false) {
			NonTerminal S = new NonTerminal("S");
			NonTerminal U = new NonTerminal("U");
			NonTerminal A = new NonTerminal("A");
			Grammar<Character> G = new Builder<Character>("test", S)
					.addRule(S, A, S, U)
					.addRule(S, U, U, U, U, A)
					.addRule(U, new CharTerminal('.'))
					.addRule(A, new CharTerminal('.'))
					.build();
			SecondaryStructureGrammar SSG = SecondaryStructureGrammar.from(G);
			System.out.println("SSG = " + SSG);
			SecondaryStructureGrammar SRFG = SSG.convertToSRF();
			System.out.println("SRFG = " + SRFG);
			RNAGrammar rnaGrammar = RNAGrammar.from(SRFG, false);
			System.out.println("rnaGrammar = " + rnaGrammar);
		}

		if (false){
			// Conversion route 1: SSG -> SSG in SRF -> RNA grammar in SRF
//			SecondaryStructureGrammar SSG = LiuGrammar.buildSecondaryStructureGrammar();
//			SecondaryStructureGrammar SSG = DowellGrammar1Bound.buildSecondaryStructureGrammar();
//			SecondaryStructureGrammar SSG = DowellGrammar3Bound.buildSecondaryStructureGrammar();
//			SecondaryStructureGrammar SSG = DowellGrammar4Bound.buildSecondaryStructureGrammar();
//			SecondaryStructureGrammar SSG = DowellGrammar5Bound.buildSecondaryStructureGrammar();
			SecondaryStructureGrammar SSG = DowellGrammar6Bound.buildSecondaryStructureGrammar();
			System.out.println("SSG = " + SSG);
			SecondaryStructureGrammar SRFG = SSG.convertToSRF();
			System.out.println("SRFG = " + SRFG);
			RNAGrammar rnaGrammar = RNAGrammar.from(SRFG, false);
			System.out.println("rnaGrammar = " + rnaGrammar);
			System.out.println(SRFNormalForm.isSRFNormalForm(rnaGrammar));
		}

		if (true) {
			// Conversion route 2: RNA grammar -> RNA grammar in SRF
//			RNAGrammar RFG = new DowellGrammar2Bound(false).getGrammar();
//			RNAGrammar RFG = new DowellGrammar7ModifiedNBound(false).getGrammar();
			RNAGrammar RFG = new DowellGrammar8ModifiedNBound(false).getGrammar();
			System.out.println("RFG = " + RFG);
			RNAGrammar rnaGrammar = RFG.convertToSRF();
			System.out.println("rnaGrammar = " + rnaGrammar);
		}
	}

}

