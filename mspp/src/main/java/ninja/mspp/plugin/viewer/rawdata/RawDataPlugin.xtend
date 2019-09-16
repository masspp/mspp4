package ninja.mspp.plugin.viewer.rawdata

import javafx.scene.Parent
import ninja.mspp.annotation.method.OnMainFrame
import ninja.mspp.annotation.parameter.FxmlLoaderParam
import ninja.mspp.annotation.parameter.MainFrameParam
import ninja.mspp.annotation.type.Plugin
import ninja.mspp.view.SpringFXMLLoader
import ninja.mspp.view.main.MainFrame

@Plugin("Raw data panel") class RawDataPlugin {
	@OnMainFrame def void setRawDataPanel(@MainFrameParam MainFrame mainFrame,
		@FxmlLoaderParam SpringFXMLLoader loader) {
		try {
			var Parent panel = loader.load(RawDataPanel, "RawDataPanel.fxml")
			mainFrame.setRawDataPane(panel)
		} catch (Exception e) {
			e.printStackTrace()
		}

	}
}
