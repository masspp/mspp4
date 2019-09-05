package ninja.mspp.plugin.viewer.heatmap;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import ninja.mspp.model.dataobject.Heatmap;

@Component
public class HeatmapPanel implements Initializable {
	@FXML
	private BorderPane panel;

	/**
	 * sets the sample
	 * @param sample sample
	 */
	public void setHeatmap( Heatmap heatmap ) {
		if( heatmap == null ) {
			this.panel.setCenter( null );
		}
		else {
			HeatmapCanvas canvas = new HeatmapCanvas( heatmap );
			this.panel.setCenter( canvas );
			canvas.widthProperty().bind( this.panel.widthProperty() );
			canvas.heightProperty().bind( this.panel.heightProperty() );
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

}
