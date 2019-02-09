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
 * @author Masaki Murase
 * @since 2018-05-30 02:24:12+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */

package ninja.mspp.plugin.io.file.mzml_jmzml_wrapper;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.mspp.annotation.FileInput;
import ninja.mspp.annotation.Plugin;
import ninja.mspp.model.dataobject.Precursor;
import ninja.mspp.model.dataobject.Sample;
import software.amazon.awssdk.utils.Md5Utils;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.Chromatogram;
import uk.ac.ebi.jmzml.model.mzml.ParamGroup;
import uk.ac.ebi.jmzml.model.mzml.Scan;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.xml.io.MzMLObjectIterator;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;

/**
 *
 * @author masakimu
*/
@Plugin( name = "mzML Input Plugin" )
public class JmzMLWrapperInputPlugin {

    private static final Logger logger = LoggerFactory.getLogger(JmzMLWrapperInputPlugin.class);

    @FileInput( title = "mzML", ext = "mzML")
    public Sample openMzML(String path) throws Exception {
        jmzMLSample sample = new jmzMLSample();
        System.out.println("file path: " + path);
        File filepath=new File(path);
        sample.setFileName(filepath.getName());
        sample.setName(sample.getFileName());
        sample.setFilePath(filepath.getPath());
        try {
            String uid = Md5Utils.md5AsBase64(new File(path));
            sample.setSampleId( uid );
        }catch(Exception e){
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
            String uid = sample.getSampleId()+"_s"+Integer.valueOf(specCount).toString();
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
                            case "elution time":
                                if("minute".equals(cvparam.getUnitName())){
                                    spec.setRt(Double.parseDouble(cvparam.getValue())*60.0);
                                }else if("second".equals(cvparam.getUnitName())){
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
            spec.setId_jmzml(jmzmlspec.getId());
            spec.setName( spec.getId_jmzml());
            spec.setTitle( sample.getFileName() + " ("+ spec.getName() + ")");
            
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

    }


    public void getChromatograms(MzMLUnmarshaller unmarshaller, jmzMLSample sample){
        MzMLObjectIterator<Chromatogram> chromatogramIterator = unmarshaller.unmarshalCollectionFromXpath(
                "/run/chromatogramList/chromatogram", Chromatogram.class);
        int chrmtCount =0;
        while(chromatogramIterator.hasNext()){

            chrmtCount++;
            Chromatogram jmzmlchrmt = chromatogramIterator.next();
            String id = sample.getSampleId() + "_c" + Integer.valueOf(chrmtCount).toString();
            jmzMLChromatogram chrmtgrm = new jmzMLChromatogram(sample);
            chrmtgrm.setId(id);
            chrmtgrm.setName(jmzmlchrmt.getId());
            chrmtgrm.setTitle(sample.getFileName() + " (" + chrmtgrm.getName() + ")");           
            chrmtgrm.setMz(getPrecursorMz( jmzmlchrmt.getPrecursor()));
            
            sample.addChromatogram(chrmtgrm);

        }
        System.out.println("Count of chromatogram: " + String.valueOf(chrmtCount));
    }

    public ArrayList<Precursor> getPrecursorList( uk.ac.ebi.jmzml.model.mzml.Precursor jmzmlprec){
        Precursor default_prec = new Precursor(0.0);
        ArrayList<Precursor> preclst = new ArrayList<>();
        if(jmzmlprec!=null ){
            if (jmzmlprec.getSelectedIonList()!=null ){
                
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
            }
            
        }
        if(preclst.size()<1){
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


