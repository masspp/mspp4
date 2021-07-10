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
package ninja.mspp.plugin.viewer.three_d;

import javafx.scene.Node;
import ninja.mspp.annotation.method.OnHeatmap;
import ninja.mspp.annotation.method.OnHeatmapRange;
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

	@SamplePanel("3D")
	public Node createPanel(@FxmlLoaderParam SpringFXMLLoader loader) throws Exception {
		Node node = loader.load(ThreeDPanel.class, "ThreeDPanel.fxml");
		this.panel = (ThreeDPanel)loader.getController();

		return node;
	}

	@OnHeatmap
	public void onHeatmap(Heatmap heatmap) {
		if(this.panel != null) {
			this.panel.setHeatmap(heatmap);
		}
	}

	@OnHeatmapRange
	public void onHeatmapRange(Heatmap heatmap) {
		if(this.panel != null) {
			if(this.panel.getHeatmap() == heatmap) {
				this.panel.setHeatmap(heatmap);
			}
		}
	}
}
