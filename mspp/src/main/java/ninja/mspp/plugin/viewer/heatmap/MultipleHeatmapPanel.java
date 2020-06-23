/*
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author satstnka
 * @since 2019
 *
 * Copyright (c) 2019 satstnka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.heatmap;

import java.net.URL;
import java.util.List;
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
import ninja.mspp.model.entity.Spectrum;
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
         *
         * @param point
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
		List< Spectrum > spectra = this.rawdataService.findSpectra( sample );
		Heatmap heatmap = new Heatmap( spectra, this.rawdataService );
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
