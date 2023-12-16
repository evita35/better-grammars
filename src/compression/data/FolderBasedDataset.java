package compression.data;

import compression.LocalConfig;
import compression.grammar.RNAWithStructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.Spliterator;

/**
 * @author Sebastian Wild (wild@uwaterloo.ca)
 */
public class FolderBasedDataset implements Dataset {

	public final String name;
	private File RNAFolder;

	/**
	 * @param name name of the dataset folder (subfolder in dataset folder)
	 */
	public FolderBasedDataset(final String name) {
		this.name = name;
		this.RNAFolder = new File(LocalConfig.GIT_ROOT + "/datasets/" + name);
//		System.out.println("rna folder is: "+ RNAFolder.toString());
//		System.out.println(RNAFolder.exists() +" "+ RNAFolder.isDirectory() +" "+ RNAFolder.listFiles()
//				+ RNAFolder.listFiles());
		if (!RNAFolder.exists() || !RNAFolder.isDirectory()
				|| RNAFolder.listFiles() == null
				|| RNAFolder.listFiles().length == 0) {
			throw new IllegalArgumentException("Dataset " + name + " is not a valid dataset folder.");
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public static RNAWithStructure readRNA(File file) throws IOException {
		BufferedReader rnaFile = new BufferedReader(new FileReader(file));
		//sample RNA string
		String RNA = rnaFile.readLine();//stores the primary sequence
		String DB = rnaFile.readLine();//stores the dot bracket string
		//System.out.println("RNA VALUE IS: " + RNA +" DB Value is :" + DB);
		//RNA = "GCU";
		//DB = "(.)";
		//System.out.println("RNA VALUE IS: " + RNA);
		// split string into list of tokens (terminals)
		return new RNAWithStructure(RNA, DB, file.getName());
	}

	public RNAWithStructure getRNA(String filename) throws IOException {
		return readRNA(new File(LocalConfig.GIT_ROOT + "/datasets/" + this.name + "/" + filename));
	}

	public void testFile() throws FileNotFoundException {
		System.out.println(new FileInputStream(RNAFolder));
	}

	@Override
	public int getSize() {
		return RNAFolder.listFiles().length;
	}

	@Override
	public Iterator<RNAWithStructure> iterator() {
		Iterator<File> iterator = Arrays.asList(RNAFolder.listFiles()).iterator();

		return new Iterator<RNAWithStructure>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public RNAWithStructure next() {
				try {
					return readRNA(iterator.next());
				} catch (IOException e) {
					throw new DatasetFileIOException(e);
				}
			}
		};
	}

	@Override
	public Spliterator<RNAWithStructure> spliterator() {
		return Arrays.stream(Objects.requireNonNull(RNAFolder.listFiles())).
				unordered().parallel().map(file-> {
			try {
				return readRNA(file);
			} catch (IOException e) {
				throw new DatasetFileIOException(e);
			}
		}).spliterator();
	}

	@Override
	public String toString() {
		return "Dataset(" +
				"name='" + name + '\'' +
				')';
	}
}
