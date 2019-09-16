/**
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
 * @author Mass++ Users Group
 * @author Satoshi Tanaka
 * @since 2018-06-01 05:51:10+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
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
