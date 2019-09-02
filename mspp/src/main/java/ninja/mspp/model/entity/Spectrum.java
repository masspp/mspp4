package ninja.mspp.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the SPECTRA database table.
 *
 */
@Entity
@Table(name="SPECTRA")
@NamedQuery(name="Spectrum.findAll", query="SELECT s FROM Spectrum s")
public class Spectrum implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private double bpi;

	private double bpm;

	@Column(name="CENTROID_MODE")
	private int centroidMode;

	@Column(name="END_RT")
	private double endRt;

	@Column(name="MS_STAGE")
	private int msStage;

	private String name;

	@Column(name="PARENT_SPECTRUM_ID")
	private int parentSpectrumId;

	@Column(name="POINT_LIST_ID")
	private int pointListId;

	private int polarity;

	private String spectrumid;

	@Column(name="START_RT")
	private double startRt;

	private double tic;

	private String title;

	//bi-directional many-to-one association to Sample
	@ManyToOne
	private Sample sample;
        
        @OneToMany(mappedBy="spectrum")
        private List<PeakList> peaklists;

	public Spectrum() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getBpi() {
		return this.bpi;
	}

	public void setBpi(double bpi) {
		this.bpi = bpi;
	}

	public double getBpm() {
		return this.bpm;
	}

	public void setBpm(double bpm) {
		this.bpm = bpm;
	}

	public int getCentroidMode() {
		return this.centroidMode;
	}

	public void setCentroidMode(int centroidMode) {
		this.centroidMode = centroidMode;
	}

	public double getEndRt() {
		return this.endRt;
	}

	public void setEndRt(double endRt) {
		this.endRt = endRt;
	}

	public int getMsStage() {
		return this.msStage;
	}

	public void setMsStage(int msStage) {
		this.msStage = msStage;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentSpectrumId() {
		return this.parentSpectrumId;
	}

	public void setParentSpectrumId(int parentSpectrumId) {
		this.parentSpectrumId = parentSpectrumId;
	}

	public int getPointListId() {
		return this.pointListId;
	}

	public void setPointListId(int pointListId) {
		this.pointListId = pointListId;
	}

	public int getPolarity() {
		return this.polarity;
	}

	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}

	public String getSpectrumid() {
		return this.spectrumid;
	}

	public void setSpectrumid(String spectrumid) {
		this.spectrumid = spectrumid;
	}

	public double getStartRt() {
		return this.startRt;
	}

	public void setStartRt(double startRt) {
		this.startRt = startRt;
	}

	public double getTic() {
		return this.tic;
	}

	public void setTic(double tic) {
		this.tic = tic;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Sample getSample() {
		return this.sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

        /**
         * @return the peaklists
         */
        public List<PeakList> getPeakLists() {
            return peaklists;
        }

        /**
         * @param peaklists the peaklists to set
         */
        public void setPeakLists(List<PeakList> peaklists) {
            this.peaklists = peaklists;
        }       
        
        public PeakList addPeakList(PeakList peaklist){
            getPeakLists().add(peaklist);
            peaklist.setSpectrum(this);
            return peaklist;
        }
        
        public PeakList removePeakList(PeakList peaklist){
            getPeakLists().remove(peaklist);
            peaklist.setSpectrum(null);
            return peaklist;
        }
        
}