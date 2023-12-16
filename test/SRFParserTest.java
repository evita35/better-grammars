import compression.grammar.*;
import compression.parser.CYKParser;
import compression.parser.SRFParser;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class SRFParserTest extends TestCase {

        NonTerminal A0 = new NonTerminal("A0");
        NonTerminal A1 = new NonTerminal("A1");
        NonTerminal A2 = new NonTerminal("A2");
        NonTerminal A3 = new NonTerminal("A3");
        NonTerminal A4 = new NonTerminal("A4");


        CharTerminal OP = new CharTerminal('(');
        CharTerminal CL = new CharTerminal(')');
        CharTerminal DT = new CharTerminal('.');


        Grammar.Builder<Character> s= new Grammar.Builder<Character>("simpleGrammar",A0)
                .addRule(A0,DT)
                .addRule(A0, A0, A0)
                .addRule(A0, OP, A4, CL)
                .addRule(A1, DT)
                .addRule(A2, OP,A4,CL)
                .addRule(A3, DT)
                .addRule(A3,A1,A3)
                .addRule(A3, A2,A3)
                .addRule(A4, OP,A4,CL)
                .addRule(A4, A3)
                ;


        Grammar<Character> simpleGrammar= s.build();
        SRFParser<Character> testingSRFParser = new SRFParser<>(simpleGrammar,null);


        List<Terminal<Character>> word = new ArrayList<>(Arrays.asList(DT,OP,DT,CL));
        List<Terminal<Character>> word2 = new ArrayList<>(Arrays.asList(OP,OP,OP,DT,OP,DT,DT,DT,CL,OP,DT,DT,CL,CL,CL,CL,DT,DT,OP,DT,CL));




    @Test
    public void testsSRFParserLeftMostDerivation() throws Exception {
        List<Terminal<Character>> word = new ArrayList<>(Arrays.asList(DT,OP,DT,CL));
        assertEquals(testingSRFParser.mostLikelyLeftmostDerivationFor(word).toString(), "[A0 → A0 A0, A0 → ., A0 → ( A4 ), A4 → A3, A3 → .]");
    }

    @Test
    public void testSRFParserDerivationToWord() throws Exception {
        List<Terminal<Character>> word = new ArrayList<>(Arrays.asList(DT,OP,OP,DT,CL,CL,DT));
        SRFParser<Character> testingSRFParser2 = new SRFParser<>(simpleGrammar,null);
        System.out.println("1: "+word);
        System.out.println("2: "+testingSRFParser2.mostLikelyLeftmostDerivationFor(word));
        System.out.println("3: "+ testingSRFParser2.derivationToWord(testingSRFParser2.mostLikelyLeftmostDerivationFor(word)));
        assertEquals(testingSRFParser2.derivationToWord(testingSRFParser2.mostLikelyLeftmostDerivationFor(word)),word);
    }

    @Test
    public void testSRFParsable() throws Exception {
        List<Terminal<Character>> word3 = new ArrayList<>(Arrays.asList(OP,DT,CL));
        SRFParser<Character> testingSRFParser3 = new SRFParser<>(simpleGrammar,null);
        //System.out.println("3: "+ testingSRFParser3.parsable(word3));
        assertEquals((testingSRFParser3.parsable(word3)),true);
    }


}
