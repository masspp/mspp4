package ninja.mspp.plugin.viewer.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.service.RawDataService;
import ninja.mspp.tools.FXTools;
import ninja.mspp.view.list.SampleTableView;

@Component
public class SampleSelectPanel implements Initializable {
	@Autowired
	private RawDataService service;

	private SampleTableView srcTable;

	private SampleTableView dstTable;

	@FXML
	private BorderPane srcSamplePane;

	@FXML
	private BorderPane dstSamplePane;

	@FXML
	private Button addButton;

	@FXML
	private Button removeButton;

	private boolean ok;

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	/**
	 * initializes table
	 */
	private void initTable() {
		this.srcTable = new SampleTableView();
		this.srcSamplePane.setCenter( this.srcTable );
		this.srcTable.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

		this.dstTable = new SampleTableView();
		this.dstSamplePane.setCenter( this.dstTable );
		this.dstTable.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

		for( Sample sample : this.service.findSamples() ) {
			this.srcTable.getItems().add( sample );
		}
	}

	/**
	 * sets buttons
	 */
	private void setButtons() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.CHEVRON_RIGHT );
		this.addButton.setText( "" );
		this.addButton.setGraphic( icon );
		this.addButton.setTooltip( new Tooltip( "Add selected samples...") );

		icon = GlyphsDude.createIcon( FontAwesomeIcon.CHEVRON_LEFT );
		this.removeButton.setText( "" );
		this.removeButton.setGraphic( icon );
		this.removeButton.setTooltip( new Tooltip( "Remove selected samples...") );
	}

	/**
	 * gets the samples
	 * @return
	 */
	public List< Sample > getSamples() {
		List< Sample > samples = new ArrayList< Sample >();

		for( Sample sample : this.dstTable.getItems() ) {
			samples.add( sample );
		}
		return samples;
	}

	@FXML
	private void onAdd( ActionEvent event ) {
		List< Sample > samples = new ArrayList< Sample >();
		for( Sample sample : this.srcTable.getSelectionModel().getSelectedItems() ) {
			samples.add( sample );
		}

		for( Sample sample : samples ) {
			this.dstTable.getItems().add( sample );
			this.srcTable.getItems().remove( sample );
		}
	}

	@FXML
	private void onRemove( ActionEvent event ) {
		List< Sample > samples = new ArrayList< Sample >();
		for( Sample sample : this.dstTable.getSelectionModel().getSelectedItems() ) {
			samples.add( sample );
		}

		for( Sample sample : samples ) {
			this.srcTable.getItems().add( sample );
			this.dstTable.getItems().remove( sample );
		}
	}

	@FXML
	private void onOk( ActionEvent event ) {
		this.setOk( true );
		FXTools.closeWindow( event );
	}

	@FXML
	private void onCancel( ActionEvent event ) {
		this.setOk( false );
		FXTools.closeWindow( event );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initTable();
		this.setButtons();
		this.ok = false;
	}
}
