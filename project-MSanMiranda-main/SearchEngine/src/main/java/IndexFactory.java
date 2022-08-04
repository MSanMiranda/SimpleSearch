import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class used to construct InvertedIndex
 * 
 * @author Michael Miranda
 * @author University of San Francisco
 * @version Fall 2020
 */
public class IndexFactory {

	/** Stemmer algorithm */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/** A logger specifically for this class. */
	private static final Logger log = LogManager.getLogger(IndexFactory.class);
	
	/**
	 * Reads a large String line by line, parses each line into cleaned and stemmed words,
	 * and keeping track of where the words go.
	 *
	 * @param largeString the input String to parse
	 * @param map      the inverted index to write to
	 * @param url the location where the large String was generated
	 *
	 * @see InvertedIndex#addIndex(String, String, Integer)
	 */
	public static void filePopulate(String largeString, String url, InvertedIndex map) {
		int i = 1;
		ArrayList<String> set = TextFileStemmer.listStems(largeString);
		for (var word : set) {
			map.addIndex(word, url, i);
			i++;
		}
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and keeping track of where the words go.
	 *
	 * @param location the input file to parse
	 * @param map      the inverted index to write to
	 * @throws IOException if unable to read or parse file
	 *
	 * @see TextParser#parse(String)
	 * @see SnowballStemmer#stem(CharSequence)
	 * @see InvertedIndex#addIndex(String, String, Integer)
	 */
	public static void filePopulate(Path location, InvertedIndex map) throws IOException {
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		int i = 1;
		String line;

		try (BufferedReader reader = Files.newBufferedReader(location)) {
			while ((line = reader.readLine()) != null) {
				for (var word : TextParser.parse(line)) {
					map.addIndex(stemmer.stem(word).toString(), location.toString(), i);
					i++;
				}
			}
		}
	}

	/**
	 * A function to build and populate the InvertedIndex given a path, whether that
	 * path is a file or a directory.
	 *
	 * @param start the input file to parse
	 * @param map   the InvertedIndex to write to
	 * @throws IOException when an IOException occurs
	 *
	 * @see TextFileFinder#checkPath(Path)
	 * @see #filePopulate(Path, InvertedIndex)
	 */
	public static void build(Path start, InvertedIndex map) throws IOException {
		for (Path location : TextFileFinder.checkPath(start)) {
			log.trace(location);
			filePopulate(location, map);
		}
	}

}
