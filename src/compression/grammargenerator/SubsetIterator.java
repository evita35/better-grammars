package compression.grammargenerator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over all size-k subset of [0..n).
 *
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class SubsetIterator implements Iterator<int[]> {

	/**
	 * Iterate over all size-k subset of [0..n).
	 */
	public SubsetIterator(int n, int k) {
		this.k = k;
		this.n = n;
		s = new int[k];
		for (int i = 0; i < k; i++)
			s[i] = i;
	}

	int[] s;
	int n, k;

	@Override
	public boolean hasNext() {
		return s != null;
	}

	private boolean isLastS() {
		for (int i = 0; i < k; i++) {
			if (s[i] < n - k + i)
				return true;
		}
		return false;
	}

	@Override
	public int[] next() {
		// find next subset
		// copy old subset to return
		int[] res = s.clone();
		if (!isLastS()) {
			s = null;
			return res;
		}
		int i;
		// find position of item that can be incremented
		i = k - 1;
		while (i >= 0 && s[i] == n - k + i) {
			i--;
		}
		if (i < 0) {
			throw new NoSuchElementException();
		}
		s[i]++;                    // increment this item
		for (++i; i < k; i++) {    // fill up remaining items
			s[i] = s[i - 1] + 1;
		}

		return res;
	}

//		// generate actual subset by index sequence
//		int[] getSubset(int[] input, int[] subset) {
//		    int[] result = new int[subset.length];
//		    for (int i = 0; i < subset.length; i++)
//		        result[i] = input[subset[i]];
//		    return result;
//		}

	public static void main(String[] args) {
		SubsetIterator it = new SubsetIterator(5, 3);
		while (it.hasNext()) {
			int[] s = it.next();
			System.out.println(java.util.Arrays.toString(s));
		}
	}

}
