import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A data structure to store an indexes Unique Words with every appearance in
 * File and Indexes those Unique Files with every appearance of that word via
 * position
 *
 * @author Michael Miranda
 * @author University of San Francisco
 * @version Fall 2020
 */
public class QueryParserMulti implements QueryParserInterface {

	/** Inverted index to reference to */
	private final InvertedIndexMulti index;

	/** Data Structure to hold SearchItems */
	private final TreeMap<String, List<InvertedIndex.SearchItem>> searchMap;

	/** WorkQueue to use */
	private final WorkQueue queue;

	/** A logger specifically for this class. */
	private static final Logger log = LogManager.getLogger(QueryParserMulti.class);

	/**
	 * Basic constructor with a reference to InvertedIndex
	 * 
	 * @param index the InvertedIndex to reference
	 * @param queue the workQueue to use
	 */
	public QueryParserMulti(InvertedIndexMulti index, WorkQueue queue) {
		this.index = index;
		this.searchMap = new TreeMap<String, List<InvertedIndex.SearchItem>>();
		this.queue = queue;
	}

	/* Contains Function */

	@Override
	public boolean containsWord(String word) {
		synchronized (searchMap) {
			return searchMap.containsKey(word);
		}
	}

	/* Size-like functions */
	@Override
	public int wordCount() {
		synchronized (searchMap) {
			return searchMap.size();
		}
	}

	/* Getter-like functions */

	@Override
	public Set<String> wordSet() {
		synchronized (searchMap) {
			return Collections.unmodifiableSet(searchMap.keySet());
		}
	}

	@Override
	public List<InvertedIndex.SearchItem> searchList(String word) {
		synchronized (searchMap) {
			if (containsWord(word))
				return Collections.unmodifiableList(searchMap.get(word));
			return null;
		}
	}

	/* Search Functions */

	@Override
	public void build(Path location, boolean exact) throws IOException {
		QueryParserInterface.super.build(location, exact);
		queue.finish();
	}

	@Override
	public void build(String line, boolean exact) {
		queue.execute(new ScanLine(line, exact));
	}

	/* JSon - String Function */

	@Override
	public void toJson(Path currentWritePath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(currentWritePath, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.asSearchItems(searchMap, writer, 0);
		}
	}

	/**
	 * Runnable function for a MultiThreaded building of Query Search, each instance
	 * reads a single line to search through an existing inverted index
	 * 
	 * @see Runnable
	 */
	private class ScanLine implements Runnable {

		/** The String to parse */
		private String line;

		/** Exact search flag */
		private boolean exact;

		/**
		 * Constructor with data to look at and data.
		 * 
		 * @param line  the string to parse
		 * @param exact the flag to use
		 */
		public ScanLine(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			log.debug("lineQuery.Run.Build");

			TreeSet<String> set = TextFileStemmer.uniqueStems(line);
			List<InvertedIndex.SearchItem> items;

			String str = String.join(" ", set);

			synchronized (searchMap) {
				if (searchMap.containsKey(str) || str.isEmpty()) {
					return;
				}
				log.debug("checks key");
			}

			items = index.search(set, exact);
			synchronized (searchMap) {
				searchMap.put(str, items);
			}
			log.debug("appended to searchmap");
			log.trace(String.format("queue.executes(%s, %s)", str, items));

		}
	}
}
