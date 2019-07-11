package ninja.mspp.tools;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/**
 * FXTools
 */
public class FXTools {
	/**
	 * sets align light
	 * @param column table column
	 */
	public static void setTableColumnRightAlign( TableColumn< ?, ? > column  ) {
		column.setStyle( "-fx-alignment: CENTER_RIGHT;" );
	}

	/**
	 * set double table column accuracy
	 * @param <T>
	 * @param accuracy
	 */
	public static <T> void setDoubleTableColumnAccuracy( TableColumn< T, Double > column, int accuracy ) {
		String format = String.format( "%%.%df", accuracy );
		column.setCellFactory(
			col -> {
				return new TableCell< T, Double  >() {
					@Override
					public void updateItem( Double value, boolean empty ) {
						if( empty || value == null ) {
							setText( null );
						}
						else {
							setText( String.format( format,  value ) );
						}
					}
				};
			}
		);
	}
}
