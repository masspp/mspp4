package ninja.mspp.plugin.viewer.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ninja.mspp.model.dataobject.GroupInfo;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.tools.FXTools;
import ninja.mspp.view.SpringFXMLLoader;


@Component
public class ProjectCreatePanel implements Initializable {
	@Autowired
	private SpringFXMLLoader loader;

	@FXML
	private TextField nameText;

	@FXML
	private TextArea descriptionText;

	@FXML
	private Button addButton;

	@FXML
	private HBox groupsBox;

	/** ok flag */
	private boolean ok;

	private List< GroupPanel > groupPanels;

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	/**
	 * gets the name
	 * @return name
	 */
	public String getName() {
		return this.nameText.getText();
	}

	/**
	 * gets the description text
	 * @return
	 */
	public String getDescription() {
		return this.descriptionText.getText();
	}

	/**
	 * gets the groups
	 * @return
	 */
	public List< GroupInfo > getGroups() {
		if( this.groupPanels.isEmpty() ) {
			return null;
		}

		List< GroupInfo > groups = new ArrayList< GroupInfo >();

		for( GroupPanel panel : this.groupPanels ) {
			List< Sample > samples = panel.getSamples();
			if( !samples.isEmpty() ) {
				GroupInfo group = new GroupInfo();
				group.setName( panel.getName() );
				group.setDescription( panel.getDescription() );
				group.setColor( panel.getColor() );
				group.setSamples( samples );
				groups.add( group );
			}
		}
		return groups;
	}

	/**
	 * adds group panel
	 */
	private void addGroupPanel() {
		try {
			Node node = this.loader.load( GroupPanel.class, "GroupPanel.fxml" );
			this.groupsBox.getChildren().add( node );
			GroupPanel panel = ( GroupPanel )this.loader.getController();
			this.groupPanels.add( panel );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * sets buttons
	 */
	private void setButtons() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.PLUS );
		this.addButton.setText( "" );
		this.addButton.setGraphic( icon );
		this.addButton.setTooltip( new Tooltip( "New project ...") );
	}

	@FXML
	private void onAddGroup( ActionEvent event ) {
		this.addGroupPanel();
	}

	@FXML
	private void onOk( ActionEvent event ) {
		this.setOk( true );
		FXTools.closeWindow( event );

	}

	@FXML
	private void onCancel( ActionEvent event ) {
		this.setOk( false );
		FXTools.closeWindow( event );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.ok = false;
		this.groupPanels = new ArrayList< GroupPanel >();
		this.setButtons();
		this.addGroupPanel();
	}
}
