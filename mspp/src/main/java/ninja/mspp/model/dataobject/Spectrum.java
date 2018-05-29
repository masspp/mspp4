/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;

import java.util.ArrayList;



/**
 *
 * @author masakimu
 */
public abstract class Spectrum {

    /**
     * @return the children
     */
    public ArrayList<Spectrum> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ArrayList<Spectrum> children) {
        this.children = children;
    }

    /**
     * @param rt the rt to set (time unit is "second")
     */
    public void setRt(Range rt) {
        this.rt = rt;
    }

    /**
     * @return the precursorlist
     */
    public ArrayList<Precursor> getPrecursorlist() {
        return precursorlist;
    }

    /**
     * @param precursorlist the precursorlist to set
     */
    public void setPrecursorlist(ArrayList<Precursor> precursorlist) {
        this.precursorlist = precursorlist;
    }
    
    /**
     * 
     * @return 
     */
    public double getPrecursorMz(){
        return precursorlist.get(0).getMz();
    }

    /**
     * 
     * @return 
     */
    public double getPrecursorIntensity(){
        return precursorlist.get(0).getIntensity();
    }
    
    public int getPrecursorCharge(){
        return precursorlist.get(0).getCharge();
    }
    
    /**
     * @return the polarity
     */
    public Polarity getPolarity() {
        return polarity;
    }

    /**
     * @param polarity the polarity to set
     */
    public void setPolarity(Polarity polarity) {
        this.polarity = polarity;
    }

    /**
     * @return the sample
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the is_centroid
     */
    public boolean IsCentroid() {
        return is_centroid;
    }

    /**
     * @param is_centroid the is_centroid to set
     */
    public void setIsCentroid(boolean is_centroid) {
        this.is_centroid = is_centroid;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the ms_stage
     */
    public int getMsStage() {
        return ms_stage;
    }

    /**
     * @param ms_stage the ms_stage to set
     */
    public void setMsStage(int ms_stage) {
        this.ms_stage = ms_stage;
    }




    /**
     * @return the has_chromatogram
     */
    public boolean getHasChromatogram() {
        return has_chromatogram;
    }

    /**
     * @param has_chromatogram the has_chromatogram to set
     */
    public void setHasChromatogram(boolean has_chromatogram) {
        this.has_chromatogram = has_chromatogram;
    }

    /**
     * @return the spotid
     */
    public String getSpotid() {
        return spotid;
    }

    /**
     * @param spotid the spotid to set
     */
    public void setSpotid(String spotid) {
        this.spotid = spotid;
    }

    /**
     * @return the MassIntensityData
     */
    public XYData getMassIntensityData() {
        return MassIntensityData;
    }

    /**
     * @param MassIntensityData the MassIntensityData to set
     */
    public void setMassIntensityData(XYData MassIntensityData) {
        this.MassIntensityData = MassIntensityData;
        this.min_x = MassIntensityData.getMinX();
        this.max_x = MassIntensityData.getMaxX();
        this.min_y = MassIntensityData.getMinY();
        this.max_y = MassIntensityData.getMaxY();
    }

    /**
     * @return the min_x
     */
    public double getMinX() {
        return min_x;
    }

    /**
     * @return the max_x
     */
    public double getMaxX() {
        return max_x;
    }

    /**
     * @return the min_y
     */
    public double getMinY() {
        return min_y;
    }

    /**
     * @return the max_y
     */
    public double getMaxY() {
        return max_y;
    }

    /**
     * @return the totalintensity
     */
    public double getTotalintensity() {
        return totalintensity;
    }

    /**
     * @param totalintensity the totalintensity to set
     */
    public void setTotalintensity(double totalintensity) {
        this.totalintensity = totalintensity;
    }

    /**
     * @return the mz of base peak
     */
    public double getBasepeakMz() {
        return basepeakmz;
    }

    /**
     * @param basepeakmass set the mz of base peak
     */
    public void setBasepeakMz(double basepeakmz) {
        this.basepeakmz = basepeakmz;
    }

    /**
     * @return the parent
     */
    public Spectrum getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Spectrum parent) {
        this.parent = parent;
    }
    
    
       /**
     * @return the is_identified
     */
    public boolean IsIdentified() {
        return is_identified;
    }

    /**
     * @param is_identified the is_identified to set
     */
    public void setIsIdentified(boolean is_identified) {
        this.is_identified = is_identified;
    }
    
    
    private Sample sample;
    private String id;
    private boolean is_centroid;
    private String title;
    private int ms_stage;
    private boolean is_identified;
    //private List<Peptide> PeptideCandidates;  // getPeptideCandidates;

    private Range<Double> rt;
    
    private boolean has_chromatogram;
        
    private String spotid; // MALDI/SELDI, etc.
    
    protected enum Polarity {
        Positive,
        Negative,
        Unknown
    }
    private Polarity polarity;
    
    private ArrayList<Precursor> precursorlist;
    private XYData MassIntensityData;
    private double min_x;
    private double max_x;
    private double min_y;
    private double max_y;
    private double totalintensity; 
    private double basepeakmz;
    
    private Spectrum parent;
    private ArrayList<Spectrum> children;
    
    public Spectrum(Sample sample){
        //Initialize
        this.sample = sample;
        this.id = null;
        this.is_centroid=false;
        this.MassIntensityData=null;
        this.basepeakmz=0.0;
        this.totalintensity=0.0;
        this.children = null;
        this.has_chromatogram=sample.getHasChromatogram();
        this.max_x=Double.MAX_VALUE;
        this.min_x=Double.MIN_VALUE;
        this.min_y=Double.MIN_VALUE;
        this.max_y=Double.MAX_VALUE;
        this.ms_stage = 1;
        this.rt=new Range<>(0.0, 0.0);
        this.parent=null;
        this.spotid="";
        this.title="";
        this.polarity=Polarity.Unknown;
        this.precursorlist=null;
        this.is_identified=false;
    }
    
    public void setRt(double rt){
        this.setRt(new Range<>(rt,rt));
    }
    
    /**
     * get retention time in "second"
     * @return 
     */
    public double getRt(){
        return (rt.getStart() + rt.getEnd())/2.0;
    }

    public abstract XYData onGetXYData(double min_x, double max_x);
    
    public abstract double  onGetTotalIntensity(double min_x, double max_x);
    
    public abstract Range<Double> onGetXRange(); // required?

}
