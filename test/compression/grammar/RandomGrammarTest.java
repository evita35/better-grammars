package compression.grammar;

import compression.GenericRNADecoder;
import compression.GenericRNAEncoderForPrecision;
import compression.coding.*;
import compression.grammargenerator.RandomGrammarExplorer;
import compression.samplegrammars.model.AdaptiveRuleProbModel;
import compression.samplegrammars.model.RuleProbModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomGrammarTest {
    @Test
    public void testRandomExplorer(){
        //random grammar
        testRandomGrammar(7, 10, 2);

        testRandomGrammar(38, 10, 4);

    }

    private static void testRandomGrammar(int seed, int nRules, int nNonterminals) {
        RandomGrammarExplorer rge = new RandomGrammarExplorer(nNonterminals);

        Random rd = new Random(seed);
        SecondaryStructureGrammar ssg=rge.randomGrammar(rd, nRules);
        RNAGrammar rnagrammar = RNAGrammar.from(ssg,true);


        //compression using random grammar with adaptive rule
        RuleProbModel adaptiveRuleProbModel = new AdaptiveRuleProbModel(rnagrammar);
        ArithmeticEncoder arithmeticEncoder = new ExactArithmeticEncoder();

        GenericRNAEncoderForPrecision encoder =
                new GenericRNAEncoderForPrecision(
                        adaptiveRuleProbModel, arithmeticEncoder,
                        rnagrammar, rnagrammar.getStartSymbol());

        //small rna for the test
        RNAWithStructure rna = new RNAWithStructure("cagug", "((.))");
        String encodedBitsAdaptive = encoder.encodeRNA(rna);


        //decompression
        adaptiveRuleProbModel = new AdaptiveRuleProbModel(rnagrammar);
        ArithmeticDecoder arithmeticDecoder = new ExactArithmeticDecoder(encodedBitsAdaptive);

        GenericRNADecoder decoder =
                new GenericRNADecoder(
                        adaptiveRuleProbModel, arithmeticDecoder,
                        rnagrammar, rnagrammar.getStartSymbol());

        RNAWithStructure decoded = decoder.decode();

        Assert.assertEquals(rna,decoded);
    }
}
