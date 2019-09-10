package ninja.mspp.tools;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import ninja.mspp.view.SpringFXMLLoader;

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
	 * sets align center
	 * @param column table column
	 */
	public static void setTableColumnCenterAlign( TableColumn< ?, ? > column  ) {
		column.setStyle( "-fx-alignment: CENTER;" );
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

	/**
	 * closes widow
	 * @param event event
	 */
	public static void closeWindow( ActionEvent event ) {
		Node node = ( Node )event.getSource();
		Scene scene = node.getScene();
		Window window = scene.getWindow();
		window.hide();
	}

	/**
	 * opens modal dialog
	 * @param loader fxml loader
	 * @param clazz dialog class
	 * @param path fxml path
	 * @return dialog controller
	 */
	public static Object openModelDialog(
			SpringFXMLLoader loader,
			String title,
			Class< ? > clazz,
			String path,
			ActionEvent event
	) throws Exception  {
		Parent parent = loader.load( clazz,  path );
		Object object = loader.getController();
		Scene scene = new Scene( parent );

		Stage stage = new Stage( StageStyle.UTILITY );
		stage.setScene( scene );
		stage.initOwner( ( ( Node )event.getSource() ).getScene().getWindow() );
		stage.initModality( Modality.WINDOW_MODAL );
		stage.setTitle( title );

		stage.showAndWait();

		return object;
	}
}
