import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class used to construct InvertedIndex MultiThreaded
 * 
 * @author Michael Miranda
 * @author University of San Francisco
 * @version Fall 2020
 */
public class IndexFactoryMulti extends IndexFactory {

	/** A logger specifically for this class. */
	private static final Logger log = LogManager.getLogger(IndexFactoryMulti.class);

	/**
	 * A function to build and populate the InvertedIndexMulti given a path, whether that
	 * path is a file or a directory.
	 *
	 * @param start the input file to parse
	 * @param map   the InvertedIndex to write to
	 * @param queue the workQueue to use
	 * @throws IOException when an IOException occurs
	 *
	 * @see TextFileFinder#checkPath(Path)
	 * @see #filePopulate(Path, InvertedIndex)
	 */
	public static void build(Path start, InvertedIndexMulti map, WorkQueue queue) throws IOException {
		for (Path location : TextFileFinder.checkPath(start)) {
			log.debug("queue.execute");
			queue.execute(new ScanFile(location, map));
		}
		queue.finish();
		log.debug("queue.finish");
	}

	/**
	 * Runnable function for a MultiThreaded building of InvertedIndex, each
	 * instance reads a single file.
	 * 
	 * @see Runnable
	 */
	private static class ScanFile implements Runnable {
		
		/** InvertedIndex Reference */
		private InvertedIndexMulti map;
		/** Path when building */
		private Path location;

		/**
		 * Constructor with data to look at and data.
		 * 
		 * @param map      InvertedIndex Reference
		 * @param location Path when building;
		 */
		public ScanFile(Path location, InvertedIndexMulti map) {
			this.location = location;
			this.map = map;
		}

		@Override
		public void run() {
			try {
				log.trace("New local index");
				InvertedIndex local = new InvertedIndex();

				log.trace("Populates local index");
				filePopulate(location, local);

				log.trace("Appends index");
				map.addAll(local);
			} catch (IOException e) {
				log.trace("ScanFile.run");
			}
		}
	}
}
