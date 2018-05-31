package ninja.mspp.plugin.viewer.heatmap;

import java.util.ArrayList;

import ninja.mspp.MsppManager;
import ninja.mspp.annotation.Menu;
import ninja.mspp.annotation.MenuAction;
import ninja.mspp.annotation.MenuPosition;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.model.dataobject.Sample;
import ninja.mspp.model.dataobject.Spectrum;
import ninja.mspp.model.gui.MenuInfo;
import ninja.mspp.view.GuiManager;
import ninja.mspp.view.main.MainFrame;

@Plugin( name = "heatmap viewer" )
@Menu
public class HeatMapViewer {
	private MenuInfo menu;

	/**
	 * constructor
	 */
	public HeatMapViewer() {
		this.menu = MenuInfo.TOOLS_MENU.item( "Heatmap" );
	}

	@MenuPosition
	public MenuInfo getPosition() {
		return this.menu;
	}

	@MenuAction
	public void action() {
		MsppManager manager = MsppManager.getInstance();
		Sample sample = manager.getSample( 0 );
		if( sample == null ) {
			return;
		}

		this.openHeatmap( sample );
	}

	/**
	 * opens heatmap
	 * @param sample sample
	 */
	protected void openHeatmap( Sample sample ) {
		ArrayList< Spectrum > spectra = this.getSpectra( sample );
		if( spectra.size() == 0 ) {
			return;
		}

		GuiManager gui = GuiManager.getInstance();
		MainFrame mainFrame = gui.getMainFrame();

		Heatmap heatmap = new Heatmap( spectra );
		HeatmapCanvas canvas = new HeatmapCanvas( heatmap, "RT", "m/z" );
		mainFrame.setMain( canvas );
	}

	/**
	 * gets the spectra
	 * @param sample sample
	 * @return spectra
	 */
	protected ArrayList< Spectrum > getSpectra( Sample sample ) {
		ArrayList< Spectrum > spectra = new ArrayList< Spectrum >();

		for( Spectrum spectrum : sample.getSpectra() ) {
			if( spectrum.getMsStage() > 1 && spectrum.getRt() > 0.0 ) {
				spectra.add( spectrum );
			}
		}

		return spectra;
	}


}
