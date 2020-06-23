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
 * @author Masayo Kimura
 * @since 2018
 *
 * Copyright (c) 2018 Masayo Kimura
 * All rights reserved.
 */
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
import ninja.mspp.model.dataobject.SampleObject;
import ninja.mspp.model.dataobject.SpectrumObject;
import ninja.mspp.model.dataobject.XYData;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;


/**
 *
 * @author masakimu
 */
public class jmzMLChromatogram extends ninja.mspp.model.dataobject.ChromatogramObject{

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
    
    private String id_jmzml;

    public jmzMLChromatogram(SampleObject sample) {
        super(sample);
    }

    
    @Override
    public XYData onGetChromatogram() {
        jmzMLUnmarshallerFactory factory = jmzMLUnmarshallerFactory.getInstance();
        MzMLUnmarshaller unmarshaller = factory.getUnmarshaller(new File(this.getSamplePath()));
        //MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(new File(this.getSamplePath()));
        uk.ac.ebi.jmzml.model.mzml.Chromatogram chrmtgrm= getJmzmlChromatogram(unmarshaller, this.getId_jmzml());
        ArrayList<Point<Double>> points = new ArrayList<>();
        int arraylength=chrmtgrm.getBinaryDataArrayList().getBinaryDataArray().get(0).getArrayLength();
        
        double[] xarray=new double[arraylength];
        double[] yarray=new double[arraylength];  
        
        
        for (BinaryDataArray binaryDataArray : chrmtgrm.getBinaryDataArrayList().getBinaryDataArray()) {
            boolean flg_time=false;
            boolean flg_intensity = false;
            boolean flg_min2sec = false;
            double ratio_min2sec = 60.0;
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
            Point< Double > p = new Point< Double >(xarray[i],yarray[i]);
            points.add(p);
        }
    
        XYData xydata = new XYData(points, true);
        
        return xydata;
        
        
        
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<SpectrumObject> onGetSpectra(double startRt, double endRt, SearchType startSearchType, SearchType endSearchType) {
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

	@Override
	public XYData getXYData() {
		return this.getChromatogramData();
	}
}
