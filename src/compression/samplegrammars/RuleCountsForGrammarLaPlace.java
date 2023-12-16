/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.samplegrammars;

import compression.LocalConfig;
import compression.data.Dataset;
import compression.grammar.RNAGrammar;
import compression.grammar.RNAWithStructure;
import compression.grammar.Grammar;
import compression.grammar.Rule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Compute the frequency of each rule in a grammar for a given dataset,
 * starting with 1 (LaPlace smoothing)
 */
public final class RuleCountsForGrammarLaPlace {


    Map<Rule, Long> RulesToFrequency;
    RNAGrammar grammar;

    int noOfRNASamples;

    public RuleCountsForGrammarLaPlace(RNAGrammar g, Dataset dataset) throws IOException {

        RulesToFrequency = new HashMap<>();
        noOfRNASamples = dataset.getSize();

        grammar = g;

        this.initialiseRTF(new ArrayList<>(grammar.getAllRules()));

        StreamSupport.stream(dataset.spliterator(), true).unordered().forEach((RNAWS) -> {
            try {
                List<Rule> rules = new LeftmostDerivation(g).rules(RNAWS);
                incrementMap(rules);
            } catch (RuntimeException runtimeException) {
                //System.out.println(RNAWS.name + " HAS PARSING ISSUE with grammar: "+grammar.name);
                throw new RuntimeException(RNAWS.name + " HAS PARSING ISSUE with grammar: "+grammar.name);
                //System.exit(0);
                /*
                    try {
                        BufferedWriter bf = new BufferedWriter(new FileWriter(LocalConfig.GIT_ROOT+"/grammars/"+"grammars-with-parsing-issues"+g.name+".txt"));

                        bf.write("parsing issue with grammar"+grammar.name+"and Rna:"+RNAWS);
                        bf.newLine();
                        bf.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                */

            }
        });
    }

    public synchronized void incrementMap(List<Rule> listOfRules) {
        listOfRules.forEach((rule) -> {
            RulesToFrequency.replace(rule, RulesToFrequency.get(rule) + 1);
        });

    }

    public void initialiseRTF(List<Rule> rules) {
        // initialise to 1 to avoid 0 probabilities
        rules.forEach((rule) -> RulesToFrequency.put(rule, 1L));
    }


    public Map<Rule, Long> getRuleToCounts() {
        return RulesToFrequency;
    }


}
