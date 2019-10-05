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
package ninja.mspp.plugin.viewer.heatmap;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.method.OnHeatmapRange;
import ninja.mspp.model.dataobject.ColorTheme;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.Rect;
import ninja.mspp.view.ColorManager;
import ninja.mspp.view.control.canvas.ProfileCanvas;

/**
 * heatmap canvas
 */
public class HeatmapCanvas extends ProfileCanvas {
	private Heatmap heatmap;
	private String rtTitle;
	private String mzTitle;
	private Image image;

	private Point< Double > startPosition;
	private Point< Double > endPosition;

	private RealMatrix matrix;

	private Stack< Rect< Double > > rangeStack;

	private List< Point< Double > > displayPoints;

	private ColorTheme theme;

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
		this.image = null;
		this.displayPoints = new ArrayList< Point< Double > >();

		this.rangeStack = new Stack< Rect< Double > >();

		HeatmapCanvas me = this;

		this.widthProperty().addListener(
			( observable, oldValue, newValue ) -> {
				this.image = null;
				me.draw();
			}
		);
		this.heightProperty().addListener(
			( observable, oldValue, newValue ) -> {
				this.image = null;
				me.draw();
			}
		);

		this.setOnMousePressed(
			( event ) -> {
				me.onMousePress( event );
			}
		);
		this.setOnMouseDragged(
			( event ) -> {
				me.onMouseDrag( event );
			}
		);
		this.setOnMouseReleased(
			( event ) -> {
				me.onMouseRelease( event );
			}
		);
		this.setOnMouseClicked(
			( event ) -> {
				me.onMouseClick( event );
			}
		);

		this.theme = ColorManager.getInstance().getThemes().get( 1 );

