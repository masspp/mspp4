/*
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author satstnka
 * @since 2019
 *
 * Copyright (c) 2019 satstnka
 * All rights reserved.
 */
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
	class ThermographyTheme extends ColorTheme {
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
	class GrayScaleTheme extends ColorTheme {
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
