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
 * @since Fri Jun 01 05:51:10 JST 2018
 *
 * Copyright (c) 2018 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.overlap;

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
 * overlap canvas
 */
public class OverlapCanvas extends ProfileCanvas {
	private String xTitle;
	private String yTitle;
	private Range< Double > displayedXRange;
	private Range< Double > xRange;

	private List< XYData > xyDataArray;
	private List< FastDrawData > drawArray;
	private List< Color > colorArray;

	private boolean centroid;


	/**
	 * constructor
	 * @param xTitle
	 * @param yTitle
	 */
	public OverlapCanvas( String xTitle, String yTitle ) {
		this.xTitle = xTitle;
		this.yTitle = yTitle;

		this.xyDataArray = new ArrayList< XYData >();
		this.drawArray = new ArrayList< FastDrawData >();
		this.colorArray = new ArrayList< Color >();

		this.centroid = false;
	}

	public boolean isCentroid() {
		return centroid;
	}

	public void setCentroid(boolean centroid) {
		this.centroid = centroid;
	}

	/**
	 * adds xy data
	 * @param xyData xy data
	 */
	public void addXYData( XYData xyData, FastDrawData data, Color color, boolean refresh ) {
		this.xyDataArray.add( xyData );
		this.drawArray.add( data );
		this.colorArray.add( color );
		this.displayedXRange = null;

		if( refresh ) {
			this.draw();
		}
	}

	public Range<Double> getXRange() {
		return xRange;
	}

	public void setXRange(Range<Double> xRange) {
		this.xRange = xRange;
	}

	/**
	 * clears data
	 */
	public void clearData( boolean refresh ) {
		this.xyDataArray.clear();
		this.drawArray.clear();
		this.colorArray.clear();
		this.displayedXRange = null;

		if( refresh ) {
			this.draw();
		}
	}

	/**
	 * gets the x range
	 * @param array xy data array
	 * @return x range
	 */
	protected Range< Double > getXRange( List< XYData > array ) {
		if( array == null || array.size() == 0 ) {
			return null;
		}

		if( this.xRange != null ) {
			return this.xRange;
		}

		Double minX = null;
		Double maxX = null;

		for( XYData data : array ) {
			double currentMinX = data.getMinX();
			double currentMaxX = data.getMaxX();

			if( minX == null || maxX == null ) {
				minX = currentMinX;
				maxX = currentMaxX;
			}
			else {
				minX = Math.min( minX,  currentMinX );
				maxX = Math.max( maxX,  currentMaxX );
			}
		}

		minX = Math.max( 0.0,  minX );
		maxX = Math.max( maxX,  minX + 0.0001 );

		Range< Double > range = new Range< Double >( minX, maxX );
		return range;
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

	@Override
	protected void onDraw(GraphicsContext g, Integer width, Integer height) {
		if( this.xyDataArray.isEmpty() ) {
			return;
		}

		if( this.displayedXRange == null ) {
			this.displayedXRange = this.getXRange( this.xyDataArray );
		}

		Range< Double > xRange = this.displayedXRange;
		List< List< DrawPoint > > arrays = new ArrayList< List< DrawPoint > >();
		for( FastDrawData data : this.drawArray ) {
			arrays.add( this.getPoints( data,  width,  xRange ) );
		}

		Range< Double > yRange = this.getYRangeFromArray( arrays,  xRange );

		Rect< Integer > margin = this.getMargin( g,  yRange,  width,  height );

		String xTitle = this.xTitle;
		String yTitle = this.yTitle;

		RealMatrix matrix = this.getDrawMatrix(
			width,
			height,
			xRange,
			yRange,
			margin,
			MatrixUtils.createRealIdentityMatrix( 3 )
		);

		g.setGlobalAlpha( 0.5 );

		for( int i = 0; i < arrays.size(); i++ ) {
			Color color = this.colorArray.get( i );
			this.drawProfile( g,  arrays.get( i ),  matrix,  color, this.centroid );
		}

		g.setGlobalAlpha( 1.0 );
		this.drawScale( g,  xRange,  yRange,  width,  height,  margin,  matrix,  xTitle,  yTitle );
	}

}
