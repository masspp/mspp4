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
package ninja.mspp.plugin.viewer.overlap;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.Group;
import ninja.mspp.model.entity.GroupSample;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.service.ProjectService;
import ninja.mspp.service.RawDataService;
import ninja.mspp.view.list.CheckableChromatogramTableView;
import ninja.mspp.view.list.CheckableSpectrumTableView;
import ninja.mspp.view.list.SpectrumTableView;

@Component
public class IntegratedPanel implements Initializable {
	@Autowired
	private RawDataService rawdataService;

	@Autowired
	private ProjectService projectService;

	@FXML
	private BorderPane chromatogramTablePane;

	@FXML
	private BorderPane chromatogramCanvasPane;

	@FXML
	private BorderPane msTablePane;

	@FXML
	private BorderPane msCanvasPane;

	@FXML
	private BorderPane msmsTablePane;

	@FXML
	private BorderPane msmsCanvasPane;

	/** chromatogram table */
	private CheckableChromatogramTableView chromatogramTable;

	/** MS spectrum table */
	private CheckableSpectrumTableView msTable;

	/** MS/MS spectrum table */
	private SpectrumTableView msmsTable;

	/** color map */
	private Map< Spectrum, Color > colorMap;

	/** color map */
	private Map< Chromatogram, Color > chromatogramColorMap;

	/** chromtogram canvas */
	private OverlapCanvas chromatogramCanvas;

	/** MS canvas */
	private OverlapCanvas msCanvas;

	/** MS/MS canvas */
	private OverlapCanvas msmsCanvas;

	/**
	 * sets the peak
	 * @param project project
	 * @param position peak position
	 */
	public void setPeak( Project project, PeakPosition position ) {
		this.chromatogramTable.getItems().clear();
		this.msTable.getItems().clear();
		this.msmsTable.getItems().clear();

		if( project == null || position == null ) {
			return;
		}

		double mz = position.getMz();
		double rt = position.getRt();

		this.msCanvas.setXRange( new Range< Double >( Math.max( 0.0,  mz - 3.0 ), mz + 3.0 ) );
		this.chromatogramCanvas.setXRange( new Range< Double >( Math.max( 0.0,  rt - 1.0 ), rt + 1.0 ) );

		for( Group group : this.projectService.findGroups( project ) ) {
			Color color = Color.valueOf( group.getColor() );
			for( GroupSample groupSample : group.getGroupSamples() ) {
				Sample sample = groupSample.getSample();
				List< Spectrum > spectra = this.getSpectra( sample, rt );

				for( Spectrum spectrum : spectra ) {
					colorMap.put( spectrum,  color );
					if( spectrum.getMsStage() > 1 ) {
						this.msmsTable.getItems().add( spectrum );
					}
					else {
						this.msTable.getItems().add( spectrum );
						this.msTable.select( spectrum, true );
					}
				}

				Chromatogram chromatogram = this.getChromatogram( sample,  mz );
				this.chromatogramColorMap.put( chromatogram,  color );
				this.chromatogramTable.getItems().add( chromatogram );
				this.chromatogramTable.select( chromatogram,  true );;
			}
		}

		this.onMsSpectrum();
		this.onChromatogram();
	}

