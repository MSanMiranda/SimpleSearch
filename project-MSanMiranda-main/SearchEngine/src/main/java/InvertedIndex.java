import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A data structure to store an indexes Unique Words with every appearance in
 * File and Indexes those Unique Files with every appearance of that word via
 * position
 *
 * @author Michael Miranda
 * @author University of San Francisco
 * @version Fall 2020
 */
public class InvertedIndex {

	/** Data Structure to hold Word to Path and Integer */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;

	/** Data Structure to hold individual counts */
	private final TreeMap<String, Integer> countMap;

	/** Basic constructor with no parameter */
	public InvertedIndex() {
		this.map = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.countMap = new TreeMap<String, Integer>();
	}

	/* Contains Function */

	/**
	 * Function to check if map has word recorded in inverted index
	 *
	 * @param word the word to check
	 * @return boolean if the word exists in Inverted Index
	 */
	public boolean containsWord(String word) {
		return map.containsKey(word);
	}

	/**
	 * Function to check if map has word recorded in count index
	 *
	 * @param location the location to check
	 * @return boolean if the location exists in CountIndex
	 */
	public boolean containsLocationCount(String location) {
		return countMap.containsKey(location);
	}

	/**
	 * Function to check if map has file recorded in inverted index
	 *
	 * @param word     the word to check
	 * @param location the location to check as a key inside word
	 * @return boolean if the location exists as a value for word
	 *
	 * @see #containsWord(String)
	 */
	public boolean containsLocation(String word, String location) {
		if (containsWord(word))
			return map.get(word).containsKey(location);
		return false;
	}

	/**
	 * Function to check if map has file recorded in inverted index
	 *
	 * @param word     the word to check
	 * @param location the location to check as a key inside word
	 * @param index    the index to check if word exists inside the location at that
	 *                 position
	 * @return boolean if the location exists as a value for word
	 *
	 * @see #containsWord(String)
	 * @see #containsLocation(String, String)
	 */
	public boolean containsIndex(String word, String location, Integer index) {
		if (containsLocation(word, location))
			return map.get(word).get(location).contains(index);
		return false;
	}

	/* Size-like functions */

	/**
	 * Function to return the total amount of locations referenced by a word
	 *
	 * @param word the word to check
	 * @return total amount of locations referenced by a word
	 *
	 * @see #containsWord(String)
	 */
	public int inWordLocations(String word) {
		if (containsWord(word))
			return map.get(word).size();
		return 0;
	}

	/**
	 * Function to return the total amount of indexs referenced by a word In a safe,
	 * not very time efficient manner
	 *
	 * @param word the word to check
	 * @return a total amount of indexs referenced by a word
	 *
	 * @see #containsWord(String)
	 * @see #inWordinLocationIndexes(String, String)
	 */
	public int inWordIndexes(String word) {
		if (containsWord(word)) {
			int i = 0;
			for (String location : map.get(word).keySet()) {
				i += inWordinLocationIndexes(word, location);
			}
			return i;
		}
		return 0;
	}

	/**
	 * Function to return the total amount of indexs referenced by a word at a
	 * location
	 *
	 * @param word     the word to check
	 * @param location the location where the word exists
	 * @return a total amount of indexs referenced by a word at a location
	 *
	 * @see #containsLocation(String, String)
	 */
	public int inWordinLocationIndexes(String word, String location) {
		if (containsLocation(word, location))
			return map.get(word).get(location).size();
		return 0;
	}

	/**
	 * Function to return the total amount of unique words inside inverted index
	 *
	 * @return a total amount of unique words inside the inverted index
	 */
	public int uniqueWords() {
		return map.size();
	}

	/* Getter-Like Functions */

