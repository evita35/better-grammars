package compression.util;

import compression.data.Dataset;
import compression.data.FolderBasedDataset;
import compression.data.TrainingDataset;
import compression.grammar.GrammarFolder;
import compression.grammar.RNAGrammar;
import compression.grammar.SecondaryStructureGrammar;

import java.io.*;
import java.io.IOException;

public class DeletesBadGrammars {
    public static void main(String[] args) throws IOException {
        Dataset dataset = new FolderBasedDataset(args[0]);
        boolean withNonCanonicalRules = Boolean.parseBoolean(args[1]);
        boolean withHairpinLengthOne = Boolean.parseBoolean(args[2]);
        GrammarFolder grammarFolder= new GrammarFolder(args[3]);

        int badGrammarCounter=0;
        for (SecondaryStructureGrammar ssg : grammarFolder) {
            RNAGrammar G = RNAGrammar.from(ssg,withNonCanonicalRules);

            System.out.println(G.name);

            System.out.println("Grammar is: " + G.getAllRules());
            // System.gc(); // Added to avoid seeing heap grow huge
            try {
                        G.computeRuleCounts(dataset);
            }
             catch (RuntimeException r) {
                File file_to_delete=getFile(grammarFolder, G);
                System.out.println("File to delete is:"+ file_to_delete.getName());
                file_to_delete.delete();
                badGrammarCounter++;
                //throw new RuntimeException("Grammar: "+G.name+" has parsing issues");
                continue;
            }

            System.out.println("Displayed SUCCESSFULLY");
        }
        System.out.println("Number of bad grammars is: "+badGrammarCounter);
    }
    public static File getFile(GrammarFolder folder, RNAGrammar g){
        for(File file: folder.getFolder().listFiles()){
            if(file.getName().equals(g.getName()+".txt"))
                return file;
        }
        return null;
    }
}
