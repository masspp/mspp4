package ninja.mspp;

import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.main.MainFrame;

/**
 * object manager
 */
public class ObjectManager {

	private static ObjectManager instance;

	private MainFrame mainFrame;
	private SpringFXMLLoader fxmlLoader;

	/**
	 * constructor
	 */
	private ObjectManager() {
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public SpringFXMLLoader getFxmlLoader() {
		return fxmlLoader;
	}

	public void setFxmlLoader(SpringFXMLLoader fxmlLoader) {
		this.fxmlLoader = fxmlLoader;
	}

	/**
	 * gets the instance
	 * @return object manager object
	 */
	public static ObjectManager getInstane() {
		if( ObjectManager.instance == null ) {
			ObjectManager.instance = new ObjectManager();
		}
		return ObjectManager.instance;
	}
}
