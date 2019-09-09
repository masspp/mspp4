package ninja.mspp.plugin.viewer.project;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;
import ninja.mspp.ObjectManager;
import ninja.mspp.annotation.method.AnalysisPanel;
import ninja.mspp.annotation.method.OnSelectPeak;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.entity.PeakAnnotation;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.service.ProjectService;
import ninja.mspp.tools.FXTools;
import ninja.mspp.view.main.MainFrame;

@Component
public class ProjectDetailsPanel implements Initializable {
	/** recent file key */
	private static String RECENT_FILE_KEY = "Recent Open File";

	/** project */
	private Project project;

	@Autowired
	private ProjectService service;

	@FXML
	private TableView< PeakPosition > table;

	@FXML
	private TabPane tabPane;

	@FXML
	private Button importButton;

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
//		FXTools.setDoubleTableColumnAccuracy( numberColumn,  1 );
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
			}
		);

	}

	/**
	 * sets buttons
	 */
	private void setButtons() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.UPLOAD );
		this.importButton.setText( "" );
		this.importButton.setGraphic( icon );
		this.importButton.setTooltip( new Tooltip( "Import pepXML file ..." ) );
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
	private void updateTables() {
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.createTable();
		this.setButtons();
	}

}
