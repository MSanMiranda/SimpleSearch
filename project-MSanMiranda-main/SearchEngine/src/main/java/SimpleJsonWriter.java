import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class SimpleJsonWriter {

	/**
	 * Indents using a tab character by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the integer element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element.toString());
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Integer> iterator = elements.iterator();

		writer.write('[');

		if (iterator.hasNext()) {
			writer.write("\n");
			indent(iterator.next(), writer, level + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",\n");
			indent(iterator.next(), writer, level + 1);
		}
		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #asObject(String, Integer, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		String next;

		writer.write("{");

		if (iterator.hasNext()) {
			next = iterator.next();
			asObject(next, elements.get(next), writer, level);
		}
		while (iterator.hasNext()) {
			next = iterator.next();
			writer.write(",");
			asObject(next, elements.get(next), writer, level);
		}
		writer.write("\n}");
	}

	/**
	 * Writes a single key pair element as a pretty JSON object.
	 *
	 * @param key    the String to write
	 * @param value  the Integer to write
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	private static void asObject(String key, Integer value, Writer writer, int level) throws IOException {
		writer.write("\n");
		indent(key, writer, level + 1);
		writer.write(": " + value.toString());
	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #asNestedArray(String, Collection, Writer, int)
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {

		Iterator<String> iterator = elements.keySet().iterator();
		String next;

		writer.write("{");

		if (iterator.hasNext()) {
			next = iterator.next();
			asNestedArray(next, elements.get(next), writer, level);
		}
		while (iterator.hasNext()) {
			next = iterator.next();
			writer.write(",");
			asNestedArray(next, elements.get(next), writer, level);
		}
		writer.write("\n}");
	}

	/**
	 * Writes a single key pair as a pretty JSON object to the nested array. The
	 * generic notation used allows this method to be used for any type of map with
	 * any type of nested collection of integer objects.
	 *
	 * @param key      the key in the key pair
	 * @param elements the generic collection of integer of objects to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	private static void asNestedArray(String key, Collection<Integer> elements, Writer writer, int level)
			throws IOException {

		writer.write("\n");
		indent(key, writer, level + 1);
		writer.write(": ");
		asArray(elements, writer, level + 1);
	}

	/**
	 * Writes the elements as a double-nested pretty JSON object.
	 *
	 * @param map    the inverted index to convert to a string
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see #doubleNestedArray(String, TreeMap, Writer, int)
	 */
	public static void doubleNestedArray(TreeMap<String, TreeMap<String, TreeSet<Integer>>> map, Writer writer,
			int level) throws IOException {
		
		Iterator<String> iterator = map.keySet().iterator();
		String next;

		indent(writer, level);
		writer.write("{");

		if (iterator.hasNext()) {
			next = iterator.next();
			doubleNestedArray(next, map.get(next), writer, level);
		}
		while (iterator.hasNext()) {
			next = iterator.next();
			writer.write(",");
			doubleNestedArray(next, map.get(next), writer, level);
		}
		indent(writer, level);
		writer.write("\n}");
	}

	/**
	 * Writes the elements as a double-nested pretty JSON object.
	 *
	 * @param key    the key in the key pair
	 * @param map    the inverted index to convert to a string
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException when it happens
	 */
	private static void doubleNestedArray(String key, TreeMap<String, TreeSet<Integer>> map, Writer writer, int level)
			throws IOException {

		writer.write("\n");
		indent(key, writer, level + 1);
		writer.write(": ");
		asNestedArray(map, writer, level + 1);
	}

	/**
	 * Writes the elements as a SearchItems pretty JSON object.
	 *
	 * @param map    the search results to convert to a string
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException when it happens
	 * 
	 * @see #asSearchItems(String, List, Writer, int)
	 */
	public static void asSearchItems(TreeMap<String, List<InvertedIndex.SearchItem>> map, Writer writer,
			int level) throws IOException {
		
		Iterator<String> iterator = map.keySet().iterator();
		String next;
		indent(writer, level);
		writer.write("{");

		if (iterator.hasNext()) {
			next = iterator.next();
			asSearchItems(next, map.get(next), writer, level + 1);
		}
		while (iterator.hasNext()) {
			next = iterator.next();
			writer.write(",");
			asSearchItems(next, map.get(next), writer, level + 1);
		}
		indent(writer, level);
		writer.write("\n}");

	}

	/**
	 * Writes the elements as a SearchItems pretty JSON object.
	 *
	 * @param next   the Word to Search
	 * @param arr    the ArrayList of SearchItems from word
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException when it happens
	 * 
	 * @see #asSearchItems(TreeMap, Writer, int)
	 */
	private static void asSearchItems(String next, List<InvertedIndex.SearchItem> arr, Writer writer, int level)
			throws IOException {

		writer.write("\n");
		indent(next, writer, level);
		writer.write(": [\n");

		if (arr.size() >= 1 && arr.get(0).getLocation() != null) {
			asSearchItems(arr, writer, level + 1);
		}

		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Writes the elements as a SearchItems pretty JSON object.
	 *
	 * @param arr    the ArrayList of SearchItems from word
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException when it happens
	 * 
	 * @see #asSearchItems(InvertedIndex.SearchItem, Writer, int)
	 */
	private static void asSearchItems(List<InvertedIndex.SearchItem> arr, Writer writer, int level)
			throws IOException {
		
		InvertedIndex.SearchItem item;
		Iterator<InvertedIndex.SearchItem> iterator = arr.iterator();

		if (iterator.hasNext()) {
			item = iterator.next();
			asSearchItems(item, writer, level);
		}
		while (iterator.hasNext()) {
			item = iterator.next();
			writer.write(",\n");
			asSearchItems(item, writer, level);
		}
		writer.write("\n");

	}

	/**
	 * Writes the elements as a SearchItems pretty JSON object.
	 *
	 * @param item    the SearchItems from word
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException when it happens
	 * 
	 */
	private static void asSearchItems(InvertedIndex.SearchItem item, Writer writer, int level)
			throws IOException {
		
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
		indent(writer, level);
		writer.write("{\n");

		indent("where", writer, level + 1);
		writer.write(": " + '"' + item.getLocation() + '"' + ",\n");
		indent("count", writer, level + 1);
		writer.write(": " + item.getAmount() + ",\n");
		indent("score", writer, level + 1);
		writer.write(": " + FORMATTER.format(item.getScore()) + "\n");

		indent(writer, level);
		writer.write("}");
	}
}
