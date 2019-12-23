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
 * @author Satoshi Tanaka
 * @since Thu May 31 22:40:25 JST 2018
 *
 * Copyright (c) 2018 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.mirror;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ninja.mspp.model.dataobject.DrawPoint;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.Rect;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.view.control.canvas.ProfileCanvas;

/**
 * mirror canvas
 */
public class MirrorCanvas extends ProfileCanvas {
	private String xTitle;
	private Range< Double > xRange;

	FastDrawData[] drawArray;
	XYData[] xyDataArray;
	Color[] colors;

	/**
	 * constructor
	 * @param xTitle x title
	 * @param yTitle y title
	 */
	public MirrorCanvas( String xTitle, String yTitle ) {
		this.xTitle = xTitle;

		this.drawArray = new FastDrawData[ 2 ];
		this.xyDataArray = new XYData[ 2 ];
		this.colors = new Color[ 2 ];
	}

	/**
	 * adds xy data
	 * @param xyData
	 */
	public void addXYData( XYData xyData, FastDrawData data, Color color, int index ) {
		this.xyDataArray[ index ] = xyData;
		this.drawArray[ index ] = data;
		this.colors[ index ] = color;

		Double minX = xyDataArray[ 0 ].getMinX();
		Double maxX = xyDataArray[ 0 ].getMaxX();
		if( xyDataArray[ 1 ] != null ) {
			minX = Math.min( minX, xyDataArray[ 1 ].getMinX() );
			maxX = Math.max( maxX, xyDataArray[ 1 ].getMaxX() );
		}
		minX = Math.max( minX,  0.0 );
		maxX = Math.max( maxX,  minX + 0.0001 );
		this.xRange = new Range< Double >( minX, maxX );

		this.draw();
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
		Integer bottom = ProfileCanvas.DEFAULT_MARGIN;
		Integer left = ProfileCanvas.DEFAULT_MARGIN;

		left = left + this.getMaxYValueWidth( g, yRange, height, top, bottom );

		Rect< Integer > margin = new Rect< Integer >( top, right, bottom, left );
		return margin;
	}

	/**
	 * creates reverse matrix
	 * @param baseLine baseline
	 * @param margin margin
	 * @return reverse matrix
	 */
	protected RealMatrix createReverseMatrix( Integer height, Integer baseLine, Rect< Integer > margin ) {
		double[][] array0 = {
			{ 1.0, 0.0, 0.0 },
			{ 0.0, 1.0, (double)( - ( height - margin.getBottom() ) ) },
			{ 0.0, 0.0, 1.0 }
		};
		RealMatrix matrix0 = MatrixUtils.createRealMatrix( array0 );

		double[][] array1 = {
			{ 1.0, 0.0, 0.0 },
			{ 0.0, -1.0, 0.0 },
			{ 0.0, 0.0, 1.0 }
		};
		RealMatrix matrix1 = MatrixUtils.createRealMatrix( array1 );

		double[][] array2 = {
			{ 1.0, 0.0, 0.0 },
			{ 0.0, 1.0, (double)baseLine },
			{ 0.0, 0.0, 1.0 }
		};
		RealMatrix matrix2 = MatrixUtils.createRealMatrix( array2 );

		RealMatrix matrix = matrix2.multiply( matrix1 ).multiply( matrix0 );
		return matrix;
	}

	@Override
	protected void onDraw( GraphicsContext g, Integer width, Integer height ) {
		Integer baseLine = height / 2;

		Range< Double > xRange = this.xRange;

		List< List< DrawPoint > > arrays = new ArrayList< List< DrawPoint > >();
		List< DrawPoint > points0 = this.getPoints( this.drawArray[ 0 ], width, xRange );
		List< DrawPoint > points1 = this.getPoints( this.drawArray[ 1 ], width, xRange );
		Color color0 = this.colors[ 0 ];
		Color color1 = this.colors[ 1 ];
		arrays.add( points0 );
		arrays.add( points1 );
		Range< Double > yRange = this.getYRangeFromArray( arrays,  xRange );
		if( yRange == null ) {
			return;
		}

		Rect< Integer > margin = this.getMargin( g,  yRange,  width,  height );

		String xTitle = this.xTitle;

		Rect< Integer > margin0 = new Rect< Integer >(
			margin.getTop(),
			margin.getRight(),
			baseLine,
			margin.getLeft()
		);
		RealMatrix drawMatrix0 = this.getDrawMatrix(
			width,
			height,
			xRange,
			yRange,
			margin0,
			MatrixUtils.createRealIdentityMatrix( 3 )
		);
		this.drawProfile( g, points0, drawMatrix0, color0 );

		if( points1 != null ) {
			Rect< Integer > margin1 = new Rect< Integer >(
				baseLine,
				margin.getRight(),
				margin.getBottom(),
				margin.getLeft()
			);

			RealMatrix translation = this.createReverseMatrix( height, baseLine, margin );
			RealMatrix drawMatrix1 = this.getDrawMatrix(
				width,
				height,
				xRange,
				yRange,
				margin1,
				translation
			);
			this.drawProfile(g, points1,  drawMatrix1, color1 );
		}

		this.drawXScale( g, xRange, width, height, margin0, drawMatrix0, xTitle,  Color.BLACK );
	}

}
