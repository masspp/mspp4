package ninja.mspp.tools;

/**
 * file tool
 */
public class FileTool {
	/**
	 * gets the extension
	 * @param path file path
	 * @return extension
	 */
	public static String getExtension( String path ) {
		if( path == null ) {
			return null;
		}

		int index = path.lastIndexOf( "." );
		if( index >= 0 ) {
			return path.substring( index + 1 );
		}
		return path;
	}
}
