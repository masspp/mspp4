package ninja.mspp.tools;

/**
 * String tool
 */
public class StringTool {
	/**
	 * null value logic
	 * @param string string
	 * @param defualtValue default value
	 * @return if string is null returns default value otherwise returns string.
	 */
	public static String nvl( String string, String defaultValue ) {
		return ( string == null ? defaultValue : string );
	}
}
