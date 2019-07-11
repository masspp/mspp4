package ninja.mspp.view.list;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.tools.FXTools;

/**
 * spectrum table view
 */
public class SpectrumTableView extends TableView< Spectrum > {
	/**
	 * constructor
	 */
	public SpectrumTableView() {
		super();
		this.initialize();
	}

	/**
	 * initializes
	 */
	protected void initialize() {
		this.getColumns().clear();

		// ID
		TableColumn< Spectrum, String > stringColumn = new TableColumn< Spectrum, String >( "ID" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, String >( "spectrumid" ) );
		this.getColumns().add( stringColumn );

		// Name
		stringColumn = new TableColumn< Spectrum, String >( "Name" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, String >( "name" )  );
		this.getColumns().add( stringColumn );

		// Title
		stringColumn = new TableColumn< Spectrum, String >( "Title" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, String >( "title" )  );
		this.getColumns().add( stringColumn );

		// RT
		TableColumn< Spectrum, Double > doubleColumn = new TableColumn< Spectrum, Double >( "RT" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "startRt" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.getColumns().add( doubleColumn );


		// Stage
		TableColumn< Spectrum, Integer > intColumn = new TableColumn< Spectrum, Integer >( "Stage" );
		intColumn.setPrefWidth( 80.0 );
		intColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Integer>( "msStage" ) );
		FXTools.setTableColumnRightAlign( intColumn );
		this.getColumns().add( intColumn );

		// Polarity
		stringColumn = new TableColumn< Spectrum, String >( "Polarity" );
		stringColumn.setPrefWidth( 80.0 );
		stringColumn.setCellValueFactory(
			( cellData ) -> {
				Spectrum spectrum = cellData.getValue();
				String string = "";
				int polarity = spectrum.getPolarity();
				if( polarity > 0 ) {
					string = "+";
				}
				else if( polarity < 0 ) {
					string = "-";
				}
				return new ReadOnlyStringWrapper( string );
			}
		);
		this.getColumns().add( stringColumn );

		// TIC
		doubleColumn = new TableColumn< Spectrum, Double >( "TIC" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "tic" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  0 );
		this.getColumns().add( doubleColumn );

		// BPM
		doubleColumn = new TableColumn< Spectrum, Double >( "BPM" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "bpm" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  3 );
		this.getColumns().add( doubleColumn );

		// BPM
		doubleColumn = new TableColumn< Spectrum, Double >( "BPI" );
		doubleColumn.setPrefWidth( 100.0 );
		doubleColumn.setCellValueFactory( new PropertyValueFactory< Spectrum, Double >( "bpi" ) );
		FXTools.setTableColumnRightAlign( doubleColumn );
		FXTools.setDoubleTableColumnAccuracy( doubleColumn,  0 );
		this.getColumns().add( doubleColumn );
	}
}
