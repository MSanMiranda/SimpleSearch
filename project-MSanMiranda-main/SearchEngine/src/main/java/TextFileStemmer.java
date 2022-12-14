import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 *
 * @see TextParser
 */
public class TextFileStemmer {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Stems line per file, put into container
	 * 
	 * @param line      the line to stem
	 * @param stemmer   the stemmer to use
	 * @param container data structure to store stemmed words
	 */
	public static void stemLine(String line, Stemmer stemmer, Collection<String> container) {
		for (String word : TextParser.parse(line)) {
			word = stemmer.stem(word).toString();
			container.add(word);
		}
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line
	 * using the default stemmer.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 */
	public static ArrayList<String> listStems(String line) {
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		ArrayList<String> list = new ArrayList<String>();
		stemLine(line, stemmer, list);
		return list;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(Path inputFile) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		String line;

		try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
			while ((line = reader.readLine()) != null) {
				stemLine(line, stemmer, list);
			}
		}
		return list;
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line using the default stemmer.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 */
	public static TreeSet<String> uniqueStems(String line) {
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		TreeSet<String> list = new TreeSet<String>();
		stemLine(line, stemmer, list);
		return list;

	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
		TreeSet<String> list = new TreeSet<String>();
		SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
		String line;

		try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
			while ((line = reader.readLine()) != null) {
				stemLine(line, stemmer, list);
			}
		}
		return list;
	}

}
