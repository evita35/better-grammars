package compression.grammar;

import compression.LocalConfig;
import compression.util.Folder;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class RuleProbsFolder implements Folder {
    String folderName;
    File ruleProbsFolder;
    public RuleProbsFolder(String name){
        folderName=name;
        this.ruleProbsFolder = new File(LocalConfig.GIT_ROOT + "/staticmodel/" + name);
//		System.out.println("rna folder is: "+ RNAFolder.toString());
//		System.out.println(RNAFolder.exists() +" "+ RNAFolder.isDirectory() +" "+ RNAFolder.listFiles()
//				+ RNAFolder.listFiles());
        if (!ruleProbsFolder.exists() || !ruleProbsFolder.isDirectory()
                || ruleProbsFolder.listFiles() == null
                || ruleProbsFolder.listFiles().length == 0) {
            throw new IllegalArgumentException("Rule probability folder " + name + " is not a valid ruleprob folder.");
        }
    }
    public String getFolderName(){
        return folderName;
    }


    public Iterator<File> iterator() {
        Iterator<File> iterator = Arrays.asList(ruleProbsFolder.listFiles()).iterator();
        return iterator;
    }

    @Override
    public String toString() {
        return "Rule Probabilities(" +
                "name='" + folderName + '\'' +
                ')';
    }
}
