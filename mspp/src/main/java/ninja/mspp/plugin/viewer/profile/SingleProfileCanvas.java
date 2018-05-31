package ninja.mspp.plugin.viewer.profile;

import java.util.ArrayList;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.DrawSpectrumForeground;
import ninja.mspp.model.PluginMethod;
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

	/**
	 * constructor
	 * @param xyData xy data
	 * @param xTitle x title
	 * @param yTitle y title
	 */
	public SingleProfileCanvas(XYData xyData, String xTitle, String yTitle) {
		this.xTitle = xTitle;
		this.yTitle = yTitle;

		setXYData( xyData );
	}

	/**
	 * sets the xy data
	 * @param xyData xy data
	 */
	public void setXYData( XYData xyData ) {
		this.xyData = xyData;
		this.drawData = new FastDrawData( xyData );

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
			ArrayList< FastDrawData.Element > points,
			Range< Double > xRange,
			Range< Double > yRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix drawMatrix
	) {
		MsppManager manager = MsppManager.getInstance();
		ArrayList< PluginMethod< DrawSpectrumForeground > > methods = manager.getMethods( DrawSpectrumForeground.class );
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
	protected void onDraw(GraphicsContext g, Integer width, Integer height) {
		Range< Double > xRange = this.xRange;
		ArrayList< FastDrawData.Element > points = this.getPoints( this.getDrawData(),  width,  xRange );
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

		this.drawProfile( g,  points,  drawMatrix,  Color.RED );

		this.drawForeGround( g, points, xRange, yRange, width, height, margin, drawMatrix );

		this.drawScale( g,  xRange,  yRange,  width, height, margin, drawMatrix, xTitle, yTitle );
	}
}
