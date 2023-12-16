package compression.grammar;

import java.util.Objects;

/**
 * Non-terminal {@link Category}.
 * Just a symbol, so contains a name to distinguish it from other non-terminals.
 */
public class NonTerminal implements Category {
    @SuppressWarnings("WeakerAccess")
    public final String name;

    /**
     * Creates a new category <code>name</code>.
     *
     * @param name The name for this category.
     * @throws IllegalArgumentException If <code>name</code> is
     *                                  <code>null</code> or zero-length.
     */
    public NonTerminal(final String name) {
        if (Strings2.isNullOrEmpty(name)) throw new IllegalArgumentException("empty name specified for category");
        if (name.contains(" ")) throw new IllegalArgumentException("name contains spaces");
        this.name = name;
    }

    public static NonTerminal of(final String name) {
        return new NonTerminal(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NonTerminal that = (NonTerminal) o;

        return Objects.equals(name, that.name);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /**
     * Gets a string representation of this category.
     *
     * @return The value of this category's name.
     */
    @Override
    public String toString() {
        return (name.length() == 0) ? "<empty>" : name;
    }
}
