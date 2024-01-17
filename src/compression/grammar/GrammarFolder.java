package compression.grammar;

import compression.LocalConfig;
import compression.data.DatasetFileIOException;
import compression.parser.GrammarReaderNWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class GrammarFolder implements Iterable<SecondaryStructureGrammar> {
    public final String folderName;
    private final File grammarFolder;

    public GrammarFolder(String name){
        folderName=name;
        this.grammarFolder = new File(LocalConfig.GIT_ROOT + "/grammars/" + name);
//		System.out.println("rna folder is: "+ RNAFolder.toString());
//		System.out.println(RNAFolder.exists() +" "+ RNAFolder.isDirectory() +" "+ RNAFolder.listFiles()
//				+ RNAFolder.listFiles());
        if (!grammarFolder.exists() || !grammarFolder.isDirectory()
                || grammarFolder.listFiles() == null
                || grammarFolder.listFiles().length == 0) {
            throw new IllegalArgumentException("Grammar folder " + name + " is not a valid grammar folder.");
        }
    }

    public File getFolder(){
        return grammarFolder;
    }

    @Override
    public Iterator<SecondaryStructureGrammar> iterator(){
        Iterator<File> iterator = Arrays.asList(grammarFolder.listFiles()).iterator();

        return new Iterator<SecondaryStructureGrammar>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public SecondaryStructureGrammar next() {
                try {
                    File file = iterator.next();

                    return new GrammarReaderNWriter(file.getPath()).getGrammarFromFile();
                } catch (IOException e) {
                    throw new DatasetFileIOException(e);
                }
            }
        };
    }

    @Override
    public String toString() {
        return "Grammar Folder(" +
                "name='" + folderName + '\'' +
                ')';
    }


}
