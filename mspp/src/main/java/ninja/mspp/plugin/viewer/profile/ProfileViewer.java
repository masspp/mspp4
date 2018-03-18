package ninja.mspp.plugin.viewer.profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import ninja.mspp.annotation.Menu;
import ninja.mspp.annotation.MenuAction;
import ninja.mspp.annotation.MenuPosition;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.gui.MenuInfo;
import ninja.mspp.view.GuiManager;
import ninja.mspp.view.main.MainFrame;

@Plugin( name = "Profile Viewer")
@Menu
public class ProfileViewer {
	/** menu position */
	private MenuInfo menu;

	/**
	 * constructor
	 */
	public ProfileViewer() {
		this.menu = MenuInfo.TOOLS_MENU.item( "Profile Viewer Test" );
	}

	@MenuPosition
	public MenuInfo getMenuPosition() {
		return this.menu;
	}

	@MenuAction
	public void openPane() throws Exception {
		GuiManager guiManager = GuiManager.getInstance();
		MainFrame mainFrame = guiManager.getMainFrame();

		BufferedReader reader = new BufferedReader(
			new InputStreamReader(
					this.getClass().getResourceAsStream( "/samples/sample_spectrum.txt" )
			)
		);

		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel( "m/z" );
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel( "Intensity" );
		LineChart< Number, Number > chart = new LineChart< Number, Number >( xAxis, yAxis );
		chart.setTitle( "Sample Spectrum" );

		XYChart.Series< Number, Number > series = new Series< Number, Number >();
		Double minX = Double.POSITIVE_INFINITY;
		Double maxX = Double.NEGATIVE_INFINITY;
		Double maxY = 1.0;

		String line = null;
		while( ( line = reader.readLine() ) != null ) {
			StringTokenizer tokenizer = new StringTokenizer( line );
			ArrayList< Double > values = new ArrayList< Double >();
			while( tokenizer.hasMoreTokens() ) {
				try {
					values.add( Double.parseDouble( tokenizer.nextToken() ) );
				}
				catch( Exception e ) {
				}
			}

			if( values.size() >= 2 ) {
				double x = values.get( 0 );
				double y = values.get( 1 );
				minX = Math.min( x,  maxX );
				maxX = Math.max( x,  maxX );
				maxY = Math.max( y,  maxY );
				series.getData().add( new Data< Number, Number >( x, y ) );
			}
		}

		reader.close();

		chart.setCreateSymbols( false );
		chart.setLegendVisible( false );
		chart.getData().add( series );
		mainFrame.setMain( chart );
	}
}
