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
 * @since Thu Sep 05 16:04:19 JST 2019
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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.tools.FXTools;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.list.SampleTableView;

@Component
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class GroupPanel implements Initializable {
	private static int count = 0;
	private static Color[] colors = {
		Color.BLUE,
		Color.RED,
		Color.GREEN,
		Color.PURPLE,
		Color.ORANGE,
		Color.CYAN,
		Color.LIME,
		Color.NAVY
	};

	@FXML
	private TextField nameText;

	@FXML
	private TextArea descriptionText;

	@FXML
	private ColorPicker color;

	@FXML
	private BorderPane samplePane;

	@FXML
	private Button addButton;

	@Autowired
	private SpringFXMLLoader loader;

	// table
	private SampleTableView table;

	/**
	 * gets the group name
	 * @return group name
	 */
	public String getName() {
		return this.nameText.getText();
	}

	/**
	 * gets the group description
	 * @return group description
	 */
	public String getDescription() {
		return this.descriptionText.getText();
	}

	/**
	 * gets the color
	 * @return
	 */
	public Color getColor() {
		return this.color.getValue();
	}

	/**
	 * gets the saples
	 * @return
	 */
	public List< Sample > getSamples() {
		List< Sample > samples = new ArrayList< Sample >();

		for( Sample sample : this.table.getItems() ) {
			samples.add( sample );
		}

		return samples;
	}

	/**
	 * sets the color
	 * @param color color
	 */
	public void setColor( Color color ) {
		this.color.setValue( color );
	}

	private void setButtons() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.PLUS );
		this.addButton.setText( "" );
		this.addButton.setGraphic( icon );
		this.addButton.setTooltip( new Tooltip( "Add Samples ...") );
	}

	@FXML
	private void onAddButton( ActionEvent event ) {
		try {
			SampleSelectPanel panel = ( SampleSelectPanel )FXTools.openModelDialog(
				this.loader,
				"Samples",
				SampleSelectPanel.class,
				"SampleSelectPanel.fxml",
				event
			);

			if( panel.isOk() ) {
				List< Sample > samples = panel.getSamples();
				this.table.getItems().clear();
				for( Sample sample : samples ) {
					this.table.getItems().add( sample );
				}
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Color color = GroupPanel.colors[ GroupPanel.count ];
		GroupPanel.count = ( GroupPanel.count + 1 ) % GroupPanel.colors.length;
		this.setButtons();
		this.color.setValue( color );
		this.table = new SampleTableView();
		this.samplePane.setCenter( table );
	}
}
