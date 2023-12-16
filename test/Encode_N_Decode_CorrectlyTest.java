/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import compression.GenericRNADecoder;
import compression.GenericRNAEncoder;
import compression.LocalConfig;
import compression.coding.ArithmeticEncoder;
import compression.coding.ExactArithmeticDecoder;
import compression.coding.ExactArithmeticEncoder;
import compression.data.Dataset;
import compression.data.FolderBasedDataset;
import compression.data.TrainingDataset;
import compression.grammar.Grammar;
import compression.grammar.PairOfChar;
import compression.grammar.RNAGrammar;
import compression.grammar.RNAWithStructure;
import compression.grammar.SecondaryStructureGrammar;
import compression.parser.GrammarReaderNWriter;
import compression.samplegrammars.DowellGrammar1Bound;
import compression.samplegrammars.SampleGrammar;
import compression.samplegrammars.model.AdaptiveRuleProbModel;
import compression.samplegrammars.model.RuleProbModel;
import compression.samplegrammars.model.SemiAdaptiveRuleProbModel;
import junit.framework.Assert;
import org.junit.Test;
//import org.testng.annotations.Test;
//import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Encode_N_Decode_CorrectlyTest {
    Dataset dataset = new FolderBasedDataset("TestDataSet");
    TrainingDataset trainingDataset = new TrainingDataset("TestTrainingData");
    boolean withNonCanonicalRules = true;
    boolean withHairpinLengthOne = true;
    List<SampleGrammar> listOfGrammars = Arrays.asList(
            new DowellGrammar1Bound(withNonCanonicalRules)
    );

    @Test
    public void testEncodeNDecode4AutoGenGrammars() throws IOException {
        List<SecondaryStructureGrammar> listOfGrammars = new ArrayList<>();
        String folderNameForGrammars= "grammars_first_filter";
        File grammarFiles = new File(LocalConfig.GIT_ROOT+"/src/GrammarGenerator/"+folderNameForGrammars);

        File[] listOfFiles = grammarFiles.listFiles();
        Random random = new Random(11124);
        for( int i =0; i<30; i++) {
            File randomGrammarFileSelection = listOfFiles[random.nextInt(listOfFiles.length)];
            RNAGrammar g = RNAGrammar.from(new GrammarReaderNWriter(randomGrammarFileSelection.getPath()).getGrammarFromFile(), false);

            //Grammar<PairOfChar> g = RNAGrammar.from(new GrammarReaderNWriter("C:/Users/evita/Documents/GitHub/compressed-rna/src/GrammarGenerator/grammars_first_filter/grammar-2NTs-3rules-1.txt").getGrammarFromFile(),false);


            System.out.println("GRAMMAR IS: " + g);
            //System.exit(0);
            Dataset dataset = new FolderBasedDataset("parsable");
            File RNAFiles = new File(LocalConfig.GIT_ROOT + "/datasets/parsable");
            File[] listOfRNAFiles = RNAFiles.listFiles();
            RNAWithStructure rnaws = FolderBasedDataset.readRNA(listOfRNAFiles[random.nextInt(listOfRNAFiles.length)]);
            //System.out.println(rnaws.secondaryStructure.length());
            //System.exit(0);
            //RNAWithStructure rnaws = FolderBasedDataset.readRNA(new File("C:/Users/evita/Documents/GitHub/compressed-rna/datasets/TestDataSet/testRna2.txt"));


            ExactArithmeticEncoder AE = new ExactArithmeticEncoder();
            RuleProbModel RPMAdaptive = new AdaptiveRuleProbModel(g);
            GenericRNAEncoder GRAdaptive = new GenericRNAEncoder(RPMAdaptive, AE, g, g.getStartSymbol());
            String encodedStringAdaptive = GRAdaptive.encodeRNA(rnaws);
            System.out.println(encodedStringAdaptive);
            //Decoding
            AdaptiveRuleProbModel RPMAdaptive4decode = new AdaptiveRuleProbModel(g);//reset probability model
            ExactArithmeticDecoder AD = new ExactArithmeticDecoder(encodedStringAdaptive);
            GenericRNADecoder GRAD = new GenericRNADecoder(RPMAdaptive4decode, AD, g, g.getStartSymbol());
            RNAWithStructure decoded = GRAD.decode();

            Assert.assertEquals(rnaws, decoded);
        }
    }

    public void testCorrectness() throws IOException {
        for (SampleGrammar grammar : listOfGrammars) {
            for (RNAWithStructure RNAWS : dataset) {
                SampleInstance4Tests SI4T = new SampleInstance4Tests(grammar);

                System.out.println(RNAWS);
                SI4T.runEncodeNDecodeStatic(RNAWS, trainingDataset);
                SI4T.runEncodeNDecode4Adaptive(RNAWS);
                SI4T.runEncodeNDecode4SemiAdaptive(RNAWS);
            }
        }
    }

}
