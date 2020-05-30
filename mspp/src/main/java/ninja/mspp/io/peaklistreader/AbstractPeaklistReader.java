/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.io.peaklistreader;

import java.nio.file.Paths;


/**
 *
 * @author masaki
 */
public abstract class AbstractPeaklistReader implements IPeaklistReader {
    
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    
    /**
     * 
     */
    protected String path = "";
    
    
    /**
     * 
     * @param path 
     */
    public AbstractPeaklistReader(String path) throws Exception {
        this.path = Paths.get(path).toAbsolutePath().normalize().toString();
    }

}
