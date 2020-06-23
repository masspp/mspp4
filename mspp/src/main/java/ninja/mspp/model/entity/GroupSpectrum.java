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
 * @since 2019
 *
 * Copyright (c) 2019 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the GROUP_SPECTRA database table.
 *
 */
@Entity
@Table(name="GROUP_SPECTRA")
@NamedQuery(name="GroupSpectrum.findAll", query="SELECT g FROM GroupSpectrum g")
public class GroupSpectrum implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="PEAK_LIST_ID")
	private Long peakListId;

	@Column(name="POINT_LIST_ID")
	private Long pointListId;

	//bi-directional many-to-one association to GroupSample
	@ManyToOne
	@JoinColumn(name="GROUP_SAMPLE_ID")
	private GroupSample groupSample;

	//uni-directional many-to-one association to Spectrum
	@ManyToOne
	private Spectrum spectrum;

	public GroupSpectrum() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPeakListId() {
		return peakListId;
	}

	public void setPeakListId(Long peakListId) {
		this.peakListId = peakListId;
	}

	public Long getPointListId() {
		return pointListId;
	}

	public void setPointListId(Long pointListId) {
		this.pointListId = pointListId;
	}

	public GroupSample getGroupSample() {
		return this.groupSample;
	}

	public void setGroupSample(GroupSample groupSample) {
		this.groupSample = groupSample;
	}

	public Spectrum getSpectrum() {
		return this.spectrum;
	}

	public void setSpectrum(Spectrum spectrum ) {
		this.spectrum = spectrum;
	}

}