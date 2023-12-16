package compression.grammar;

import java.util.Objects;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public final class IgnoringSecondPartPairOfCharTerminal implements Terminal<IgnoringSecondPartPairOfChar> {
	private final IgnoringSecondPartPairOfChar chars;

	public IgnoringSecondPartPairOfCharTerminal(final IgnoringSecondPartPairOfChar chars) {
		this.chars = chars;
	}

	public IgnoringSecondPartPairOfChar getChars() {
		return chars;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final IgnoringSecondPartPairOfCharTerminal that = (IgnoringSecondPartPairOfCharTerminal) o;
		return chars.equals(that.chars);
	}

	@Override
	public int hashCode() {
		return Objects.hash(chars);
	}

	@Override
	public String toString() {
		return chars.toString();
	}


}
