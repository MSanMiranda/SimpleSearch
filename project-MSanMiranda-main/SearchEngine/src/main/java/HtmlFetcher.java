import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A specialized version of {@link HttpsFetcher} that follows redirects and
 * returns HTML content if possible.
 *
 * @see HttpsFetcher
 */
public class HtmlFetcher {

	/**
	 * Returns {@code true} if and only if there is a "Content-Type" header and the
	 * first value of that header starts with the value "text/html"
	 * (case-insensitive).
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isHtml(Map<String, List<String>> headers) {
		for (String header : headers.keySet()) {
			for (String word : headers.get(header)) {
				if (word.contains("text/html")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Parses the HTTP status code from the provided HTTP headers, assuming the
	 * status line is stored under the {@code null} key.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return the HTTP status code or -1 if unable to parse for any reasons
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		for (String header : headers.keySet()) {
			for (String word : headers.get(header)) {
				if (word.startsWith("HTTP/1.1")) {
					return Integer.parseInt(word.substring(9, 12));
				}
			}
		}
		return 0;
	}

	/**
	 * Returns {@code true} if and only if the HTTP status code is between 300 and
	 * 399 (inclusive) and there is a "Location" header with at least one value.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {
		if (headers.containsKey("Location")) {
			int code = getStatusCode(headers);
			return (code >= 300 && code < 400) && !headers.get("Location").isEmpty();
		}
		return false;
	}

	/**
	 * Returns {@code true} if and only if the HTTP status code is between 200 and
	 * 299 (inclusive)
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isOK(Map<String, List<String>> headers) {
		int code = getStatusCode(headers);
		if (code >= 200 && code < 300)
			return true;
		return false;
	}

	/**
	 * Fetches the resource at the URL using HTTP/1.1 and sockets. If the status
	 * code is 200 and the content type is HTML, returns the HTML as a single
	 * string. If the status code is a valid redirect, will follow that redirect if
	 * the number of redirects is greater than 0. Otherwise, returns {@code null}.
	 *
	 * @param url       the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 *
	 * @see #isHtml(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetch(URL url, int redirects) {
		try (Socket gate = HttpsFetcher.openConnection(url);
				BufferedReader reader = new BufferedReader(new InputStreamReader(gate.getInputStream()));
				PrintWriter writer = new PrintWriter(gate.getOutputStream());) {

			HttpsFetcher.printGetRequest(writer, url);
			Map<String, List<String>> headers = HttpsFetcher.getHeaderFields(reader);

			if (isHtml(headers)) {
				if (isOK(headers)) {
					StringWriter str = new StringWriter();
					List<String> content = HttpsFetcher.getContent(reader);
					Iterator<String> line = content.iterator();

					if (line.hasNext()) {
						str.append(line.next());
					}
					while (line.hasNext()) {
						str.append("\n");
						str.append(line.next());
					}

					return str.toString();
				} else if (isRedirect(headers) && redirects > 0) {
					return fetch(headers.get("Location").get(0), redirects - 1);
				}
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetch(URL, int)}.
	 *
	 * @param url       the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 *
	 * @see #fetch(URL, int)
	 */
	public static String fetch(String url, int redirects) {
		try {
			return fetch(new URL(url), redirects);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetch(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 *
	 * @see #fetch(URL, int)
	 */
	public static String fetch(String url) {
		return fetch(url, 0);
	}

	/**
	 * Calls {@link #fetch(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the
	 *         resource is not html
	 */
	public static String fetch(URL url) {
		return fetch(url, 0);
	}
}
