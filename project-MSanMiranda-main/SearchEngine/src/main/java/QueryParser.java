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

/**
 * A data structure to store parsed query lines Sorted alphabetically without
 * duplicates, what locations they appear in along with their count
 *
 * @author Michael Miranda
 * @author University of San Francisco
 * @version Fall 2020
 */
public class QueryParser implements QueryParserInterface {

	/** Inverted index to reference to */
	private final InvertedIndex index;

	/** Data Structure to hold SearchItems */
	private final TreeMap<String, List<InvertedIndex.SearchItem>> searchMap;

	/**
	 * Basic constructor with a reference to InvertedIndex
	 * 
	 * @param index the InvertedIndex to reference
	 */
	public QueryParser(InvertedIndex index) {
		this.index = index;
		this.searchMap = new TreeMap<String, List<InvertedIndex.SearchItem>>();
	}

	/* Contains Function */

	@Override
	public boolean containsWord(String word) {
		return searchMap.containsKey(word);
	}

	/* Size-like functions */

	@Override
	public int wordCount() {
		return searchMap.size();
	}

	/* Getter-like functions */

	@Override
	public Set<String> wordSet() {
		return Collections.unmodifiableSet(searchMap.keySet());
	}

	@Override
	public List<InvertedIndex.SearchItem> searchList(String word) {
		if (containsWord(word))
			return Collections.unmodifiableList(searchMap.get(word));
		return null;
	}

	/* Search Functions */

	@Override
	public void build(String line, boolean exact) {
		TreeSet<String> set = TextFileStemmer.uniqueStems(line);
		List<InvertedIndex.SearchItem> items;

		String str = String.join(" ", set);

		if (!searchMap.containsKey(str) && !str.isEmpty()) {
			items = index.search(set, exact);
			searchMap.put(str, items);
		}
	}

	/* JSon - String Function */
	@Override
	public void toJson(Path currentWritePath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(currentWritePath, StandardCharsets.UTF_8)) {
			SimpleJsonWriter.asSearchItems(searchMap, writer, 0);
		}
	}
}
