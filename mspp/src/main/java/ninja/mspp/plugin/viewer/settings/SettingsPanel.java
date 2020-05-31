package ninja.mspp.plugin.viewer.settings;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

@Component
public class SettingsPanel implements Initializable {
	@FXML
	private BorderPane settingsPane;

	@FXML
	private TreeView<String> tree;

	@FXML
	private void onApply(ActionEvent event) {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
