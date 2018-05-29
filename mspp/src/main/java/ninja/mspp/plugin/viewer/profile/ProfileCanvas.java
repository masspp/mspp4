package ninja.mspp.plugin.viewer.profile;

import java.util.ArrayList;
import java.util.Collections;

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
import ninja.mspp.model.dataobject.XYData;

/**
 * profile canvas
 */
public class ProfileCanvas extends Canvas {
	/** default margin */
	public static final Integer DEFAULT_MARGIN = 25;

	public static final Integer SCALE_RANGE = 30;

	public static final Integer SCALE_LENGTH = 5;

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

	/**
	 * constructor
	 * @param xyData xy data
	 */
	public ProfileCanvas( XYData xyData, String xTitle, String yTitle ) {
		this.xyData = xyData;
		this.drawData = new FastDrawData( xyData );
		this.xTitle = xTitle;
		this.yTitle = yTitle;

		double minX = Math.max( xyData.getMinX(), 0.0 );
		double maxX = Math.max( xyData.getMaxX(), minX + 0.0001 );
		this.xRange = new Range< Double >( minX, maxX );

		this.widthProperty().addListener( evt -> draw() );
		this.heightProperty().addListener( evt -> draw() );
		draw();
	}

	/**
	 * gets the draw data
	 * @return draw data
	 */
	public FastDrawData getDrawData() {
		return this.drawData;
	}

	/**
	 * draw
	 */
	private void draw() {
		this.draw( this.getGraphicsContext2D(), this.getDrawData() );
	}

	/**
	 * draw
	 * @param g graphics
	 * @param data data
	 */
	private void draw( GraphicsContext g, FastDrawData data ) {
		Double width = this.getWidth();
		Double height = this.getHeight();
		System.out.println( "width = " + width + ", height = " + height );

		g.beginPath();
		g.setFill( Color.WHITE );
		g.setStroke( Color.WHITE );;
		g.rect( 0.0,  0.0,  width,  height );
		g.closePath();
		g.fill();

		Integer level = FastDrawData.getLevel( (int)Math.round( width ), this.xRange.getEnd() - this.xRange.getStart() );
		ArrayList< FastDrawData.Element > points = this.drawData.getPoints( level );

		this.draw( g, points, (int)Math.round( width ), (int)Math.round( height ) );
	}

	/**
	 * draw
	 * @param g graphics
	 * @param points points
	 */
	private void draw( GraphicsContext g, ArrayList< FastDrawData.Element > points, Integer width, Integer height ) {
		FastDrawData.Element startElement = new FastDrawData.Element( this.xRange.getStart(), 0.0 );
		FastDrawData.Element endElement = new FastDrawData.Element( this.xRange.getEnd(), 0.0 );

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

		this.draw( g, points, this.xRange, yRange, width, height );
	}

	/**
	 * draw
	 * @param g graphics
	 * @param points points
	 * @param xRange x range
	 * @param yRange y range
	 * @param width window width
	 * @param height window height
	 */
	private void draw(
			GraphicsContext g,
			ArrayList< FastDrawData.Element > points,
			Range< Double > xRange,
			Range< Double > yRange,
			Integer width,
			Integer height
	) {
		Rect< Integer > margin = this.getMargin( g, xRange, yRange, width, height );

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

		RealMatrix transformMatrix = i2WindowMatrix.multiply( MatrixUtils.inverse( i2dataMatrix ) );

		this.drawProfile( g, Color.RED, points, transformMatrix );
		this.drawScale( g, xRange, yRange, width, height, margin, transformMatrix );
	}

	/**
	 * draw
	 * @param g graphics
	 * @param points points
	 * @param matrix transform matrix
	 */
	private void drawProfile( GraphicsContext g, Paint paint, ArrayList< FastDrawData.Element > points, RealMatrix matrix ) {
		g.beginPath();
		g.setStroke( paint );

		Point< Integer > previousPoint = null;
		for( FastDrawData.Element point : points ) {
			if( previousPoint != null ) {
				Point< Integer > left = this.getPoint( point.getLeft(), matrix );
				this.drawLine( g, previousPoint, left );
			}
			Point< Integer > top = this.getPoint( point.getMax(), matrix );
			Point< Integer > bottom = this.getPoint( point.getMin(), matrix );
			this.drawLine( g,  top,  bottom );

			previousPoint = this.getPoint( point.getRight(), matrix );
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
	private void drawScale(
			GraphicsContext g,
			Range< Double > xRange,
			Range< Double > yRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix transformMatrix
	) {
		g.beginPath();
		g.setStroke( Color.BLACK );

		drawXScale( g, xRange, width, height, margin, transformMatrix );
		drawYScale( g, yRange, width, height, margin, transformMatrix );

		g.closePath();
		g.stroke();
	}

	/**
	 * draws x scale
	 * @param g graphics
	 * @param xRange x range
	 * @param width width
	 * @param margin margin
	 * @param transformMatrix transform matrix
	 */
	private void drawXScale(
			GraphicsContext g,
			Range< Double > xRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix transformMatrix
	) {
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

				if(  px - stringWidth / 2 > prevPx ) {
					g.strokeText( string,  px - stringWidth / 2,  py + ProfileCanvas.SCALE_LENGTH + 1 + fontHeight );
				}
			}

			prevPx = px + stringWidth / 2;
			index++;
		}

		Integer stringWidth = this.getTextWidth( g,  this.xTitle );
		g.strokeText( this.xTitle, ( width - stringWidth ) / 2, py + ProfileCanvas.SCALE_LENGTH + fontHeight * 2 + 3 );

		g.moveTo( left,  py );
		g.lineTo( right,  py );
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
			RealMatrix transformMatrix
	) {
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
				g.strokeText( string,  px - ProfileCanvas.SCALE_LENGTH - stringWidth - 1, py + fontHeight / 2 );
			}

			index++;
		}

		Integer stringWidth = this.getTextWidth( g,  this.yTitle );
		g.strokeText( this.yTitle, px - stringWidth / 2, top - 1 );

		g.moveTo( px,  top );
		g.lineTo( px,  bottom );
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
	 *
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

		Integer level = this.getScaleLevel( xRange.getEnd() - xRange.getStart(),  height - top - bottom );

		String maxString = "0.0";
		if( level < 0 ) {
			String format = "%." + ( - level ) + "f";
			maxString = String.format( format,  yRange.getEnd() );
		}
		else {
			maxString = String.format( "%d", (int)Math.round( yRange.getEnd() ) );
		}

		left = left + this.getTextWidth( g,  maxString );

		Rect< Integer > margin = new Rect< Integer >( top, right, bottom, left );
		return margin;
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
