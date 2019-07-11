package ninja.mspp.plugin.viewer.single;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.service.PointsService;
import ninja.mspp.view.list.SpectrumTableView;

@Scope( BeanDefinition.SCOPE_PROTOTYPE )
@Component
public class SingleSpectrumPanel implements Initializable {
	@FXML
	private BorderPane canvasPane;

	@FXML
	private BorderPane listPane;

	@Autowired
	private PointsService service;

	// table
	private SpectrumTableView table;

	// canvas
	private SingleProfileCanvas canvas;

	public SingleSpectrumPanel() {
		this.canvas = null;
	}

	// on sample
	public void setSample( Sample sample ) {
		this.table.getItems().clear();
		for( Spectrum spectrum : sample.getSpectras() ) {
			this.table.getItems().add( spectrum );
		}
	}

	// on spectrum
	private void onSpectrum( Spectrum spectrum ) {
		int pointId = spectrum.getPointListId();
		PointList list = this.service.findPointList( pointId );
		XYData xyData = list.getXYData();
		FastDrawData data = this.service.getFastDrawData( list );
		SingleProfileCanvas canvas = new SingleProfileCanvas(
			xyData,
			data,
			list.getXunit(),
			list.getYunit(),
			Color.RED,
			spectrum.getCentroidMode() > 0
		);
		this.canvas = canvas;
		this.canvasPane.setCenter( canvas );
		this.canvas.widthProperty().bind( this.canvasPane.widthProperty() );
		this.canvas.heightProperty().bind( this.canvasPane.heightProperty() );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SpectrumTableView table = new SpectrumTableView();
		listPane.setCenter( table );
		this.table = table;

		SingleSpectrumPanel me = this;
		this.table.getSelectionModel().selectedItemProperty().addListener(
			( observable, oldValue, newValue ) -> {
				me.onSpectrum( newValue );
			}
		);
	}
}
