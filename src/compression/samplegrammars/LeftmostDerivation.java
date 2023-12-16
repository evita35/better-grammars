package compression.samplegrammars;

import compression.grammar.*;
import compression.grammargenerator.UnparsableException;
import compression.parser.CYKParser;
import compression.parser.Parser;
import compression.parser.SRFParser;

import java.util.List;

/**
 * Parse RNA with grammar {@link #G} and return list of rules used in the parse.
 */
public class LeftmostDerivation {

    RNAGrammar G;
    private Parser<PairOfChar> parser;

    public LeftmostDerivation(RNAGrammar G) {
        this.G = G;
        this.parser = new SRFParser<>(G);
    }


    /**
     * Compute the leftmost derivation for the given RNA sequence
     * using grammar {@link #G}
     */
    public List<Rule> rules(RNAWithStructure RNA) {
        List<Terminal<PairOfChar>> terminals = RNA.asTerminals();
        try {
            return parser.leftmostDerivationFor(terminals);
        } catch (UnparsableException e) {
            throw new RuntimeException("In here, we assume grammars parse all RNAs, but failed to parse " + RNA + " with grammar " + G, e);
        }
    }


}
