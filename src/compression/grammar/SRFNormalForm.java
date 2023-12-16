package compression.grammar;

import compression.parser.SRFParser;

import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class SRFNormalForm {

	public enum RuleType {
		/** type 1 Ai→ Aj Al */
		TYPE_I,
		/** type2 Ai → . */
		TYPE_II,
		/** type 3 Ai →(Aj) */
		TYPE_III,
		/** type4 Ai→ Aj  with  j < i */
		TYPE_IV,
	}


	public static <T> boolean isSRFNormalForm(final Grammar<T> g) {
		for (final Rule r : g.getAllRules()) {
			try {
				getRuleType(r);
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return type of rule if in SRF normal form, throws {@link IllegalArgumentException}
	 * otherwise.
	 * For type 4 rules, the order of the nonterminals is checked (alphabet by name).
	 */
	public static RuleType getRuleType(final Rule rule) {
		if (rule.right.length == 1) {
			if (Category.isTerminal(rule.right[0])) {
				return RuleType.TYPE_II;
			} else {
				NonTerminal lhs = rule.left;
				NonTerminal rhs = (NonTerminal) rule.right[0];
				if (rhs.toString().compareTo(lhs.toString()) < 0) {
					return RuleType.TYPE_IV;
				} else {
					throw new IllegalArgumentException("Rule " + rule + " is not in SRF form: wrong order of type 4 nonterminals.");
				}
			}
		} else if (rule.right.length == 2) {
			if (!Category.isTerminal(rule.right[0]) && !Category.isTerminal(rule.right[1])) {
				return RuleType.TYPE_I;
			} else
				throw new IllegalArgumentException("Rule " + rule + " is not in SRF form: length 2 rhs but not all nonterminals.");
		} else if (rule.right.length == 3) {
			if (Category.isTerminal(rule.right[0]) && !Category.isTerminal(rule.right[1]) && Category.isTerminal(rule.right[2])) {
				return RuleType.TYPE_III;
			} else
				throw new IllegalArgumentException("Rule " + rule + " is not in SRF form: length 3 rhs but not of type 3.");
		}
		throw new IllegalArgumentException("Error found in rule: " + rule + " rule is not in SRF form");
	}



}
