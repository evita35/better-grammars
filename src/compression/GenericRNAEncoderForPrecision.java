package compression;

import compression.coding.ArithmeticEncoder;
import compression.grammar.PairOfChar;
import compression.grammar.RNAGrammar;
import compression.grammar.RNAWithStructure;
import compression.samplegrammars.model.RuleProbModel;
import compression.grammar.NonTerminal;
import compression.grammar.Grammar;
import compression.grammar.Rule;


public class GenericRNAEncoderForPrecision extends GenericRNAEncoder {

    public GenericRNAEncoderForPrecision(RuleProbModel model, ArithmeticEncoder acEncoder, RNAGrammar grammar, NonTerminal startSymbol) {
        super(model, acEncoder, grammar, startSymbol);
    }

    public int getPrecisionForRNACode(RNAWithStructure RNA) {
        for (Rule rule : leftmostDerivationFor(RNA )) {
            acEncoder.encodeNext(model.getIntervalFor(rule));
        }
        return acEncoder.getFinalPrecision();
    }

}
