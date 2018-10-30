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
package ninja.mspp.view.control.canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.Rect;

/**
 * profile canvas
 */
public abstract class ProfileCanvas extends Canvas {
	/** default margin */
	public static final Integer DEFAULT_MARGIN = 25;

	public static final Integer SCALE_RANGE = 30;

	public static final Integer SCALE_LENGTH = 5;

	public static final Integer SCALE_SPACING = 10;

	/**
	 * constructor
	 * @param xyData xy data
	 */
	public ProfileCanvas() {
		this.widthProperty().addListener( evt -> draw() );
		this.heightProperty().addListener( evt -> draw() );
	}

	/**
	 * on draw
	 * @param g graphics
	 */
	protected abstract void onDraw( GraphicsContext g, Integer width, Integer height );

	/**
	 * draw
	 */
	public void draw() {
		GraphicsContext g = this.getGraphicsContext2D();
		Double width = this.getWidth();
		Double height = this.getHeight();

		g.beginPath();
		g.setFill( Color.WHITE );
		g.setStroke( Color.WHITE );;
		g.rect( 0.0,  0.0,  width,  height );
		g.closePath();
		g.fill();

		this.onDraw( g, (int)Math.round( width ), (int)Math.round( height ) );
	}

	/**
	 * gets the draw points
	 * @param data fast draw data
	 * @param width window width
	 * @param xRange x range
	 * @return points
	 */
	protected ArrayList< FastDrawData.Element > getPoints( FastDrawData data, Integer width, Range< Double > xRange ) {
		if( data == null ) {
			return null;
		}
		Integer level = FastDrawData.getLevel( width, xRange.getEnd() - xRange.getStart() );
		ArrayList< FastDrawData.Element > points = data.getPoints( level );
		return points;
	}

	/**
	 * gets the y range
	 * @param points points
	 * @param xRange xrange
	 * @return y range
	 */
	protected Range< Double > getYRange( ArrayList< FastDrawData.Element > points, Range< Double > xRange ) {
		FastDrawData.Element startElement = new FastDrawData.Element( xRange.getStart(), 0.0 );
		FastDrawData.Element endElement = new FastDrawData.Element( xRange.getEnd(), 0.0 );

		int startIndex = Collections.binarySearch( points, startElement );
		if( startIndex < 0 ) {
			startIndex = - startIndex - 1;
		}
		int endIndex = Collections.binarySearch( points,  endElement );
		if( endIndex < 0 ) {
			endIndex = - endIndex - 2;
		}

		double minY = 0.0;
		double maxY = 0.0001;
		for( int i = startIndex; i <= endIndex; i++ ) {
			FastDrawData.Element element = points.get( i );
			minY = Math.min( minY,  element.getMin().getY() );
			maxY = Math.max( maxY,  element.getMax().getY() );
		}

		Range< Double > yRange = new Range< Double >( minY, maxY );
		return yRange;
	}

	/**
	 * gets the y range
	 * @param arrays arrays
	 * @param xRange x range
	 * @return y range
	 */
	protected Range< Double > getYRange( List< ArrayList< FastDrawData.Element > > arrays, Range< Double > xRange ) {
		Range< Double > yRange = null;
		for( ArrayList< FastDrawData.Element > array : arrays ) {
			if( array != null ) {
				Range< Double > tmpRange = this.getYRange( array, xRange );
				if( yRange == null ) {
					yRange = tmpRange;
				}
				else {
					yRange = new Range< Double >(
						Math.min( yRange.getStart(), tmpRange.getStart() ),
						Math.max( yRange.getEnd(), tmpRange.getEnd() )
					);
				}
			}
		}
		return yRange;
	}

	/**
	 * gets the draw matrix
	 * @param width width
	 * @param height height
	 * @param xRange x range
	 * @param yRange y range
	 * @param margin margin
	 * @param translation translation
	 * @return draw matrix
	 */
	protected RealMatrix getDrawMatrix(
			Integer width,
			Integer height,
			Range< Double > xRange,
			Range< Double > yRange,
			Rect< Integer > margin,
			RealMatrix translation
	) {
		double[][] i2data = {
			{ xRange.getEnd() - xRange.getStart(), 0.0, xRange.getStart() },
			{ 0.0, yRange.getEnd() - yRange.getStart(), yRange.getStart() },
			{ 0.0, 0.0, 1.0 }
		};
		RealMatrix i2dataMatrix = MatrixUtils.createRealMatrix( i2data );

		double[][] i2Window = {
			{ (double)( width - margin.getLeft() - margin.getRight() ), 0.0, (double)margin.getLeft() },
			{ 0.0, - (double)( height - margin.getTop() - margin.getBottom() ), (double)( height - margin.getBottom() ) },
			{ 0.0, 0.0, 1.0 }
		};
		RealMatrix i2WindowMatrix = MatrixUtils.createRealMatrix( i2Window );
		RealMatrix drawMatrix = i2WindowMatrix.multiply( MatrixUtils.inverse( i2dataMatrix ) );

		if( translation != null ) {
			drawMatrix = translation.multiply( drawMatrix );
		}
		return drawMatrix;
	}

