package ninja.mspp.plugin.viewer.heatmap;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.service.RawDataService;

@Component
public class HeatmapPanel implements Initializable {
	@FXML
	private BorderPane panel;

	@Autowired
	private RawDataService service;

	/**
	 * sets the sample
	 * @param sample sample
	 */
	public void setSample( Sample sample ) {
		if( sample == null ) {
			this.panel.setCenter( null );
		}
		else {
			Heatmap heatmap = new Heatmap( sample.getSpectras(), this.service );
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
