package ninja.mspp.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the PEAK_LISTS database table.
 *
 */
@Entity
@Table(name="PEAK_LISTS")
@NamedQuery(name="PeakList.findAll", query="SELECT p FROM PeakList p")
public class PeakList implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	//bi-directional many-to-one association to Peak
	@OneToMany(mappedBy="peaklist")
	private List<Peak> peaks;
        
        @ManyToOne
        private PeakListHeader peaklistheader;
        
        @ManyToOne
        private Spectrum spectrum;
        
        
        //Scan Number (start from 1)
        private int index;
        
        @Column(name="MS_STAGE", nullable=true)
        private int msStage;
        
        @Column(name="RT", nullable=true)
        private String rt;
        
        @Column(name="PRECURSOR_MZ", nullable=true)
        private double precursorMz;
        
        @Column(name="PRECURSOR_CHARGE", nullable=true)
        private int precursorCharge;
        
        private String title;

	public PeakList() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Peak> getPeaks() {
		return this.peaks;
	}

	public void setPeaks(List<Peak> peaks) {
		this.peaks = peaks;
	}

	public Peak addPeak(Peak peak) {
		getPeaks().add(peak);
		peak.setPeakList(this);

		return peak;
	}

	public Peak removePeak(Peak peak) {
		getPeaks().remove(peak);
		peak.setPeakList(null);

		return peak;
	}
        
        
        /**
         * @return the index
         */
        public int getIndex() {
            return index;
        }

        /**
         * @param index the index to set
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * @return the precursorCharge
         */
        public int getPrecursorCharge() {
            return precursorCharge;
        }

        /**
         * @param precursorCharge the precursorCharge to set
         */
        public void setPrecursorCharge(int precursorCharge) {
            this.precursorCharge = precursorCharge;
        }

        /**
         * @return the precursorMz
         */
        public double getPrecursorMz() {
            return precursorMz;
        }

        /**
         * @param precursorMz the precursorMz to set
         */
        public void setPrecursorMz(double precursorMz) {
            this.precursorMz = precursorMz;
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
        * @return the msStage
        */
        public int getMsStage() {
            return msStage;
        }

        /**
         * @param msStage the msStage to set
         */
        public void setMsStage(int msStage) {
            this.msStage = msStage;
        }
        
        
        /**
         * @return the rt
         */
        public String getRt() {
            return rt;
        }

        /**
         * @param rt the rt to set
         */
        public void setRt(String rt) {
            this.rt = rt;
        }
        
        /**
        * @return the peaklistheader
        */
        public PeakListHeader getPeakListHeader() {
            return peaklistheader;
        }

        /**
         * @param peaklistheader the peaklistheader to set
         */
        public void setPeakListHeader(PeakListHeader peaklistheader) {
            this.peaklistheader = peaklistheader;
        }

        /**
         * @return the spectrum
         */
        public Spectrum getSpectrum() {
            return spectrum;
        }

        /**
         * @param spectrum the spectrum to set
         */
        public void setSpectrum(Spectrum spectrum) {
            this.spectrum = spectrum;
        }

    
}