	/**
	 * gets the chromtogram
	 * @param sample sample
	 * @param mz m/z
	 * @return chromatogram
	 */
	private Chromatogram getChromatogram( Sample sample, double mz ) {
		Chromatogram chromatogram = null;
		List< Chromatogram > chromatograms = this.rawdataService.findChromatograms( sample );
		for( Chromatogram currentChromatogram : chromatograms ) {
			Double currentMz = currentChromatogram.getMz();
			if( currentMz != null ) {
				if( Math.abs( currentMz - mz ) <= 0.1 ) {
					chromatogram = currentChromatogram;
				}
			}
		}

		if( chromatogram == null ) {
			List< Point< Double > > points = new ArrayList< Point< Double > >();
			List< Spectrum > spectra = this.rawdataService.findSpectra( sample );
			for( Spectrum spectrum : spectra ) {
				if( spectrum.getMsStage() == 1 ) {
					double rt = spectrum.getStartRt();
					double intensity = 0.0;

					XYData xyData = this.rawdataService.findDataPoints( spectrum.getPointListId() );
					for( Point< Double > point : xyData ) {
						if( Math.abs( point.getX() - mz ) <= 0.5 ) {
							intensity += point.getY();
						}
					}
					points.add( new Point< Double >( rt, intensity ) );
				}
			}
			try {
				chromatogram = this.rawdataService.saveChromatogram( sample, points, String.format( "XIC (mz=%.2f)",  mz ), mz );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
		return chromatogram;
	}

	/**
	 * gets spectra
	 * @param sample sample
	 * @return spectra
	 */
	private List< Spectrum > getSpectra( Sample sample, double rt ) {
		List< Spectrum > list = new ArrayList< Spectrum >();

		Map< Long, Spectrum > idMap = new HashMap< Long, Spectrum >();
		Spectrum spectrum = null;
		double diff = 1.0;
		List< Spectrum > spectra = this.rawdataService.findSpectra( sample );
		for( Spectrum currentSpectrum : spectra ) {
			idMap.put( currentSpectrum.getId(), currentSpectrum );
			double currentDiff = Math.abs( currentSpectrum.getStartRt() - rt );
			if( currentDiff < diff ) {
				spectrum = currentSpectrum;
				diff = currentDiff;
			}
		}

		if( spectrum != null ) {
			if( spectrum.getMsStage() > 1 ) {
				spectrum = idMap.get( spectrum.getParentSpectrumId() );
			}
			list.add( spectrum );

			for( Spectrum currentSpectrum : spectra ) {
				Long parentId = currentSpectrum.getParentSpectrumId();
				if( parentId != null ) {
					if( parentId.equals( spectrum.getId() ) ) {
						list.add( currentSpectrum );
					}
				}
			}
		}

		return list;
	}

	/**
	 * on chromatogram
	 */
	private void onChromatogram() {
		List< Chromatogram > chromatograms = this.chromatogramTable.getSelectedChromatograms();
		this.chromatogramCanvas.clearData( false );
		for( Chromatogram chromatogram : chromatograms ) {
			XYData xyData = this.rawdataService.findDataPoints( chromatogram.getPointListId() );
			FastDrawData data = new FastDrawData( xyData );
			Color color = this.chromatogramColorMap.get( chromatogram );

			this.chromatogramCanvas.addXYData( xyData,  data,  color, false );
		}
		this.chromatogramCanvas.draw();

	}

	/**
	 * on MS spectrum
	 */
	private void onMsSpectrum() {
		List< Spectrum > spectra = this.msTable.getSelectedSpectra();
		this.msCanvas.clearData( false );
		for( Spectrum spectrum : spectra ) {
			XYData xyData = this.rawdataService.findDataPoints( spectrum.getPointListId() );
			FastDrawData data = new FastDrawData( xyData );
			Color color = this.colorMap.get( spectrum );

			this.msCanvas.addXYData( xyData,  data,  color, false );
		}

		this.msCanvas.draw();
	}

	/**
	 * on MS/MS spectrum
	 */
	private void onMsmsSpectrum() {
		Spectrum spectrum = this.msmsTable.getSelectionModel().getSelectedItem();
		this.msmsCanvas.clearData( false );
		if( spectrum == null ) {
			return;
		}

		XYData xyData = this.rawdataService.findDataPoints( spectrum.getPointListId() );
		FastDrawData data = new FastDrawData( xyData );
		Color color = this.colorMap.get( spectrum );

		this.msmsCanvas.addXYData( xyData,  data,  color, true );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		IntegratedPanel me = this;

		this.colorMap = new HashMap< Spectrum, Color >();
		this.chromatogramColorMap = new HashMap< Chromatogram, Color >();

		this.chromatogramTable = new CheckableChromatogramTableView();
		this.chromatogramTablePane.setCenter( this.chromatogramTable );
		this.chromatogramCanvas = new OverlapCanvas( "RT", "Int." );
		this.chromatogramCanvasPane.setCenter( this.chromatogramCanvas );
		this.chromatogramTable.setEvent(
			( chromatogram, selected ) -> {
				me.onChromatogram();
			}
		);
		this.chromatogramCanvas.widthProperty().bind( this.chromatogramCanvasPane.widthProperty() );
		this.chromatogramCanvas.heightProperty().bind( this.chromatogramCanvasPane.heightProperty() );


		this.msTable = new CheckableSpectrumTableView();
		this.msTablePane.setCenter( this.msTable );
		this.msCanvas = new OverlapCanvas( "m/z", "Int." );
		this.msCanvasPane.setCenter( this.msCanvas );
		this.msTable.setEvent(
			( spectrum, selected ) -> {
				me.onMsSpectrum();
			}
		);
		this.msCanvas.widthProperty().bind( this.msCanvasPane.widthProperty() );
		this.msCanvas.heightProperty().bind( this.msCanvasPane.heightProperty() );

		this.msmsTable = new SpectrumTableView();
		this.msmsTablePane.setCenter( this.msmsTable );
		this.msmsCanvas = new OverlapCanvas( "m/z", "Int." );
		this.msmsCanvas.setCentroid( true );
		this.msmsCanvasPane.setCenter( this.msmsCanvas );
		this.msmsTable.getSelectionModel().selectedItemProperty().addListener(
			( observable, oldValue, newValue ) -> {
				me.onMsmsSpectrum();
			}
		);
		this.msmsCanvas.widthProperty().bind( this.msmsCanvasPane.widthProperty() );
		this.msmsCanvas.heightProperty().bind( this.msmsCanvasPane.heightProperty() );
	}

}
