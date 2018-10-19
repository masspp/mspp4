/**
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author: Mass++ User Group
 * @since: 2018
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
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
import ninja.mspp.annotation.OnOpenSample;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.dataobject.Sample;
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
			Sample sample = openFile( file );
			if( sample == null ) {
				Alert alert = new Alert( AlertType.ERROR );
				alert.setTitle( "Error" );
				alert.setHeaderText( messages.getString( "file.open.error.header" ) );
				alert.setContentText( messages.getString( "file.open.error.content" ) );
				alert.showAndWait();
			}
			else {
				ArrayList< PluginMethod< OnOpenSample > > onOpenMethods = msppManager.getMethods( OnOpenSample.class );
				for( PluginMethod< OnOpenSample > method : onOpenMethods ) {
					Object plugin = method.getPlugin();
					try {
						method.getMethod().invoke( plugin, sample );
					}
					catch( Exception e ) {
						e.printStackTrace();
					}
				}
				msppManager.addSample( sample );
				msppManager.saveString( RECENT_FILE_KEY,  file.getAbsolutePath() );
			}
		}
	}

	/**
	 * opens file
	 * @param file file
	 * @return file data
	 */
	protected Sample openFile( File file ) {
		String path = file.getAbsolutePath();
		String ext = FileTool.getExtension( path );
		Sample sample = null;

		for( PluginMethod< FileInput > method: this.methods ) {
			Object plugin = method.getPlugin();
			FileInput annotation = method.getAnnotation();
			if( sample == null && annotation.ext().compareToIgnoreCase( ext ) == 0 ) {
				try {
					sample = (Sample)method.getMethod().invoke( plugin,  path );
				}
				catch( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		return sample;
	}
}
