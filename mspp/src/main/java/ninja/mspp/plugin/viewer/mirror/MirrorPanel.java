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
 * @since Mon Sep 16 11:08:33 JST 2019
 *
 * Copyright (c) 2019 satstnka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.mirror;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.service.RawDataService;
import ninja.mspp.view.list.ChromatogramTableView;
import ninja.mspp.view.list.SpectrumTableView;

@Component
public class MirrorPanel implements Initializable {
	private int spectrumIndex;
	private int chromatogramIndex;
	private SpectrumTableView spectrumTable;
	private ChromatogramTableView chromatogramTable;
	private MirrorCanvas spectrumCanvas;
	private MirrorCanvas chromatogramCanvas;
	private Map< Spectrum, Color > spectrumColorMap;
	private Map< Chromatogram, Color > chromatogramColorMap;

	@Autowired
	private RawDataService rawdataService;


	@FXML
	private Button spectrumButton;

	@FXML
	private Button chromatogramButton;

	@FXML
	private Button chromatogramDownButton;

	@FXML
	private BorderPane spectrumTablePane;

	@FXML
	private BorderPane spectrumCanvasPane;

	@FXML
	private BorderPane chromatogramTablePane;

	@FXML
	private BorderPane chromatogramCanvasPane;

	@FXML
	private void onSpectrum( ActionEvent event ) {
		this.spectrumIndex = 1 - this.spectrumIndex;
		Text icon = null;
		if( this.spectrumIndex == 0 ) {
			icon = GlyphsDude.createIcon( FontAwesomeIcon.ARROW_UP );
		}
		else {
			icon = GlyphsDude.createIcon( FontAwesomeIcon.ARROW_DOWN );
		}
		this.spectrumButton.setGraphic( icon );
	}

	@FXML
	private void onChromatogram( ActionEvent event ) {
		this.chromatogramIndex = 1 - this.chromatogramIndex;
		Text icon = null;
		if( this.chromatogramIndex == 0 ) {
			icon = GlyphsDude.createIcon( FontAwesomeIcon.ARROW_UP );
		}
		else {
			icon = GlyphsDude.createIcon( FontAwesomeIcon.ARROW_DOWN );
		}
		this.chromatogramButton.setGraphic( icon );
	}

	/**
	 * sets the peak position
	 * @param project
	 * @param position
	 */
	public void setPeak( Project project, PeakPosition position ) {
		this.spectrumCanvasPane.setCenter( new Label( "Now Loading..." ) );
		this.chromatogramCanvasPane.setCenter( new Label( "Now Loading..." ) );

		this.spectrumColorMap = new HashMap< Spectrum, Color >();
		this.chromatogramColorMap = new HashMap< Chromatogram, Color >();

		this.spectrumTable.getItems().clear();
		this.chromatogramTable.getItems().clear();

		this.spectrumCanvas = null;
		this.chromatogramCanvas = null;
	}

	/**
	 * sets chromatograms
	 * @param chromatograms chromatograms
	 * @param colorMap color map
	 */
	public void setChromatograms( List< Chromatogram > chromatograms, Map< Chromatogram, Color > colorMap ) {
		if( this.chromatogramCanvas == null ) {
			this.chromatogramCanvas = new MirrorCanvas( "RT", "Int." );
			this.chromatogramCanvasPane.setCenter( this.chromatogramCanvas );
			this.chromatogramCanvas.widthProperty().bind( this.chromatogramCanvasPane.widthProperty() );
			this.chromatogramCanvas.heightProperty().bind( this.chromatogramCanvasPane.heightProperty() );
		}
		for( Chromatogram chromatogram : chromatograms ) {
			this.chromatogramTable.getItems().add( chromatogram );
		}
		this.chromatogramColorMap.putAll( colorMap );
	}

	/**
	 * saets MS spectra
	 * @param spectra MS spectra
	 * @param colorMap color map
	 */
	public void setSpectra( List< Spectrum > spectra, Map< Spectrum, Color > colorMap ) {
		if( this.spectrumCanvas == null ) {
			this.spectrumCanvas = new MirrorCanvas( "m/z", "Int." );
			this.spectrumCanvasPane.setCenter( this.spectrumCanvas );
			this.spectrumCanvas.widthProperty().bind( this.spectrumCanvasPane.widthProperty() );
			this.spectrumCanvas.heightProperty().bind( this.spectrumCanvasPane.heightProperty() );
		}
		for( Spectrum spectrum : spectra ) {
			this.spectrumTable.getItems().add( spectrum );
		}
		this.spectrumColorMap.putAll( colorMap );
	}


	/**
	 * sets buttons
	 */
	private void setButtons() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.ARROW_UP );
		this.spectrumButton.setText( "" );
		this.spectrumButton.setGraphic( icon );

		icon = GlyphsDude.createIcon( FontAwesomeIcon.ARROW_UP );
		this.chromatogramButton.setText( "" );
		this.chromatogramButton.setGraphic( icon );
	}

	/**
	 * opens spectrum
	 * @param spectrum
	 */
	private void openSpectrum( Spectrum spectrum ) {
		if( spectrum == null || this.spectrumCanvas == null ) {
			return;
		}
		Color color = this.spectrumColorMap.get( spectrum );

		XYData points = this.rawdataService.findDataPoints( spectrum.getPointListId() );
		FastDrawData data = new FastDrawData( points );
		this.spectrumCanvas.addXYData( points,  data,  color, this.spectrumIndex );
		this.spectrumCanvas.draw();
	}

	/**
	 * opens chromatogram
	 * @param chromatogram
	 */
	private void openChromatogram( Chromatogram chromatogram ) {
		if( chromatogram == null || this.chromatogramCanvas == null ) {
			return;
		}
		Color color =this.chromatogramColorMap.get( chromatogram );

		XYData points = this.rawdataService.findDataPoints( chromatogram.getPointListId() );
		FastDrawData data = new FastDrawData( points );
		this.chromatogramCanvas.addXYData( points,  data,  color, this.chromatogramIndex );
		this.chromatogramCanvas.draw();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MirrorPanel me = this;

		this.setButtons();

		this.spectrumIndex = 0;
		this.chromatogramIndex = 0;

		this.spectrumTable = new SpectrumTableView();
		this.spectrumTablePane.setCenter( this.spectrumTable );
		this.spectrumTable.getSelectionModel().selectedItemProperty().addListener(
			( observable, oldValue, newValue ) -> {
				me.openSpectrum( newValue );
			}
		);

		this.spectrumCanvas = new MirrorCanvas( "m/z", "Int." );
		this.spectrumCanvasPane.setCenter( this.spectrumCanvas );

		this.chromatogramTable = new ChromatogramTableView();
		this.chromatogramTablePane.setCenter( this.chromatogramTable );
		this.chromatogramTable.getSelectionModel().selectedItemProperty().addListener(
			( observable, oldValue, newValue ) -> {
				me.openChromatogram( newValue );
			}
		);

		this.chromatogramCanvas = new MirrorCanvas( "RT", "Int." );
		this.chromatogramCanvasPane.setCenter( this.chromatogramCanvas );
	}
}
