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
 * @since Fri Sep 06 13:47:14 JST 2019
 *
 * Copyright (c) 2019 satstnka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.project;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;
import ninja.mspp.ObjectManager;
import ninja.mspp.annotation.method.AnalysisPanel;
import ninja.mspp.annotation.method.OnPeakChromatograms;
import ninja.mspp.annotation.method.OnPeakMsSpectra;
import ninja.mspp.annotation.method.OnPeakMsmsSpectra;
import ninja.mspp.annotation.method.OnSelectPeak;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.Group;
import ninja.mspp.model.entity.GroupSample;
import ninja.mspp.model.entity.PeakAnnotation;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.service.ProjectService;
import ninja.mspp.service.RawDataService;
import ninja.mspp.tools.FXTools;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.main.MainFrame;

@Component
public class ProjectDetailsPanel implements Initializable {
	/** recent file key */
	private static String RECENT_FILE_KEY = "Recent Open File";

	/** project */
	private Project project;

	@Autowired
	private ProjectService service;

	@Autowired
	private RawDataService rawdataService;

	@FXML
	private TableView< PeakPosition > table;

	@FXML
	private TabPane tabPane;

	@FXML
	private Button importButton;

	@FXML
	private Button analysisButton;

	/**
	 * creates table
	 */
	private void createTable() {
		TableColumn< PeakPosition, Double > doubleColumn = new TableColumn< PeakPosition, Double >( "RT" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< PeakPosition, Double >( "rt" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.table.getColumns().add( doubleColumn );

		doubleColumn = new TableColumn< PeakPosition, Double >( "m/z" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< PeakPosition, Double >( "mz" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.table.getColumns().add( doubleColumn );

		TableColumn< PeakPosition, Number > numberColumn = new TableColumn< PeakPosition, Number >( "Score" );
		numberColumn.setPrefWidth( 100.0 );
		numberColumn.setCellValueFactory(
			( cellData ) -> {
				Double value = null;
				PeakPosition position = cellData.getValue();
				PeakAnnotation annotation = null;
				List< PeakAnnotation > annotations = position.getPeakAnnotations();
				if( annotations != null && !annotations.isEmpty() ) {
					annotation = annotations.get( 0 );
				}
				if( annotation != null ) {
					value = annotation.getScore();
				}
				if( value == null ) {
					return null;
				}
				return new ReadOnlyDoubleWrapper( value );
			}
		);
		FXTools.setTableColumnRightAlign( numberColumn );
		this.table.getColumns().add( numberColumn );


		TableColumn< PeakPosition, String > stringColumn = new TableColumn< PeakPosition, String >( "Protein" );
		stringColumn.setPrefWidth( 200.0 );
		stringColumn.setCellValueFactory(
				( cellData ) -> {
					String value = "";
					PeakPosition position = cellData.getValue();
					PeakAnnotation annotation = null;
					List< PeakAnnotation > annotations = position.getPeakAnnotations();
					if( annotations != null && !annotations.isEmpty() ) {
						annotation = annotations.get( 0 );
					}
					if( annotation != null ) {
						value = annotation.getProtein();
					}
					return new ReadOnlyStringWrapper( value );
				}
			);
		this.table.getColumns().add( stringColumn );

		stringColumn = new TableColumn< PeakPosition, String >( "Peptide" );
		stringColumn.setPrefWidth( 250.0 );
		stringColumn.setCellValueFactory(
			( cellData ) -> {
				String value = "";
				PeakPosition position = cellData.getValue();
				PeakAnnotation annotation = null;
				List< PeakAnnotation > annotations = position.getPeakAnnotations();
				if( annotations != null && !annotations.isEmpty() ) {
					annotation = annotations.get( 0 );
				}
				if( annotation != null ) {
					value = annotation.getPeptide();
				}
				return new ReadOnlyStringWrapper( value );
			}
		);
		this.table.getColumns().add( stringColumn );

		stringColumn = new TableColumn< PeakPosition, String >( "Description" );
		stringColumn.setPrefWidth( 300.0 );
		stringColumn.setCellValueFactory(
			( cellData ) -> {
				String value = "";
				PeakPosition position = cellData.getValue();
				PeakAnnotation annotation = null;
				List< PeakAnnotation > annotations = position.getPeakAnnotations();
				if( annotations != null && !annotations.isEmpty() ) {
					annotation = annotations.get( 0 );
				}
				if( annotation != null ) {
					value = annotation.getDescription();
				}
				return new ReadOnlyStringWrapper( value );
			}
		);
		this.table.getColumns().add( stringColumn );

		MsppManager manager = MsppManager.getInstance();
		this.table.getSelectionModel().selectedItemProperty().addListener(
			( observable, oldValue, newValue ) -> {
				try {
					manager.invokeAll( OnSelectPeak.class, newValue );
				}
				catch( Exception e ) {
					e.printStackTrace();
				}
				PeakPosition position = this.table.getSelectionModel().getSelectedItem();
				if( position != null ) {
					this.getChromatogramList( position );
					this.getSpectrumList( position );
				}
			}
		);
	}

	/**
	 * get chromatogram list
	 * @param position peak position
	 */
	private void getChromatogramList( PeakPosition position ) {
		if( position == null ) {
			return;
		}

		double mz = position.getMz();
		ProjectDetailsPanel me = this;
		MsppManager manager = MsppManager.getInstance();
		Project project = this.project;

		Thread thread = new Thread(
			() -> {
				List< Chromatogram > chromatograms = new ArrayList< Chromatogram >();
				Map< Chromatogram, Color > colorMap = new HashMap< Chromatogram, Color >();
				for( Group group : me.service.findGroups( project ) ) {
					Color color = Color.valueOf( group.getColor() );
					for( GroupSample groupSample : group.getGroupSamples() ) {
						Sample sample = groupSample.getSample();
						Chromatogram chromatogram = me.getChromatogram( sample,  mz );
						colorMap.put( chromatogram,  color );
						chromatograms.add( chromatogram );
					}
				}
				Platform.runLater(
					() -> {
						manager.invokeAll( OnPeakChromatograms.class, chromatograms, colorMap );
					}
				);
			}
		);
		thread.start();
	}

	/**
	 * get spectrum list
	 * @param position peak position
	 */
	private void getSpectrumList( PeakPosition position ) {
		double mz = position.getMz();
		double rt = position.getRt();

		ProjectDetailsPanel me = this;
		MsppManager manager = MsppManager.getInstance();
		Project project = this.project;

		Thread thread = new Thread(
			() -> {
				List< Spectrum > msSpectra = new ArrayList< Spectrum >();
				List< Spectrum > msmsSpectra = new ArrayList< Spectrum >();
				Map< Spectrum, Color > msColorMap = new HashMap< Spectrum, Color >();
				Map< Spectrum, Color > msmsColorMap = new HashMap< Spectrum, Color >();

				for( Group group : me.service.findGroups( project ) ) {
					Color color = Color.valueOf( group.getColor() );
					for( GroupSample groupSample : group.getGroupSamples() ) {


						Sample sample = groupSample.getSample();
						List< Spectrum > spectra = this.getSpectra( sample, rt );
						for( Spectrum spectrum : spectra ) {
							if( spectrum.getMsStage() == 1 ) {
								msSpectra.add( spectrum );
								msColorMap.put( spectrum, color );
							}
							else if( spectrum.getPrecursor() != null
									&& Math.abs( spectrum.getPrecursor() - mz ) <= 0.1 ) {
								msmsSpectra.add( spectrum );
								msmsColorMap.put( spectrum, color );
							}
						}
					}
				}

				Platform.runLater(
					() -> {
						manager.invokeAll( OnPeakMsSpectra.class, msSpectra, msColorMap );
						manager.invokeAll( OnPeakMsmsSpectra.class, msmsSpectra, msmsColorMap );
					}
				);
			}
		);
		thread.start();
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
						if( Math.abs( point.getX() - mz ) <= 0.1 ) {
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
	 * sets buttons
	 */
	private void setButtons() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.UPLOAD );
		this.importButton.setText( "" );
		this.importButton.setGraphic( icon );
		this.importButton.setTooltip( new Tooltip( "Import pepXML file ..." ) );

		icon = GlyphsDude.createIcon(FontAwesomeIcon.BAR_CHART);
		this.analysisButton.setText("");
		this.analysisButton.setGraphic(icon);
		this.analysisButton.setTooltip(new Tooltip("Identification and Quantitation ..."));
	}

	/**
	 * sets project panels
	 *
	 * @param project project
	 */
	private void setProjectPanels( Project project ) {
		MsppManager manager = MsppManager.getInstance();
		List< PluginMethod< AnalysisPanel > > methods = manager.getMethods( AnalysisPanel.class );
		for( PluginMethod< AnalysisPanel > method : methods ) {
			Node node = ( Node )method.invoke( project );
			if( node != null ) {
				Tab tab = new Tab( method.getAnnotation().value() );
				BorderPane pane = new BorderPane();
				pane.setCenter( node );
				tab.setContent( pane );
				this.tabPane.getTabs().add( tab );
			}
		}
	}

	/**
	 * sets the project
	 */
	public void setProject( Project project ) {
		this.project = project;
		this.updateTables();
		this.setProjectPanels( project );
	}

	/**
	 * update tables
	 */
	void updateTables() {
		this.table.getItems().clear();

		List< PeakPosition > positions = this.service.findPeakPositions( this.project );
		for( PeakPosition position : positions ) {
			this.table.getItems().add( position );
		}
	}

	@FXML
	private void onClose( ActionEvent event ) {
		ObjectManager manager = ObjectManager.getInstane();
		MainFrame mainFrame = manager.getMainFrame();
		mainFrame.setProjectPane( ProjectPlugin.getProjectPanel() );
	}

	@FXML
	private void onImport( ActionEvent event ) {
		MsppManager manager = MsppManager.getInstance();
		String path = manager.loadString( RECENT_FILE_KEY, "" );
		File file = null;
		if( !path.isEmpty() ) {
			file = new File( path );
		}

		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Open File" );
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add( new ExtensionFilter( "All Files", "*.*" ) );

		if( file != null ) {
			chooser.setInitialDirectory( file.getParentFile() );
			chooser.setInitialFileName( file.getName() );
		}

		file = chooser.showOpenDialog( new Stage() );

		if( file != null ) {
			try {
				this.service.importPepxml( this.project, file.getAbsolutePath() );
				this.updateTables();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void onAnalysis(ActionEvent event) throws Exception {
		ObjectManager objectManager = ObjectManager.getInstane();
		SpringFXMLLoader loader = objectManager.getFxmlLoader();
		Parent parent = loader.load(IdentificationDialog.class, "IdentificationDialog.fxml");
		Stage stage = new Stage();
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.show();

		IdentificationDialog dialog = (IdentificationDialog)loader.getController();
		dialog.setProject(this.project);
		dialog.setParentPanel(this);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.createTable();
		this.setButtons();
	}

}
