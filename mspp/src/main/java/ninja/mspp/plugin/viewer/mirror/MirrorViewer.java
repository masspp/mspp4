package ninja.mspp.plugin.viewer.mirror;

import ninja.mspp.annotation.OpenSpectrum;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.dataobject.Spectrum;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.view.GuiManager;
import ninja.mspp.view.main.MainFrame;

@Plugin( name = "mirror canvas" )
public class MirrorViewer {
	/** canvas */
	private static MirrorCanvas canvas = null;

	/**
	 * constructor
	 */
	public MirrorViewer() {
	}

	@OpenSpectrum( view = "mirror" )
	public void addSpectrum( Spectrum spectrum ) {
		if( MirrorViewer.canvas == null ) {
			MirrorCanvas canvas = new MirrorCanvas( "m/z", "Int." );
			GuiManager gui = GuiManager.getInstance();
			MainFrame mainFrame = gui.getMainFrame();
			mainFrame.setMain( canvas );
			MirrorViewer.canvas = canvas;
		}

		XYData xyData = spectrum.getXYData();
		MirrorViewer.canvas.addXYData( xyData );
	}
}
