package ninja.mspp.plugin.viewer.three_d;

import javafx.scene.Node;
import ninja.mspp.annotation.method.OnHeatmap;
import ninja.mspp.annotation.method.SamplePanel;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.view.SpringFXMLLoader;

@Plugin( "3D Panel" )
public class ThreeDPlugin {
	private ThreeDPanel panel;

	/**
	 * constructor
	 */
	public ThreeDPlugin() {
		this.panel = null;
	}

	@SamplePanel( "3D" )
	public Node createPanel( @FxmlLoaderParam SpringFXMLLoader loader ) throws Exception {
		Node node = loader.load( ThreeDPanel.class, "ThreeDPanel.fxml" );
		this.panel = ( ThreeDPanel )loader.getController();

		return node;
	}

	@OnHeatmap
	public void onHeatmap( Heatmap heatmap ) {
		if( panel != null ) {
			this.panel.setHeatmap( heatmap );
		}
	}
}
