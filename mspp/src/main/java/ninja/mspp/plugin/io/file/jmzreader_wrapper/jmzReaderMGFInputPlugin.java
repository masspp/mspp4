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
package ninja.mspp.plugin.io.file.jmzreader_wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

import ninja.mspp.annotation.method.PeaklistFileInput;
import ninja.mspp.annotation.type.Plugin;

import ninja.mspp.model.entity.PeakList;
import ninja.mspp.model.entity.Peak;
import ninja.mspp.repository.PeakListRepository;

import ninja.mspp.model.dataobject.PeakListObject; // just for test
import ninja.mspp.model.dataobject.XYData; //just for test
import ninja.mspp.model.dataobject.Point; // just for test

/**
 *
 * @author masakimu
 */
@Plugin( "MGF Input Plugin" )
public class jmzReaderMGFInputPlugin {
    
    ArrayList<PeakListObject> peaklistobjs = new ArrayList<>();
    
    public jmzReaderMGFInputPlugin(){
    }
    
    protected class Property{
        
        public Property(){
            index=null;
            msStage=null;
            rt=null;
            precursorMz=null;
            charge=null;
            title=null;
        }
    
        private Integer index;

        private Integer msStage;

        private String rt;

        private Double precursorMz;

        private Integer charge;

        private String title;
    }
    

    @Autowired
    PeakListRepository peaklistRepository;
        
    @PeaklistFileInput( title = "MGF", extensions = {"mgf","txt"})
    public void saveMGFtoDB(String path) throws Exception {
        MgfFile mgfFile = new MgfFile( new File(path));
        for (Ms2Query q: mgfFile.getMs2QueryIterator()){
            PeakList peaklist = new PeakList();
            
            // Obtain Peaklist Properties
            Property props = getPropertiesbyTitle(q.getTitle());
            props = UpdatePropertiesByAPI(q, props);          
            peaklist.setIndex(props.index);
            peaklist.setMsStage(props.msStage);
            peaklist.setRt(props.rt);
            peaklist.setPrecursorMz(props.precursorMz);
            peaklist.setPrecursorCharge(props.charge);
            peaklist.setTitle(props.title);
            
            // Obtain Peaklist Data
            q.getPeakList().entrySet().forEach((mgfpeak) -> {
                Peak peak = new Peak();
                peak.setArea(mgfpeak.getKey());
                peak.setIntensity(mgfpeak.getValue());
                peaklist.addPeak(peak);
            });

            peaklistRepository.save(peaklist);
        }
 
    }
    
    
    public void test_print(String str){
        System.out.println("given string: " + str);
    }
    
    public ArrayList<PeakListObject> openMGF(String path) throws Exception{

        MgfFile mgfFile = new MgfFile( new File(path));

        for (Ms2Query q: mgfFile.getMs2QueryIterator()){
            PeakListObject peaklist=new PeakListObject();
            
            // Obtain Peaklist Properties
            Property props = getPropertiesbyTitle(q.getTitle());
            props = UpdatePropertiesByAPI(q, props);          
            peaklist.setIndex(props.index);
            peaklist.setMsStage(props.msStage);
            peaklist.setRt(props.rt);
            peaklist.setPrecursorMz(props.precursorMz);
            peaklist.setPrecursorCharge(props.charge);
            peaklist.setTitle(props.title);
            
            // Obtain Peaks
            ArrayList<Point<Double>> points = new ArrayList<>();
            q.getPeakList().entrySet().forEach((mgfpeak) -> {
                points.add(new Point(mgfpeak.getKey(), mgfpeak.getValue()));
            });
            peaklist.setPeaks(new XYData(points, true));
            
            peaklistobjs.add(peaklist);
        }

        return peaklistobjs;
 
    }
    
    private Property UpdatePropertiesByAPI(Ms2Query q, Property props){

        if (props.index ==null){
            props.index=Integer.parseInt(q.getId());  // Take Care: This is not Scan No
        }
        if (props.msStage==null){
            props.msStage=q.getMsLevel();
        }
        if (props.rt==null){
            props.rt=q.getRetentionTime();
        }
        if (props.precursorMz == null){
            props.precursorMz = q.getPrecursorMZ();
        }
        if (props.charge == null){
            props.charge = q.getPrecursorCharge();
        }
    
        return props;
    }

    private Property getPropertiesbyTitle(String title){
 
        if (title ==null){
           title = "This is test. spec_id: 22515, sample_index: 0, sample_name: MS, file_path: D://hogehoge\\^10.242.132.48\\^taba@jp\\ManualInputFile\\150211tk04-whole_2m8h-4.raw, spec_rt: 114.08659, spec_prec: 494.301454039255, spec_stage: 2, charge: 1, Precursor: 0 _multi_, polarity: 1 - Scan22515, File: 150211tk04-whole_2m8h-4.raw";
        }
        
        Property props = new Property();
        props.title = title;
        
        // extract sepc_id
        String spec_id_str = "spec_id: ";
        int spec_id_start = title.indexOf(spec_id_str)+spec_id_str.length();
        int spec_id_end = spec_id_start + title.substring(spec_id_start).indexOf(",");
        if (spec_id_start > -1 && spec_id_end >-1){
            props.index = Integer.parseInt(title.substring(spec_id_start, spec_id_end));
            //System.out.println("Title: " + title+ "\n");
            //System.out.println(spec_id_str+ props.index );
        }
        
        // extract spec_rt
        String spec_rt_str = "spec_rt: ";
        int spec_rt_start = title.indexOf(spec_rt_str )+spec_rt_str.length();
        int spec_rt_end = spec_rt_start + title.substring(spec_rt_start).indexOf(",");
        if (spec_rt_start > -1 && spec_rt_end>-1){
            props.rt = title.substring(spec_rt_start,spec_rt_end);
            //System.out.println(spec_rt_str + props.rt.toString() );
        }
        
        // extract precursor mz
        String spec_prec_str = "spec_prec: ";
        int spec_prec_start = title.indexOf(spec_prec_str)+spec_prec_str.length();
        int spec_prec_end = spec_prec_start + title.substring(spec_prec_start).indexOf(",");
        if (spec_prec_start > -1 && spec_prec_end > -1){
            props.precursorMz = Double.parseDouble(title.substring(spec_prec_start,spec_prec_end));
            //System.out.println(spec_prec_str + props.precursorMz.toString());
        }
        
        // extract MS stage
        String spec_stage_str = "spec_stage: ";
        int spec_stage_start = title.indexOf(spec_stage_str)+spec_stage_str.length();
        int spec_stage_end = spec_stage_start + title.substring(spec_stage_start).indexOf(",");
        if (spec_stage_start > -1 && spec_stage_end >-1){
            props.msStage = Integer.parseInt(title.substring(spec_stage_start,spec_stage_end));
            //System.out.println(spec_stage_str + props.msStage.toString() );
        }
        
        
        // extract precursor charge
        String charge_str = "charge: ";
        int charge_start = title.indexOf(charge_str)+charge_str.length();
        int charge_end = charge_start + title.substring(charge_start).indexOf(",");
        if (charge_start > -1 && charge_end >-1){
            props.charge = Integer.parseInt(title.substring(charge_start,charge_end));
            //System.out.println(charge_str + props.charge );
        }   
        
        return props;
        
    }
    
    
    
}

