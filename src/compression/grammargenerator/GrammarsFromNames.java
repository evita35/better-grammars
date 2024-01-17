package compression.grammargenerator;

import compression.LocalConfig;
import compression.grammar.SecondaryStructureGrammar;
import compression.parser.GrammarReaderNWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class GrammarsFromNames {
	public static void main(String[] args) throws IOException {
		System.out.println(fromRandomExplorer("RandomGrammar_3_9_110000001010110100000000000000000010010000"));
		System.out.println(fromExhaustiveExplorer("RandomGrammar_3_0+1+8+10+12+13+15+34+37"));
//		System.out.println(fromExhaustiveGenerator("grammar-3NTs-6rules-531024"));
		goodGrammars();
	}

	private static void goodGrammars() throws IOException {
		String[] goodGrammars = {
				"RandomGrammar_3_9_110000001000010100000000000000000010010000",
				"RandomGrammar_3_9_110000001010110100000000000000000010010000",
				"RandomGrammar_3_9_110000001000010100000000001000000010010000",
				"RandomGrammar_3_9_110000001000010100000000000001000010010000",
				"RandomGrammar_5_10_000000000010000000000000010000010100000000000000100000000100000000000000100100000000000000000000000000000000000000000000000000000001000000000000001000000000000000000",
				"RandomGrammar_5_10_100000000000000000000000010000000000000000000000000000000000000000000000000000001000000000000000100000100000000000000000000010000000000000001100000000000100000100000",
				"RandomGrammar_5_10_110000000000000010000000100000000000000000000000100000000000000000000000000000000000000000000000110000000000000000000000000000000000000000000000000000000100100000010",
				"RandomGrammar_6_10_000000000000000000000000000000000000100000010100000000000000000000000000000000000000000000000000000000000000000000000100001000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000100000000000001001000000000000000000000000000010000000000000",
				"RandomGrammar_6_10_000000000000000000000000000000000000100000000000000000000000000000000100000000000000000101000000000000000000000000000000000000000000000000000000000000100000000010000000000000000000000000100000000000000000000000000000000000000001000000000000000001000001000000000000000000000",
				"RandomGrammar_6_10_000000000000000000000000000000000000100000000000000000000000000000000000000000000100000000010000000000000000000000000000000000000000101000000000000000000000000000000000000000000000000000000000000000000000100000000001000000000001000000000000000000000000000000010000000000000"
		};
		for (String name : goodGrammars) {
			SecondaryStructureGrammar G = fromRandomExplorer(name);
			GrammarReaderNWriter readerNWriter = new GrammarReaderNWriter(new File(LocalConfig.GIT_ROOT + "/grammars/random-good/"), G);
			readerNWriter.writeGrammarToFile(G);
		}

	}

	public static SecondaryStructureGrammar fromExhaustiveGenerator(final String name) {
		// find 3 numbers in name
		Matcher matcher = Pattern.compile("(\\d+)").matcher(name);
		matcher.find();
		int nNTs = Integer.parseInt(matcher.group());
		matcher.find();
		int nRules = Integer.parseInt(matcher.group());
		matcher.find();
		int index = Integer.parseInt(matcher.group());

		throw new UnsupportedOperationException("not implemented yet");
	}

	public static SecondaryStructureGrammar fromExhaustiveExplorer(final String name) {
		String[] parts = name.split("_");
		int nNTs = Integer.parseInt(parts[1]);
		String s = parts[2];
		ExhaustiveGrammarExplorer e = new ExhaustiveGrammarExplorer(nNTs);
		// +-separated list to list of indices
		String[] indicesString = s.split("\\+");
		int[] indices = new int[indicesString.length];
		for (int i = 0; i < indicesString.length; i++) {
			indices[i] = Integer.parseInt(indicesString[i]);
		}
		return e.grammarFor(indices, name);
	}

	public static SecondaryStructureGrammar fromRandomExplorer(final String name) {
		String[] parts = name.split("_");
		int nNTs = Integer.parseInt(parts[1]);
//		int nRules = Integer.parseInt(parts[2]);
		String s = parts[3];
		int nRules = 0;
		ExhaustiveGrammarExplorer e = new ExhaustiveGrammarExplorer(nNTs);
		// convert s into list of bits
		boolean[] bits = new boolean[s.length()];
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '1') {
				bits[i] = true;
				++nRules;
			}
		}
		// convert bitsstring to list of indices
		int[] indices = new int[nRules];
		int j = 0;
		for (int i = 0; i < bits.length; i++) {
			if (bits[i]) {
				indices[j++] = i;
			}
		}
		String newName = "RandomGrammar_" + nNTs + "_" +
				Arrays.stream(indices).mapToObj(Integer::toString).reduce((a, b) -> a + "+" + b).get();
		return e.grammarFor(indices, newName);
	}
}
