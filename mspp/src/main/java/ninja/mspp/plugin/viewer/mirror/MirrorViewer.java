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
package ninja.mspp.plugin.viewer.mirror;

import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import ninja.mspp.annotation.method.AnalysisPanel;
import ninja.mspp.annotation.method.OnPeakChromatograms;
import ninja.mspp.annotation.method.OnPeakMsSpectra;
import ninja.mspp.annotation.method.OnPeakMsmsSpectra;
import ninja.mspp.annotation.method.OnSelectPeak;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.view.SpringFXMLLoader;

@Plugin( value = "mirror canvas", order = 7 )
public class MirrorViewer {
	/** panel */
	private MirrorPanel mirrorPanel;

	/** project */
	private Project project;

	/**
	 * constructor
	 */
	public MirrorViewer() {
	}

 	@AnalysisPanel( "Mirror" )
	public Node getMirrorPanel(
			@FxmlLoaderParam SpringFXMLLoader loader,
			Project project
	) {
		this.project = project;
		Parent parent = null;
		try {
			parent = loader.load( MirrorPanel.class, "MirrorPanel.fxml" );
			this.mirrorPanel = ( MirrorPanel )loader.getController();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return parent;
	}

	@OnSelectPeak
	public void setPeak( PeakPosition position ) {
		if( this.project != null && this.mirrorPanel != null ) {
			this.mirrorPanel.setPeak( this.project,  position );
		}
	}

	@OnPeakMsSpectra
	public void setMsSpectra( List< Spectrum > spectra, Map< Spectrum, Color > colorMap ) {
		if( this.project != null && this.mirrorPanel != null ) {
			this.mirrorPanel.setSpectra( spectra,  colorMap );
		}
	}

	@OnPeakMsmsSpectra
	public void setMsmsSpectra( List< Spectrum > spectra, Map< Spectrum, Color > colorMap ) {
		if( this.project != null && this.mirrorPanel != null ) {
			this.mirrorPanel.setSpectra( spectra,  colorMap );
		}
	}

	@OnPeakChromatograms
	public void setChromatograms( List< Chromatogram > chromatograms, Map< Chromatogram, Color > colorMap ) {
		if( this.project != null && this.mirrorPanel != null ) {
			this.mirrorPanel.setChromatograms( chromatograms, colorMap );
		}
	}
}
