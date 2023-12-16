package compression.data;

import compression.grammar.RNAWithStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

/**
 * @author Sebastian Wild (wild@liverpool.ac.uk)
 */
public class CachedDataset implements Dataset {
	private final Dataset dataset;
	private final List<RNAWithStructure> rnas;

	public CachedDataset(final Dataset dataset) {
		this(dataset, null);
	}

	public CachedDataset(final Dataset dataset, Comparator<RNAWithStructure> sortBy) {
		this.dataset = dataset;
		this.rnas = new ArrayList<>(dataset.getSize());
		for (RNAWithStructure rna : dataset)
			rnas.add(rna);
		if (sortBy != null) rnas.sort(sortBy);
//		System.out.println("rnas = " + rnas);
	}

	public List<RNAWithStructure> getRNAs() {
		return Collections.unmodifiableList(rnas);
	}

	@Override
	public String getName() {
		return dataset.getName();
	}

	@Override
	public int getSize() {
		return dataset.getSize();
	}

	@Override
	public Iterator<RNAWithStructure> iterator() {
		return rnas.iterator();
	}

	@Override
	public Spliterator<RNAWithStructure> spliterator() {
		return rnas.spliterator();
	}

	@Override
	public String toString() {
		return "CachedDataset(" +
				"dataset=" + dataset + ')';
	}
}
