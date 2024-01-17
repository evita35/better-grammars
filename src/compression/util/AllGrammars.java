package compression.util;

import compression.grammar.SecondaryStructureGrammar;
import compression.samplegrammars.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllGrammars {

    public static Map<String, SampleGrammar> allGrammars(boolean withNonCanonicalRules) {

        Map<String, SampleGrammar> allGrammars = new TreeMap<>();

        allGrammars.put("G1B", new DowellGrammar1Bound(withNonCanonicalRules));
        allGrammars.put("G2B", new DowellGrammar2Bound(withNonCanonicalRules));
        allGrammars.put("G3B", new DowellGrammar3Bound(withNonCanonicalRules));
        allGrammars.put("G4B", new DowellGrammar4Bound(withNonCanonicalRules));
        allGrammars.put("G5B", new DowellGrammar5Bound(withNonCanonicalRules));
        allGrammars.put("G6B", new DowellGrammar6Bound(withNonCanonicalRules));
        allGrammars.put("G7B", new DowellGrammar7ModifiedNBound(withNonCanonicalRules));
        allGrammars.put("G8B", new DowellGrammar8ModifiedNBound(withNonCanonicalRules));
        allGrammars.put("LiuGrammar", new LiuGrammar(withNonCanonicalRules));
        allGrammars.put("SchulzGrammar", new SchulzGrammar(withNonCanonicalRules));
        return allGrammars;
    }

    public static List<SampleGrammar> getGrammarsFromCmdLine(String grammars, final boolean withNonCanonicalRules) {
        Map<String, SampleGrammar> allGrammars = allGrammars(withNonCanonicalRules);
        List<SampleGrammar> listOfGrammars = new ArrayList<>();
        if ("ALL".equalsIgnoreCase(grammars)) {
            listOfGrammars.addAll(allGrammars.values());
        } else {
            String[] names = grammars.split(",");
            for (final String name : names) {
                if (allGrammars.containsKey(name)) {
                    listOfGrammars.add(allGrammars.get(name));
                } else {
                    System.err.println("Don't know grammar " + name);
                }
            }
        }

        if (listOfGrammars.isEmpty()) {
            System.out.println("No valid grammars given");
            System.exit(97);
        }
        return listOfGrammars;
    }

    public static Collection<String> allGrammarNames() {
        return allGrammars(false).keySet();
    }


}
