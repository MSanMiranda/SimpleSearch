import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {

	/** A logger specifically for this class. */
	private static final Logger log = LogManager.getLogger(Driver.class);

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 *
	 */
	public static void main(String[] args) {
		Instant start = Instant.now();

		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex library = null;
		InvertedIndexMulti multiLibrary = null;
		QueryParserInterface query = null;
		WorkQueue queue = null;
		int maxCrawls = 1;

		if (argMap.hasFlag("-max")) {
			String max = argMap.getString("-max");
			if (max != null) {
				try {
					maxCrawls = Integer.parseInt(max);
				} catch (Exception e) {
					maxCrawls = 0;
				}
				if (maxCrawls < 1) {
					log.error("Invalid CrawlArg: " + max);
					return;
				}
			}
		}

		/* Enable MultiThreading */
		if (argMap.hasFlag("-threads") || argMap.hasFlag("-url")) {
			String threadArg = argMap.getString("-threads");
			int threads = 5;
			if (threadArg != null) {
				try {
					threads = Integer.parseInt(threadArg);
				} catch (Exception e) {
					threads = 0;
				}
			}
			if (threads < 1) {
				log.error("Invalid Thread: " + threadArg);
				return;
			}
			queue = new WorkQueue(threads);
			multiLibrary = new InvertedIndexMulti();
			library = multiLibrary;
			query = new QueryParserMulti(multiLibrary, queue);
		} else {
			library = new InvertedIndex();
			query = new QueryParser(library);
		}

		/* Build Inverted Index via Files */
		if (argMap.hasFlag("-path") && argMap.getString("-path") != null) {
			Path currentReadPath = Paths.get(argMap.getString("-path"));
			try {
				if (multiLibrary != null) {
					IndexFactoryMulti.build(currentReadPath, multiLibrary, queue);
				} else {
					IndexFactory.build(currentReadPath, library);
				}
			} catch (Exception e) {
				System.out.println("-path Unable to read something given by path " + currentReadPath);
				return;
			}
		}

		/* Build Inverted Index via WebCrawling */
		if (argMap.hasFlag("-url") && argMap.getString("-url") != null) {
			URL seedURL = null;
			try {
				log.debug("Converts URL");
				seedURL = new URL(argMap.getString("-url"));
				log.debug("Builds WebCrawler");
				WebCrawler.build(seedURL, maxCrawls, multiLibrary, queue);
			} catch (Exception e) {
				System.out.println("-max Unable to read something given by URL " + seedURL);
				return;
			}
		}

		/* QuerySearch through InvertedIndex */
		if (argMap.hasFlag("-queries") && argMap.getString("-queries") != null) {
			Path currentReadPath = Paths.get(argMap.getString("-queries"));
			try {
				query.build(currentReadPath, argMap.hasFlag("-exact"));
			} catch (Exception e) {
				System.out.println("Unable to Query + search something given by path " + currentReadPath);
				return;
			}
		}

		/* toJson Write functions */
		if (argMap.hasFlag("-index")) {
			Path currentWritePath = argMap.getPath("-index", Path.of("index.json"));
			try {
				library.toJson(currentWritePath);
			} catch (Exception e) {
				System.out.println("Unable to write the InvertedIndex to JSON file at " + currentWritePath);
				return;
			}
		}

		if (argMap.hasFlag("-counts")) {
			Path currentWritePath = argMap.getPath("-counts", Path.of("counts.json"));
			try {
				library.toCountJson(currentWritePath);
			} catch (Exception e) {
				System.out.println("Unable to write the CountIndex to JSON file at " + currentWritePath);
				return;
			}
		}

		if (argMap.hasFlag("-results")) {
			Path currentWritePath = argMap.getPath("-results", Path.of("results.json"));
			try {
				query.toJson(currentWritePath);
			} catch (Exception e) {
				System.out.println("Unable to write the SearchResults to JSON file at " + currentWritePath);
				return;
			}
		}

		if (queue != null) {
			queue.join();
		}
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		log.info(String.format("Elapsed: %f seconds%n", seconds));

	}
}
