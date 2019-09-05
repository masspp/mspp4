package ninja.mspp.plugin.viewer.project;

import javafx.scene.Parent;
import ninja.mspp.annotation.method.OnMainFrame;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.parameter.MainFrameParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.main.MainFrame;

@Plugin( "Project panel" )
public class ProjectPlugin {
	@OnMainFrame
	public void setProjectPanel(
			@MainFrameParam MainFrame mainFrame,
			@FxmlLoaderParam SpringFXMLLoader loader
	) {
		try {
			Parent parent = loader.load( ProjectPanel.class, "ProjectPanel.fxml" );
			mainFrame.setProjectPane( parent );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
