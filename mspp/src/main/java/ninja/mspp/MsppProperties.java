package ninja.mspp;

import java.util.Properties;

public class MsppProperties extends Properties {
	// instance
	private static MsppProperties instance;

	public static final String KEY_SPECTRUM_VIEW = "spec_view";

	/**
	 * constructor
	 */
	protected MsppProperties() {
	}


	/**
	 * gets the instance
	 * @return
	 */
	public static MsppProperties getInstance() {
		if( MsppProperties.instance == null ) {
			MsppProperties.instance = new MsppProperties();
		}
		return MsppProperties.instance;
	}
}
