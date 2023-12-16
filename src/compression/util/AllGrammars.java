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

       // allGrammars.put("G1", new DowellGrammar1(withNonCanonicalRules));
        allGrammars.put("G1B", new DowellGrammar1Bound(withNonCanonicalRules));
       // allGrammars.put("G2", new DowellGrammar2(withNonCanonicalRules));
        allGrammars.put("G2B", new DowellGrammar2Bound(withNonCanonicalRules));
        //allGrammars.put("G3", new DowellGrammar3(withNonCanonicalRules));
        allGrammars.put("G3B", new DowellGrammar3Bound(withNonCanonicalRules));
        //allGrammars.put("G4", new DowellGrammar4(withNonCanonicalRules));
        allGrammars.put("G4B", new DowellGrammar4Bound(withNonCanonicalRules));
        //allGrammars.put("G5", new DowellGrammar5(withNonCanonicalRules));
        allGrammars.put("G5B", new DowellGrammar5Bound(withNonCanonicalRules));
        //allGrammars.put("G6", new DowellGrammar6(withNonCanonicalRules, withHairpinLengthOne));
        allGrammars.put("G6B", new DowellGrammar6Bound(withNonCanonicalRules));
        //allGrammars.put("G6BM", new DowellGrammar6BoundMirror(withNonCanonicalRules, withHairpinLengthOne));
        //allGrammars.put("G7", new DowellGrammar7Modified(withNonCanonicalRules));
        allGrammars.put("G7B", new DowellGrammar7ModifiedNBound(withNonCanonicalRules));
        //allGrammars.put("G8",new DowellGrammar8Modified(withNonCanonicalRules));
        allGrammars.put("G8B", new DowellGrammar8ModifiedNBound(withNonCanonicalRules));
        //allGrammars.put("AG", new ArbitraryGrammar(withNonCanonicalRules));
        //allGrammars.put("AGB", new ArbitraryGrammarBound(withNonCanonicalRules));
        allGrammars.put("LiuGrammar", new LiuGrammar(withNonCanonicalRules));
//        allGrammars.put("LiuGrammarB", new LiuGrammarBound(withNonCanonicalRules));
        allGrammars.put("SchulzGrammar", new SchulzGrammar(withNonCanonicalRules));
        //sALL_GRAMMARS.put("SchulzGrammarB", new SchulzGrammarBound(withNonCanonicalRules));
        return allGrammars;
    }

    public static List<SampleGrammar> getGrammarsFromCmdLine(String grammars, final boolean withNonCanonicalRules) {
        Map<String, SampleGrammar> allGrammars = allGrammars(withNonCanonicalRules);
        System.out.println(allGrammars);
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

        if (listOfGrammars.size() == 0) {
            System.out.println("No valid grammars given");
            System.exit(97);
        }
        return listOfGrammars;
    }

    public static Collection<String> allGrammarNames() {
        return allGrammars(false).keySet();
    }


    public static void main(String[] args) {

        for (SampleGrammar G : new ArrayList<>(allGrammars(true).values())) {
            System.out.println(G);
            System.out.println("nNTs = " + G.getGrammar().getNonTerminals().size());
            System.out.println("nRules = " + G.getGrammar().getAllRules().size());
        }
        List<SecondaryStructureGrammar> Gs=  new ArrayList<>();
        Gs.add(LiuGrammar.buildSecondaryStructureGrammar().convertToSRF());
        Gs.add(DowellGrammar1Bound.buildSecondaryStructureGrammar().convertToSRF());
        Gs.add(DowellGrammar3Bound.buildSecondaryStructureGrammar().convertToSRF());
        Gs.add(DowellGrammar4Bound.buildSecondaryStructureGrammar().convertToSRF());
        Gs.add(DowellGrammar5Bound.buildSecondaryStructureGrammar().convertToSRF());
        Gs.add(DowellGrammar6Bound.buildSecondaryStructureGrammar().convertToSRF());
        for (SecondaryStructureGrammar G : Gs) {
            System.out.println(G);
            System.out.println("nNTs = " + G.getNonTerminals().size());
            System.out.println("nRules = " + G.getAllRules().size());
        }
    }

}
