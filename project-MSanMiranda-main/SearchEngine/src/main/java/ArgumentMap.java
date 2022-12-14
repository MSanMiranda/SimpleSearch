import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;

/**
 * Parses and stores command-line arguments into simple key = value pairs.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class ArgumentMap {

	/**
	 * Hash set, Hash, Hash Map Tree set / Tree / Tree map Stores command-line
	 * arguments in key = value pairs.
	 */
	private final Map<String, String> map;

	/**
	 * Initializes an argument map.
	 */
	public ArgumentMap() {
		this.map = new HashMap<>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into flag/value
	 * pairs where possible. Some flags may not have associated values. If a flag is
	 * repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 * @see #ArgumentMap()
	 * @see #parse(String[])
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Function to test if map is empty
	 * 
	 * @return boolean if map is empty
	 */
	public Boolean isEmpty() {
		return this.map.isEmpty();
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may not
	 * have associated values. If a flag is repeated, its value is overwritten.
	 *
	 * Detects if there is a Flag, If so, map the flag within another empty value.
	 * If not, and there is a flag before it, update map key/vals. If not, and there
	 * wasn't another Flag before it, throw away.
	 *
	 * @param args the command line arguments to parse
	 * @see #isFlag(String)
	 * 
	 */
	public void parse(String[] args) {
		String key = "";
		boolean keyIn = false;
		for (String item : args) {
			if (isFlag(item)) {
				key = item;
				this.map.put(key, null);
				keyIn = !keyIn;
			} else {
				if (keyIn == true) {
					this.map.put(key, item);
					keyIn = false;
				}
			}
		}
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-digit character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#charAt(int)
	 * @see Character#isDigit(char)
	 */
	public static boolean isFlag(String arg) {
		if (arg == null || arg.length() < 2)
			return false;
		return ((arg.charAt(0) == '-') && !(Character.isDigit(arg.charAt(1))));
	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 */
	public static boolean isValue(String arg) {
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return this.map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return this.map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return this.map.get(flag) != null;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {
		return this.map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or the default value if there is no mapping.
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default value
	 *         if there is no mapping
	 */
	public String getString(String flag, String defaultValue) {
		String value = this.map.get(flag);
		if (value != null)
			return value;
		return defaultValue;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * {@code null} if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or no value exists).
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag) {
		String value = this.getString(flag);
		if (value != null)
			return Path.of(value);
		else
			return null;
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * @param flag         the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 * 
	 */
	public Path getPath(String flag, Path defaultValue) {
		String value = this.getString(flag);
		if (value != null)
			return Path.of(value);
		else
			return defaultValue;
	}

	/**
	 * Returns the value the specified flag is mapped as an int value, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to an int or if no value exists).
	 *
	 * @param flag         the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a int, or the default value
	 *         if there is no valid mapping
	 *
	 * @see Integer#parseInt(String, int)
	 */
	public int getInteger(String flag, int defaultValue) {
		try {
			return Integer.parseInt(map.get(flag));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public String toString() {
		return this.map.toString();
	}

}
