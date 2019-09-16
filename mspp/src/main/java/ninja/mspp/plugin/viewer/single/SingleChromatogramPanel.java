package ninja.mspp.plugin.viewer.single;

import java.net.URL;
import java.util.List;
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
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.service.RawDataService;
import ninja.mspp.view.list.ChromatogramTableView;

@Scope( BeanDefinition.SCOPE_PROTOTYPE )
@Component
public class SingleChromatogramPanel implements Initializable {
	@FXML
	private BorderPane canvasPane;

	@FXML
	private BorderPane listPane;

	@Autowired
	private RawDataService rawDataService;

	// canvas
	private SingleProfileCanvas canvas;

	// table
	private ChromatogramTableView table;

	// set sample
	public void setSample( Sample sample ) {
		this.table.getItems().clear();
		List< Chromatogram > chromatograms = this.rawDataService.findChromatograms( sample );
		for( Chromatogram chromatogram : chromatograms ) {
			this.table.getItems().add( chromatogram );
		}
	}

	// on chromatogram
	private void onChromatogram( Chromatogram chromatogram ) {
		Long pointId = chromatogram.getPointListId();
		XYData xyData = this.rawDataService.findDataPoints( pointId );
		FastDrawData data = new FastDrawData( xyData );
		SingleProfileCanvas canvas = new SingleProfileCanvas(
			xyData,
			data,
			"RT",
			"Int.",
			Color.BLUE,
			false
		);
		this.canvas = canvas;
		this.canvasPane.setCenter( canvas );
		this.canvas.widthProperty().bind( this.canvasPane.widthProperty() );
		this.canvas.heightProperty().bind( this.canvasPane.heightProperty() );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ChromatogramTableView table = new ChromatogramTableView();
		listPane.setCenter( table );
		this.table = table;

		SingleChromatogramPanel me = this;
		this.table.getSelectionModel().selectedItemProperty().addListener(
			( observable, oldValue, newValue ) -> {
				me.onChromatogram( newValue );
			}
		);
	}
}
