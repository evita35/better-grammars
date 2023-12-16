package compression.grammar;

/**
 * A category in a grammar, also known as a type.
 * Categories are the atomic sub-parts that make up
 * {@link Rule grammar rules}.
 *
 * Categories can either be <em>terminal</em> or <em>non-terminal</em>. A
 * terminal category is one from which no further category can be derived,
 * while non-terminal category can yield a series of other category when
 * they occur as the {@link Rule#getLeft() left-hand side} of a rule.
 *
 * Once created, category are immutable and have no <code>setXxx</code>
 * methods. This ensures that, once loaded in a grammar, a category will
 * remain as it was when created.
 *
 * @see Rule
 */
public interface Category {

    /**
     * Gets the terminal status of this category.
     *
     * @return The terminal status specified for this category upon
     * construction.
     */
    static boolean isTerminal(final Category c) {
        return c instanceof Terminal;
    }


    static boolean isNonTerminal(final Category c) {
        return c instanceof NonTerminal;
    }

    /**
     * Creates a new non-terminal category with the specified name.
     *
     */
    static NonTerminal nonTerminal(String name) {// modified  and add name as parameter
        return new NonTerminal(name);
    }


    /**
     * Returns the given category
     *
     */
    static <T> Terminal<T> terminal(final Terminal<T> terminal) {
        if (terminal == null)
            throw new Error("Can not instantiate category with null function. Did you mean to create a null category?");
        return terminal;
    }
}
