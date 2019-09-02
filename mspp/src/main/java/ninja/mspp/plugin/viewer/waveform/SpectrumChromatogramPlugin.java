package ninja.mspp.plugin.viewer.waveform;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import ninja.mspp.annotation.method.SamplePanel;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.view.SpringFXMLLoader;

@Plugin( value = "Spectrum Chromatogram Panel", order = 1 )
public class SpectrumChromatogramPlugin {

	private List< SpectrumChromatogramPanel > controllers;

	/**
	 * constructor
	 */
	public SpectrumChromatogramPlugin() {
		this.controllers = new ArrayList< SpectrumChromatogramPanel >();
	}

	@SamplePanel( "Spectrum / Chromatogram" )
	public Node createPanel( @FxmlLoaderParam SpringFXMLLoader loader ) throws Exception {
		Node node = loader.load( SpectrumChromatogramPanel.class, "SpectrumChromatogramPanel.fxml" );
		SpectrumChromatogramPanel controller = ( SpectrumChromatogramPanel )loader.getController();
		if( controller != null ) {
			this.controllers.add( controller );
		}

		return node;
	}
}
