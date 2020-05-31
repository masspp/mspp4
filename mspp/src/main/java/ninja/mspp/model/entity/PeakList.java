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
 * @author Satoshi Tanaka
 * @since Thu Jul 11 20:44:24 JST 2019
 *
 * Copyright (c) 2019 Satoshi Tanaka
 * All rights reserved.
 */
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
        
        /**
         * just reserved (not used yet)
         */
        @ManyToOne
        private Spectrum spectrum;
        
        
        /**
         * Scan Number of spectrum (start from 1)
         */
        @Column
        private Long scanNo;
        
        /**
         * peak list internal serial number (start from 1)
         */
        @Column
        private Long index;
          
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
         * @return the scanNo
         */
        public Long getScanNo() {
            return scanNo;
        }

        /**
         * @param index the scanNo to set
         */
        public void setScanNo(Long index) {
            this.scanNo = index;
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
     * @return the index
     */
    public Long getIndex() {
        return index;
    }

    /**
     * @param index_positional the index to set
     */
    public void setIndex(Long index) {
        this.index = index;
    }

    
}
