package compression.grammar;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class RNAGrammarTest extends TestCase {


	@Test
	public void testConversion() {
		NonTerminal S = new NonTerminal("S");
		NonTerminal Z = new NonTerminal("Z");
		Grammar.Builder<Character> GB = new Grammar.Builder<>("test", S);
		GB.addRule(S, new CharTerminal('.'));
		GB.addRule(S, new CharTerminal('('), Z, new CharTerminal(')'));
		GB.addRule(Z, S);
		Grammar<Character> GG = GB.build();
		SecondaryStructureGrammar G = SecondaryStructureGrammar.from(GG);

		System.out.println("G = " + G);
		Assert.assertTrue(SRFNormalForm.isSRFNormalForm(G));

		RNAGrammar RG = RNAGrammar.from(G, false);
		System.out.println("RG = " + RG);
		Assert.assertEquals(RG.getRules(Z).size(),1);
		Assert.assertEquals(RG.getRules(S).size(),10);

		RG = RNAGrammar.from(G, true);
		System.out.println("RG = " + RG);
		Assert.assertEquals(RG.getRules(Z).size(),1);
		Assert.assertEquals(RG.getRules(S).size(),20);
	}


}
