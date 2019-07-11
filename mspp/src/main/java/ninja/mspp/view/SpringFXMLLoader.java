package ninja.mspp.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

@Component
public class SpringFXMLLoader {
	public static SpringFXMLLoader INSTANCE;

	@Autowired
	private ApplicationContext context;

	private Object controller;

	/**
	 * constructor
	 */
	public SpringFXMLLoader() {
		this.controller = null;
		SpringFXMLLoader.INSTANCE = this;
	}

	/**
	 * loads fxml
	 * @param clazz class
	 * @param path fxml path
	 * @return parent
	 * @throws IOException
	 */
	public Parent load( Class< ? > clazz, String path ) throws IOException {
		FXMLLoader loader = new FXMLLoader( clazz.getResource( path ) );
		loader.setControllerFactory( this.context::getBean );
		Parent parent =  loader.load();

		this.controller = loader.getController();

		return parent;
	}

	/**
	 * gets the controller
	 * @return
	 */
	public Object getController() {
		return this.controller;
	}
}
