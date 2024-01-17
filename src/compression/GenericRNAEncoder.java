package compression;

import compression.coding.ArithmeticEncoder;
import compression.parser.SRFParser;
import compression.parser.StochasticParser;
import compression.samplegrammars.model.RuleProbModel;
import compression.samplegrammars.LeftmostDerivation;
import compression.grammar.NonTerminal;
import compression.grammar.Rule;

import compression.grammar.*;
import compression.samplegrammars.model.StaticRuleProbModel;

import java.util.List;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class GenericRNAEncoder {

    protected final ArithmeticEncoder acEncoder;
    protected final RuleProbModel model;
    protected final RNAGrammar grammar;
    protected final NonTerminal startSymbol;
    protected final StochasticParser<PairOfChar> parser;

    public GenericRNAEncoder(RuleProbModel model, ArithmeticEncoder acEncoder, RNAGrammar grammar, NonTerminal startSymbol) {
        this.acEncoder = acEncoder;
        this.model = model;
        this.grammar = grammar;
        this.startSymbol = startSymbol;
        // Only use the ruleProbModel in the parser if it is static (otherwise use dummy model)
        // NB: We should NOT use a semiadaptive model in the parser (even though it is static after training),
        // as require to get the SAME derivation
        if (model instanceof StaticRuleProbModel)
            this.parser = new SRFParser<>(grammar, model);
        else
            this.parser = new SRFParser<>(grammar, RuleProbModel.DONT_CARE);
    }

    public List<Rule> leftmostDerivationFor(RNAWithStructure RNA){
        return LeftmostDerivation.rules(parser, RNA);
    }


    public String encodeRNA(RNAWithStructure RNA) {
        for (Rule rule : leftmostDerivationFor(RNA)) {
            acEncoder.encodeNext(model.getIntervalFor(rule));
        }
        return acEncoder.getFinalEncoding();
    }

}
