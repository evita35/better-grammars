/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import compression.coding.ExactArithmeticEncoder;
import compression.grammargenerator.UnparsableException;
import compression.parser.CYKParser;
import compression.parser.Parser;
import compression.parser.SRFParser;
import compression.samplegrammars.model.StaticRuleProbModel;
import compression.grammar.NonTerminal;

import compression.grammar.*;
import compression.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import compression.samplegrammars.*;
import junit.framework.Assert;


public class GenericRNAEncoderTest {

    String pry = "G";
    String sec = ".";
    RNAWithStructure RNAWS;
    LiuGrammar Liu;
    Parser<PairOfChar> parser;
    NonTerminal S;
    
    public GenericRNAEncoderTest() {
        RNAWS = new RNAWithStructure(pry, sec);
        Liu= new LiuGrammar(false);
        // TODO: This doesn't work with the old grammars unless in SRF
        parser = new SRFParser<>(Liu.getGrammar());
        S = new NonTerminal("S");
    }

    @Test
    public void testTraverseParseTree() throws UnparsableException {

        // split string into list of terminals (terminals)
        List<Terminal<PairOfChar>> terminals = RNAWS.asTerminals();

        
        NonTerminal T = new NonTerminal("T");
        PairOfCharTerminal gu = new PairOfChar('G', '.').asTerminal();
        List<Rule> der = parser.leftmostDerivationFor(terminals);

        List<Rule> der2 = new ArrayList<>();
        // TODO Construct derivation manually and compare with the one from the parser

        Assert.assertEquals(der2, der);
        
    
    }

    @Test
    public void testencodeRNA() {

        GenericRNAEncoder GRA = new GenericRNAEncoder(
                new StaticRuleProbModel(Liu.getGrammar(),
                        new LiuProbs4Tests().LiuEtAlRuleProbs()),
                new ExactArithmeticEncoder(), null, S);// FIXME: Grammar is null
        String encodedString = GRA.encodeRNA(RNAWS);
        Assert.assertEquals("11100011", encodedString);
    }
}
