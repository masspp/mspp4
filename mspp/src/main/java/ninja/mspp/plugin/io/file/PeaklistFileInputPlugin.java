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

import org.springframework.stereotype.Component;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;
import ninja.mspp.annotation.method.PeaklistFileInput;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.tools.FileTool;



@Plugin( "Peaklist File IO" )
@Component
public class PeaklistFileInputPlugin {
    

        
	private static String RECENT_PEAKLIST_FILE_KEY = "Recent Open Peaklist File";

	//@Autowired
	//private PeakListService PeakListService;


	/**
	 * save peak list file
	 * @param File file
         * 
	 */
	protected void savePeakList( File file ) {
		MsppManager manager = MsppManager.getInstance();

		String path = file.getAbsolutePath();
		String ext = FileTool.getExtension( path );
		//SampleObject sample = null; 
                // Need to prevent to register same peaklist(same path?) repeatedly. 

		List< PluginMethod< PeaklistFileInput > > methods = manager.getMethods( PeaklistFileInput.class );

		for( PluginMethod< PeaklistFileInput > method: methods ) {
			Object plugin = method.getPlugin();
			PeaklistFileInput annotation = method.getAnnotation();
                        boolean is_ext_matched=false;
                        for(String annotation_ext: annotation.extensions()){
                            if (annotation_ext.compareToIgnoreCase(ext)==0){
                                is_ext_matched=true;
                                break;
                            }
                        }
			if( is_ext_matched ) {
				try {
                                    method.getMethod().invoke( plugin,  path );
				}
				catch( Exception e ) {
                                    e.printStackTrace();
				}
			}
		}

	}
}
