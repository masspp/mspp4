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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.SampleObject;
import ninja.mspp.model.dataobject.XYData;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray.Precision;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author masakimu
 */
public class jmzMLSpectrum extends ninja.mspp.model.dataobject.SpectrumObject{

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


    public jmzMLSpectrum(SampleObject sample) {
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
                        Number[] numarray = binaryDataArray.getBinaryDataAsNumberArray();
                        for (int i = 0; i < length; i++){
                            xarray[i]=numarray[i].doubleValue() ;
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
                        //long time_intensity1 = System.currentTimeMillis();
                        Number[] numarray = binaryDataArray.getBinaryDataAsNumberArray();
                        for (int i = 0; i < length; i++){
                            yarray[i]=numarray[i].doubleValue();
                        }
                        //long time_intensity2 = System.currentTimeMillis();
                        //System.out.println("Duration to convert Intensity to array:"+Long.toString(time_intensity2- time_intensity1));
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
