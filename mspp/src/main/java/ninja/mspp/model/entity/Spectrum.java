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
	private Long id;

	private Double bpi;

	private Double bpm;

	@Column
	private Integer centroidMode;

	@Column
	private Double endRt;

	@Column
	private Integer msStage;

	@Column
	private String name;

	@Column
	private Long parentSpectrumId;

	@Column
	private Double precursor;

	@Column
	private Long pointListId;

	@Column
	private Integer polarity;

	@Column
	private String spectrumId;

	@Column
	private double startRt;

	@Column
	private double tic;

	@Column
	private String title;

	@Column
	private Double lowerMz;

	@Column
	private Double upperMz;

	@Column
	private Double maxIntensity;

	//bi-directional many-to-one association to Sample
	@ManyToOne
	private Sample sample;
        
        @OneToMany(mappedBy="spectrum")
        private List<PeakList> peaklists;

	public Spectrum() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getBpi() {
		return bpi;
	}

	public void setBpi(Double bpi) {
		this.bpi = bpi;
	}

	public Double getBpm() {
		return bpm;
	}

	public void setBpm(Double bpm) {
		this.bpm = bpm;
	}

	public Integer getCentroidMode() {
		return centroidMode;
	}

	public void setCentroidMode(Integer centroidMode) {
		this.centroidMode = centroidMode;
	}

	public Double getEndRt() {
		return endRt;
	}

	public void setEndRt(Double endRt) {
		this.endRt = endRt;
	}

	public Integer getMsStage() {
		return msStage;
	}

	public void setMsStage(Integer msStage) {
		this.msStage = msStage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentSpectrumId() {
		return parentSpectrumId;
	}

	public void setParentSpectrumId(Long parentSpectrumId) {
		this.parentSpectrumId = parentSpectrumId;
	}

	public Double getPrecursor() {
		return precursor;
	}

	public void setPrecursor(Double precursor) {
		this.precursor = precursor;
	}

	public Long getPointListId() {
		return pointListId;
	}

	public void setPointListId(Long pointListId) {
		this.pointListId = pointListId;
	}

	public Integer getPolarity() {
		return polarity;
	}

	public void setPolarity(Integer polarity) {
		this.polarity = polarity;
	}

	public String getSpectrumId() {
		return spectrumId;
	}

	public void setSpectrumId(String spectrumId) {
		this.spectrumId = spectrumId;
	}

	public double getStartRt() {
		return startRt;
	}

	public void setStartRt(double startRt) {
		this.startRt = startRt;
	}

	public double getTic() {
		return tic;
	}

	public void setTic(double tic) {
		this.tic = tic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getLowerMz() {
		return lowerMz;
	}

	public void setLowerMz(Double lowerMz) {
		this.lowerMz = lowerMz;
	}

	public Double getUpperMz() {
		return upperMz;
	}

	public void setUpperMz(Double upperMz) {
		this.upperMz = upperMz;
	}

	public Double getMaxIntensity() {
		return maxIntensity;
	}

	public void setMaxIntensity(Double maxIntensity) {
		this.maxIntensity = maxIntensity;
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