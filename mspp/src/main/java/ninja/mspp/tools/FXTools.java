package ninja.mspp.tools;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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

	/**
	 * shows error dialog
	 * @param message error message
	 */
	public static void error(String message ) {
		Alert alert = new Alert( AlertType.ERROR, message, ButtonType.OK );
		alert.showAndWait();
	}

	/**
	 * shows warning dialog
	 * @param message warning message
	 */
	public static void warning( String message ) {
		Alert alert = new Alert( AlertType.WARNING, message, ButtonType.OK );
		alert.showAndWait();
	}

	/**
	 * show confirms dialog
	 * @param message confirm message
	 * @return If true, the OK button is clicked.
	 */
	public static boolean confirm( String message ) {
		Alert alert = new Alert( AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL );
		Optional< ButtonType > optional = alert.showAndWait();
		if( optional.isPresent() ) {
			if( optional.get() == ButtonType.OK ) {
				return true;
			}
		}
		return false;
	}
}
