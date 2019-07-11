package ninja.mspp.plugin.viewer.single;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import ninja.mspp.annotation.method.ChromatogramPanel;
import ninja.mspp.annotation.method.OnRawdataSample;
import ninja.mspp.annotation.method.SpectrumPanel;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.view.SpringFXMLLoader;

@Plugin( value = "Single View Plugin", order = 1 )
public class SingleViewPlugin {
	private List< SingleSpectrumPanel > spectrumPanels;
	private List< SingleChromatogramPanel > chromatogramPanels;

	/**
	 * constructor
	 */
	public SingleViewPlugin() {
		this.spectrumPanels = new ArrayList< SingleSpectrumPanel >();
		this.chromatogramPanels = new ArrayList< SingleChromatogramPanel >();
	}


	@SpectrumPanel( "Single View" )
	public Node openSpectrum(
			@FxmlLoaderParam SpringFXMLLoader loader
	) throws Exception {
		Node node = loader.load( SingleSpectrumPanel.class, "SingleSpectrumPanel.fxml" );
		SingleSpectrumPanel controller = ( SingleSpectrumPanel )loader.getController();
		this.spectrumPanels.add( controller );

		return node;
	}

	@ChromatogramPanel( "Single View" )
	public Node openChromatogram(
			@FxmlLoaderParam SpringFXMLLoader loader
	) throws Exception {
		Node node = loader.load( SingleChromatogramPanel.class, "SingleChromatogramPanel.fxml" );
		SingleChromatogramPanel controller = ( SingleChromatogramPanel )loader.getController();
		this.chromatogramPanels.add( controller );
		return node;
	}

	@OnRawdataSample
	public void onRawDataSample( Sample sample ) {
		for( SingleSpectrumPanel panel : this.spectrumPanels ) {
			panel.setSample( sample );
		}
		for( SingleChromatogramPanel panel : this.chromatogramPanels ) {
			panel.setSample( sample );
		}
	}
}
