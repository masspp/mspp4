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
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.Sample;
import ninja.mspp.model.dataobject.XYData;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray.Precision;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author masakimu
 */
public class jmzMLSpectrum extends ninja.mspp.model.dataobject.Spectrum{

    /**
     * @return the id_jmzml
     */
    public String getId_jmzml() {
        return id_jmzml;
    }

    /**
     * @param id_jmzml the id_jmzml to set
     */
    public void setId_jmzml(String id_jmzml) {
        this.id_jmzml = id_jmzml;
    }

    /**
     * @return the iteroffset
     */
    public int getIterOffset() {
        return iteroffset;
    }

    /**
     * @param iteroffset the iteroffset to set
     */
    public void setIterOffset(int iteroffset) {
        this.iteroffset = iteroffset;
    }


    private int iteroffset;
    private String id_jmzml;


    public jmzMLSpectrum(Sample sample) {
        super(sample);
    }

    private uk.ac.ebi.jmzml.model.mzml.Spectrum getJmzmlSpectrum(MzMLUnmarshaller unmarshaller, String id){
        try {
            uk.ac.ebi.jmzml.model.mzml.Spectrum spec = unmarshaller.getSpectrumById( id );
            return spec;
        } catch (MzMLUnmarshallerException ex) {
            // TODO: fix lator by Masaki Murase

            Logger.getLogger(jmzMLSpectrum.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * TODO: use min_x and max_x
     * @param min_x reserved
     * @param max_x reserved
     * @return
     */
    @Override
    public XYData onGetXYData(double min_x, double max_x) {
       
        MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(new File(this.getSamplePath()));
        
        uk.ac.ebi.jmzml.model.mzml.Spectrum spec = getJmzmlSpectrum(unmarshaller, this.getId_jmzml()); 
        ArrayList<Point<Double>> points = new ArrayList<>();
        int arraylength=spec.getBinaryDataArrayList().getBinaryDataArray().get(0).getArrayLength();
        double[] xarray=new double[arraylength];
        double[] yarray=new double[arraylength];
        for (BinaryDataArray binaryDataArray : spec.getBinaryDataArrayList().getBinaryDataArray()) {
            BinaryDataArray.DataType type = binaryDataArray.getDataType();
            int length = binaryDataArray.getArrayLength();

            if (type.equals(BinaryDataArray.DataType.MZ_VALUES)) {
                    boolean mzDataFound = true;
                    Precision precision = binaryDataArray.getPrecision();
                switch (precision) {
                    case FLOAT32BIT:
                    case INT32BIT:
                    case INT64BIT:
                    case FLOAT64BIT:
                        for (int i = 0; i < length; i++){
                            xarray[i]=binaryDataArray.getBinaryDataAsNumberArray()[i].doubleValue() ;
                        }
                        break;
                    default:
                        break;
                }

                System.out.println("found m/z data [" + Integer.toString(length) + "]");
            }
            if (type.equals(BinaryDataArray.DataType.INTENSITY)) {
                boolean intensityDataFound = true;
                Precision precision = binaryDataArray.getPrecision();
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
    public double onGetTotalIntensity(double min_x, double max_x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<Double> onGetXRange() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
