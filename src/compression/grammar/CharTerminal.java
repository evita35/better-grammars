package compression.grammar;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public final class CharTerminal implements Terminal<Character> {
	private final char c;

	public CharTerminal(final char c) {
		this.c = c;
	}

	public Character getChars() {
		return c;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof CharTerminal)) return false;
		final CharTerminal that = (CharTerminal) o;
		return c == that.c;
	}

	@Override
	public int hashCode() {
		return Character.hashCode(c);
	}

	@Override
	public String toString() {
		return Character.toString(c);
	}
}
