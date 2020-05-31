package ninja.mspp.plugin.viewer.settings;

import ninja.mspp.annotation.method.OnMainFrame;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.parameter.MainFrameParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.main.MainFrame;

@Plugin("Settings panel")
public class SettingsPlugin {
	@OnMainFrame
	public void setProjectPanel(
			@MainFrameParam MainFrame mainFrame,
			@FxmlLoaderParam SpringFXMLLoader loader
	) {
/*
		try {
			Parent parent = loader.load( ProjectPanel.class, "ProjectPanel.fxml" );
			ProjectPlugin.panel = parent;
			mainFrame.setProjectPane( parent );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
*/
	}
}
