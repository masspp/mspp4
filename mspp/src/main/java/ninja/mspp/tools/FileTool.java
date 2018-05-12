package ninja.mspp.tools;

import java.io.File;

import ninja.mspp.MsppManager;

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

	/**
	 * creates tmp file
	 * @param folder folder
	 * @param prefix prefix
	 * @param suffix suffix
	 * @return tmp file
	 */
	public static File createTmpFile( String folder, String prefix, String suffix ) {
		MsppManager msppManager = MsppManager.getInstance();
		File dir = new File( msppManager.getTmpDir(), folder );
		if( !dir.exists() ) {
			dir.mkdirs();
		}

		File file = null;
		try{
			file = File.createTempFile( prefix,  suffix, dir );
		}
		catch( Exception e ) {
			System.out.println( msppManager.getTmpDir() );
			System.out.println( dir );
			e.printStackTrace();
		}

		return file;
	}
}
