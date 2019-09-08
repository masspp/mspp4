package ninja.mspp.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Lob;


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
        
        
        /**
         * Scan Number of spectrum (start from 1)
         */
        private Integer index;
        
        /**
         * position index in peaklist file (start from 1)
         */
        private Integer index_positional;
        
        @Column(nullable=true)
        private Integer msStage;
        
        @Column(nullable=true)
        private String rt;
        
        @Column(nullable=true)
        private Double precursorMz;
        
        @Column(nullable=true)
        private Integer precursorCharge;
        
        @Column(columnDefinition="clob") // just for derby to override default settings of clob(255)
        @Lob
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
            this.peaks.sort((p1, p2)-> Double.compare(p1.getPeakPosition(), p2.getPeakPosition()));;
            return this.peaks;
	}

	public void setPeaks(List<Peak> peaks) {
		this.peaks = peaks;
	}

	public Peak addPeak(Peak peak) {
		this.getPeaks().add(peak);
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
        public Integer getIndex() {
            return index;
        }

        /**
         * @param index the index to set
         */
        public void setIndex(Integer index) {
            this.index = index;
        }

        /**
         * @return the precursorCharge
         */
        public Integer getPrecursorCharge() {
            return precursorCharge;
        }

        /**
         * @param precursorCharge the precursorCharge to set
         */
        public void setPrecursorCharge(Integer precursorCharge) {
            this.precursorCharge = precursorCharge;
        }

        /**
         * @return the precursorMz
         */
        public Double getPrecursorMz() {
            return precursorMz;
        }

        /**
         * @param precursorMz the precursorMz to set
         */
        public void setPrecursorMz(Double precursorMz) {
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
        public Integer getMsStage() {
            return msStage;
        }

        /**
         * @param msStage the msStage to set
         */
        public void setMsStage(Integer msStage) {
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
        
   /**
     * @return the index_positional
     */
    public Integer getIndex_positional() {
        return index_positional;
    }

    /**
     * @param index_positional the index_positional to set
     */
    public void setIndex_positional(Integer index_positional) {
        this.index_positional = index_positional;
    }

    
}
