package ninja.mspp.plugin.viewer.mirror;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;

@Component
public class MirrorPanel implements Initializable {
	private int spectrumIndex = 0;
	private int chromatogramIndex = 0;

	@FXML
	private Button spectrumButton;

	@FXML
	private Button chromatogramButton;

	@FXML
	private Button chromatogramDownButton;

	@FXML
	private BorderPane spectrumTablePane;

	@FXML
	private BorderPane spectrumCanvasPane;

	@FXML
	private BorderPane chromatogramTablePane;

	@FXML
	private BorderPane chromatogramCanvasPane;

	@FXML
	private void onSpectrum( ActionEvent event ) {

	}

	@FXML
	private void onChromatogram( ActionEvent event ) {

	}


	/**
	 * sets the peak position
	 * @param project
	 * @param position
	 */
	public void setPeak( Project project, PeakPosition position ) {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
