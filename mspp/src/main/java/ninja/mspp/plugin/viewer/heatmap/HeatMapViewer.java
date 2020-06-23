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
 * @author Satoshi Tanaka
 * @since 2018
 *
 * Copyright (c) 2018 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.heatmap;

import javafx.scene.Node;
import ninja.mspp.annotation.method.AnalysisPanel;
import ninja.mspp.annotation.method.OnHeatmap;
import ninja.mspp.annotation.method.OnSelectPeak;
import ninja.mspp.annotation.method.SamplePanel;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.view.SpringFXMLLoader;

@Plugin( "heatmap viewer" )
public class HeatMapViewer {
	private HeatmapPanel panel;
	private MultipleHeatmapPanel multiplePanel;

	/**
	 * constructor
	 */
	public HeatMapViewer() {
		this.panel = null;
	}

	@SamplePanel( "Heatmap" )
	public Node createPanel( @FxmlLoaderParam SpringFXMLLoader loader ) throws Exception {
		Node node = loader.load( HeatmapPanel.class, "HeatmapPanel.fxml" );
		this.panel = ( HeatmapPanel )loader.getController();
		return node;
	}

	@AnalysisPanel( "Heatmap" )
	public Node createMultipleHeatmapPanel(
			@FxmlLoaderParam SpringFXMLLoader loader,
			Project project
	) throws Exception {
		Node node = loader.load( MultipleHeatmapPanel.class,  "MultipleHeatmapPanel.fxml" );
		MultipleHeatmapPanel panel = ( MultipleHeatmapPanel )loader.getController();
		this.multiplePanel = panel;
		panel.setProject( project );

		return node;
	}

	@OnHeatmap
	public void onRawDataSample( Heatmap heatmap ) {
		if( this.panel != null ) {
			this.panel.setHeatmap( heatmap );
		}
	}

	@OnSelectPeak
	public void onSelectPeak( PeakPosition position ) {
		if( this.multiplePanel != null ) {
			Point< Double > point = null;
			if( position != null ) {
				point = new Point< Double >( position.getRt(), position.getMz() );
			}
			this.multiplePanel.setPoint( point );
		}
	}
}
