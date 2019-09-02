/**
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
 * @author Mass++ Users Group
 * @author Satoshi Tanaka
 * @since 2018-06-01 05:51:10+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.heatmap;

import java.nio.IntBuffer;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.Rect;
import ninja.mspp.view.control.canvas.ProfileCanvas;

/**
 * heatmap canvas
 */
public class HeatmapCanvas extends ProfileCanvas {
	private Heatmap heatmap;
	private String rtTitle;
	private String mzTitle;

	/**
	 * constructor
	 * @param heatmap heatmap data
	 * @param rtTitle RT title
	 * @param mzTitle m/z title
	 */
	public HeatmapCanvas( Heatmap heatmap, String rtTitle, String mzTitle ) {
		this.heatmap = heatmap;
		this.rtTitle = rtTitle;
		this.mzTitle = mzTitle;
		this.draw();
	}

	/**
	 * constructor
	 * @param heatmap heatmap data
	 */
	public HeatmapCanvas( Heatmap heatmap ) {
		this( heatmap, "RT", "m/z" );
	}

	/**
	 * gets the margin
	 * @param g graphics
	 * @param yRange y range
	 * @param width  width
	 * @param height height
	 * @return margin
	 */
	Rect< Integer > getMargin( GraphicsContext g, Range< Double > yRange, Integer width, Integer height ) {
		Integer top = ProfileCanvas.DEFAULT_MARGIN;
		Integer right = ProfileCanvas.DEFAULT_MARGIN;
		Integer bottom = 50;
		Integer left = ProfileCanvas.DEFAULT_MARGIN;

		left = left + this.getMaxYValueWidth( g, yRange, height, top, bottom );

		Rect< Integer > margin = new Rect< Integer >( top, right, bottom, left );
		return margin;
	}

	/**
	 * draws image
	 * @param g graphics
	 * @param width width
	 * @param height height
	 * @param margin margin
	 * @param data data
	 */
	protected void drawImage( GraphicsContext g, Integer width, Integer height, Rect< Integer > margin, double[][] data ) {
		int rtSize = Heatmap.RT_SIZE;
		int mzSize = Heatmap.MZ_SIZE;

		WritableImage image = new WritableImage( rtSize, mzSize );
		PixelWriter writer = image.getPixelWriter();

		WritablePixelFormat< IntBuffer > format = WritablePixelFormat.getIntArgbInstance();
		int[] pixels = new int[ rtSize * mzSize ];

		for( int i = 0; i < mzSize; i++ ) {
			for( int j = 0; j < rtSize; j++ ) {
				int index = i * rtSize + j;
				double intensity = Math.sqrt( data[ i ][ j ] );
				intensity = Math.max( 0.0, Math.min( 1.0,  intensity ) );

				int red = 0;
				int green = 0;
				int blue = 0;
				int alpha = 255;

				if( intensity < 0.1 ) {    // black -> blue
					double value = intensity / 0.1;
					blue = ( int )Math.round( 255.0 * value );
				}
				else if( intensity < 0.25 ) {    // blue -> cyan
					double value = ( intensity - 0.1 ) / 0.15;
					blue = 255;
					green = ( int )Math.round( 255.0 * value );
				}
				else if( intensity < 0.45 ) {    // cyan -> green
					double value = ( intensity - 0.25 ) / 0.2;
					green = 255;
					blue = ( int )Math.round( 255.0 * ( 1.0 - value ) );
				}
				else if( intensity < 0.7 ) {    // green -> yeallow
					double value = ( intensity - 0.45 ) / 0.25;
					green = 255;
					red = ( int )Math.round( 255.0 * value );
				}
				else {    // yeallow -> red
					double value = ( intensity - 0.7) / 0.3;
					red = 255;
					green = ( int )Math.round( 255.0 * ( 1.0 - value ) );
				}

				int pixel = ( alpha << 24 ) | ( red << 16 ) | ( green << 8 ) | blue;
				pixels[ index ] = pixel;
			}
		}
		writer.setPixels( 0,  0,  rtSize,  mzSize, format, pixels, 0, rtSize );

		g.drawImage(
			image,
			0,
			0,
			rtSize,
			mzSize,
			margin.getLeft(),
			margin.getTop(),
			width - margin.getLeft() - margin.getRight(),
			height - margin.getTop() - margin.getBottom()
		);
	}

	@Override
	protected void onDraw(GraphicsContext g, Integer width, Integer height) {
		Heatmap heatmap = this.heatmap;
		String rtTitle = this.rtTitle;
		String mzTitle = this.mzTitle;
		Rect< Integer > margin = this.getMargin( g, heatmap.getMzRange(), width, height );
		RealMatrix matrix = this.getDrawMatrix(
				width,
				height,
				heatmap.getRtRange(),
				heatmap.getMzRange(),
				margin,
				MatrixUtils.createRealIdentityMatrix( 3 )
		);

		this.drawImage( g, width, height, margin, heatmap.getData() );

		this.drawScale(
				g,
				heatmap.getRtRange(),
				heatmap.getMzRange(),
				width,
				height,
				margin,
				matrix,
				rtTitle,
				mzTitle
		);
	}

}