	/**
	 * Function to return a set of words stored by the inverted index
	 *
	 * @return a set of words
	 */
	public Set<String> getWordSet() {
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * Function to return a set of locations given the word stored by the inverted
	 * index
	 *
	 * @param word the word to check
	 * @return a set of locations
	 *
	 * @see #containsWord(String)
	 */
	public Set<String> getLocationSet(String word) {
		if (containsWord(word))
			return Collections.unmodifiableSet(map.get(word).keySet());
		return null;
	}

	/**
	 * Function to return a set of indexs given the word and which location stored
	 * in the inverted index
	 *
	 * @param word     the word to check
	 * @param location the location where the word exists
	 * @return a set of indexs
	 *
	 * @see #containsLocation(String, String)
	 */
	public Set<Integer> getIndexSet(String word, String location) {
		if (containsLocation(word, location))
			return Collections.unmodifiableSet(map.get(word).get(location));
		return null;
	}

	/**
	 * Function to return an unmodifiable set of location referenced by countMap
	 *
	 * @return total amount of locations referenced by a CountIndex
	 */
	public Set<String> keySetCount() {
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * Function to get total number of words at location in countMap
	 *
	 * @param location the location to check
	 * @return total number of words at location
	 */
	public Integer getCount(String location) {
		if (containsLocationCount(location)) {
			return countMap.get(location);
		}
		return 0;
	}

	/* Data-Adding Functions */

	/**
	 * Function to record a word's location and index position
	 *
	 * @param word     the word to check
	 * @param location the location where the word is found
	 * @param index    the position inside of location where the word is found
	 */
	public void addIndex(String word, String location, Integer index) {
		map.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		map.get(word).putIfAbsent(location, new TreeSet<Integer>());
		map.get(word).get(location).add(index);

		if (!countMap.containsKey(location) || countMap.get(location) < index) {
			countMap.put(location, index);
		}
	}

	/**
	 * A function to combine 2 InvertedIndex data
	 * 
	 * @param other another InvertedIndex
	 */
	public void addAll(InvertedIndex other) {
		Set<String> words = other.map.keySet();
		Set<String> locations;
		for (String word : words) {
			if (!map.containsKey(word)) {
				this.map.put(word, other.map.get(word));
			} else {
				locations = other.map.get(word).keySet();
				for (String location : locations) {
					if (!map.get(word).containsKey(location)) {
						this.map.get(word).put(location, other.map.get(word).get(location));
					} else {
						this.map.get(word).get(location).addAll(other.map.get(word).get(location));
					}
				}
			}
		}
		locations = other.countMap.keySet();
		for (String location : locations) {
			if (!this.countMap.containsKey(location)) {
				this.countMap.put(location, other.countMap.get(location));
			} else if(this.countMap.get(location) < other.countMap.get(location)) {
				this.countMap.put(location, other.countMap.get(location));
			}
		}
	}

	/* JSon - String Function */

	/**
	 * Function that takes a path, attempts to open the file with BufferedWriter and
	 * writes a pretty-Json formatted String
	 *
	 * @param path the path to write a pretty-Json formated doubleNestedArray
	 * @throws IOException when an IOException occurs
	 */
	public void toJson(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.doubleNestedArray(map, writer, 0);
		}
	}

	/**
	 * Function that takes a path, attempts to open the file with BufferedWriter and
	 * writes a pretty-Json formatted String
	 *
	 * @param path the path to write a pretty-Json formated doubleNestedArray
	 * @throws IOException when an IOException occurs
	 *
	 */
	public void toCountJson(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.asObject(countMap, writer, 0);
		}
	}

	/* Search Functions */

