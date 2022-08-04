import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for finding all text files in a directory using lambda
 * functions and streams.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class TextFileFinder {

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt
	 * or .text extension (case-insensitive). Useful for
	 * {@link Files#walk(Path, FileVisitOption...)}.
	 *
	 * @see Files#isRegularFile(Path, java.nio.file.LinkOption...)
	 * @see Path#getFileName()
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static final Predicate<Path> IS_TEXT = (Path p) -> {
		String location = p.toString().toLowerCase();
		return (location.endsWith("text") || location.endsWith("txt")) && (Files.isRegularFile(p));
	};

	/**
	 * Returns a stream of matching files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @param keep  function that determines whether to keep a file
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #IS_TEXT
	 * @see FileVisitOption#FOLLOW_LINKS
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static Stream<Path> find(Path start, Predicate<Path> keep) throws IOException {
		if (!Files.isDirectory(start) && Files.isReadable(start)) {
			return Files.walk(start);
		}
		return Files.walk(start, FileVisitOption.FOLLOW_LINKS).filter(keep);
	};

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #find(Path, Predicate)
	 * @see #IS_TEXT
	 */
	public static Stream<Path> find(Path start) throws IOException {
		return find(start, IS_TEXT);
	}

	/**
	 * Returns a list of text files using streams.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #find(Path)
	 * @see Collectors#toList()
	 */
	public static List<Path> list(Path start) throws IOException {
		return find(start).collect(Collectors.toList());
	}

	/**
	 * Function that reads if a file is a directory or readable file, returning a
	 * list of path(s).
	 *
	 * @param arg to check if it is directory or file
	 * @return a list of 1 path, list of paths from directory or an empty folder if
	 *         file cannot be read
	 * @throws IOException if an IOException occurs
	 *
	 * @see TextFileFinder#list(Path)
	 */
	public static List<Path> checkPath(Path arg) throws IOException {
		if (Files.isDirectory(arg)) {
			return TextFileFinder.list(arg);
		}

		List<Path> folder = new ArrayList<Path>();
		if (Files.isReadable(arg)) {
			folder.add(arg);
		}

		return folder;
	}
}
