package ninja.mspp.view;

import javafx.stage.Stage;
import ninja.mspp.view.main.MainFrame;

/**
 * gui manager
 */
public class GuiManager {
	// instance (This is the only object.)
	private static GuiManager instance;

	Stage stage;
	MainFrame mainFrame;

	/**
	 * constructor
	 */
	private GuiManager() {
		this.mainFrame = null;
	}

	/**
	 * sets the main frame
	 * @param frame main frame
	 */
	public void setMainFrame( MainFrame frame ) {
		this.mainFrame = frame;
	}

	/**
	 * gets the main frame
	 * @return main frame
	 */
	public MainFrame getMainFrame() {
		return this.mainFrame;
	}

	/**
	 * sets the stage
	 * @param stage stage
	 */
	public void setStage( Stage stage ) {
		this.stage = stage;
	}

	/**
	 * gets the stage
	 * @return stage
	 */
	public Stage getStage() {
		return this.stage;
	}

	/**
	 * gets the instance
	 * @return Gui manager instance. (This is the only object.)
	 */
	public static GuiManager getInstance() {
		if( GuiManager.instance == null ) {
			GuiManager.instance = new GuiManager();
		}
		return GuiManager.instance;
	}


}
