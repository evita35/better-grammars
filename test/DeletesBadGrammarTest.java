import compression.data.Dataset;
import compression.data.FolderBasedDataset;
import compression.grammar.GrammarFolder;
import compression.grammar.RNAGrammar;
import compression.grammar.Rule;
import compression.grammar.SecondaryStructureGrammar;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static compression.util.DeletesBadGrammars.getFile;


public class DeletesBadGrammarTest extends TestCase {
        Dataset dataset = new FolderBasedDataset("TestDataSet");
        boolean withNonCanonicalRules = Boolean.parseBoolean("true");
        boolean withHairpinLengthOne = Boolean.parseBoolean("true");
        GrammarFolder grammarFolder= new GrammarFolder("Grammars_To_Test_Delete");


    @Test
    public void testDelete() throws IOException {
        //add a bad grammar to the file
        BufferedWriter bf = new BufferedWriter(new FileWriter(new File(grammarFolder.getFolder().getAbsolutePath()+"/"+"badGrammar.txt")));
        bf.write("GrammarName\nbadGrammar\nStart Symbol\nA0\nNon-Terminals\nA1\nA0\n\nTerminals\n(\n)\n.\n\nRules\n\nA0 -> ( A0 )\nA0 -> A0 A1\nA0 -> .");
        bf.close();
        //System.exit(0);
        File[] namesOfGrammars = grammarFolder.getFolder().listFiles();

        int badGrammarCounter=0;
        ArrayList<File> namesOfGrammarsList= new ArrayList<File>(Arrays.asList(grammarFolder.getFolder().listFiles()));
            for (SecondaryStructureGrammar ssg : grammarFolder) {
            RNAGrammar G = RNAGrammar.from(ssg,withNonCanonicalRules);

            System.out.println(G.name);

            System.out.println("Grammar is: " + G.getAllRules());

            // System.gc(); // Added to avoid seeing heap grow huge
            try {
                G.computeRuleCounts(dataset);
            }
            catch (RuntimeException r) {
                badGrammarCounter++;
                File file_to_delete=getFile(grammarFolder, G);
                System.out.println("File to delete is:"+ file_to_delete.getName());
                file_to_delete.delete();
                namesOfGrammarsList.remove(file_to_delete);

                //throw new RuntimeException("Grammar: "+G.name+" has parsing issues");
                continue;
            } catch (IOException e) {
                throw new IOException(e);
            }

                System.out.println("Bad grammar counter is:"+ badGrammarCounter);
        }
        File[] namesOfGrammars2 = grammarFolder.getFolder().listFiles();

        ArrayList<File> namesOfGrammarsList2= new ArrayList<File>(Arrays.asList(grammarFolder.getFolder().listFiles()));

        assertEquals(namesOfGrammarsList,namesOfGrammarsList2);
    }



    }
