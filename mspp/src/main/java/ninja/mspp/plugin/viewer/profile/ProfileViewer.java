package ninja.mspp.plugin.viewer.profile;

import ninja.mspp.annotation.OpenSpectrum;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.dataobject.Spectrum;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.view.GuiManager;
import ninja.mspp.view.main.MainFrame;

@Plugin( name = "Profile Viewer")
public class ProfileViewer {
	private static SingleProfileCanvas canvas;

	/**
	 * constructor
	 */
	public ProfileViewer() {
	}

	@OpenSpectrum( view = "profile" )
	public void openSpectrumCanvas( Spectrum spectrum ) {
		GuiManager gui = GuiManager.getInstance();
		MainFrame mainFrame = gui.getMainFrame();

		XYData xyData = spectrum.getXYData();

		if( ProfileViewer.canvas == null ) {
			SingleProfileCanvas canvas = new SingleProfileCanvas( xyData, "m/z", "Int." );
			mainFrame.setMain( canvas );
			ProfileViewer.canvas = canvas;
		}
		else {
			ProfileViewer.canvas.setXYData( xyData );
		}
	}
}
