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
package ninja.mspp.plugin.viewer.project;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.method.OnProject;
import ninja.mspp.model.entity.Project;
import ninja.mspp.service.ProjectService;
import ninja.mspp.tools.FXTools;
import ninja.mspp.view.SpringFXMLLoader;

@Component
public class ProjectPanel implements Initializable {
	@FXML
	private BorderPane pane;

	@FXML
	private TableView< Project > table;

	@FXML
	private Button addButton;

	@FXML
	private Button deleteButton;

	@Autowired
	private ProjectService service;

	@Autowired
	private SpringFXMLLoader loader;

	/**
	 * sets the add button
	 */
	private void setAddButton() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.PLUS );
		this.addButton.setText( "" );
		this.addButton.setGraphic( icon );
		this.addButton.setTooltip( new Tooltip( "New project ...") );
	}

	/**
	 * sets the delete button
	 */
	private void setDeleteButton() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.REMOVE );
		this.deleteButton.setText( "" );
		this.deleteButton.setGraphic( icon );
		this.deleteButton.setTooltip( new Tooltip( "Delete the selected project ..." ) );
	}

	/**
	 * creates table
	 */
	private void createTable() {
		ProjectPanel me = this;
		MsppManager manager = MsppManager.getInstance();

		TableColumn< Project, String > stringColumn = new TableColumn< Project, String >( "Name" );
		stringColumn.setPrefWidth( 200.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Project, String >( "name" ) );
		this.table.getColumns().add( stringColumn );

		TableColumn< Project, Timestamp > timeColumn = new TableColumn< Project, Timestamp >( "Date" );
		timeColumn.setPrefWidth( 150.0 );;
		timeColumn.setCellValueFactory( new PropertyValueFactory< Project, Timestamp >( "registrationDate" ) );
		this.table.getColumns().add( timeColumn );

		stringColumn = new TableColumn< Project, String >( "Description" );
		stringColumn.setPrefWidth( 400.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Project, String >( "description" ) );
		this.table.getColumns().add( stringColumn );

		this.table.setOnMouseClicked(
			( event ) -> {
				if( event.getClickCount() >= 2 ) {
					Project project = me.table.getSelectionModel().getSelectedItem();
					if( project != null ) {
						try {
							manager.invokeAll( OnProject.class, project );
						}
						catch( Exception e ) {
							e.printStackTrace();
						}
					}
				}
			}
		);
	}


	/**
	 * updates table
	 */
	private void updateTable() {
		this.table.getItems().clear();
		for( Project project : this.service.findAllProjects() ) {
			this.table.getItems().add( project );
		}
	}

	/**
	 * registers project
	 * @param panel project panel
	 */
	private void registerProject( ProjectCreatePanel panel ) {
		this.service.registerProject( panel.getName(), panel.getDescription(), panel.getGroups() );
	}

	@FXML
	private void onAdd( ActionEvent event ) {
		try {
			ProjectCreatePanel panel = ( ProjectCreatePanel )FXTools.openModelDialog(
				this.loader,
				"New Porject",
				ProjectCreatePanel.class,
				"ProjectCreatePanel.fxml",
				event
			);

			if( panel.isOk() ) {
				this.registerProject( panel );
				this.updateTable();
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onDelete( ActionEvent event ) {
		Project project = this.table.getSelectionModel().getSelectedItem();
		if( project == null ) {
			FXTools.error( "Select a project before clicking the delete button." );
			return;
		}

		ProjectPanel me = this;
		if( FXTools.confirm( "Are you sure to delete the project? [" + project.getName() + "]" ) ) {
			this.service.deleteProject( project );
			me.updateTable();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setAddButton();
		this.setDeleteButton();
		this.createTable();
		this.updateTable();
	}
}
