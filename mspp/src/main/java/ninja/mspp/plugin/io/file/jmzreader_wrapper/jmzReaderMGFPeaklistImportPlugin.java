/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package ninja.mspp.plugin.io.file.jmzreader_wrapper;

import ninja.mspp.annotation.method.PeaklistFileInput;
import ninja.mspp.annotation.type.Plugin;


/**
 *
 * @author masakimu
 */
@Plugin( "MGF Peaklist Import Plugin")
public class jmzReaderMGFPeaklistImportPlugin {
    
    @PeaklistFileInput( title="MGF", extensions = {"mgf", "txt"})
    public jmzReaderMGFPeaklistReader getMGFImporter(String path) throws Exception{
        jmzReaderMGFPeaklistReader importer = new jmzReaderMGFPeaklistReader(path);
    
        return importer;  
    }
    
    
}
