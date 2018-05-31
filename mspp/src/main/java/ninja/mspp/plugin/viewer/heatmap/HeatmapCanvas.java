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

	public HeatmapCanvas( Heatmap heatmap, String rtTitle, String mzTitle ) {
		this.heatmap = heatmap;
		this.rtTitle = rtTitle;
		this.mzTitle = mzTitle;
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

				int red = 255;
				if( intensity < 0.5 ) {
					red = (int)Math.round( 255.0 * intensity / 0.5 );
				}

				int green = 0;
				if( intensity > 0.5 ) {
					green = (int)Math.round( 255.0 * ( intensity - 0.5 ) / 0.5 );
				}

				int blue = 0;
				int alpha = 255;

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
