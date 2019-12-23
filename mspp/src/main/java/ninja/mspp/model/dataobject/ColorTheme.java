package ninja.mspp.model.dataobject;

/**
 * color theme
 */
public interface ColorTheme {
	/**
	 * gets the theme name
	 * @return color theme name
	 */
	public String getName();

	/**
	 * gets the color
	 * @param value value
	 * @return color
	 */
	public Rgba getColor( double value );

}
