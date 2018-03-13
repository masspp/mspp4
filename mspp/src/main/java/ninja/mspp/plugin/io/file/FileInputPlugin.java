package ninja.mspp.plugin.io.file;

import java.io.File;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.FileInput;
import ninja.mspp.annotation.Menu;
import ninja.mspp.annotation.MenuAction;
import ninja.mspp.annotation.MenuPosition;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.gui.MenuInfo;
import ninja.mspp.model.gui.MenuInfo.Order;
import ninja.mspp.tools.FileTool;
import ninja.mspp.view.GuiManager;

@Plugin( name = "File IO" )
@Menu
public class FileInputPlugin {
	private static String RECENT_FILE_KEY = "Rectn Open File";
	private MenuInfo menu;
	private ArrayList< PluginMethod< FileInput > > methods;



	/**
	 * constructor
	 */
	public FileInputPlugin() {
		this.menu = MenuInfo.FILE_MENU.item( "Open...", "file", Order.HIGHEST );
		this.methods = null;
	}

	@MenuPosition
	public MenuInfo getMenuItem() {
		return this.menu;
	}

	@MenuAction
	public void action() {
		MsppManager msppManager = MsppManager.getInstance();
		GuiManager guiManager = GuiManager.getInstance();
		ResourceBundle messages = msppManager.getMessages();

		if( this.methods == null ) {
			this.methods = msppManager.getMethods( FileInput.class );
		}

		String path = msppManager.loadString( RECENT_FILE_KEY, "" );
		File file = null;
		if( !path.isEmpty() ) {
			file = new File( path );
		}

		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Open File" );
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add( new ExtensionFilter( "All Files", "*.*" ) );
		for( PluginMethod< FileInput > method: this.methods ) {
			FileInput annotation = method.getAnnotation();
			chooser.getExtensionFilters().add(
				new ExtensionFilter( annotation.title(), "*." + annotation.ext() )
			);
		}

		if( file != null ) {
			chooser.setInitialDirectory( file.getParentFile() );
			chooser.setInitialFileName( file.getName() );
		}

		file = chooser.showOpenDialog( guiManager.getStage() );
		if( file != null ) {
			Object data = openFile( file );
			if( data == null ) {
				Alert alert = new Alert( AlertType.ERROR );
				alert.setTitle( "Error" );
				alert.setHeaderText( messages.getString( "file.open.error.header" ) );
				alert.setContentText( messages.getString( "file.open.error.content" ) );
				alert.showAndWait();
			}
			else {
				msppManager.saveString( RECENT_FILE_KEY,  file.getAbsolutePath() );
			}
		}
	}

	/**
	 * opens file
	 * @param file file
	 * @return file data
	 */
	protected Object openFile( File file ) {
		String path = file.getAbsolutePath();
		String ext = FileTool.getExtension( path );
		Object sample = null;

		for( PluginMethod< FileInput > method: this.methods ) {
			Object plugin = method.getPlugin();
			FileInput annotation = method.getAnnotation();
			if( sample == null && annotation.ext().compareToIgnoreCase( ext ) == 0 ) {
				try {
					sample = method.getMethod().invoke( plugin,  path );
				}
				catch( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		return sample;
	}
}
