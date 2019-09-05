package ninja.mspp.plugin.viewer.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.tools.FXTools;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.list.SampleTableView;

@Component
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class GroupPanel implements Initializable {
	@FXML
	private TextField nameText;

	@FXML
	private TextArea descriptionText;

	@FXML
	private ColorPicker color;

	@FXML
	private BorderPane samplePane;

	@FXML
	private Button addButton;

	@Autowired
	private SpringFXMLLoader loader;

	// table
	private SampleTableView table;

	/**
	 * gets the group name
	 * @return group name
	 */
	public String getName() {
		return this.nameText.getText();
	}

	/**
	 * gets the group description
	 * @return group description
	 */
	public String getDescription() {
		return this.descriptionText.getText();
	}

	/**
	 * gets the color
	 * @return
	 */
	public Color getColor() {
		return this.color.getValue();
	}

	/**
	 * gets the saples
	 * @return
	 */
	public List< Sample > getSamples() {
		List< Sample > samples = new ArrayList< Sample >();

		for( Sample sample : this.table.getItems() ) {
			samples.add( sample );
		}

		return samples;
	}

	/**
	 * sets the color
	 * @param color color
	 */
	public void setColor( Color color ) {
		this.color.setValue( color );
	}

	private void setButtons() {
		Text icon = GlyphsDude.createIcon( FontAwesomeIcon.PLUS );
		this.addButton.setText( "" );
		this.addButton.setGraphic( icon );
		this.addButton.setTooltip( new Tooltip( "Add Samples ...") );
	}

	@FXML
	private void onAddButton( ActionEvent event ) {
		try {
			SampleSelectPanel panel = ( SampleSelectPanel )FXTools.openModelDialog(
				this.loader,
				"Samples",
				SampleSelectPanel.class,
				"SampleSelectPanel.fxml",
				event
			);

			if( panel.isOk() ) {
				List< Sample > samples = panel.getSamples();
				this.table.getItems().clear();
				for( Sample sample : samples ) {
					this.table.getItems().add( sample );
				}
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setButtons();
		this.color.setValue( Color.RED );
		this.table = new SampleTableView();
		this.samplePane.setCenter( table );
	}
}
