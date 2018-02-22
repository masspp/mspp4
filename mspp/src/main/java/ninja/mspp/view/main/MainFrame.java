package ninja.mspp.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * main frame
 * @author Satoshi Tanaka
 *
 */
public class MainFrame {
	@FXML private MenuBar menubar;
	@FXML private BorderPane mainPane;
	@FXML private TabPane topTabPane;
	@FXML private TabPane leftTabPane;
	@FXML private TabPane rightTabPane;
	@FXML private TabPane bottomTabPane;

	/**
	 * gets the menubar
	 * @return
	 */
	public MenuBar getManuBar() {
		return this.menubar;
	}

	/**
	 * initializes main frame
	 */
	public void initialize() {

	}
}