	/**
	 * draws profile
	 * @param g graphics
	 * @param points points
	 * @param drawMatrix draw matrix
	 * @param paint paint
	 */
	protected void drawProfile( GraphicsContext g, ArrayList< FastDrawData.Element > points, RealMatrix drawMatrix, Paint paint	) {
		this.drawProfile( g, points, drawMatrix, paint, true );
	}

	/**
	 * draw
	 * @param g graphics
	 * @param points points
	 * @param matrix transform matrix
	 */
	protected void drawProfile( GraphicsContext g, ArrayList< FastDrawData.Element > points, RealMatrix drawMatrix, Paint paint, boolean centroid ) {
		g.beginPath();
		g.setStroke( paint );

		Point< Integer > previousPoint = null;
		for( FastDrawData.Element point : points ) {
			if( previousPoint != null && !centroid ) {
				Point< Integer > left = this.getPoint( point.getLeft(), drawMatrix );
				this.drawLine( g, previousPoint, left );
			}
			Point< Integer > top = this.getPoint( point.getMax(), drawMatrix );
			Point< Integer > bottom = this.getPoint( point.getMin(), drawMatrix );
			if( centroid ) {
				Point< Double > zero = new Point< Double >( point.getMax().getX(), 0.0 );
				bottom = this.getPoint( zero,  drawMatrix );
			}
			this.drawLine( g,  top,  bottom );

			previousPoint = this.getPoint( point.getRight(), drawMatrix );
		}

		g.closePath();
		g.stroke();
	}


	/**
	 * draw scales
	 * @param g graphis
	 * @param xRange x range
	 * @param yRange y range
	 * @param width width
	 * @param height height
	 * @param margin margin
	 * @param transformMatrix transform matrix
	 */
	protected void drawScale(
			GraphicsContext g,
			Range< Double > xRange,
			Range< Double > yRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix drawMatrix,
			String xTitle,
			String yTitle
	) {
		this.drawXScale( g, xRange, width, height, margin, drawMatrix, xTitle, Color.BLACK );
		this.drawYScale( g, yRange, width, height, margin, drawMatrix, yTitle, Color.BLACK );
	}

	/**
	 * draws x scale
	 * @param g graphics
	 * @param xRange x range
	 * @param width width
	 * @param margin margin
	 * @param transformMatrix transform matrix
	 */
	protected void drawXScale(
			GraphicsContext g,
			Range< Double > xRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix transformMatrix,
			String xTitle,
			Paint paint
	) {
		g.beginPath();
		g.setStroke( paint );

		Integer left = margin.getLeft();
		Integer right = width - margin.getRight();
		Integer py = height - margin.getBottom();

		Integer level = this.getScaleLevel( xRange.getEnd() - xRange.getStart(),  width - margin.getLeft() - margin.getRight() );
		Double unit = Math.pow( 10.0,  (double)level );

		String format = "%d";
		if( level < 0 ) {
			format = "%." + ( - level ) + "f";
		}

		Integer fontHeight = (int)Math.round( g.getFont().getSize() );

		Integer index = (int)Math.floor( xRange.getStart() / unit );
		Integer px = 0;
		Integer prevPx = 0;

		while( px <= right ) {
			double x = unit * (double)index;
			px = (int)Math.round( x * transformMatrix.getEntry( 0,  0 ) + 0.0 * transformMatrix.getEntry( 0,  1 ) + transformMatrix.getEntry( 0,  2 ) );
			String string = String.format( format, Math.round( x ) );
			if( level < 0 ) {
				string = String.format( format,  x );
			}

			Integer stringWidth = this.getTextWidth( g,  string );

			if( px >= left && px <= right ) {
				g.moveTo( px,  py );
				g.lineTo( px, py + ProfileCanvas.SCALE_LENGTH );

				if(  px - stringWidth / 2 > prevPx + ProfileCanvas.SCALE_SPACING ) {
					g.strokeText( string,  px - stringWidth / 2,  py + ProfileCanvas.SCALE_LENGTH + 1 + fontHeight );
					prevPx = px + stringWidth / 2;
				}
			}

			index++;
		}

		Integer stringWidth = this.getTextWidth( g,  xTitle );
		g.strokeText( xTitle, ( width - stringWidth ) / 2, py + ProfileCanvas.SCALE_LENGTH + fontHeight * 2 + 3 );

		g.moveTo( left,  py );
		g.lineTo( right,  py );

		g.closePath();
		g.stroke();
	}

