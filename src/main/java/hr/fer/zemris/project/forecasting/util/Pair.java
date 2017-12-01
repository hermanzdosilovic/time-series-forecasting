package hr.fer.zemris.project.forecasting.util;

/**
 * Represents a pair of two generic types.
 *
 * @author Herman Zvonimir Dosilovic
 */
public class Pair<F, S> {

    /**
     * First value in pair.
     */
    private F first;

    /**
     * Second value in pair.
     */
    private S second;

    /**
     * Empty default constructor.
     */
    public Pair() {
    }

    /**
     * Creates <code>Pair</code> with two members.
     *
     * @param first  - first member of pair
     * @param second - second member of pair
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns first member of pair.
     *
     * @return first member of pair
     */
    public F getFirst() {
        return first;
    }

    /**
     * Sets first member of pair.
     *
     * @param first - new value of first member
     */
    public void setFirst(F first) {
        this.first = first;
    }

    /**
     * Returns second member of pair.
     *
     * @return second member of pair
     */
    public S getSecond() {
        return second;
    }

    /**
     * Sets second member of pair.
     *
     * @param second - new value of second member
     */
    public void setSecond(S second) {
        this.second = second;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        return true;
    }

    @Override public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

