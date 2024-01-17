/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

import compression.coding.ArithmeticDecoder;
import compression.coding.Interval;
import compression.grammar.*;
import compression.samplegrammars.model.RuleProbModel;
import compression.grammar.Category;
import compression.grammar.NonTerminal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class implementing our decoder for RNA with structure.
 */
public class GenericRNADecoder {

    private final ArithmeticDecoder acDecoder;
    private final RuleProbModel model;

	private final NonTerminal startSymbol;




    public GenericRNADecoder(RuleProbModel model, ArithmeticDecoder acDecoder, NonTerminal startSymbol) {
        this.acDecoder = acDecoder;
        this.model = model;
	    this.startSymbol= startSymbol;
    }

    /** decode from given ArithmeticDecoder */
    public RNAWithStructure decode() {
        final List<Category> leftmostDerivation = new LinkedList<>();
        NonTerminal leftmostNT = startSymbol;
        leftmostDerivation.add(leftmostNT);
        while (leftmostNT != null) {
            final List<Interval> options = model.getIntervalList(leftmostNT);
            Interval interval = acDecoder.decodeNext(options);
            List<Category> rhs = model.getRhsFor(interval, leftmostNT);
            leftmostNT = replaceFirstNonterminal(leftmostDerivation, rhs);
        }
        return getRNAString(decodeCategoryList(leftmostDerivation));
    }


    /**
     * replace the first nonterminal in the derivation with rhs and
     * return the new leftmost nonterminal in the derivation.
     * */
    private static NonTerminal replaceFirstNonterminal(final List<Category> leftmostDerivation, List<Category> rhs) {
	    for (ListIterator<Category> iterator = leftmostDerivation.listIterator(); iterator.hasNext(); ) {
		    final Category cat = iterator.next();
		    if (Category.isNonTerminal(cat)) {
                // replace the nonterminal with the rhs
                iterator.remove();
			    for (Category category : rhs) iterator.add(category);
			    // find next nonterminal, can be either in rhs or in rest of derivation
                for (Category category : rhs) iterator.previous(); // backtrack
                while (iterator.hasNext()) {
                    final Category nextCat = iterator.next();
                    if (Category.isNonTerminal(nextCat)) {
                        return (NonTerminal) nextCat;
                    }
                }
		    }
	    }
        return null; // no more nonterminals
    }

    private ArrayList<PairOfChar> decodeCategoryList(List<Category> catList) {
        ArrayList<PairOfChar> pairOfCharList = new ArrayList<>();
        for (Category cat : catList) {
            if (!Category.isTerminal(cat)) throw new IllegalArgumentException("only terminals allowed here");
            pairOfCharList.add(((PairOfCharTerminal) cat).getChars());
        }
        return pairOfCharList;
    }

    public RNAWithStructure getRNAString(ArrayList<PairOfChar> POCList) {
        StringBuilder primary = new StringBuilder(POCList.size()),
                secondary = new StringBuilder(POCList.size());
        for (PairOfChar POC : POCList) {
            primary.append(POC.getPry());
            secondary.append(POC.getSec());
        }
        return new RNAWithStructure(primary.toString(), secondary.toString());
    }

    public static void main(String[] args) {
        // Test inserting via ListIterator
        List<String> list = new LinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        ListIterator<String> iterator = list.listIterator();
        iterator.next();
        iterator.remove();
        iterator.add("d");
        iterator.add("e");
        iterator.previous();
        iterator.previous();
        while (iterator.hasNext())
            System.out.println("iterator.next() = " + iterator.next());
        System.out.println("list = " + list);
    }

}