	/**
	 * draws y scale
	 * @param g graphics
	 * @param yRange y range
	 * @param height height
	 * @param margin margin
	 * @param transformMatrix transform matrix
	 */
	private void drawYScale(
			GraphicsContext g,
			Range< Double > yRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix transformMatrix,
			String yTitle,
			Paint paint
	) {
		g.beginPath();
		g.setStroke( paint );

		Integer top = margin.getTop();
		Integer bottom = height - margin.getBottom();
		Integer px = margin.getLeft();

		Integer level = this.getScaleLevel( yRange.getEnd() - yRange.getStart(),  height - margin.getTop() - margin.getBottom() );
		Double unit = Math.pow( 10.0,  (double)level );

		String format = "%d";
		if( level < 0 ) {
			format = "%." + ( - level ) + "f";
		}

		Integer fontHeight = (int)Math.round( g.getFont().getSize() );

		Integer index = (int)Math.floor( yRange.getStart() / unit );
		Integer py = Integer.MAX_VALUE;

		Integer prevPy = bottom;

		while( py >= top ) {
			double y = unit * (double)index;
			py = (int)Math.round( 0.0 * transformMatrix.getEntry( 1,  0 ) + y * transformMatrix.getEntry( 1,  1 ) + transformMatrix.getEntry( 1,  2 ) );
			String string = String.format( format, Math.round( y ) );
			if( level < 0 ) {
				string = String.format( format,  y );
			}

			Integer stringWidth = this.getTextWidth( g,  string );

			if( py >= top && py <= bottom ) {
				g.moveTo( px,  py );
				g.lineTo( px - ProfileCanvas.SCALE_LENGTH, py );
				if( py < prevPy - ( fontHeight / 2 ) - ProfileCanvas.SCALE_SPACING  ) {
					g.strokeText( string,  px - ProfileCanvas.SCALE_LENGTH - stringWidth - 1, py + fontHeight / 2 );
					prevPy = py;
				}
			}

			index++;
		}

		Integer stringWidth = this.getTextWidth( g,  yTitle );
		g.strokeText( yTitle, px - stringWidth / 2, top - 1 );

		g.moveTo( px,  top );
		g.lineTo( px,  bottom );

		g.closePath();
		g.stroke();
	}

	/**
	 * gets the point
	 * @param data data point
	 * @param matrix matrix
	 * @return point
	 */
	Point< Integer > getPoint( Point< Double > data, RealMatrix matrix ) {
		double x = data.getX() * matrix.getEntry( 0,  0 ) + data.getY() * matrix.getEntry( 0, 1 ) + matrix.getEntry( 0,  2 );
		double y = data.getX() * matrix.getEntry( 1,  0 ) + data.getY() * matrix.getEntry( 1, 1 ) + matrix.getEntry( 1,  2 );

		Point< Integer > point = new Point< Integer >( (int)Math.round( x ), (int)Math.round( y ) );
		return point;
	}

	/**
	 * draws line
	 * @param g graphics
	 * @param point1 point 1
	 * @param point2 point 2
	 */
	void drawLine( GraphicsContext g, Point< Integer > point1, Point< Integer > point2 ) {
		g.moveTo( (double)point1.getX(), (double)point1.getY() );
		g.lineTo( (double)point2.getX(), (double)point2.getY() );
	}

	/**
	 * gets the max y value width
	 * @param g
	 * @param xRange
	 * @param yRange
	 * @param height
	 * @param topMargin
	 * @param bottomMargin
	 * @return
	 */
	protected Integer getMaxYValueWidth(
			GraphicsContext g,
			Range< Double > yRange,
			Integer height,
			Integer topMargin,
			Integer bottomMargin
	) {
		Integer level = this.getScaleLevel( yRange.getEnd() - yRange.getStart(),  height - topMargin - bottomMargin );

		String maxString = "0.0";
		if( level < 0 ) {
			String format = "%." + ( - level ) + "f";
			maxString = String.format( format,  yRange.getEnd() );
		}
		else {
			maxString = String.format( "%d", (int)Math.round( yRange.getEnd() ) );
		}

		return this.getTextWidth( g,  maxString );

	}

	/**
	 * gets the scale level
	 * @param range range
	 * @param width width
	 * @return
	 */
	Integer getScaleLevel( Double range, Integer width ) {
		double elementRange = range * ProfileCanvas.SCALE_RANGE.doubleValue() / width.doubleValue();
		int level = (int)Math.round( Math.log10( elementRange ) );
		return level;
	}

	/**
	 * gets the text width
	 * @param g graphics
	 * @param string string
	 * @return text width
	 */
	Integer getTextWidth( GraphicsContext g, String string ) {
		Text text = new Text( string );
		text.setFont( g.getFont() );
		int width = (int)Math.round( text.getLayoutBounds().getWidth() );
		return width;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public double prefWidth( double height ) {
		return 0.0;
	}

	@Override
	public double prefHeight( double width ) {
		return 0.0;
	}
}
