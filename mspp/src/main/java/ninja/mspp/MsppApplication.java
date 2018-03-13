package ninja.mspp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ninja.mspp.view.GuiManager;
import ninja.mspp.view.main.MainFrame;

@SpringBootApplication
public class MsppApplication extends Application {
    // context
	private static ConfigurableApplicationContext context;

	/**
	 * main method
	 * @param args arguments
	 */
	public static void main(String[] args) {
		MsppApplication.context = SpringApplication.run( MsppApplication.class, args );
		launch( args );
	}

	@Override
	public void start( Stage stage ) throws Exception {
		MsppManager msppManager = MsppManager.getInstance();
		GuiManager guiManager = GuiManager.getInstance();

		guiManager.setStage( stage );

		FXMLLoader loader = new FXMLLoader(
			MainFrame.class.getResource( "MainFrame.fxml" )
		);

		Parent root = loader.load();

		MainFrame mainFrame = (MainFrame)loader.getController();
		guiManager.setMainFrame( mainFrame );

		String title = msppManager.getConfig().getString( "mspp.title" );
		stage.setTitle( title );

		Image icon = new Image( getClass().getResourceAsStream( "/images/MS_icon_24.png" ) );
		stage.getIcons().add( icon );

		Scene scene = new Scene( root );
		stage.setScene( scene );;

		stage.show();
	}

	@Override
	public void stop() throws Exception {
		MsppApplication.context.close();
	}
}
