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
 * @since Thu Jul 11 20:44:24 JST 2019
 *
 * Copyright (c) 2019 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.rawdata;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.method.FileInput;
import ninja.mspp.annotation.method.OnHeatmap;
import ninja.mspp.annotation.method.OnRawdataSample;
import ninja.mspp.annotation.method.SamplePanel;
import ninja.mspp.io.msdatareader.AbstractMSDataReader;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.dataobject.Heatmap;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.service.RawDataService;
import ninja.mspp.tools.FXTools;
import ninja.mspp.tools.FileTool;
import ninja.mspp.view.list.SampleTableView;


@Component
public class RawDataPanel implements Initializable {
	private static String RECENT_FILE_KEY = "Recent Open File";

	@FXML
	private TableView< Sample > table;

	@FXML
	private TabPane tabPane;

	@FXML
	private BorderPane upperPane;

	@FXML
	private Button importButton;

	@FXML
	private Button commentButton;

	@FXML
	private Button deleteButton;

	@Autowired
	private RawDataService rawDataService;

	private volatile ProgressBar progress;

	@FXML
	private void onImport( ActionEvent event ) {
		MsppManager manager = MsppManager.getInstance();
		String path = manager.loadString( RECENT_FILE_KEY, "" );
		File file = null;
		if( !path.isEmpty() ) {
			file = new File( path );
		}

		List< PluginMethod< FileInput > > methods = manager.getMethods( FileInput.class );

		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Open File" );
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add( new ExtensionFilter( "All Files", "*.*" ) );
		for( PluginMethod< FileInput > method: methods ) {
			FileInput annotation = method.getAnnotation();
                        List<String> extensions = new ArrayList<>();
                        for(String annot_ext: annotation.extensions()){
                            extensions.add("*." + annot_ext);
                        }
                        chooser.getExtensionFilters().add( new ExtensionFilter( annotation.title(), extensions) );
		}

		if( file != null ) {
			chooser.setInitialDirectory( file.getParentFile() );
			chooser.setInitialFileName( file.getName() );
		}

		this.progress = new ProgressBar();
		this.progress.setProgress( 0.0 );
		this.progress.setPrefHeight( 15.0 );
		this.progress.prefWidthProperty().bind( this.upperPane.widthProperty().subtract( 10.0 ) );
		this.upperPane.setBottom( this.progress );

		Stage stage = new Stage();
		List< File > files = chooser.showOpenMultipleDialog( stage );
		stage.close();

		if( files != null && !files.isEmpty() ) {
			int count = files.size();

			try {
				for( int i = 0; i < files.size(); i++ ) {
					file = files.get( i );
					double start = ( double )i / ( double )count;
					double end = ( double )( i + 1 ) / ( double )count;
                                        AbstractMSDataReader reader = this.openFile( file);
					this.rawDataService.register( reader, progress, start, end );
				}
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
			this.updateTable();
		}

		this.upperPane.setBottom( null );
	}
        
        /**
         * 
         * @param file data file
         * @return MS Data reader object
         */
        private AbstractMSDataReader openFile(File file){
            MsppManager manager = MsppManager.getInstance();

            String path = file.getAbsolutePath();
            String ext = FileTool.getExtension( path );
            
            AbstractMSDataReader reader = null;
            
            List< PluginMethod< FileInput > > methods = manager.getMethods( FileInput.class );

            for( PluginMethod< FileInput > method: methods ) {
                Object plugin = method.getPlugin();
                FileInput annotation = method.getAnnotation();
                boolean is_ext_matched=false;
                for(String annot_ext: annotation.extensions() ){
                    if( reader == null && annot_ext.compareToIgnoreCase( ext ) == 0 ) {
                        is_ext_matched = true;
                        break;
                    }
                    if( is_ext_matched){
                        try {
                                reader = (AbstractMSDataReader)method.getMethod().invoke( plugin,  path );
                        }
                        catch( Exception e ) {
                                e.printStackTrace();
                        }
                    }
                }
            }
            
            return reader;  // you need to check reader is null later...
	}
                  
        

	@FXML
	private void onComment( ActionEvent event ) {
		Sample sample = this.table.getSelectionModel().getSelectedItem();
		if( sample == null ) {
			Alert alert = new Alert( AlertType.ERROR );
			alert.setTitle( "Error" );
			alert.setHeaderText( "A sample is not selected." );
			alert.setContentText( "Select a sample before setting comment." );
			alert.showAndWait();
		}
		else {
			TextInputDialog dialog = new TextInputDialog( sample.getUserComment() );
			dialog.getEditor().setPrefWidth( 400.0 );
			dialog.setTitle( "Sample comment" );
			dialog.setHeaderText( sample.getName() );
			dialog.setContentText( "Input the comment." );
			Optional< String > result = dialog.showAndWait();
			if( result.isPresent() ) {
				sample.setUserComment( result.get() );
				this.rawDataService.saveSample( sample );
				this.table.refresh();
			}
		}
	}

	@FXML
	private void onDelete( ActionEvent event ) {
		Sample sample = this.table.getSelectionModel().getSelectedItem();
		if( sample == null ) {
			FXTools.error( "Select a sample before clicking the delete button." );
			return;
		}

		if( FXTools.confirm( "Are you sure to delete the sample? [" + sample.getFilename() + "]" ) ) {
			this.rawDataService.deleteSample( sample );
			this.updateTable();

			MsppManager manager = MsppManager.getInstance();
			try {
				manager.invokeAll( OnRawdataSample.class, ( Sample )null );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * updates table
	 */
	private void updateTable() {
		this.table.getItems().clear();
		for( Sample sample : this.rawDataService.findSamples() ) {
			this.table.getItems().add( sample );
		}
	}

	/**
	 * import button icon
	 */
	private void setImportButtonIcon() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.UPLOAD );
		this.importButton.setText( "" );
		this.importButton.setGraphic( icon );
		this.importButton.setTooltip( new Tooltip( "Import from file ..." ) );
	}

	/**
	 * comment button icon
	 */
	private void setCommentButtonIcon() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.COMMENT );
		this.commentButton.setText( "" );
		this.commentButton.setGraphic( icon );
		this.commentButton.setTooltip( new Tooltip( "Comment ..." ) );
	}

