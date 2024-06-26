/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compression.grammar;

import java.util.Objects;

/**
 * value-semantics class for pairs of characters
 */
public final class PairOfChar {
    private final char pry;//character for primary sequence
    private final char sec;//character for secondary structure string

    public PairOfChar(final char pry, final char sec) {
        this.pry = pry;
        this.sec = sec;
    }

    public char getPry() {
        return pry;
    }

    public char getSec() {
        return sec;
    }

    public PairOfCharTerminal asTerminal() {
        return new PairOfCharTerminal(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PairOfChar that = (PairOfChar) o;
        return Character.toLowerCase(pry) == Character.toLowerCase(that.pry) && sec == that.sec;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pry, sec);
    }
    @Override
    public String toString()
    {
        return "<"+pry+"|"+sec+">";
    }

}
