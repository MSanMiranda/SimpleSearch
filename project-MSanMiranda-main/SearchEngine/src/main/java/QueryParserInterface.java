import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;


/**
 * A data structure to store parsed query lines Sorted alphabetically without
 * duplicates, what locations they appear in along with their count
 *
 * @author Michael Miranda
 * @author University of San Francisco
 * @version Fall 2020
 */
public interface QueryParserInterface {
	
	/* Contains Function */
	
	/**
	 * Function to check if String exists in searchMap
	 * 
	 * @param word the word line to check
	 * @return a boolean if String exists in searchMap
	 */
	public boolean containsWord(String word);
	
	/* Size-like functions */

	/**
	 * Function to check the amount of Stings stored in searchMap
	 * 
	 * @return the size of searchMap
	 */
	public int wordCount();
	
	/**
	 * Function to return a copy of searchMap keys
	 * 
	 * @return unmodifiableSet of searchMap keys
	 */
	public Set<String> wordSet();
	
	/**
	 * Function to return a copy of searchMap value given key or null if key is not
	 * found
	 * 
	 * @param word the key in serachMap
	 * @return unmodifiableList of searchMap value
	 */
	public List<InvertedIndex.SearchItem> searchList(String word);
	
	/* Search Functions */

	/**
	 * Reads Query file line by line
	 * 
	 * @param location the location of query file
	 * @param exact    the boolean for partial/exact search
	 * @throws IOException when IOException occurs
	 * 
	 * @see #build(String, boolean)
	 */
	public default void build(Path location, boolean exact) throws IOException {
		String line;
		try (BufferedReader reader = Files.newBufferedReader(location)) {
			while ((line = reader.readLine()) != null) {
				build(line, exact);
			}
		}
	}
	
	/**
	 * Reads cleans and stems query lines into a set of query words, searches
	 * through the inverted index
	 *
	 * @param line  the query line to clean and stem
	 * @param exact the boolean for partial/exact search
	 *
	 * @see InvertedIndex#search(Set, boolean)
	 */
	public void build(String line, boolean exact);
	
	/* JSon - String Function */

	/**
	 * Opens a BufferedWriter to write the searchMap at currentWritePath
	 *
	 * @param currentWritePath the path to write
	 * @throws IOException when an IO exception occurs
	 */
	public void toJson(Path currentWritePath) throws IOException;

}
