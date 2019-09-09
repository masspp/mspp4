package ninja.mspp.plugin.viewer.heatmap;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.entity.Group;
import ninja.mspp.model.entity.GroupSample;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.service.ProjectService;
import ninja.mspp.service.RawDataService;
import ninja.mspp.view.list.SampleTableView;

@Component
public class MultipleHeatmapPanel implements Initializable {
	private SampleTableView table;
	private HeatmapCanvas canvas;

	@Autowired
	private RawDataService rawdataService;

	@Autowired
	private ProjectService projectService;

	@FXML
	private BorderPane samplesPanel;

	@FXML
	private BorderPane canvasPanel;

	/**
	 * sets the project
	 * @param project
	 */
	public void setProject( Project project ) {
		this.canvasPanel.setCenter( null );
		this.table.getItems().clear();

		for( Group group : this.projectService.findGroups( project ) ) {
			for( GroupSample groupSample : group.getGroupSamples() ) {
				Sample sample = groupSample.getSample();
				this.table.getItems().add( sample );
			}
		}
	}

	/**
	 * sets the point
	 * @param x x
	 * @param y y
	 */
	public void setPoint( Point< Double > point ) {
		if( this.canvas != null ) {
			this.canvas.getDisplayPoints().clear();
			this.canvas.getDisplayPoints().add( point );
			this.canvas.draw();
		}
	}

	/**
	 * opens heatmap
	 * @param sample
	 */
	private void openHeatmap( Sample sample ) {
		Heatmap heatmap = new Heatmap( sample.getSpectras(), this.rawdataService );
		this.canvas = new HeatmapCanvas( heatmap );
		canvasPanel.setCenter( this.canvas );
		canvas.widthProperty().bind( canvasPanel.widthProperty() );
		canvas.heightProperty().bind( canvasPanel.heightProperty() );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.table = new SampleTableView();
		this.samplesPanel.setCenter( this.table );

		MultipleHeatmapPanel me = this;
		this.table.setOnMouseClicked(
			( event ) -> {
				Sample sample = me.table.getSelectionModel().getSelectedItem();
				me.openHeatmap( sample );
			}
		);
	}

}
