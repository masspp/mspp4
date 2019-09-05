package ninja.mspp.view.list;

import java.sql.Timestamp;
import java.util.List;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.tools.FXTools;

/**
 * sample table view
 */
public class SampleTableView extends TableView< Sample >{
	/**
	 * constructor
	 */
	public SampleTableView() {
		super();
		this.initialize();
	}

	/**
	 * initializes table
	 */
	private void initialize() {
		// acquisition software 150
		// registration date 150
		// spectra 100
		// comment 250

		TableColumn< Sample, String > stringColumn = new TableColumn< Sample, String >( "Name" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "filename" ) );
		this.getColumns().add( stringColumn );

		stringColumn = new TableColumn< Sample, String >( "Instrument" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "instrumentVendor" ) );
		this.getColumns().add( stringColumn );

		stringColumn = new TableColumn< Sample, String >( "Acquisition Software" );
		stringColumn.setPrefWidth( 150.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "acquisitionsoftware" ) );
		this.getColumns().add( stringColumn );

		TableColumn< Sample, Timestamp > timeColumn = new TableColumn< Sample, Timestamp >( "Registration Date" );
		timeColumn.setPrefWidth( 150.0 );
		timeColumn.setCellValueFactory( new PropertyValueFactory< Sample, Timestamp >( "registrationDate" ) );
		this.getColumns().add( timeColumn );

		TableColumn< Sample, Number > intColumn = new TableColumn< Sample, Number >( "#Spectra" );
		intColumn.setPrefWidth( 100.0 );
		intColumn.setCellValueFactory(
			( cellData ) -> {
				Integer num = 0;
				List< Spectrum > spectra = cellData.getValue().getSpectras();
				if( spectra != null ) {
					num = spectra.size();
				}
				return new ReadOnlyIntegerWrapper( num );
			}
		);
		FXTools.setTableColumnRightAlign( intColumn );
		this.getColumns().add( intColumn );

		stringColumn = new TableColumn< Sample, String >( "Comment" );
		stringColumn.setPrefWidth( 250.0 );
		stringColumn.setCellValueFactory( new PropertyValueFactory< Sample, String >( "userComment" ) );
		this.getColumns().add( stringColumn );
	}
}
