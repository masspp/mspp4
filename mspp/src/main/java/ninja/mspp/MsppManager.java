package ninja.mspp;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ninja.mspp.annotation.Plugin;

/**
 * Mass++ manager (Singleton class)
 */
public class MsppManager {
	// instance
	private static MsppManager instance = null;

	// config properties
	private ResourceBundle config;

	// plugins
	ArrayList< Object > plugins;

	/**
	 * constructor
	 */
	private MsppManager() {
		initialize();
	}

	/**
	 * initializes object
	 */
	private void initialize() {
		this.config = ResourceBundle.getBundle( "application" );
		this.plugins = createPluginArray();
	}

	/**
	 * gets the config properties
	 * @return config properties
	 */
	public ResourceBundle getConfig() {
		return this.config;
	}

	// array list
	private static ArrayList< Object > createPluginArray() {
		ArrayList< String > files = getFiles();
		ArrayList< Object > array = getPlugins( files );

		return array;
	}

	/**
	 * get classpath files
	 * @return files
	 */
	private static ArrayList< String > getFiles() {
		ArrayList< String > paths = new ArrayList< String >();

		String classPaths = System.getProperty( "java.class.path" );
		StringTokenizer tokenizer = new StringTokenizer( classPaths, File.pathSeparator );

		while( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			File file = new File( token );

			if( file.isDirectory() ) {
				ArrayList< String > files = getFiles( file, "" );
				paths.addAll( files );
			}
			else {
				try {
					JarFile jar = new JarFile( file );
					Enumeration< JarEntry > entries = jar.entries();
					while( entries.hasMoreElements() ) {
						JarEntry entry = entries.nextElement();
						paths.add( entry.getName() );
					}
					jar.close();
				}
				catch( Exception e ) {
					e.printStackTrace();
				}
			}
		}

		return paths;
	}

	/**
	 * gets plugins
	 * @param files files
	 * @return plugins
	 */
	private static ArrayList< Object > getPlugins( ArrayList< String > files ) {
		ArrayList< Object > plugins = new ArrayList< Object >();
		Set< String > exceptions = new HashSet< String >();
		exceptions.add( "com.sun.deploy.uitoolkit.impl.fx.Utils" );

		for( String file : files ) {
			if( file.endsWith( ".class" ) ) {
				String className = file.replace( ".class", "" );
				className = className.replace( "/",  "." );
				int index = className.indexOf( "$" );

				if( index >= 0 ) {
					className = className.substring( 0, index );
					exceptions.add( className );
				}
				else if( !exceptions.contains( className ) ) {
					try {
						Class< ? > clazz = Class.forName( className );
						Plugin annotation = clazz.getAnnotation( Plugin.class );
						if( annotation != null ) {
							Object object = clazz.newInstance();
							plugins.add( object );
						}
					}
					catch( Exception e ) {
					}
					catch( Error e ) {
					}
				}
			}
		}

		return plugins;

	}

	/**
	 * gets files
	 * @param dir directory
	 * @return file array
	 */
	private static ArrayList< String > getFiles( File dir, String packageName ) {
		ArrayList< String > array = new ArrayList< String >();

		File[] files = dir.listFiles();
		for( File file : files ) {
			String name = file.getName();
			if( !packageName.isEmpty() ) {
				name = packageName + "/" + name;
			}

			if( file.isDirectory() ) {
				ArrayList< String > children = getFiles( file, name );
				array.addAll( children );
			}
			else {
				array.add( name );
			}
		}

		return array;
	}

	/**
	 * gets the instance
	 * @return MsppManager instance (This is the only object.)
	 */
	public static MsppManager getInstance() {
		if( MsppManager.instance == null ) {
			MsppManager.instance = new MsppManager();
		}
		return MsppManager.instance;
	}
}