	/**
	 * delete button icon
	 */
	private void setDeleteButton() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.REMOVE );
		this.deleteButton.setText( "" );
		this.deleteButton.setGraphic( icon );
		this.deleteButton.setTooltip( new Tooltip( "Delete the selected raw data." ) );
	}

	/**
	 * sets sample panels
	 */
	private void setSamplePanels() {
		MsppManager manager = MsppManager.getInstance();
		List< PluginMethod< SamplePanel > > methods = manager.getMethods( SamplePanel.class );
		for( PluginMethod< SamplePanel > method : methods ) {
			Node node = ( Node )method.invoke();
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
	 * creates heatmap
	 * @param sample sample
	 */
	private void createHeatmap( Sample sample ) {
		MsppManager manager = MsppManager.getInstance();
		RawDataPanel me = this;
		List< Spectrum > spectra = this.rawDataService.findSpectra( sample );
		Thread thread = new Thread(
			() -> {
				Heatmap heatmap = new Heatmap( spectra, me.rawDataService );
				Platform.runLater(
					() -> {
						manager.invokeAll( OnHeatmap.class, sample == null ? null : heatmap );
					}
				);
			}
		);
		thread.start();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setImportButtonIcon();
		this.setCommentButtonIcon();
		this.setDeleteButton();

		this.setSamplePanels();

		SampleTableView table = new SampleTableView();
		this.table = table;
		this.upperPane.setCenter( table );

		this.updateTable();

		MsppManager manager = MsppManager.getInstance();

		RawDataPanel me = this;
		this.table.getSelectionModel().selectedItemProperty().addListener(
			( observable, oldValue, newValue ) -> {
				manager.invokeAll( OnRawdataSample.class, newValue );
				me.createHeatmap( newValue );
			}
		);
	}
}
