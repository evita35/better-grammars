import compression.grammar.*;
import compression.parser.CYKParser;
//import junit.framework.Assert;
import compression.parser.GrammarReaderNWriter;
import junit.framework.TestCase;
//import org.testng.annotations.Test;
//import junit.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrammarReaderNWriterTest extends TestCase {

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
    public void testGrammarReadWriteFunctions() throws IOException {
        NonTerminal A0 = new NonTerminal("A0");
        NonTerminal A1 = new NonTerminal("A1");

        CharTerminal OP = new CharTerminal('(');
        CharTerminal CL = new CharTerminal(')');
        CharTerminal DT = new CharTerminal('.');


        Grammar.Builder<Character> s= new Grammar.Builder<Character>("simpleGrammar1",A0)
                .addRule(A0,DT)
                .addRule(A0, A0, A0)
                .addRule(A0, OP,A1,CL)
                .addRule(A1, OP,A1,CL)
                .addRule(A1, A0)
                ;


        Grammar<Character> simpleGrammar1= s.build();
        Grammar<Character> simpleGrammar2=s.build();

        GrammarReaderNWriter newGrammar = new GrammarReaderNWriter("simpleGrammar1");
        newGrammar.writeGrammarToFile(SecondaryStructureGrammar.from(simpleGrammar1));
        System.out.println(newGrammar.getGrammarFromFile().toString());
        System.out.println(simpleGrammar1.toString());
        //assertEquals(simpleGrammar1.toString(),simpleGrammar2.toString());
        assertEquals(simpleGrammar1.toString(),newGrammar.getGrammarFromFile().toString());
        }

}
