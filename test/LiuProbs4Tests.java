import compression.grammar.PairOfChar;
import compression.grammar.PairOfCharTerminal;
import compression.samplegrammars.LiuGrammar;
import compression.samplegrammars.SampleGrammar;
import org.junit.*;
import compression.grammar.NonTerminal;
import compression.grammar.Rule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class LiuProbs4Tests {

	SampleGrammar Liu = new LiuGrammar(false);

	NonTerminal S = NonTerminal.of("S");
	NonTerminal T = NonTerminal.of("T");

	PairOfCharTerminal ao = new PairOfChar('A', '(').asTerminal();
	PairOfCharTerminal co = new PairOfChar('C', '(').asTerminal();
	PairOfCharTerminal go = new PairOfChar('G', '(').asTerminal();
	PairOfCharTerminal uo = new PairOfChar('U', '(').asTerminal();
	PairOfCharTerminal ac = new PairOfChar('A', ')').asTerminal();
	PairOfCharTerminal cc = new PairOfChar('C', ')').asTerminal();
	PairOfCharTerminal gc = new PairOfChar('G', ')').asTerminal();
	PairOfCharTerminal uc = new PairOfChar('U', ')').asTerminal();
	PairOfCharTerminal au = new PairOfChar('A', '.').asTerminal();
	PairOfCharTerminal cu = new PairOfChar('C', '.').asTerminal();
	PairOfCharTerminal gu = new PairOfChar('G', '.').asTerminal();
	PairOfCharTerminal uu = new PairOfChar('U', '.').asTerminal();


	public Map<Rule, Double> LiuEtAlRuleProbs() {

		HashMap<Rule, Double> res = new HashMap<>();

		res.put(Rule.create(S, T, S), 0.66);
		res.put(Rule.create(S, T), 0.34);
		res.put(Rule.create(T, ao, S, uc), 0.071602);
		res.put(Rule.create(T, uo, S, ac), 0.094385);
		res.put(Rule.create(T, co, S, gc), 0.144020);
		res.put(Rule.create(T, go, S, cc), 0.113914);
		res.put(Rule.create(T, uo, S, gc), 0.026851);
		res.put(Rule.create(T, go, S, uc), 0.017901);
		res.put(Rule.create(T, au), 0.183076);
		res.put(Rule.create(T, cu), 0.087876);
		res.put(Rule.create(T, gu), 0.101709);
		res.put(Rule.create(T, uu), 0.158666);

		return res;
	}
	public Map<Rule, Double> UniformRuleProbs() {
		HashMap<Rule, Double> res = new HashMap<>();

		res.put(Rule.create(S, T, S), 0.5);
		res.put(Rule.create(S, T), 0.5);
		res.put(Rule.create(T, ao, S, uc), 0.1);
		res.put(Rule.create(T, uo, S, ac), 0.1);
		res.put(Rule.create(T, co, S, gc), 0.1);
		res.put(Rule.create(T, go, S, cc), 0.1);
		res.put(Rule.create(T, uo, S, gc), 0.1);
		res.put(Rule.create(T, go, S, uc), 0.1);
		res.put(Rule.create(T, au), 0.1);
		res.put(Rule.create(T, cu), 0.1);
		res.put(Rule.create(T, gu), 0.1);
		res.put(Rule.create(T, uu), 0.1);

		return res;
	}


}
