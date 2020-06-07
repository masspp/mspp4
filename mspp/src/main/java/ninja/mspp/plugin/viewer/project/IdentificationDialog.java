package ninja.mspp.plugin.viewer.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
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
import ninja.mspp.service.ProjectService;

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

	@Autowired
	private ProjectService projectService;

	private Project project;
	private ProjectDetailsPanel parentPanel;

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
	private ChoiceBox<Integer> cleavageCombo;

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

	@FXML
	private CheckBox decoyCheck;

	@FXML
	private TextField maxVariableModsText;

	@FXML
	private TextField requireVariableModText;

	@FXML
	private TextField maxParentChargeText;

	@FXML
	private TextField maxFragmentChargeText;

	@FXML
	private TextField termDistanceText;

	@FXML
	private TextField minParentMzText;

	@FXML
	private TextField maxParentMzText;

	@FXML
	private TextField pepTolText;

	@FXML
	private TextField fragmentBinTolText;

	@FXML
	private TextField fragmentBinOffsetText;

	@FXML
	private CheckBox aIonCheck;

	@FXML
	private CheckBox bIonCheck;

	@FXML
	private CheckBox cIonCheck;

	@FXML
	private CheckBox xIonCheck;

	@FXML
	private CheckBox yIonCheck;

	@FXML
	private CheckBox zIonCheck;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setParentPanel(ProjectDetailsPanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	private void setPath(TextField text) {
		String path = text.getText();
		FileChooser chooser = new FileChooser();
		if(path != null && path.length() > 0) {
			File file = new File(path);
			if(file.exists()) {
				chooser.setInitialDirectory(file.getParentFile());
				chooser.setInitialFileName(file.getName());
			}
		}

		File file = chooser.showOpenDialog(new Stage());
		if(file != null) {
			text.setText(file.getAbsolutePath());
		}
	}

	private File detectPeaks(List<File> files, ProgressBar progress, double end) throws Exception {
		MsppManager manager = MsppManager.getInstance();

		String command = this.proteoWizardText.getText();
		manager.saveString(IdentificationDialog.KEY_PROTEOWIZARD, command);

		String method = this.methodCombo.getValue();
		boolean deisotope = this.deisotopeCheck.isSelected();
		double spacing = Double.parseDouble(this.peakSpacingText.getText());
		double sn = Double.parseDouble(this.peakSnText.getText());

		File merged = File.createTempFile("merged_peaks",  ".mgf");
		PrintWriter writer = new PrintWriter(new FileWriter(merged));


		int count = 0;
		for(File file : files) {
			File mgfFile = this.service.callProteoWizard(file, command,  method,  deisotope,  spacing,  sn);
			if(mgfFile != null) {
				BufferedReader reader = new BufferedReader(new FileReader(mgfFile));
				String line;
				while((line = reader.readLine()) != null) {
					writer.println(line);
				}
				reader.close();
				writer.println("");
			}

			count++;
			double now = end * (double)count / files.size();
			progress.setProgress(now);
		}

		writer.close();
		return merged;
	}

	private File identify(File mgfFile) throws IOException, InterruptedException {
		MsppManager manager = MsppManager.getInstance();

		String command = this.cometText.getText();
		manager.saveString(IdentificationDialog.KEY_COMET, command);

		List<File> taxons = new ArrayList<File>();
		for(TreeItem<File> item : this.taxonView.getSelectionModel().getSelectedItems()) {
			taxons.add(item.getValue());
		}

		boolean decoy = this.decoyCheck.isSelected();
		String enzyme = this.enzymeCombo.getValue();
		String enzymeTerminal = this.terminalCombo.getValue();

		List<String> fixedMods = new ArrayList<String>();
		for(String item : this.fixedModList.getSelectionModel().getSelectedItems()) {
			fixedMods.add(item);
		}

		List<String> variableMods = new ArrayList<String>();
		for(String item : this.variableModList.getSelectionModel().getSelectedItems()) {
			variableMods.add(item);
		}

		int maxVariableMods = Integer.parseInt(this.maxVariableModsText.getText());
		int requireVariableMods = Integer.parseInt(this.requireVariableModText.getText());
		int maxParentCharge = Integer.parseInt(this.maxParentChargeText.getText());
		int maxFragmentCharge = Integer.parseInt(this.maxFragmentChargeText.getText());
		int termDistance = Integer.parseInt(this.termDistanceText.getText());
		double minParentMz = Double.parseDouble(this.minParentMzText.getText());
		double maxParentMz = Double.parseDouble(this.maxParentMzText.getText());
		double peptideTol = Double.parseDouble(this.pepTolText.getText());
		String unit = this.tolUnitCombo.getValue();
		double fragmentBinTol = Double.parseDouble(this.fragmentBinTolText.getText());
		double fragmentBinOffset = Double.parseDouble(this.fragmentBinOffsetText.getText());
		String theoreticalFragmentIons = this.theoriticalCombo.getValue();
		String isotopeError = this.isotopeCombo.getValue();
		int minCleavage = this.cleavageCombo.getValue();
		boolean aIon = this.aIonCheck.isSelected();
		boolean bIon = this.bIonCheck.isSelected();
		boolean cIon = this.cIonCheck.isSelected();
		boolean xIon = this.xIonCheck.isSelected();
		boolean yIon = this.yIonCheck.isSelected();
		boolean zIon = this.zIonCheck.isSelected();

		File file = this.service.processCometSearch(
				command, mgfFile, decoy, taxons, enzyme, enzymeTerminal, fixedMods, variableMods, maxVariableMods,
				requireVariableMods, maxParentCharge, maxFragmentCharge, termDistance, minParentMz, maxParentMz,
				peptideTol, unit, fragmentBinTol, fragmentBinOffset, theoreticalFragmentIons, isotopeError,
				minCleavage, aIon, bIon, cIon, xIon, yIon, zIon
		);
		System.out.println(file.getAbsolutePath());
		return file;
	}

	private boolean checkParameters() {
		if(this.taxonView.getSelectionModel().getSelectedItems().size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("No taxons are selected.");
			alert.setContentText("Select one or more taxons.");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	@FXML
	private void onOK(ActionEvent evnet) {
		boolean checked = this.checkParameters();
		if(checked) {
			List<File> files = this.service.getRawDataFiles(this.project);
			this.progress.setProgress(0.0);

			try {
				System.out.println("Detecting Peaks ...");
				File mgfFile = this.detectPeaks(files, this.progress, 0.5);
				System.out.println("Identification ...");
				File pepXmlFile = this.identify(mgfFile);
				this.progress.setProgress(0.75);
				this.projectService.importPepxml( this.project, pepXmlFile.getAbsolutePath());
				this.parentPanel.updateTables();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				this.progress.setProgress(1.0);
			}
		}
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
		MsppManager manager = MsppManager.getInstance();
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
		manager.saveString(IdentificationDialog.KEY_DATABASE_DIR, dir.getAbsolutePath());
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

		this.taxonView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		this.peaksCombo.getItems().add("ProteoWizard");
		this.peaksCombo.getSelectionModel().select(0);

		this.identificationCombo.getItems().add("Comet");
		this.identificationCombo.getSelectionModel().select(0);

		this.enzymeCombo.getItems().add("No Enzyme");
		this.enzymeCombo.getItems().add("Trypsin");
		this.enzymeCombo.getItems().add("Trypsin/P");
		this.enzymeCombo.getItems().add("Lys C");
		this.enzymeCombo.getItems().add("Lys N");
		this.enzymeCombo.getItems().add("Arg C");
		this.enzymeCombo.getItems().add("Asp N");
		this.enzymeCombo.getItems().add("CNBr");
		this.enzymeCombo.getItems().add("Glu C");
		this.enzymeCombo.getItems().add("PepsinA");
		this.enzymeCombo.getItems().add("NoCleavage");
		this.enzymeCombo.getSelectionModel().select(1);

		this.terminalCombo.getItems().add("Fully Digested");
		this.terminalCombo.getSelectionModel().select(0);

		List<Modification> modifications = this.loadModifications();
		for(Modification modification: modifications) {
			if(!modification.isHidden()) {
				this.fixedModList.getItems().add(modification.getTitle());
				this.variableModList.getItems().add(modification.getTitle());
			}
		}

		this.tolUnitCombo.getItems().add("amu");
		this.tolUnitCombo.getItems().add("mmu");
		this.tolUnitCombo.getItems().add("ppm");
		this.tolUnitCombo.getSelectionModel().select(0);

		this.theoriticalCombo.getItems().add("Use Flanking Peaks");
		this.theoriticalCombo.getSelectionModel().select(0);

		this.isotopeCombo.getItems().add("Standard C13 Error");
		this.isotopeCombo.getItems().add("0, +1, +2 Isotope Offsets");
		this.isotopeCombo.getItems().add("0, +1, +2, +3 Isotope Offsets");
		this.isotopeCombo.getItems().add("Searches -8, -4, 0, +4, +8 Isotope Offsets");
		this.isotopeCombo.getItems().add("-1, 0, +1, +2, +3 Isotope Offsets");
		this.isotopeCombo.getSelectionModel().select(0);


		this.cleavageCombo.getItems().addAll(1);
		this.cleavageCombo.getItems().addAll(2);
		this.cleavageCombo.getItems().addAll(3);
		this.cleavageCombo.getItems().addAll(4);
		this.cleavageCombo.getSelectionModel().clearAndSelect(0);

		methodCombo.getItems().add("cwt");
		methodCombo.getSelectionModel().select(0);

		this.pane.widthProperty().addListener(
				(obervable, oldValue, newValue) -> {
					this.progress.setPrefWidth(newValue.doubleValue());
				}
		);

		MsppManager manager = MsppManager.getInstance();

		String file = manager.loadString(IdentificationDialog.KEY_PROTEOWIZARD, null);
		if(file != null) {
			this.proteoWizardText.setText(file);
		}

		file = manager.loadString(IdentificationDialog.KEY_COMET, null);
		if(file != null) {
			this.cometText.setText(file);
		}

		file = manager.loadString(IdentificationDialog.KEY_DATABASE_DIR, null);
		if(file != null) {
			this.dbText.setText(file);
			this.onChangeDb(file);
		}
	}
}
