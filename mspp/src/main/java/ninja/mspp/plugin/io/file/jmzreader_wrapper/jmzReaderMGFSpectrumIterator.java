/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.jmzreader_wrapper;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;

import org.apache.commons.lang3.tuple.Pair;

import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;


/**
 * 
 * Iterator to generate MS1 and MS2 spectra from MGF(MS2)
 *    MS1 spectra contain just a precursor ion and used for heatmap on Mass++.
 * 
 * TODO: add support for PMF(MS1)
 *
 * @author masakimu
 */
public class jmzReaderMGFSpectrumIterator implements Iterable<Pair<Spectrum, PointList>>{
    
    private final MgfFile mgffile ;
    private final Sample sample;
    
    public jmzReaderMGFSpectrumIterator(MgfFile mgffile, Sample sample){
        this.mgffile = mgffile;
        this.sample = sample;
    }

    @Override
    public Iterator<Pair<Spectrum, PointList>> iterator() {
        return new Iterator<Pair<Spectrum, PointList>>(){
            
            private final Iterator<Ms2Query> peaklists = mgffile.getMs2QueryIterator();
            private boolean next_is_ms2 = false;
            private Pair<Spectrum, PointList> ms2_spec_point;
            
            @Override
            public boolean hasNext() {
                if ( next_is_ms2 && (ms2_spec_point!=null)){
                    return true; 
                }else{
                    return peaklists.hasNext();
                }
            }

            @Override
            public Pair<Spectrum, PointList> next() {
                if ( next_is_ms2 && (ms2_spec_point != null)){
                    next_is_ms2 = false;
                    return ms2_spec_point;
                }else{
                    ms2_spec_point = null;
                    
                    // start to create ms2 spectrum and point from  mgf peaklist
                    Ms2Query q = peaklists.next();
                    Spectrum spectrum  = new Spectrum();

                    Property props = getPropertiesbyTitle(q.getTitle());
                    props = UpdatePropertiesByAPI(q, props);

                    Map<Double, Double> peakList = q.getPeakList();
                    PointList pointList = null;
                    XYData xydata = null;
                    Pair<PointList,XYData> points = null;
                    try {
                        points = this.createPointList(
                                (Double []) peakList.keySet().toArray(new Double[0]),
                                (Double []) peakList.values().toArray(new Double[0])
                        );
                    } catch (Exception ex) {
                        Logger.getLogger(jmzReaderMGFSpectrumIterator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    pointList = points.getLeft();
                    xydata = points.getRight();

                    spectrum.setSpectrumId( Integer.toString(2*props.index) );
                    spectrum.setName( props.title);
                    spectrum.setSample(sample);
                    if (props.rt !=null){
                        spectrum.setStartRt( Double.parseDouble(props.rt) );
                        spectrum.setEndRt(  spectrum.getStartRt());
                    }
                    double tic = 0.0;
                    for(Point p : xydata){
                        tic = tic + (double) p.getX();
                    }
                    spectrum.setTic(tic);
                    spectrum.setBpi(xydata.getMaxY());
                    spectrum.setBpm(xydata.getX( xydata.searchNearestIndicies_by_Y(xydata.getMaxY()).get(0) ));
                    spectrum.setCentroidMode(1);
                    spectrum.setMsStage(props.msStage);
                    if (pointList.getMinX()!=null){
                        spectrum.setLowerMz( pointList.getMinX() );
                    }else{
                        spectrum.setLowerMz(0.0);
                    }
                    if(pointList.getMaxX()!=null){
                        spectrum.setUpperMz( pointList.getMaxX() );
                    }else{
                        spectrum.setUpperMz( 0.0 );
                    }
                    if(  (props.charge != null)){
                        spectrum.setPolarity( (props.charge > 0)?1:-1) ;
                    }
                    spectrum.setPrecursor(  props.precursorMz );
                    
                    next_is_ms2=true; 
                    ms2_spec_point = Pair.of(spectrum, pointList);
                    
                    
                    // start to create MS1 peak data from precursor information
                    // and return it 
                    Spectrum prec_spec = new Spectrum();
                    prec_spec.setSpectrumId( Integer.toString(2*props.index -1));
                    prec_spec.setName(  "Precursor for " + props.title);
                    prec_spec.setSample(sample);
                    if (props.rt !=null){
                        prec_spec.setStartRt( Double.parseDouble(props.rt)-0.001 );
                        prec_spec.setEndRt(  prec_spec.getStartRt());
                    }
                    
                    prec_spec.setCentroidMode(1);
                    prec_spec.setMsStage(1);
                    
                    PointList prec_pointList = null;
                    XYData prec_xydata = null;
                    Pair<PointList,XYData> prec_points = null;
                    Double ar_prec_mz[] = { props.precursorMz };
                    Double ar_prec_intensity[] = new Double[1];
                    
                    if(q.getPrecursorIntensity()!=null){
                        ar_prec_intensity[0] =  q.getPrecursorIntensity() ;
                    }else{
                        ar_prec_intensity[0] = spectrum.getTic() ;
                    }
                    prec_spec.setTic(ar_prec_intensity[0]);
                    prec_spec.setBpm( props.precursorMz );
                    prec_spec.setBpi(ar_prec_intensity[0] );


                    try {
                        prec_points = this.createPointList( ar_prec_mz, ar_prec_intensity );
                        prec_pointList = prec_points.getLeft();
  
                    } catch (Exception ex) {
                        Logger.getLogger(jmzReaderMGFSpectrumIterator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if (prec_pointList.getMinX()!=null){
                        prec_spec.setLowerMz( prec_pointList.getMinX() -1.0 );
                    }else{
                        prec_spec.setLowerMz(0.0);
                    }
                    if(prec_pointList.getMaxX()!=null){
                        prec_spec.setUpperMz( prec_pointList.getMaxX() + 1.0 );
                    }else{
                        prec_spec.setUpperMz( 0.0 );
                    }
                    if(  (props.charge != null)){
                        prec_spec.setPolarity( spectrum.getPolarity() ) ;
                    }

                    Pair<Spectrum, PointList> precursor_spec_point = Pair.of(prec_spec, prec_pointList);

                    return precursor_spec_point;
                }
               
            }
            
            /**
             *
             * @param xArray
             * @param yArray
             * @return
             * @throws Exception
             *
             */
            private Pair<PointList,XYData> createPointList(Double[] xArray, Double[] yArray) throws Exception {
                PointList pointList = new PointList();

                int count = 0;
                if (xArray != null && yArray != null) {
                    count = Math.min(xArray.length, yArray.length);
                }
                pointList.setDataLength(count);

                ByteArrayOutputStream xCache = new ByteArrayOutputStream();
                ByteArrayOutputStream yCache = new ByteArrayOutputStream();

                DataOutputStream xOut = new DataOutputStream(xCache);
                DataOutputStream yOut = new DataOutputStream(yCache);

                Double minX = null;
                Double maxX = null;
                Double minY = null;
                Double maxY = null;

                ArrayList<Point<Double>> points = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    double x = xArray[i];
                    double y = yArray[i];
                    
                    Point p = new Point(x,y);
                    points.add(p);
                    

                    xOut.writeDouble(x);
                    yOut.writeDouble(y);

                    if (minX == null || x < minX) {
                        minX = x;
                    }
                    if (maxX == null || x > maxX) {
                        maxX = x;
                    }
                    if (minY == null || y < minY) {
                        minY = y;
                    }
                    if (maxY == null || y > maxY) {
                        maxY = y;
                    }
                }
                XYData xydata = new XYData(points, true);

                xOut.close();
                yOut.close();
                xCache.close();
                yCache.close();

                pointList.setMinX(minX);
                pointList.setMaxX(maxX);
                pointList.setMinY(minY);
                pointList.setMaxY(maxY);
                pointList.setxArray(xCache.toByteArray());
                pointList.setyArray(yCache.toByteArray());

                return Pair.of(pointList,xydata);
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
            props.index = Integer.parseInt(title.substring(spec_id_start, spec_id_end));
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
    
    /**
     * 
     * @param q: MS2Query
     * @param props: Property
     * @return 
     */
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
    
    
}
