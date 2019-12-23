package ninja.mspp.view;

import java.util.ArrayList;
import java.util.List;

import ninja.mspp.model.dataobject.ColorTheme;
import ninja.mspp.model.dataobject.Rgba;

/**
 * color manager
 */
public class ColorManager {
	private static ColorManager instance = null;

	private List< ColorTheme > themes;

	/**
	 * thermograhy
	 */
	class ThermographyTheme implements ColorTheme {
		@Override
		public String getName() {
			return "Thermography";
		}

		@Override
		public Rgba getColor(double value) {
			int red = 0;
			int green = 0;
			int blue = 0;
			int alpha = 255;

			if( value < 0.1 ) {    // black -> blue
				double degree = value / 0.1;
				blue = ( int )Math.round( 255.0 * degree );
			}
			else if( value < 0.25 ) {    // blue -> cyan
				double degree = ( value - 0.1 ) / 0.15;
				blue = 255;
				green = ( int )Math.round( 255.0 * degree );
			}
			else if( value < 0.45 ) {    // cyan -> green
				double degree = ( value - 0.25 ) / 0.2;
				green = 255;
				blue = ( int )Math.round( 255.0 * ( 1.0 - degree ) );
			}
			else if( value < 0.7 ) {    // green -> yeallow
				double degree = ( value - 0.45 ) / 0.25;
				green = 255;
				red = ( int )Math.round( 255.0 * degree );
			}
			else {    // yeallow -> red
				double degree = ( value - 0.7 ) / 0.3;
				red = 255;
				green = ( int )Math.round( 255.0 * ( 1.0 - degree ) );
			}

			Rgba rgba = new Rgba( red, green, blue, alpha );
			return rgba;
		}
	}

	/**
	 * grayscale theme
	 */
	class GrayScaleTheme implements ColorTheme {
		@Override
		public String getName() {
			return "GrayScale";
		}

		@Override
		public Rgba getColor(double value) {
			double degree = Math.pow( value, 0.5 );
			int rgb = ( int )Math.round( 255.0 * degree );
			int alpha = 255;
			Rgba rgba = new Rgba( rgb, rgb, rgb, alpha );
			return rgba;
		}
	}

	/**
	 * constructor
	 */
	private ColorManager() {
		this.init();
	}

	/**
	 * initializes
	 */
	private void init() {
		this.themes = new ArrayList< ColorTheme >();
		this.themes.add( new ThermographyTheme() );
		this.themes.add( new GrayScaleTheme() );
	}

	public List<ColorTheme> getThemes() {
		return themes;
	}

	/**
	 * gets the instance
	 * @return color manager instance
	 */
	public static ColorManager getInstance() {
		if( ColorManager.instance == null ) {
			ColorManager.instance = new ColorManager();
		}
		return ColorManager.instance;
	}
}