		this.draw();
	}

	/**
	 * constructor
	 * @param heatmap heatmap data
	 */
	public HeatmapCanvas( Heatmap heatmap ) {
		this( heatmap, "RT", "m/z" );
	}

	public List<Point<Double>> getDisplayPoints() {
		return displayPoints;
	}

	public void setDisplayRanges(List<Point<Double>> displayPoints) {
		this.displayPoints = displayPoints;
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

		if( this.image == null ) {
			WritableImage image = new WritableImage( rtSize, mzSize );
			PixelWriter writer = image.getPixelWriter();

			WritablePixelFormat< IntBuffer > format = WritablePixelFormat.getIntArgbInstance();
			int[] pixels = new int[ rtSize * mzSize ];

			for( int i = 0; i < mzSize; i++ ) {
				for( int j = 0; j < rtSize; j++ ) {
					int index = ( mzSize - 1 - i ) * rtSize + j;
					double intensity = Math.sqrt( data[ j ][ i ] );
					intensity = Math.max( 0.0, Math.min( 1.0,  intensity ) );
					int pixel = this.theme.getColor( intensity ).getPixel();
					pixels[ index ] = pixel;
				}
			}
			writer.setPixels( 0,  0,  rtSize,  mzSize, format, pixels, 0, rtSize );
			this.image = image;
		}

		g.drawImage(
			this.image,
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

	/**
	 * draws display points
	 * @param g graphics
	 * @param matrix matrix
	 * @param displayPoints display points
	 * @param margin margin
	 */
	private void drawDisplayPoints(
			GraphicsContext g,
			RealMatrix matrix,
			List< Point< Double > > displayPoints,
			int width,
			int height,
			Rect< Integer > margin
	) {
		for( Point< Double > point : displayPoints ) {
			double x = point.getX();
			double y = point.getY();
			if( x >= this.heatmap.getRtRange().getStart() && x <= this.heatmap.getRtRange().getEnd()
					&& y >= this.heatmap.getMzRange().getStart() && y <= this.heatmap.getMzRange().getEnd() ) {
				Point< Integer > windowPoint = this.getPoint( point, matrix );
				int windowLeft = margin.getLeft();
				int windowTop = margin.getTop();
				int windowRight = width - margin.getRight();
				int windowBottom = height - margin.getBottom();

				int left = windowPoint.getX() - 2;
				left = Math.max( windowLeft, Math.min( windowRight, left ) );
				int right = windowPoint.getX() + 3;
				right = Math.max( windowLeft, Math.min( windowRight, right ) );
				int top = windowPoint.getY() - 2;
				top = Math.max( windowTop,  Math.min( windowBottom, top ) );
				int bottom = windowPoint.getY() + 3;
				bottom = Math.max( windowTop,  Math.min( windowBottom, bottom ) );

				double lineWidth = g.getLineWidth();
				g.setLineWidth( 2.0 );
				g.setStroke( Color.MAGENTA );
				g.setFill( Color.TRANSPARENT );
				g.strokeRect( ( double )left,  ( double )top,  ( double )( right - left ),  ( double )( bottom - top ) );
				g.setLineWidth( lineWidth );
			}
		}
	}

	/**
	 * draws rect
	 * @param g g
	 * @param matrix matrix
	 */
	private void drawRect( GraphicsContext g, RealMatrix matrix ) {
		if( this.startPosition == null || this.endPosition == null ) {
			return;
		}
		Point< Integer > startPoint = this.getPoint( this.startPosition, matrix );
		Point< Integer > endPoint = this.getPoint( this.endPosition, matrix );

		int px = Math.min( startPoint.getX(),  endPoint.getX() );
		int py = Math.min( startPoint.getY(), endPoint.getY() );
		int width = Math.abs( endPoint.getX() - startPoint.getX() );
		int height = Math.abs( endPoint.getY() - startPoint.getY() );

		g.setStroke( Color.WHITE );
		g.setFill( Color.TRANSPARENT );
		g.strokeRect( ( double )px, ( double )py, ( double )width,  ( double )height );
	}

	/**
	 * changes range
	 */
	private void changeRange() {
		if( this.heatmap == null || this.startPosition == null || this.endPosition == null ) {
			return;
		}

		MsppManager manager = MsppManager.getInstance();

		double startRt = Math.min( this.startPosition.getX(), this.endPosition.getX() );
		double endRt = Math.max( this.startPosition.getX(), this.endPosition.getX() );
		double startMz = Math.min( this.startPosition.getY(), this.endPosition.getY() );
		double endMz = Math.max( this.startPosition.getY(), this.endPosition.getY() );

		if( endRt - startRt >= 0.001 && endMz - startMz >= 0.001 ) {
			Rect< Double > range = new Rect< Double >(
					this.heatmap.getMzRange().getEnd(),
					this.heatmap.getRtRange().getEnd(),
					this.heatmap.getMzRange().getStart(),
					this.heatmap.getRtRange().getStart()
			);
			this.rangeStack.push( range );
			this.heatmap.changeRange( startRt, endRt, startMz, endMz );
			this.image = null;

			try {
				manager.invokeAll( OnHeatmapRange.class, this.heatmap );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}

		this.startPosition = null;
		this.endPosition = null;
		this.draw();
	}

	/**
	 * on mouse pressed
	 * @param event mouse event
	 */
	private void onMousePress( MouseEvent event ) {
		if( this.heatmap == null ) {
			return;
		}

		int px = ( int )Math.round( event.getX() );
		int py = ( int )Math.round( event.getY() );
		Point< Double > point  = this.inversePoint( new Point< Integer >( px, py ), this.matrix );

		double x = point.getX();
		double y = point.getY();

		if( x >= this.heatmap.getRtRange().getStart() && x <= this.heatmap.getRtRange().getEnd()
				&& y >= this.heatmap.getMzRange().getStart() && y <= this.heatmap.getMzRange().getEnd() ) {
			this.startPosition = point;
			this.endPosition = null;
		}
		else {
			this.startPosition = null;
			this.endPosition = null;
		}
	}

	/**
	 * on mouse dragged
	 * @param event mouse event
	 */
	private void onMouseDrag( MouseEvent event ) {
		if( this.heatmap == null ) {
			return;
		}

		if( this.startPosition != null ) {
			int px = ( int )Math.round( event.getX() );
			int py = ( int )Math.round( event.getY() );
			Point< Double > point  = this.inversePoint( new Point< Integer >( px, py ), this.matrix );

			double x = point.getX();
			double y = point.getY();

			x = Math.max( this.heatmap.getRtRange().getStart(), Math.min( x,  this.heatmap.getRtRange().getEnd() ) );
			y = Math.max( this.heatmap.getMzRange().getStart(), Math.min( y,  this.heatmap.getMzRange().getEnd() ) );

			point = new Point< Double >( x, y );
			this.endPosition = point;

			this.draw();
		}
	}

	/**
	 * on mouse released
	 * @param event mouse event
	 */
	private void onMouseRelease( MouseEvent event ) {
		if( this.heatmap == null ) {
			return;
		}

		if( this.startPosition != null ) {
			int px = ( int )Math.round( event.getX() );
			int py = ( int )Math.round( event.getY() );
			Point< Double > point  = this.inversePoint( new Point< Integer >( px, py ), this.matrix );

			double x = point.getX();
			double y = point.getY();

			x = Math.max( this.heatmap.getRtRange().getStart(), Math.min( x,  this.heatmap.getRtRange().getEnd() ) );
			y = Math.max( this.heatmap.getMzRange().getStart(), Math.min( y,  this.heatmap.getMzRange().getEnd() ) );

			point = new Point< Double >( x, y );
			this.endPosition = point;

			this.changeRange();
		}
	}

	/**
	 * on mouse click
	 * @param event
	 */
	private void onMouseClick( MouseEvent event ) {
		if( event.getClickCount() >= 2 ) {
			this.startPosition = null;
			this.endPosition = null;

			if( !this.rangeStack.isEmpty() && this.heatmap != null ) {
				Rect< Double > range = this.rangeStack.pop();
				this.heatmap.changeRange(
					range.getLeft(),
					range.getRight(),
					range.getBottom(),
					range.getTop()
				);
				this.image = null;
				this.draw();
			}
		}
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
		this.matrix = matrix;

		this.drawImage( g, width, height, margin, heatmap.getData() );
		this.drawDisplayPoints( g, matrix, this.displayPoints, width, height, margin );

		this.drawRect( g, matrix );

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
