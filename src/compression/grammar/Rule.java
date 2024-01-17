
package compression.grammar;




import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;


/**
 * Represents a production rule in a {@link Grammar context-free grammar}.
 *
 * Rules contain a single {@link Category category} on the
 * {@link Rule#getLeft() left side} that produces the series of category on
 * the {@link Rule#getRight() right side}.
 *
 * Rules are immutable and cannot be changed once instantiated.
 *
 * @see Category
 * @see Grammar
 */
public class Rule {

    public final NonTerminal left;
    public final Category[] right;



    /**
     * Creates a new rule with the specified left side category and series of
     * category on the right side.
     *
     * @param left                         The left side (trigger) for this production rule.
     * @param right                        The right side (productions) licensed for this rule's
     *                                     left side.
     * @throws IllegalArgumentException If
     *                                  <ol>
     *                                  <li>the specified left or right category are <code>null</code>,</li>
     *                                  <li>the right series is zero-length,</li>
     *                                  <li>the right side contains a <code>null</code> category.</li>
     *                                  </ol>
     */
    public Rule(final NonTerminal left, final Category... right) {

        if (left == null) throw new IllegalArgumentException("empty left category");
        if (right == null || right.length == 0) throw new IllegalArgumentException("no right category");

        // check for nulls on right
        for (final Category r : right)
            if (r == null) throw new IllegalArgumentException(
                    "right contains null category: " + Arrays.toString(right));


        this.left = left;
        this.right = right.clone();

    }

    /**
     * Defaults to rule probability 1.0
     *
     * @param LHS      LHS
     * @param RHS      RHS
     * @return Rule with p=1.0
     */
    public static Rule create(final NonTerminal LHS, final Category... RHS) {
        return new Rule( LHS, RHS);
    }


    /**
     * Gets the left side category of this rule.
     */
    public NonTerminal getLeft() {
        return left;
    }

    /**
     * Gets the series of category on the right side of this rule.
     */
    public Category[] getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return left.equals(rule.left) && Arrays.equals(right, rule.right);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(left);
        result = 31 * result + Arrays.hashCode(right);
        return result;
    }


    /**
     * Gets a string representation of this rule.
     *
     * @return &quot;<code>S → NP VP</code>&quot; for a rule with a left side
     * category of <code>S</code> and a right side sequence
     * <code>[NP, VP]</code>.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(left.toString());
        sb.append(" →");

        for (final Category aRight : right) {
            sb.append(' '); // space between category
            sb.append(aRight.toString());
        }


        return sb.toString();
    }



}
