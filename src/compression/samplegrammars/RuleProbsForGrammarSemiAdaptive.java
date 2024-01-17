/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.samplegrammars;

import compression.grammar.RNAGrammar;
import compression.grammar.RNAWithStructure;
import compression.grammar.Rule;
import compression.samplegrammars.model.RuleProbModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compute the frequency of each rule in a grammar for a given RNA, starting with 0 (no
 * LaPlace smoothing since used only on a single RNA). For ambiguous grammars, no
 * guarantee is made as to which derivation is counted.
 */
public final class RuleProbsForGrammarSemiAdaptive {

	private final Map<Rule, Double> rulesToProbs;

	public RuleProbsForGrammarSemiAdaptive(RNAGrammar grammar, RNAWithStructure rna) {
		Map<Rule, Long> rulesToFrequency = new HashMap<>();
		// initizlize map to all 0
		grammar.getAllRules().forEach(r -> rulesToFrequency.put(r, 0L));
		// compute derivation
		List<Rule> rules = LeftmostDerivation.rules(grammar, rna);
		// count rules
		rules.forEach((rule) -> {
			rulesToFrequency.replace(rule, rulesToFrequency.get(rule) + 1);
		});
		this.rulesToProbs = RuleProbModel.computeRuleProbs(grammar, rulesToFrequency);
	}


	public Map<Rule, Double> ruleProbs() {
		return Collections.unmodifiableMap(rulesToProbs);
	}


}
