package ninja.mspp.plugin.viewer.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import ninja.mspp.model.dataobject.Modification;

@Component
public class IdentificationDialog implements Initializable {
	@FXML
	private ChoiceBox<String> peaksCombo;

	@FXML
	private ChoiceBox<String> methodCombo;

	@FXML
	private TextField peakSnText;

	@FXML
	private TextField peakSpacingText;

	@FXML
	private ChoiceBox<String> identificationCombo;

	@FXML
	private TreeView<String> taxonView;

	@FXML
	private ChoiceBox<String> enzymeCombo;

	@FXML
	private ChoiceBox<String> terminalCombo;

	@FXML
	private ListView<String> fixedModList;

	@FXML
	private ListView<String> variableModList;

	@FXML
	private ChoiceBox<String> tolUnitCombo;

	@FXML
	private ChoiceBox<String> theoriticalCombo;

	@FXML
	private ChoiceBox<String> isotopeCombo;

	@FXML
	private ChoiceBox<String> cleavageCombo;

	@FXML
	private TextField proteoWizardText;

	@FXML
	private TextField cometText;

	@FXML
	private TextField dbText;

	@FXML
	private void onProteoWizard(ActionEvent event) {

	}

	@FXML
	private void onComet(ActionEvent event) {

	}

	@FXML
	private void onDb(ActionEvent event) {

	}

	private List<Modification> loadModifications() {
		List<Modification> list = new ArrayList<Modification>();
		InputStream stream = getClass().getResourceAsStream("/identification/mod_file");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));


		try {
			String line = null;
			Modification modification = null;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("Title:")) {
					String title = line.replace("Title:",  "");
					modification = new Modification();
					modification.setTitle(title);
					modification.setHidden(false);
					list.add(modification);
				}
				else if(line.equalsIgnoreCase("hidden")) {
					modification.setHidden(true);
				}
			}
			stream.close();
			reader.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.peaksCombo.getItems().add("ProteoWizard");
		this.peaksCombo.getSelectionModel().select(0);

		this.identificationCombo.getItems().add("Comet");
		this.identificationCombo.getSelectionModel().select(0);

		this.enzymeCombo.getItems().add("Trypsin/P");
		this.enzymeCombo.getSelectionModel().select(0);

		this.terminalCombo.getItems().add("Fully Digested");
		this.terminalCombo.getSelectionModel().select(0);

		List<Modification> modifications = this.loadModifications();
		for(Modification modification: modifications) {
			if(!modification.isHidden()) {
				this.fixedModList.getItems().add(modification.getTitle());
				this.variableModList.getItems().add(modification.getTitle());
			}
		}

		this.tolUnitCombo.getItems().add("ppm");
		this.tolUnitCombo.getSelectionModel().select(0);

		this.theoriticalCombo.getItems().add("Use Flanking Peaks");
		this.theoriticalCombo.getSelectionModel().select(0);

		this.isotopeCombo.getItems().add("Standard C13 Error");
		this.isotopeCombo.getSelectionModel().select(0);

		this.cleavageCombo.getItems().add("1");
		this.cleavageCombo.getSelectionModel().clearAndSelect(0);

		TreeItem<String> root = new TreeItem<String>("taxonomy");
		this.taxonView.setRoot(root);

		TreeItem<String> uniprot = new TreeItem<String>("uniprot");
		this.taxonView.getRoot().getChildren().add(uniprot);

		TreeItem<String> sprot = new TreeItem<String>("sprot");
		uniprot.getChildren().add(sprot);

		TreeItem<String> trembl = new TreeItem<String>("trembl");
		uniprot.getChildren().add(trembl);

		String[] taxons = {"archaea", "bacteria", "fungi", "human", "invertebrates", "mammals", "plants", "rodents", "vertebrates", "viruses"};
		for(String taxon: taxons) {
			TreeItem<String> sprotItem = new TreeItem<String>(taxon);
			TreeItem<String> tremblItem = new TreeItem<String>(taxon);

			sprot.getChildren().add(sprotItem);
			trembl.getChildren().add(tremblItem);
		}

		methodCombo.getItems().add("cwt");
		methodCombo.getSelectionModel().select(0);
	}
}