	/**
	 * A Search that calls searchExact and searchPartial based off boolean.
	 * 
	 * @param queries the set of query words
	 * @param exact   the boolean value for partial or exact searching
	 * @return a list of search items
	 *
	 * @see #searchExact(HashMap, Set, List)
	 * @see #searchPartial(HashMap, Set, List)
	 */
	public List<SearchItem> search(Set<String> queries, boolean exact) {
		List<SearchItem> results = new ArrayList<SearchItem>();
		HashMap<String, SearchItem> lookup = new HashMap<String, SearchItem>();

		if (exact) {
			searchExact(lookup, queries, results);
		} else {
			searchPartial(lookup, queries, results);
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Function to perform an exact word lookup in the inverted index
	 *
	 * @param lookup  the HashMap to reference
	 * @param queries the set of query words
	 * @param results the result list to reference
	 *
	 * @see #searchCore(String, Set, HashMap, List)
	 */
	private void searchExact(HashMap<String, SearchItem> lookup, Set<String> queries, List<SearchItem> results) {
		Set<String> locationIndex;
		for (var word : queries) {
			if (map.containsKey(word)) {
				locationIndex = map.get(word).keySet();
				searchCore(word, locationIndex, lookup, results);
			}
		}
	}

	/**
	 * Function to perform an partial word lookup in the inverted index
	 *
	 * @param lookup  the HashMap to reference
	 * @param queries the set of query words
	 * @param results the result list to reference
	 *
	 * @see #searchCore(String, Set, HashMap, List)
	 */
	private void searchPartial(HashMap<String, SearchItem> lookup, Set<String> queries, List<SearchItem> results) {
		Set<String> locationIndex;
		for (var word : queries) {
			for (String indexWord : map.tailMap(word).keySet()) {
				if (!indexWord.startsWith(word)) {
					break;
				}
				locationIndex = map.get(indexWord).keySet();
				searchCore(indexWord, locationIndex, lookup, results);
			}
		}
	}

	/**
	 * Function to create and append SearchItems, adding it to HashMap for lookup
	 *
	 * @param word          the word used to create or append a SearchItem
	 * @param locationIndex the locations where the word was found
	 * @param lookup        a HashMap to store new SearchItems and append to older
	 *                      ones
	 * @param results       the result list to reference
	 *
	 */
	private void searchCore(String word, Set<String> locationIndex, HashMap<String, SearchItem> lookup,
			List<SearchItem> results) {

		for (var location : locationIndex) {
			if (!lookup.containsKey(location)) {
				var item = new SearchItem(location);
				lookup.put(location, item);
				results.add(item);
			}

			lookup.get(location).add(word);
		}
	}

	/**
	 * A data structure to store search results per word from query
	 *
	 * @author Michael Miranda
	 * @author University of San Francisco
	 * @version Fall 2020
	 */
	public class SearchItem implements Comparable<SearchItem> {

		/** The location where the word was found */
		private final String location;

		/** The amount of occurrence of word at location */
		private int amount;

		/** The percentage of words that matched */
		private double score;

		/** Dirty Bit for score calculation */
		private boolean calculated;

		/**
		 * Constructor
		 *
		 * @param location where the word was found
		 */
		public SearchItem(String location) {
			this.location = location;
			this.amount = 0;
			this.score = 0d;
			this.calculated = false;
		}

		/**
		 * Function to return location in SearchItem
		 * 
		 * @return String
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * Function to return amount in SearchItem
		 * 
		 * @return amount
		 */
		public int getAmount() {
			return amount;
		}

		/**
		 * Function to return score in SearchItem
		 * 
		 * @return score
		 */
		public double getScore() {
			if (!calculated) {
				findScore();
			}
			return score;
		}

		/**
		 * Function to add to count
		 * 
		 * @param word amount to add via InvertedIndex search
		 */
		private void add(String word) {
			calculated = false;
			amount += map.get(word).get(location).size();
		}

		/**
		 * Function to calculate the score of SearchItem
		 */
		private void findScore() {
			if (this.amount == 0) {
				this.score = 0d;
			} else {
				this.score = Double.valueOf(amount) / countMap.get(location);
			}
		}

		@Override
		public int compareTo(SearchItem item) {
			// Checking dirty bits
			if (this.calculated != true) {
				this.findScore();
			}
			if (item.calculated != true) {
				item.findScore();
			}

			int i = 0;
			i = Double.compare(item.score, this.score);
			if (i != 0) {
				return i;
			}

			i = Integer.compare(item.amount, this.amount);
			if (i != 0) {
				return i;
			}

			return this.location.compareTo(item.location);
		}

	}
	
		@Override
		public String toString() {
			return map.toString();
		}
}
