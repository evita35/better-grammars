package compression;

import compression.coding.ArithmeticEncoder;
import compression.samplegrammars.model.RuleProbModel;
import compression.samplegrammars.LeftmostDerivation;
import compression.grammar.NonTerminal;
import compression.grammar.Grammar;
import compression.grammar.Rule;

import compression.grammar.*;

import java.util.List;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class GenericRNAEncoder {

    ArithmeticEncoder acEncoder;
    RuleProbModel model;
    RNAGrammar grammar;
    NonTerminal startSymbol;
    private LeftmostDerivation leftmostDerivation;

    public GenericRNAEncoder(RuleProbModel model, ArithmeticEncoder acEncoder, RNAGrammar grammar, NonTerminal startSymbol) {
        this.acEncoder = acEncoder;
        this.model = model;
        this.grammar = grammar;
        this.startSymbol = startSymbol;
        this.leftmostDerivation = new LeftmostDerivation(this.grammar);
    }

    public List<Rule> leftmostDerivationFor(RNAWithStructure RNA){
        return leftmostDerivation.rules(RNA);
    }


    public String encodeRNA(RNAWithStructure RNA) {
        for (Rule rule : leftmostDerivationFor(RNA)) {

            acEncoder.encodeNext(model.getIntervalFor(rule));

        }
        return acEncoder.getFinalEncoding();
    }

}
