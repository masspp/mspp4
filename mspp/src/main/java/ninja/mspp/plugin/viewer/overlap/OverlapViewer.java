package ninja.mspp.plugin.viewer.overlap;

import ninja.mspp.annotation.OpenSpectrum;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.dataobject.Spectrum;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.view.GuiManager;
import ninja.mspp.view.main.MainFrame;

@Plugin( name = "overlap canvas")
public class OverlapViewer {
	/** canvas */
	private static OverlapCanvas canvas = null;

	/**
	 * constructor
	 */
	public OverlapViewer() {
	}

	@OpenSpectrum( view = "overlap" )
	public void addSpectrum( Spectrum spectrum ) {
		if( OverlapViewer.canvas == null ) {
			OverlapCanvas canvas = new OverlapCanvas( "m/z", "Int." );
			GuiManager gui = GuiManager.getInstance();
			MainFrame mainFrame = gui.getMainFrame();
			mainFrame.setMain( canvas );
			OverlapViewer.canvas = canvas;
		}

		XYData xyData = spectrum.getXYData();
		OverlapViewer.canvas.addXYData( xyData );
	}
}
