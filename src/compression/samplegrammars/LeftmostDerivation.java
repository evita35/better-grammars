package compression.samplegrammars;

import compression.grammar.*;
import compression.grammargenerator.UnparsableException;
import compression.parser.CYKParser;
import compression.parser.Parser;
import compression.parser.SRFParser;
import compression.samplegrammars.model.RuleProbModel;

import java.util.List;

/**
 * Convenience methods to parse RNA with parser and return list of rules used in the parse.
 */
public class LeftmostDerivation {

    private LeftmostDerivation() {}

    /**
     * Compute a leftmost derivation for the given RNA sequence given using parser.
     * Captures any {@link UnparsableException} and rethrows it as a {@link RuntimeException}.
     */
    public static List<Rule> rules(Parser<PairOfChar> parser, RNAWithStructure RNA) {
        List<Terminal<PairOfChar>> terminals = RNA.asTerminals();
        try {
            return parser.leftmostDerivationFor(terminals);
        } catch (UnparsableException e) {
            throw new RuntimeException("In here, we assume grammars parse all RNAs, " +
                    "but failed to parse " + RNA + " with grammar " + parser.getGrammar(), e);
        }
    }

    /**
     * Convenience method that creates a throw-away parser for the given grammar and model.
     */
    public static List<Rule> rules(RNAGrammar grammar, RNAWithStructure RNA) {
        return rules(new SRFParser<>(grammar, RuleProbModel.DONT_CARE), RNA);
    }


}
