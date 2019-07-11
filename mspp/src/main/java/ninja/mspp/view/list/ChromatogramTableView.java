package ninja.mspp.view.list;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.tools.FXTools;

/**
 * chromatogram table view
 */
public class ChromatogramTableView extends TableView< Chromatogram > {
	/**
	 * constructor
	 */
	public ChromatogramTableView() {
		super();
		this.initialize();
	}

	/**
	 * initializes
	 */
	private void initialize() {
		this.getColumns().clear();

		// ID
		TableColumn< Chromatogram, String > stringColumn = new TableColumn< Chromatogram, String >( "Name" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Chromatogram, String >( "name" ) );
		this.getColumns().add( stringColumn );

		// title
		stringColumn = new TableColumn< Chromatogram, String >( "Title" );
		stringColumn.setPrefWidth( 180.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Chromatogram, String >( "title" ) );
		this.getColumns().add( stringColumn );

		// m/z
		TableColumn< Chromatogram, Double > doubleColumn = new TableColumn< Chromatogram, Double >( "m/z" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Chromatogram, Double >( "mz" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.getColumns().add( doubleColumn );

	}
}
