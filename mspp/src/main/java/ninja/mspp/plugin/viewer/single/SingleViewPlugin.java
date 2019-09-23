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
 * @since 2019
 *
 * Copyright (c) Mon Sep 23 19:52:11 JST 2019 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.single;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import ninja.mspp.annotation.method.ChromatogramPanel;
import ninja.mspp.annotation.method.OnRawdataSample;
import ninja.mspp.annotation.method.SpectrumPanel;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.view.SpringFXMLLoader;

@Plugin( value = "Single View Plugin", order = 1 )
public class SingleViewPlugin {
	private List< SingleSpectrumPanel > spectrumPanels;
	private List< SingleChromatogramPanel > chromatogramPanels;

	/**
	 * constructor
	 */
	public SingleViewPlugin() {
		this.spectrumPanels = new ArrayList< SingleSpectrumPanel >();
		this.chromatogramPanels = new ArrayList< SingleChromatogramPanel >();
	}


	@SpectrumPanel( "Single View" )
	public Node openSpectrum(
			@FxmlLoaderParam SpringFXMLLoader loader
	) throws Exception {
		Node node = loader.load( SingleSpectrumPanel.class, "SingleSpectrumPanel.fxml" );
		SingleSpectrumPanel controller = ( SingleSpectrumPanel )loader.getController();
		this.spectrumPanels.add( controller );

		return node;
	}

	@ChromatogramPanel( "Single View" )
	public Node openChromatogram(
			@FxmlLoaderParam SpringFXMLLoader loader
	) throws Exception {
		Node node = loader.load( SingleChromatogramPanel.class, "SingleChromatogramPanel.fxml" );
		SingleChromatogramPanel controller = ( SingleChromatogramPanel )loader.getController();
		this.chromatogramPanels.add( controller );
		return node;
	}

	@OnRawdataSample
	public void onRawDataSample( Sample sample ) {
		for( SingleSpectrumPanel panel : this.spectrumPanels ) {
			panel.setSample( sample );
		}
		for( SingleChromatogramPanel panel : this.chromatogramPanels ) {
			panel.setSample( sample );
		}
	}
}
