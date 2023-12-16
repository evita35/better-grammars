package compression.data;

import compression.grammar.RNAWithStructure;

import java.util.Iterator;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public interface Dataset extends Iterable<RNAWithStructure> {
	int getSize();

	String getName();

}
