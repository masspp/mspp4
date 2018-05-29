/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.mzml_jmzml_wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.Sample;
import ninja.mspp.model.dataobject.Spectrum;
import ninja.mspp.model.dataobject.XYData;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author masakimu
 */
public class jmzMLChromatogram extends ninja.mspp.model.dataobject.Chromatogram{

    public jmzMLChromatogram(Sample sample) {
        super(sample);
    }

    
    @Override
    public XYData onGetChromatogram() {
        Sample sample = this.getSample();
        String path = sample.getFilePath() + File.separator + sample.getName();
        MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(new File(path));
        uk.ac.ebi.jmzml.model.mzml.Chromatogram chrmtgrm= getJmzmlChromatogram(unmarshaller, this.getId());
        ArrayList<Point<Double>> points = new ArrayList<>();
        int arraylength=chrmtgrm.getBinaryDataArrayList().getBinaryDataArray().get(0).getArrayLength();
        
        double[] xarray=new double[arraylength];
        double[] yarray=new double[arraylength];  
        
        
        for (BinaryDataArray binaryDataArray : chrmtgrm.getBinaryDataArrayList().getBinaryDataArray()) {
            boolean flg_time=false;
            boolean flg_intensity = false;
            boolean flg_min2sec = false;
            double ratio_min2sec = 1.0/60;
            double time_ratio = 1.0;
            
            
            for(CVParam cvparam: binaryDataArray.getCvParam()){
                switch(cvparam.getName()){
                    case "time array":
                        flg_time=true;
                        if (cvparam.getUnitName()=="minute"){
                            time_ratio = ratio_min2sec;
                        }else{
                            time_ratio = 1.0;
                        }
                        break;
                    case "intensity array":
                        flg_intensity = true;
                        break;
                    default: 
                        break;
                }
            }
            
            BinaryDataArray.DataType type = binaryDataArray.getDataType();
            int length = binaryDataArray.getArrayLength();
            if (flg_time) {
                    BinaryDataArray.Precision precision = binaryDataArray.getPrecision();
                switch (precision) {
                    case FLOAT32BIT:
                    case INT32BIT:
                    case INT64BIT:
                    case FLOAT64BIT: 
                        for (int i = 0; i < length; i++){
                            // unit of time array is converted to "second"
                            xarray[i]=binaryDataArray.getBinaryDataAsNumberArray()[i].doubleValue()*time_ratio ;
                        }
                        break;
                    default:
                        break;
                }

                System.out.println("found time array data [" + Integer.toString(length) + "]");
            }
            if (flg_intensity ) {
                BinaryDataArray.Precision precision = binaryDataArray.getPrecision();
                switch (precision) {
                    case FLOAT32BIT:
                    case INT32BIT:
                    case INT64BIT:
                    case FLOAT64BIT: 
                        for (int i = 0; i < length; i++){
                            yarray[i]=binaryDataArray.getBinaryDataAsNumberArray()[i].doubleValue();
                        }
                        break;
                    default:
                        break;
                }
                System.out.println("found intensity data [" + Integer.toString(length) + "]");
            } 
        }
        
        for(int i = 0; i < xarray.length; i++){
            Point p = new Point(xarray[i],yarray[i]);
            points.add(p);
        }
    
        XYData xydata = new XYData(points, true);
        
        return xydata;
        
        
        
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Spectrum> onGetSpectra(double startRt, double endRt, SearchType startSearchType, SearchType endSearchType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double onGetMass(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int onGetMsStage(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double onGetMz(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private uk.ac.ebi.jmzml.model.mzml.Chromatogram getJmzmlChromatogram(MzMLUnmarshaller unmarshaller, String id){
        try {
            uk.ac.ebi.jmzml.model.mzml.Chromatogram chrmtgrm = unmarshaller.getChromatogramById( id );
            return chrmtgrm;
        } catch (MzMLUnmarshallerException ex) {
            // TODO: fix lator by Masaki Murase
            
            Logger.getLogger(jmzMLSpectrum.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
}
