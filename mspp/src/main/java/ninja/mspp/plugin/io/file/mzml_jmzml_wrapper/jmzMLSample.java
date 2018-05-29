/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.mzml_jmzml_wrapper;

import uk.ac.ebi.jmzml.xml.io.MzMLObjectIterator;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author masakimu
 */
public class jmzMLSample extends ninja.mspp.model.dataobject.Sample {
        /**
     * @return the spectrumiterator
     */
    public MzMLObjectIterator getSpectrumiterator() {
        return spectrumiterator;
    }

    /**
     * @param spectrumiterator the spectrumiterator to set
     */
    public void setSpectrumiterator(MzMLObjectIterator spectrumiterator) {
        this.spectrumiterator = spectrumiterator;
    }

    /**
     * @return the chromatogramiterator
     */
    public MzMLObjectIterator getChromatogramiterator() {
        return chromatogramiterator;
    }

    /**
     * @param chromatogramiterator the chromatogramiterator to set
     */
    public void setChromatogramiterator(MzMLObjectIterator chromatogramiterator) {
        this.chromatogramiterator = chromatogramiterator;
    }
    
       /**
     * @return the unmarshaller
     */
    public MzMLUnmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    /**
     * @param unmarshaller the unmarshaller to set
     */
    public void setUnmarshaller(MzMLUnmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }
    
    private MzMLObjectIterator spectrumiterator;
    private MzMLObjectIterator chromatogramiterator;
    private MzMLUnmarshaller unmarshaller;
    
    public jmzMLSample(){
           
    }

    @Override
    public boolean onOpenFile(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onCloseFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 
    
}
