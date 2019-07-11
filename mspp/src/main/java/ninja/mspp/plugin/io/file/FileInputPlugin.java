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
 * @author Mass++ Users Group
 * @author Satoshi Tanaka
 * @since 2018-03-13 18:14:26+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.plugin.io.file;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.method.FileInput;
import ninja.mspp.annotation.method.MenuAction;
import ninja.mspp.annotation.method.MenuPosition;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.dataobject.SampleObject;
import ninja.mspp.model.gui.MenuNode;
import ninja.mspp.model.gui.MenuNode.Order;
import ninja.mspp.service.RawDataService;
import ninja.mspp.tools.FileTool;



@Plugin( "File IO" )
@Component
public class FileInputPlugin {
	private static String RECENT_FILE_KEY = "Recent Open File";
	private MenuNode menu;

	@Autowired
	private RawDataService rawDataService;

	/**
	 * constructor
	 */
	public FileInputPlugin() {
		this.menu = MenuNode.FILE_MENU.item( "Open...", "file", Order.HIGHEST );
	}

	@MenuPosition
	public MenuNode getMenuItem() {
		return this.menu;
	}

	@MenuAction
	public void action() {
		MsppManager manager = MsppManager.getInstance();
		String path = manager.loadString( RECENT_FILE_KEY, "" );
		File file = null;
		if( !path.isEmpty() ) {
			file = new File( path );
		}

		List< PluginMethod< FileInput > > methods = manager.getMethods( FileInput.class );

		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Open File" );
		chooser.getExtensionFilters().clear();
		chooser.getExtensionFilters().add( new ExtensionFilter( "All Files", "*.*" ) );
		for( PluginMethod< FileInput > method: methods ) {
			FileInput annotation = method.getAnnotation();
			chooser.getExtensionFilters().add(
				new ExtensionFilter( annotation.title(), "*." + annotation.ext() )
			);
		}

		if( file != null ) {
			chooser.setInitialDirectory( file.getParentFile() );
			chooser.setInitialFileName( file.getName() );
		}

		Stage stage = new Stage();
		file = chooser.showOpenDialog( stage );
		if( file != null ) {
			SampleObject sampleObject = this.openFile( file );
			this.rawDataService.register( sampleObject, null );
		}
	}


	/**
	 * opens file
	 * @param file file
	 * @return file data
	 */
	protected SampleObject openFile( File file ) {
		MsppManager manager = MsppManager.getInstance();

		String path = file.getAbsolutePath();
		String ext = FileTool.getExtension( path );
		SampleObject sample = null;

		List< PluginMethod< FileInput > > methods = manager.getMethods( FileInput.class );

		for( PluginMethod< FileInput > method: methods ) {
			Object plugin = method.getPlugin();
			FileInput annotation = method.getAnnotation();
			if( sample == null && annotation.ext().compareToIgnoreCase( ext ) == 0 ) {
				try {
					sample = (SampleObject)method.getMethod().invoke( plugin,  path );
				}
				catch( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		return sample;
	}
}
