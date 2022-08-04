import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Parses URL links from the anchor tags within HTML text.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class LinkParser {

	/** A logger specifically for this class. */
	private static final Logger log = LogManager.getLogger(LinkParser.class);

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url the url to normalize
	 * @return normalized url
	 * @throws URISyntaxException    if unable to craft new URI
	 * @throws MalformedURLException if unable to craft new URL
	 */
	public static URL normalize(URL url) throws MalformedURLException, URISyntaxException {
		return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
				url.getQuery(), null).toURL();
	}

	/**
	 * Returns a list of all the valid HTTP(S) links found in the href attribute of
	 * the anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and normalized (removing fragments and encoding special
	 * characters as necessary).
	 * 
	 * Any links that are unable to be properly parsed (throwing an
	 * {@link MalformedURLException}) or that do not have the HTTP/S protocol will
	 * not be included.
	 *
	 * @param base the base url used to convert relative links to absolute
	 * @param html the raw html associated with the base url
	 * @return list of all valid http(s) links in the order they were found
	 */
	public static ArrayList<URL> getValidLinks(String base, String html) {
		String regex = "<[aA][^>]*[\\s]*[hH][rR][eE][fF][\\n\\s]*[=][\\n\\s]*.+?[\'\"]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		ArrayList<URL> arr = new ArrayList<URL>();
		URL item;

		while (matcher.find()) {
			StringBuffer matched = new StringBuffer(matcher.group());
			log.debug("Matcher.find()" + matched.toString());

			log.trace("// Delete \"<a ... href =\"");
			// Delete "<a ... href ="
			int index = matched.indexOf("href");
			matched.delete(0, index + 4);
			index = matched.indexOf("\"");
			matched.delete(0, index + 1);

			try {
				log.debug("	// Delete everything after URL"+ matched.toString());
				// Delete everything after URL
				index = matched.indexOf("\"", index);
				if(index == -1) {
					index = matched.indexOf("\'", index);
				}

				matched.setLength(index);
			}
			catch(IndexOutOfBoundsException e) {
				log.debug("	// setLength failed " + index);
			}
			
			
			log.trace("Clean");
			item = clean(matched, base);
			if (item != null) {
				arr.add(item);
			}
		}
		log.debug("Return arr");
		return arr;
	}

	/**
	 * Function to cleanup href
	 * 
	 * @param matched the string that matches href
	 * @param base    URL base
	 * @return a URL based off cleaned match string
	 */
	private static URL clean(StringBuffer matched, String base) {
		
		URL item = null;
		try {
			String match = matched.toString();
			if (!match.contains("javascript:") && !match.contains("mailto:")) {
				if (!match.toLowerCase().contains("http")) {
					String str = base.toString();
					StringBuffer strb = new StringBuffer(str);
					if ((str.endsWith(".html") || str.endsWith(".htm"))) {
						if (match.contains("#link")) {
							strb.delete(strb.lastIndexOf("/"), str.length());
							matched = new StringBuffer(strb);
							matched.delete(0, matched.lastIndexOf("/"));
							matched.append(".htm");
						} else {
							strb.delete(strb.lastIndexOf("/") + 1, str.length());
						}
					} else if (!str.endsWith("/")) {
						strb.append("/");
					}
					strb.append(matched);
					matched = strb;
				}
				item = new URL(matched.toString());
				item = normalize(item);
			}
		} catch (Exception e) {
			log.debug("LinkParser Exception e");
		}
		return item;
	}
}
