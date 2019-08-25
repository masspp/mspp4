/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.mzml_jmzml_wrapper;

import java.util.HashMap;
import java.util.Map.Entry;
import java.io.File;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author masakimu
 * 
 * Singleton class which manage hash pool of generated mzMLUnmarshaller instance.
 * 
 */
public class jmzMLUnmarshallerFactory {
    
    private HashMap<String, MzMLUnmarshaller> pool = new HashMap<>();
    
    private jmzMLUnmarshallerFactory(){};
    
    public static jmzMLUnmarshallerFactory getInstance(){
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder {
        private static final jmzMLUnmarshallerFactory INSTANCE = new jmzMLUnmarshallerFactory();
    }
    
    public synchronized MzMLUnmarshaller getUnmarshaller(File file){
        String fullpath = file.getAbsolutePath();
        MzMLUnmarshaller unmarshaller = pool.get(""+fullpath);
        
        if(unmarshaller ==null){
            unmarshaller = new MzMLUnmarshaller(file);
            pool.put(fullpath, unmarshaller);
        }         
        return unmarshaller;
    }
    
    public synchronized void removeUnmarshaller(File file){
        pool.remove(file.getAbsolutePath());
    }
    
    public synchronized void clearAllUnmarshaller(){
        pool.clear();
    }
    
}

