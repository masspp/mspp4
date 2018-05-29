/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.mzml_jmzml_wrapper;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import uk.ac.ebi.jmzml.xml.io.MzMLObjectIterator;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import uk.ac.ebi.jmzml.model.mzml.Scan;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.model.mzml.Chromatogram;
import uk.ac.ebi.jmzml.model.mzml.ParamGroup;
import uk.ac.ebi.jmzml.model.mzml.CVParam;

import ninja.mspp.annotation.FileInput;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.utils.MD5Utils;
import ninja.mspp.model.dataobject.Precursor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author masakimu
*/
@Plugin( name = "mzML Input Plugin" )
public class JmzMLWrapperInputPlugin {
    
    private static final Logger logger = LoggerFactory.getLogger(JmzMLWrapperInputPlugin.class);
    
    @FileInput( title = "mzML", ext = "mzML")
    public Object openMzML(String path) throws Exception {
        jmzMLSample sample = new jmzMLSample(); 
        System.out.println("file path: " + path);
        File filepath=new File(path);
        sample.setFileName(filepath.getName());
        sample.setName(sample.getFileName());
        sample.setFilePath(filepath.getPath());
        try {
            String uid = MD5Utils.generateHash(path);
            sample.setSampleId( uid );
        }catch(NoSuchAlgorithmException e){
            String msg = "Failed to generate unique id for mzML file";
            logger.error(msg, e);
        }
        
        MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(filepath);
        getSpectra(unmarshaller, sample);
        getChromatograms(unmarshaller, sample);
        
        return sample; 
        
    }
    
    public void getCvListContents(){}
    
    public void getFileDesciptionContents(){}
    
    public void getReferenceParamGroupListContens(){}
    
    public void getSampleListContentes(){}
    
    public void getInstrumentCOnfigurationListContents(){}
    
    public void getSoftwareListContents(){}
    
    public void getDataProcessingListContents(){}
    
    public void getAcquisitionSettingsList(){}
    
    public void getSpectra(MzMLUnmarshaller unmarshaller, jmzMLSample sample){
        MzMLObjectIterator<Spectrum> specIterator = unmarshaller.unmarshalCollectionFromXpath(
                "/run/spectrumList/spectrum", Spectrum.class);
        int specCount = 0;
        while(specIterator.hasNext()){
            jmzMLSpectrum spec = new jmzMLSpectrum(sample);
            specCount++;
            Spectrum jmzmlspec = specIterator.next();
            String uid = jmzmlspec.getId();
            for(CVParam cvparam: jmzmlspec.getCvParam()){
                switch(cvparam.getName()){
                    case "ms level":
                        spec.setMsStage(Integer.parseInt(cvparam.getValue()));
                        break;
                    case "centroid spectrum":
                        spec.setIsCentroid(true);
                        break;
                    case "profile spectrum":
                        spec.setIsCentroid(false);
                        break;
                    case "highest observed m/z":
                        break;
                    case "lowest observed m/z":
                        break;
                    case "total ion current":
                        spec.setTotalintensity(Double.parseDouble(cvparam.getValue()));
                        break;
                    case "spectrum title":
                        break;
                    case "base peak m/z":
                        spec.setBasepeakMz(Double.parseDouble(cvparam.getValue()));
                    case "base peak intensity":
                        break;
                    default:
                        break;
                }
            }
            if(jmzmlspec.getScanList()!=null){
                for(Scan scan: jmzmlspec.getScanList().getScan()){
                    for(CVParam cvparam: scan.getCvParam()){
                        switch(cvparam.getName()){
                            case "scan start time":
                                if(cvparam.getUnitName()=="minute"){
                                    spec.setRt(Double.parseDouble(cvparam.getValue())/60.0);
                                }else if(cvparam.getUnitName()=="second"){
                                    spec.setRt(Double.parseDouble(cvparam.getValue()));
                                }
                                break;
                            default:
                                break;
                        
                        }
                    }
                }
            }
            spec.setId( uid );
            spec.setTitle( sample.getFileName() + " ("+ uid + ")");
            String msg = "loaded Spectrum(Id: " + uid + 
                    ", Title: " + spec.getTitle() +  ")";
            // TODO: check next line.
            if (jmzmlspec.getPrecursorList()!=null){
                spec.setPrecursorlist(getPrecursorList(jmzmlspec.getPrecursorList().getPrecursor().get(0)));
            }
            
            logger.info(msg);
            sample.addSpectrum(spec);
        }
        System.out.println("Count of spectra: " + String.valueOf(specCount));
        
        sample.setSpectrumiterator(specIterator);
        
    }
    
    
    public void getChromatograms(MzMLUnmarshaller unmarshaller, jmzMLSample sample){
        MzMLObjectIterator<Chromatogram> chromatogramIterator = unmarshaller.unmarshalCollectionFromXpath(
                "/run/chromatogramList/chromatogram", Chromatogram.class);
        int chrmtCount =0;
        while(chromatogramIterator.hasNext()){
            
            chrmtCount++;
            Chromatogram jmzmlchrmt = chromatogramIterator.next();
            String id = jmzmlchrmt.getId();
            jmzMLChromatogram chrmtgrm = new jmzMLChromatogram(sample);
            chrmtgrm.setId(id);
            chrmtgrm.setTitle(sample.getFileName() + " (" + id + ")");           
            chrmtgrm.setName(chrmtgrm.getTitle());
            chrmtgrm.setMz(getPrecursorMz( jmzmlchrmt.getPrecursor())); 
        
        }
        System.out.println("Count of chromatogram: " + String.valueOf(chrmtCount));
    }
 
    public ArrayList<Precursor> getPrecursorList( uk.ac.ebi.jmzml.model.mzml.Precursor jmzmlprec){
        Precursor default_prec = new Precursor(0.0);
        ArrayList<Precursor> preclst = new ArrayList<>();
        if(jmzmlprec!=null){
            for(ParamGroup jmzmlselectedion : jmzmlprec.getSelectedIonList().getSelectedIon()){

                for(CVParam cvparam: jmzmlselectedion.getCvParam()){
                    switch(cvparam.getName()){
                        case "selected ion m/z":
                            Precursor prec = new Precursor( Double.parseDouble(cvparam.getValue()) );
                            preclst.add(prec);
                            break;        
                    }
                }

            }
            
        }else{
            preclst.add(default_prec);
        }
        
        return preclst;
    }
    
    /**
     * obtain precursor mz  eeeeeeefirst SelectedIon tag
     * @param jmzmlpreclst
     * @return 
     */
    private double getPrecursorMz(uk.ac.ebi.jmzml.model.mzml.Precursor jmzmlprec){
        return getPrecursorList(jmzmlprec).get(0).getMz();
    }
}
      

