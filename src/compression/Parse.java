package compression;

import compression.grammar.CharTerminal;
import compression.grammar.PairOfChar;
import compression.grammar.RNAGrammar;
import compression.grammar.RNAWithStructure;
import compression.grammar.Rule;
import compression.grammar.SecondaryStructureGrammar;
import compression.grammar.Terminal;
import compression.grammargenerator.UnparsableException;
import compression.parser.GrammarReaderNWriter;
import compression.parser.SRFParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class Parse {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Parse grammar-file secondary-structure [primary-structure] [non-canonical-pairs]");
			System.out.println("  grammar-file:       the grammar file used to parse the RNA");
			System.out.println("  secondary-structure: the secondary structure of the RNA");
			System.out.println("  primary-structure:   the primary structure of the RNA; can be omitted, then only the secondary structure part is tested");
			System.out.println("  non-canonical-pairs: [true|false] whether to allow non-canonical pairs in the secondary structure; default is false");
			System.out.println();
			System.out.println("Example: Parse grammar.txt \"((.))\" \"ACGU\"");
			System.out.println("Example: Parse grammar.txt \"((.))\" \"ACGU\" true");
			System.out.println("Example: Parse grammar.txt \"((.))\"");
			System.out.println();
			System.exit(1);
		}
		// Parse args
		String grammarFile = args[0];
		String secondaryStructure = args[1];
		String primaryStructure = args.length > 2 ? args[2] : null;
		boolean allowNonCanonicalPairs = args.length > 3 ? Boolean.parseBoolean(args[3]) : false;
		// Parse grammar
		GrammarReaderNWriter grammarReader = new GrammarReaderNWriter(grammarFile);
		SecondaryStructureGrammar G = grammarReader.getGrammarFromFile();
		System.out.println("G = " + G);
		try {
			List<Rule> rules;
			if (primaryStructure == null) {
				// Convert secondary structure to list of token and parse
				SRFParser<Character> parser = new SRFParser<>(G);
				List<Terminal<Character>> word = secondaryStructure.chars().mapToObj(c -> new CharTerminal((char) c)).collect(Collectors.toList());
				rules = parser.leftmostDerivationFor(word);
			} else {
				// Convert secondary structure and primary structure to list of token and parse
				// Convert grammar to RNAGrammar
				RNAGrammar rnaGrammar = RNAGrammar.from(G, allowNonCanonicalPairs);
				RNAWithStructure rna = new RNAWithStructure(primaryStructure, secondaryStructure);
				List<Terminal<PairOfChar>> word = rna.asTerminals();
				SRFParser<PairOfChar> parser = new SRFParser<>(rnaGrammar);
				rules = parser.leftmostDerivationFor(word);
			}
			int i = 0;
			for (Rule rule : rules) {
				System.out.println("[" + i++ + "] " + rule);
			}
		} catch (UnparsableException e) {
			System.out.println("Unparsable: " + e);
			System.exit(1);
		}
	}

}
