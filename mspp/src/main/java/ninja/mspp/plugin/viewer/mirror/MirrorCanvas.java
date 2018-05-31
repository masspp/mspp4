package ninja.mspp.plugin.viewer.mirror;

import java.util.ArrayList;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
	private String yTitle;
	private Range< Double > xRange;

	FastDrawData[] drawArray;
	XYData[] xyDataArray;

	/**
	 * constructor
	 * @param xTitle x title
	 * @param yTitle y title
	 */
	public MirrorCanvas( String xTitle, String yTitle ) {
		this.xTitle = xTitle;
		this.yTitle = yTitle;

		this.drawArray = new FastDrawData[ 2 ];
		this.xyDataArray = new XYData[ 2 ];
	}

	/**
	 * adds xy data
	 * @param xyData
	 */
	public void addXYData( XYData xyData ) {
		xyDataArray[ 1 ] = xyDataArray[ 0 ];
		xyDataArray[ 0 ] = xyData;
		drawArray[ 1 ] = drawArray[ 0 ];
		drawArray[ 0 ] = new FastDrawData( xyData );

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
	protected void onDraw(GraphicsContext g, Integer width, Integer height ) {
		Integer baseLine = height / 2;

		Range< Double > xRange = this.xRange;

		ArrayList< ArrayList< FastDrawData.Element > > arrays = new ArrayList< ArrayList< FastDrawData.Element > >();
		ArrayList< FastDrawData.Element > points0 = this.getPoints( this.drawArray[ 0 ], width, xRange );
		ArrayList< FastDrawData.Element > points1 = this.getPoints( this.drawArray[ 1 ], width, xRange );
		arrays.add( points0 );
		arrays.add( points1 );
		Range< Double > yRange = this.getYRange( arrays,  xRange );
		if( yRange == null ) {
			return;
		}

		Rect< Integer > margin = this.getMargin( g,  yRange,  width,  height );

		String xTitle = this.xTitle;
		String yTitle = this.yTitle;

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
		this.drawProfile( g, points0, drawMatrix0, Color.RED );

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
			this.drawProfile(g, points1,  drawMatrix1,  Color.RED );
		}

		this.drawXScale( g, xRange, width, height, margin0, drawMatrix0, xTitle,  Color.BLACK );
	}

}
