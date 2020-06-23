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
