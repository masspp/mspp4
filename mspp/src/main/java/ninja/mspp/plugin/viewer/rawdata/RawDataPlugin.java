package ninja.mspp.plugin.viewer.rawdata;

import javafx.scene.Parent;
import ninja.mspp.annotation.method.OnMainFrame;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.parameter.MainFrameParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.main.MainFrame;

@Plugin( "Raw data panel" )
public class RawDataPlugin {
	@OnMainFrame
	public void setRawDataPanel(
			@MainFrameParam MainFrame mainFrame,
			@FxmlLoaderParam SpringFXMLLoader loader
	) {
		try {
			Parent panel = loader.load( RawDataPanel.class, "RawDataPanel.fxml" );
			mainFrame.setRawDataPane( panel );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
