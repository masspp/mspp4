package ninja.mspp.plugin.viewer.color;

import javafx.scene.Node;
import ninja.mspp.annotation.method.Settings;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.view.SpringFXMLLoader;

@Plugin("Color Theme")
public class ColorThemePlugin {
	@Settings("Heatmap Theme")
	public Node getColorThemePanel(
			@FxmlLoaderParam SpringFXMLLoader loader
	) {
		return null;
	}
}
