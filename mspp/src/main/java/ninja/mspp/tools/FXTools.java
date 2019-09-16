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
