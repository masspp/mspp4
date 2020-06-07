package ninja.mspp.plugin.viewer.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;
import ninja.mspp.model.dataobject.Modification;
import ninja.mspp.model.entity.Project;
import ninja.mspp.service.IdentificationService;

@Component
public class IdentificationDialog implements Initializable {
	private static final String KEY_PROTEOWIZARD = "Proteo Wizard";
	private static final String KEY_COMET = "Comet";
	private static final String KEY_DATABASE_DIR = "Database Dir";

	private
	class SimpleFile extends File {
		public SimpleFile(String pathname) {
			super(pathname);
		}

		@Override
		public String toString() {
			return super.getName();
		}
	}

	@Autowired
	private IdentificationService service;

	private Project project;

	@FXML
	private ChoiceBox<String> peaksCombo;

	@FXML
	private ChoiceBox<String> methodCombo;

	@FXML
	private CheckBox deisotopeCheck;

	@FXML
	private TextField peakSnText;

	@FXML
	private TextField peakSpacingText;

	@FXML
	private ChoiceBox<String> identificationCombo;

	@FXML
	private TreeView<File> taxonView;

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
	private ProgressBar progress;

	@FXML
	private BorderPane pane;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	private void setPath(TextField text) {
		String path = text.getText();
		FileChooser chooser = new FileChooser();
		if(path != null && path.length() > 0) {
			File file = new File(path);
			chooser.setInitialDirectory(file.getParentFile());
			chooser.setInitialFileName(file.getName());
		}

		File file = chooser.showOpenDialog(new Stage());
		if(file != null) {
			text.setText(file.getAbsolutePath());
		}
	}

	private File detectPeaks(List<File> files, ProgressBar progress, double end) throws Exception {
		String command = this.proteoWizardText.getText();
		String method = this.methodCombo.getValue();
		boolean deisotope = this.deisotopeCheck.isSelected();
		double spacing = Double.parseDouble(this.peakSpacingText.getText());
		double sn = Double.parseDouble(this.peakSnText.getText());

		File merged = File.createTempFile("merged_peaks",  ".mgf");
		PrintWriter writer = new PrintWriter(new FileWriter(merged));

		System.out.println(merged.getAbsolutePath());

		int count = 0;
		for(File file : files) {
			File mgfFile = this.service.callProteoWizard(file, command,  method,  deisotope,  spacing,  sn);
			if(mgfFile != null) {
				BufferedReader reader = new BufferedReader(new FileReader(mgfFile));
				String line;
				while((line = reader.readLine()) != null) {
					writer.println(line);
					System.out.println(line);
				}
				reader.close();
				writer.println("");
				System.out.println("aaaaaaaaaaa");
			}
			else {
				System.out.println("hogehogheoge");
			}

			count++;
			double now = end * (double)count / files.size();
			progress.setProgress(now);
		}

		writer.close();
		return merged;
	}

	@FXML
	private void onOK(ActionEvent evnet) {
		MsppManager manager = MsppManager.getInstance();
		List<File> files = this.service.getRawDataFiles(this.project);
		this.progress.setProgress(0.0);

		try {
			File mgfFile = this.detectPeaks(files, this.progress, 0.5);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			this.progress.setProgress(1.0);
		}

		// part of the way

	}

	@FXML
	private void onCancel(ActionEvent event) {
		Button button = (Button)event.getSource();
		Scene scene = button.getScene();
		Stage stage = (Stage)scene.getWindow();
		stage.close();
	}

	@FXML
	private void onProteoWizard(ActionEvent event) {
		this.setPath(this.proteoWizardText);
	}

	@FXML
	private void onComet(ActionEvent event) {
		this.setPath(this.cometText);
	}

	@FXML
	private void onDb(ActionEvent event) {
		String path = this.dbText.getText();
		DirectoryChooser chooser = new DirectoryChooser();
		if(path != null && path.length() > 0) {
			File dir = new File(path);
			if(dir.exists() && dir.isDirectory()) {
				chooser.setInitialDirectory(dir);
			}
		}

		File dir = chooser.showDialog(new Stage());
		if(dir != null) {
			this.dbText.setText(dir.getAbsolutePath());
		}
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

	private void setFileAndFolders() {
		MsppManager manager = MsppManager.getInstance();
		IdentificationDialog me = this;

		String proteoWizard = manager.loadString("Proteo Wizard", null);
		if(proteoWizard != null) {
			this.proteoWizardText.setText(proteoWizard);
		}

		String comet = manager.loadString("Comet", null);
		if(comet != null) {
			this.cometText.setText(comet);;
		}

		String db = manager.loadString("Databases",  null);
		if(db != null) {
			this.dbText.setText(db);
		}

		this.dbText.textProperty().addListener(
			(observable, oldValue, newValue) -> {
				me.onChangeDb(newValue);
			}
		);
	}

	private void onChangeDb(String dir) {
		System.out.println(dir);
		File file = new SimpleFile(dir);
		TreeItem<File> root = new TreeItem<File>(file);
		this.taxonView.setRoot(root);
		this.setTree(file, root);
	}

	private void setTree(File dir, TreeItem<File> parent) {
		if(dir.isDirectory()) {
			parent.setExpanded(true);
			File[] children = dir.listFiles();
			for(File child : children) {
				File file = new SimpleFile(child.getAbsolutePath());
				TreeItem<File> item = new TreeItem<File>(file);
				parent.getChildren().add(item);
				if(child.isDirectory()) {
					setTree(child, item);
				}
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setFileAndFolders();

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

		methodCombo.getItems().add("cwt");
		methodCombo.getSelectionModel().select(0);

		this.pane.widthProperty().addListener(
				(obervable, oldValue, newValue) -> {
					this.progress.setPrefWidth(newValue.doubleValue());
				}
		);
	}
}
