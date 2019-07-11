package ninja.mspp.plugin.viewer.waveform;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.method.ChromatogramPanel;
import ninja.mspp.annotation.method.SpectrumPanel;
import ninja.mspp.model.PluginMethod;

@Component
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class SpectrumChromatogramPanel implements Initializable {
	@FXML
	private TabPane spectrumTabPane;

	@FXML
	private TabPane chromatogramTabPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MsppManager manager = MsppManager.getInstance();

		List< PluginMethod< SpectrumPanel > > spectrumMethods = manager.getMethods( SpectrumPanel.class );
		for( PluginMethod< SpectrumPanel > spectrumMethod : spectrumMethods ) {
			Node node = ( Node )spectrumMethod.invoke();
			if( node != null ) {
				Tab tab = new Tab( spectrumMethod.getAnnotation().value() );
				BorderPane pane = new BorderPane();
				pane.setCenter( node );
				tab.setContent( pane );
				this.spectrumTabPane.getTabs().add( tab );
			}
		}

		List< PluginMethod< ChromatogramPanel > > chromatogramMethods = manager.getMethods( ChromatogramPanel.class );
		for( PluginMethod< ChromatogramPanel > chromatogramMethod : chromatogramMethods ) {
			Node node = ( Node )chromatogramMethod.invoke();
			if( node != null ) {
				Tab tab = new Tab( chromatogramMethod.getAnnotation().value() );
				BorderPane pane = new BorderPane();
				pane.setCenter( node );
				tab.setContent( pane );
				this.chromatogramTabPane.getTabs().add( tab );
			}
		}
	}

}
