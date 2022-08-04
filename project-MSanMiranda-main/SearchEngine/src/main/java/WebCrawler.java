import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A WebCrawler designed to retrieved and parse HTML, along with going through
 * their redirects
 * 
 * @author MichaelMiranda
 * @author University of SanFrancisco
 * @version Fall 2020
 *
 */
public class WebCrawler {

	/** A logger specifically for this class. */
	private static final Logger log = LogManager.getLogger(WebCrawler.class);

	/** A structure to see what locations have already been visited */
	private static HashSet<URL> locationMap = new HashSet<URL>();

	/**
	 * A function to build and populate the InvertedIndexMulti given a URL base &
	 * limit of redirects
	 *
	 * @param currentReadPath start the URLs to parse
	 * @param crawlsLeft      the max amount of crawling allowed
	 * @param multiLibrary    InvertedIndex to reference
	 * @param queue           work Queue to use
	 */
	public static void build(URL currentReadPath, int crawlsLeft, InvertedIndexMulti multiLibrary, WorkQueue queue) {
		String base = currentReadPath.toString();
		int endSlash = base.lastIndexOf("/");
		base = base.substring(0, endSlash + 1);
		log.debug("Seed is is" + base);

		ArrayList<URL> list = new ArrayList<URL>();
		list.add(currentReadPath);
		while (crawlsLeft > 0) {
			ArrayList<URL> nextList = new ArrayList<URL>();
			for (URL link : list) {
				if (crawlsLeft > 0 && !locationMap.contains(link) && base.length() < link.toString().length()) {
					locationMap.add(link);
					log.debug("Link is" + link.toString());
					queue.execute(new buildURL(link, multiLibrary, nextList, base));
					crawlsLeft--;
				}
			}
			queue.finish();
			list = nextList;
		}
	}

	/**
	 * A runnable MultiThreaded method to add to workQueue, will read HTML, add any
	 * links that it finds to list given.
	 * 
	 * @author MichaelMiranda
	 *
	 */
	private static class buildURL implements Runnable {

		/** InvertedIndex Reference */
		private InvertedIndexMulti map;

		/** Path when building */
		private URL location;

		/** List of redirects to update */
		private ArrayList<URL> nextList;

		/** Base URL String */
		private String base;

		/**
		 * Constructor with data to look at and data.
		 * 
		 * @param map      InvertedIndex Reference
		 * @param location Path when building
		 * @param nextList the list to append to for future crawling
		 * @param base     the base string url
		 */
		public buildURL(URL location, InvertedIndexMulti map, ArrayList<URL> nextList, String base) {
			this.location = location;
			this.map = map;
			this.nextList = nextList;
			this.base = base;
		}

		@Override
		public void run() {
			try {

				log.trace("New local index");
				InvertedIndex local = new InvertedIndex();

				log.trace("Fetch");
				String html = HtmlFetcher.fetch(location, 0);

				/*
				 * log.trace("Strip Block Valid Links"); html =
				 * HtmlCleaner.stripBlockElements(html);
				 */

				log.trace("Get Valid Links @ " + location.toString());
				ArrayList<URL> newList = LinkParser.getValidLinks(base, html);

				log.trace("Strip HTML");
				html = HtmlCleaner.stripHtml(html);

				synchronized (nextList) {
					nextList.addAll(newList);
				}

				log.trace("Populates local index");
				IndexFactory.filePopulate(html, location.toString(), local);

				log.trace("Appends index");
				map.addAll(local);

			} catch (Exception e) {
				log.trace("WebCrawler Exception!");
			}
		}
	}

}
