package ninja.mspp.plugin.viewer.project;

import javafx.scene.Node;
import javafx.scene.Parent;
import ninja.mspp.annotation.method.OnMainFrame;
import ninja.mspp.annotation.method.OnProject;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.parameter.MainFrameParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.entity.Project;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.main.MainFrame;

@Plugin( "Project panel" )
public class ProjectPlugin {
	private static Node panel = null;

	@OnMainFrame
	public void setProjectPanel(
			@MainFrameParam MainFrame mainFrame,
			@FxmlLoaderParam SpringFXMLLoader loader
	) {
		try {
			Parent parent = loader.load( ProjectPanel.class, "ProjectPanel.fxml" );
			ProjectPlugin.panel = parent;
			mainFrame.setProjectPane( parent );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	@OnProject
	public void openDetails(
			@MainFrameParam MainFrame mainFrame,
			@FxmlLoaderParam SpringFXMLLoader loader,
			Project project
	) {
		try {
			Parent parent = loader.load( ProjectDetailsPanel.class, "ProjectDetailsPanel.fxml" );
			ProjectDetailsPanel panel = ( ProjectDetailsPanel )loader.getController();
			panel.setProject( project );
			mainFrame.setProjectPane( parent );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * gets the project panel
	 * @return project panel
	 */
	public static Node getProjectPanel() {
		return ProjectPlugin.panel;
	}

}
