package ninja.mspp;

import java.util.ResourceBundle;

/**
 * Mass++ manager (Singleton class)
 */
public class MsppManager {
	// instance
	private static MsppManager instance = null;

	// config properties
	private ResourceBundle config;

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
	}

	/**
	 * gets the config properties
	 * @return config properties
	 */
	public ResourceBundle getConfig() {
		return this.config;
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
