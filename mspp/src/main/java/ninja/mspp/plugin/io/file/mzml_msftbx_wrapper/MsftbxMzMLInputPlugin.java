/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.mzml_msftbx_wrapper;

//MSPP4
import ninja.mspp.annotation.method.FileInput;
import ninja.mspp.annotation.type.Plugin;


/**
 *
 * @author masakimu
 */
@Plugin( "mzML Input Plugin" )
public class MsftbxMzMLInputPlugin {

    /**
     * 
     * @param path
     * @return 
     */
    @FileInput( title = "mzML", extensions = {"mzML"})
    public MsftbxMzMLReader getMzMLInputAdapter(String path) {
        MsftbxMzMLReader datareader = new MsftbxMzMLReader(path);
        return datareader;
    }
    
    
}
