package compression.util;

import compression.grammar.SecondaryStructureGrammar;

import java.io.File;
import java.util.Iterator;

public interface GrammarIterator extends Iterable<SecondaryStructureGrammar>{
    public Iterator<SecondaryStructureGrammar> iterator();
}
