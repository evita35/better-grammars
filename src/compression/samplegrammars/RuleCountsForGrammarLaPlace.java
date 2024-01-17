/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.samplegrammars;

import compression.data.Dataset;
import compression.grammar.RNAGrammar;
import compression.grammar.Rule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Compute the frequency of each rule in a grammar for a given dataset, starting with 1
 * (LaPlace smoothing).
 * For ambiguous grammars, no guarantee is made as to which derivation is counted.
 */
public final class RuleCountsForGrammarLaPlace {

	private final Map<Rule, Long> rulesToFrequency = new HashMap<>();
	private final RNAGrammar grammar;

	public RuleCountsForGrammarLaPlace(RNAGrammar grammar, Dataset dataset) {
		this.grammar = grammar;
		initializeMap(this.grammar.getAllRules());
		StreamSupport.stream(dataset.spliterator(), true).unordered().forEach((RNAWS) -> {
			try {
				List<Rule> rules = LeftmostDerivation.rules(this.grammar, RNAWS);
				incrementMap(rules);
			} catch (RuntimeException e) {
				throw new RuntimeException(RNAWS.name + " HAS PARSING ISSUE with grammar: " + this.grammar, e);
			}
		});
	}

	public synchronized void incrementMap(List<Rule> listOfRules) {
		listOfRules.forEach((rule) -> {
			rulesToFrequency.replace(rule, rulesToFrequency.get(rule) + 1);
		});
	}

	public void initializeMap(Collection<Rule> rules) {
		// initialise to 1 to avoid 0 probabilities
		rules.forEach((rule) -> rulesToFrequency.put(rule, 1L));
	}


	public Map<Rule, Long> ruleCounts() {
		return Collections.unmodifiableMap(rulesToFrequency);
	}


}
