package ninja.mspp;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.prefs.Preferences;

import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.PluginObject;
import ninja.mspp.model.dataobject.Sample;

/**
 * Mass++ manager (Singleton class)
 */
public class MsppManager implements Iterable< Object > {
	// instance
	private static MsppManager instance = null;

	// config properties
	private ResourceBundle config;

	// message properties
	private ResourceBundle messages;

	// plugins
	private ArrayList< Object > plugins;

	// preferences
	private Preferences preferences;

	// temporary directory
	File tmpDir;

	// samples
	ArrayList< Sample > samples;

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
		this.messages = ResourceBundle.getBundle( "messages" );
		this.plugins = createPluginArray();
		this.preferences = Preferences.userRoot().node( "mspp4/parameters" );

		this.samples = new ArrayList< Sample >();
	}

	/**
	 * gets the config properties
	 * @return config properties
	 */
	public ResourceBundle getConfig() {
		return this.config;
	}

	/**
	 * gets the messages
	 * @return messages
	 */
	public ResourceBundle getMessages() {
		return this.messages;
	}

	// array list
	private static ArrayList< Object > createPluginArray() {
		ArrayList< String > files = getFiles();
		ArrayList< Object > array = searchPlugins( files );

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
	 * search plugins
	 * @param files files
	 * @return plugins
	 */
	private static ArrayList< Object > searchPlugins( ArrayList< String > files ) {
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
	 * gets specified plug-ins
	 * @param clazz annotation class
	 * @return plug-in array
	 */
	public < A extends Annotation > ArrayList< PluginObject< A > > getPlugins( Class< A > clazz ) {
		ArrayList< PluginObject< A > > array = new ArrayList< PluginObject< A > >();
		for( Object plugin : this.plugins ) {
			A annotation = plugin.getClass().getAnnotation( clazz );
			if( annotation != null ) {
				PluginObject< A > object = new PluginObject< A >( plugin, annotation );
				array.add( object );
			}
		}

		return array;
	}

	/**
	 * gets plug-in methods
	 * @param clazz class
	 * @return plug-in methods
	 */
	public < A extends Annotation > ArrayList< PluginMethod< A > > getMethods( Class< A > clazz ) {
		ArrayList< PluginMethod< A > > array = new ArrayList< PluginMethod< A > >();
		for( Object plugin : this.plugins ) {
			Method[] methods = plugin.getClass().getDeclaredMethods();
			for( Method method : methods ) {
				A annotation = method.getAnnotation( clazz );
				if( annotation != null ) {
					PluginMethod< A > pluginMethod = new PluginMethod< A >( plugin, method, annotation );
					array.add( pluginMethod );
				}
			}
		}

		return array;
	}


	/**
	 * save string value
	 * @param name parameter name
	 * @param value parameter value
	 */
	public void saveString( String name, String value ) {
		this.preferences.put( name, value );
	}

	/**
	 * loads string value
	 * @param name parameter name
	 * @param defaultValue default value
	 * @return parameter value
	 */
	public String loadString( String name, String defaultValue ) {
		return this.preferences.get( name,  defaultValue );
	}


	/**
	 * gets the temporary directory
	 * @return temporary directory
	 */
	public File getTmpDir() {
		if( this.tmpDir == null ) {
			File tmpDir = new File( System.getProperty( "java.io.tmpdir" ) );
			this.tmpDir = new File( tmpDir, "mspp" + Long.toString( System.currentTimeMillis() ) );
			this.tmpDir.mkdir();
		}

		return this.tmpDir;
	}

	/**
	 * adds sample
	 * @param sample sample
	 */
	public void addSample( Sample sample ) {
		this.samples.add( sample );
	}

	/**
	 * removes sample
	 * @param sample sample
	 */
	public void removeSample( Sample sample ) {
		this.samples.remove( sample );
	}

	@Override
	public Iterator<Object> iterator() {
		return this.plugins.iterator();
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
