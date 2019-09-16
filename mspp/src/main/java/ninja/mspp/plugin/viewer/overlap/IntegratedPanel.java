package ninja.mspp.plugin.viewer.overlap;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.service.RawDataService;
import ninja.mspp.view.list.CheckableChromatogramTableView;
import ninja.mspp.view.list.CheckableSpectrumTableView;
import ninja.mspp.view.list.SpectrumTableView;

@Component
public class IntegratedPanel implements Initializable {
	@Autowired
	private RawDataService rawdataService;

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

	/** chromtogram canvas */
	private OverlapCanvas chromatogramCanvas;

	/** MS canvas */
	private OverlapCanvas msCanvas;

	/** MS/MS canvas */
	private OverlapCanvas msmsCanvas;

	private Map< Spectrum, Color > msColorMap;
	private Map< Spectrum, Color > msmsColorMap;
	private Map< Chromatogram, Color > chromatogramColorMap;

	/**
	 * sets the peak
	 * @param project project
	 * @param position peak position
	 */
	public void setPeak( Project project, PeakPosition position ) {
		this.chromatogramTable.clearSelection();
		this.chromatogramTable.getItems().clear();
		this.msTable.clearSelection();
		this.msTable.getItems().clear();
		this.msmsTable.getItems().clear();

		this.msCanvasPane.setCenter( new Label( "Loading..." ) );
		this.msmsCanvasPane.setCenter( new Label( "Loading..." ) );
		this.chromatogramCanvasPane.setCenter( new Label( "Loading..." ) );

		if( project == null || position == null ) {
			return;
		}

		double mz = position.getMz();
		double rt = position.getRt();

		this.msCanvas.setXRange( new Range< Double >( Math.max( 0.0,  mz - 3.0 ), mz + 3.0 ) );
		this.msCanvas.clearData( true );
		this.chromatogramCanvas.setXRange( new Range< Double >( Math.max( 0.0,  rt - 1.0 ), rt + 1.0 ) );
		this.chromatogramCanvas.clearData( true );
		this.msmsCanvas.clearData( true );

		this.msColorMap = null;
		this.msmsColorMap = null;
		this.chromatogramColorMap = null;
	}

	/**
	 * set chromatograms
	 * @param chromatograms
	 * @param colorMap
	 */
	public void setChromatograms( List< Chromatogram > chromatograms, Map< Chromatogram, Color > colorMap ) {
		this.chromatogramCanvasPane.setCenter( this.chromatogramCanvas );
		this.chromatogramColorMap = colorMap;

		this.chromatogramTable.clearSelection();
		this.chromatogramTable.getItems().clear();

		for( Chromatogram chromatogram : chromatograms ) {
			this.chromatogramTable.getItems().add( chromatogram );
			this.chromatogramTable.select( chromatogram,  true );
		}

		this.drawChromatograms();
	}

	/**
	 * on chromatogram
	 */
	private void drawChromatograms() {
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
	public void setMsSpectrum( List< Spectrum > spectra, Map< Spectrum, Color > colorMap ) {
		this.msCanvasPane.setCenter( this.msCanvas );
		this.msTable.getItems().clear();
		for( Spectrum spectrum : spectra ) {
			this.msTable.getItems().add( spectrum );
			this.msTable.select( spectrum,  true );
		}
		this.msColorMap = colorMap;
		this.drawMsSpectra();
	}

	/**
	 * draws ms spectra
	 */
	private void drawMsSpectra() {
		List< Spectrum > spectra = this.msTable.getSelectedSpectra();
		this.msCanvas.clearData( false );

		for( Spectrum spectrum : spectra ) {
			XYData xyData = this.rawdataService.findDataPoints( spectrum.getPointListId() );
			FastDrawData data = new FastDrawData( xyData );
			Color color = this.msColorMap.get( spectrum );
			this.msCanvas.addXYData( xyData,  data,  color, false );
		}
		this.msCanvas.draw();
	}

	/**
	 * on MS/MS spectrum
	 */
	public void setMsmsSpectrum( List< Spectrum > spectra, Map< Spectrum, Color > colorMap ) {
		this.msmsCanvasPane.setCenter( this.msmsCanvas );
		this.msmsCanvas.clearData( true );
		this.msmsTable.getItems().clear();
		this.msmsColorMap = colorMap;
		for( Spectrum spectrum : spectra ) {
			this.msmsTable.getItems().add( spectrum );
		}
	}

	/**
	 * draws MS/MS spectra
	 */
	private void drawMsmsSpectrum() {
		Spectrum spectrum = this.msmsTable.getSelectionModel().getSelectedItem();
		this.msmsCanvas.clearData( false );

		if( spectrum != null ) {
			XYData xyData = this.rawdataService.findDataPoints( spectrum.getPointListId() );
			FastDrawData data = new FastDrawData( xyData );
			Color color = this.msmsColorMap.get( spectrum );
			this.msmsCanvas.addXYData( xyData,  data,  color, false );
		}
		this.msmsCanvas.draw();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		IntegratedPanel me = this;

		this.chromatogramTable = new CheckableChromatogramTableView();
		this.chromatogramTablePane.setCenter( this.chromatogramTable );
		this.chromatogramCanvas = new OverlapCanvas( "RT", "Int." );
		this.chromatogramCanvasPane.setCenter( this.chromatogramCanvas );
		this.chromatogramTable.setEvent(
			( chromatogram, selected ) -> {
				me.drawChromatograms();
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
				me.drawMsSpectra();
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
				me.drawMsmsSpectrum();
			}
		);
		this.msmsCanvas.widthProperty().bind( this.msmsCanvasPane.widthProperty() );
		this.msmsCanvas.heightProperty().bind( this.msmsCanvasPane.heightProperty() );
	}

}
