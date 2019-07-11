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
 * @since 2018-05-31 22:40:25+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.single;

import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.method.DrawSpectrumForeground;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.dataobject.DrawPoint;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.Rect;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.view.control.canvas.ProfileCanvas;

/**
 * single profile canvas
 */
public class SingleProfileCanvas extends ProfileCanvas {
	/** draw data */
	private FastDrawData drawData;

	/** xy data */
	XYData xyData;

	/** x range */
	Range< Double > xRange;

	/** x title */
	String xTitle;

	/** y title */
	String yTitle;

	/** color */
	Color color;

	/** centroid mode */
	boolean centroid;

	/**
	 * constructor
	 * @param xyData xy data
	 * @param xTitle x title
	 * @param yTitle y title
	 */
	public SingleProfileCanvas( XYData xyData, FastDrawData drawData, String xTitle, String yTitle, Color color, boolean centroid ) {
		this.xTitle = xTitle;
		this.yTitle = yTitle;
		this.color = color;
		this.centroid = centroid;

		setXYData( xyData, drawData );
	}

	/**
	 * sets the xy data
	 * @param xyData xy data
	 */
	public void setXYData( XYData xyData, FastDrawData drawData ) {
		this.xyData = xyData;
		this.drawData = drawData;

		double minX = Math.max( xyData.getMinX(), 0.0 );
		double maxX = Math.max( xyData.getMaxX(), minX + 0.0001 );
		this.xRange = new Range< Double >( minX, maxX );

		this.draw();
	}

	/**
	 * gets the draw data
	 * @return draw data
	 */
	public FastDrawData getDrawData() {
		return this.drawData;
	}

	/**
	 * gets the margin
	 * @param g
	 * @param xRange
	 * @param yRange
	 * @param width
	 * @param height
	 * @return
	 */
	Rect< Integer > getMargin( GraphicsContext g, Range< Double > xRange, Range< Double > yRange, Integer width, Integer height ) {
		Integer top = ProfileCanvas.DEFAULT_MARGIN;
		Integer right = ProfileCanvas.DEFAULT_MARGIN;
		Integer bottom = 50;
		Integer left = ProfileCanvas.DEFAULT_MARGIN;

		left = left + this.getMaxYValueWidth( g, yRange, height, top, bottom );

		Rect< Integer > margin = new Rect< Integer >( top, right, bottom, left );
		return margin;
	}

	/**
	 * draws foreground
	 * @param g graphics
	 * @param points points
	 * @param xRange x range
	 * @param yRange y range
	 * @param width width
	 * @param height height
	 * @param margin margin
	 * @param drawMatrix draw matrix
	 */
	void drawForeGround(
			GraphicsContext g,
			List< DrawPoint > points,
			Range< Double > xRange,
			Range< Double > yRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix drawMatrix
	) {
		MsppManager manager = MsppManager.getInstance();
		List< PluginMethod< DrawSpectrumForeground > > methods = manager.getMethods( DrawSpectrumForeground.class );
		for( PluginMethod< DrawSpectrumForeground > method : methods ) {
			Object plugin = method.getPlugin();
			try {
				method.getMethod().invoke( plugin,  g, points, xRange, yRange, width, height, margin, drawMatrix );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDraw( GraphicsContext g, Integer width, Integer height ) {
		Range< Double > xRange = this.xRange;
		List< DrawPoint > points = this.getPoints( this.getDrawData(),  width,  xRange );
		Range< Double > yRange = this.getYRange( points,  xRange );
		Rect< Integer > margin = this.getMargin( g,  xRange,  yRange,  width,  height );
		RealMatrix drawMatrix = this.getDrawMatrix(
				width,
				height,
				xRange,
				yRange,
				margin,
				MatrixUtils.createRealIdentityMatrix( 3 )
		);
		String xTitle = this.xTitle;
		String yTitle = this.yTitle;

		this.drawProfile( g,  points,  drawMatrix,  this.color, this.centroid );
		this.drawForeGround( g, points, xRange, yRange, width, height, margin, drawMatrix );
		this.drawScale( g,  xRange,  yRange,  width, height, margin, drawMatrix, xTitle, yTitle );
	}
}
