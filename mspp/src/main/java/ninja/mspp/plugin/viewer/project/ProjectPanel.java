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
	 * creates table
	 */
	private void createTable() {
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setAddButton();
		this.createTable();
		this.updateTable();
	}
}
