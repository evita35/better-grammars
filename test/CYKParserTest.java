import compression.grammar.*;
import compression.parser.CYKParser;
//import junit.framework.Assert;
import junit.framework.TestCase;
//import org.testng.annotations.Test;
//import junit.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CYKParserTest extends TestCase {

    NonTerminal S = new NonTerminal("S");
    NonTerminal O = new NonTerminal("O");
    NonTerminal A = new NonTerminal("A");
    NonTerminal D = new NonTerminal("D");
    NonTerminal B = new NonTerminal("B");
    NonTerminal C = new NonTerminal("C");
    NonTerminal E = new NonTerminal("E");
    NonTerminal F = new NonTerminal("F");


    CharTerminal OP = new CharTerminal('(');
    CharTerminal CL = new CharTerminal(')');
    CharTerminal DT = new CharTerminal('.');

    Grammar.Builder<Character> s= new Grammar.Builder<Character>("simpleGrammar",S)
            .addRule(S, O, A)
            .addRule(S, O, D)
            .addRule(S, DT)
            .addRule(O, OP)
            .addRule(A, S, B)
            .addRule(B, C, S)
            .addRule(C, CL)
            .addRule(D, S, C)
            .addRule(S, F, S)
            .addRule(F, DT)
            ;



    Grammar<Character> simpleGrammar= s.build();
    CYKParser<Character> testingCYKParser = new CYKParser<>(simpleGrammar,null);

    @Test
    public void testCYKParserLeftMostDerivation(){
        List<Terminal<Character>> word = new ArrayList<>(Arrays.asList(DT,OP,DT,CL));
        assertEquals(testingCYKParser.mostLikelyLeftmostDerivationFor(word).toString(), "[S → F S, F → ., S → O D, O → (, D → S C, S → ., C → )]");
    }
    @Test
    public void testCYKParserDerivationToWord(){
        List<Terminal<Character>> word = new ArrayList<>(Arrays.asList(DT,OP,DT,CL));
        CYKParser<Character> testingCYKParser2 = new CYKParser<>(simpleGrammar,null);
        assertEquals(testingCYKParser2.derivationToWord(testingCYKParser2.mostLikelyLeftmostDerivationFor(word)),word);
    }

}
