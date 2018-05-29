package ninja.mspp.plugin.viewer.profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ninja.mspp.annotation.Menu;
import ninja.mspp.annotation.MenuAction;
import ninja.mspp.annotation.MenuPosition;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;
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

		ArrayList< Point< Double > > points = new ArrayList< Point< Double > >();

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
				Point< Double > point = new Point< Double >( x, y );
				points.add( point );
			}
		}
		reader.close();

		XYData xyData = new XYData( points );
		ProfileCanvas canvas = new ProfileCanvas( xyData, "m/z", "Int." );
		mainFrame.setMain( canvas );
	}
}
