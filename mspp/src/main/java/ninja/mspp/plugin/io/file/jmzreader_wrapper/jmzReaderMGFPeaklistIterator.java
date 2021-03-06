/*
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
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author Masaki Murase
 * @since 2020
 *
 * Copyright (c) 2020 Masaki Murase
 * All rights reserved.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.jmzreader_wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.PeakList;
import ninja.mspp.model.entity.PeakListHeader;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

/**
 *
 * @author masaki
 */
public class jmzReaderMGFPeaklistIterator implements Iterable<Pair<PeakList, XYData>>{
    
    private final PeakListHeader header;
    private final MgfFile mgffile;
    
    public jmzReaderMGFPeaklistIterator(MgfFile mgffile, PeakListHeader header){
        this.mgffile = mgffile;
        this.header = header;
    }

    @Override
    public Iterator<Pair<PeakList, XYData>> iterator() {
        return new Iterator<Pair<PeakList, XYData>>(){
            
            private final Iterator<Ms2Query> query = mgffile.getMs2QueryIterator();
            
            @Override
            public boolean hasNext() {
                return query.hasNext();
            }

            @Override
            public Pair<PeakList, XYData> next() {               
                Ms2Query q = query.next();
               
                //Generate XYData instance from MGF peak data
                Map<Double, Double> peaks = q.getPeakList();
                Double[] mzArray= peaks.keySet().toArray(new Double[0]);
                Double[] intArray = peaks.values().toArray(new Double[0]);
                
                int count = 0;
                if (mzArray !=null && intArray !=null){
                    count = Math.min(mzArray.length, intArray.length);
                }
                
                ArrayList<Point<Double>> points = new ArrayList<>();
                for(int i =0 ; i < count ; i++){
                    Point p = new Point(mzArray[i],intArray[i]);
                    points.add(p);
                }
                XYData xydata = new XYData(points, true);
                
                
                //Generate PeakList properties
                PeakList peaklist = new PeakList();
                
                jmzReaderMGFPeaklistIterator.Property props = getPropertiesbyTitle(q.getTitle());
                props = UpdatePropertiesByAPI(q, props);
                
                peaklist.setIndex( props.index);
                peaklist.setScanNo( props.index); 
                peaklist.setTitle(props.title);
                //header.addPeaklist(peaklist);
                if (props.rt !=null){
                    peaklist.setRt( props.rt);
                }
                peaklist.setMsStage(props.msStage);
                peaklist.setPrecursorCharge(props.charge);
                peaklist.setPrecursorMz(props.precursorMz);               
                
                return Pair.of(peaklist, xydata);

               
            }
            
        };
                
    }


       
    /**
     * extract meta information from peaklist title
     * 
     * @param title
     * @return 
     */
    private Property getPropertiesbyTitle(String title){
 
        if (title ==null){
           title = "This is test. spec_id: 22515, sample_index: 0, sample_name: MS, file_path: D://hogehoge\\^10.242.132.48\\^taba@jp\\ManualInputFile\\150211tk04-whole_2m8h-4.raw, spec_rt: 114.08659, spec_prec: 494.301454039255, spec_stage: 2, charge: 1, Precursor: 0 _multi_, polarity: 1 - Scan22515, File: 150211tk04-whole_2m8h-4.raw";
        }
        
        Property props = new Property();
        props.title = title;
        
        // extract sepc_id
        String spec_id_str = "spec_id: ";  // TODO: use property file to specify matching strings
        int pos = title.indexOf(spec_id_str);
        if (pos > -1){
            int spec_id_start = pos+spec_id_str.length();
            int spec_id_end = spec_id_start + title.substring(spec_id_start).indexOf(",");
            props.index = Long.parseLong(title.substring(spec_id_start, spec_id_end));
            //System.out.println("Title: " + title+ "\n");
            //System.out.println(spec_id_str+ props.index );
        }
        
        // extract spec_rt
        String spec_rt_str = "spec_rt: ";
        pos = title.indexOf(spec_rt_str );
        if (pos > -1 ){
            int spec_rt_start = pos+spec_rt_str.length();
            int spec_rt_end = spec_rt_start + title.substring(spec_rt_start).indexOf(",");
            props.rt = title.substring(spec_rt_start,spec_rt_end);
            //System.out.println(spec_rt_str + props.rt.toString() );
        }
        
        // extract precursor mz
        String spec_prec_str = "spec_prec: ";
        pos = title.indexOf(spec_prec_str);
        if (pos > -1){
            int spec_prec_start = pos+spec_prec_str.length();
            int spec_prec_end = spec_prec_start + title.substring(spec_prec_start).indexOf(",");
            props.precursorMz = Double.parseDouble(title.substring(spec_prec_start,spec_prec_end));
            //System.out.println(spec_prec_str + props.precursorMz.toString());
        }
        
        // extract MS stage
        String spec_stage_str = "spec_stage: ";
        pos=title.indexOf(spec_stage_str);
        if ( pos > -1 ){
            int spec_stage_start = pos+spec_stage_str.length();
            int spec_stage_end = spec_stage_start + title.substring(spec_stage_start).indexOf(",");
            props.msStage = Integer.parseInt(title.substring(spec_stage_start,spec_stage_end));
            //System.out.println(spec_stage_str + props.msStage.toString() );
        }
        
        
        // extract precursor charge
        String charge_str = "charge: ";
        pos= title.indexOf(charge_str);
        if (pos > -1 ){
            int charge_start = pos+charge_str.length();
            int charge_end = charge_start + title.substring(charge_start).indexOf(",");
            props.charge = Integer.parseInt(title.substring(charge_start,charge_end));
            //System.out.println(charge_str + props.charge );
        }   
        
        return props;
        
    }
    
    
    //TODO: change following code as static class and functions because jmaReaderMGF*Iterator has same one
 
    /**
     * 
     * @param q: MS2Query
     * @param props: Property
     * @return 
     */
    private Property UpdatePropertiesByAPI(Ms2Query q, Property props){

        if (props.index ==null){
            props.index=Long.parseLong(q.getId());  // Take Care: This is not Scan No
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
            props.precursorIntensity  = q.getPrecursorIntensity();
        }
        if (props.charge == null){
            props.charge = q.getPrecursorCharge();
        }
   
        return props;
    }
    
    
    protected class Property{
        
        public Property(){
            index=null;
            msStage=null;
            rt=null;
            precursorMz=null;
            precursorIntensity=null;
            charge=null;
            title=null;
        }
    
        private Long index;

        private Integer msStage;

        private String rt;

        private Double precursorMz;
        
        private Double precursorIntensity;

        private Integer charge;

        private String title;
        
    }
}
