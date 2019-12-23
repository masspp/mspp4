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
 * @author satstnka
 * @since Fri Sep 06 13:47:14 JST 2019
 *
 * Copyright (c) 2019 satstnka
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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table( name = "PEAK_POSITIONS" )
@NamedQuery(name="PeakPosition.findAll", query="SELECT p FROM PeakPosition p")
public class PeakPosition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column
	private Double mz;

	@Column
	private Double rt;

	@Column
	private Double pValue;

	@ManyToOne
	private Project project;

	@OneToMany( mappedBy = "peakPosition",  fetch = FetchType.EAGER )
	private List<  PeakAnnotation > peakAnnotations;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getMz() {
		return mz;
	}

	public void setMz(Double mz) {
		this.mz = mz;
	}

	public Double getRt() {
		return rt;
	}

	public void setRt(Double rt) {
		this.rt = rt;
	}

	public Double getpValue() {
		return pValue;
	}

	public void setpValue(Double pValue) {
		this.pValue = pValue;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<PeakAnnotation> getPeakAnnotations() {
		return peakAnnotations;
	}

	public void setPeakAnnotations(List<PeakAnnotation> peakAnnotations) {
		this.peakAnnotations = peakAnnotations;
	}

	public PeakAnnotation addPeakAnnotation( PeakAnnotation peakAnnotation ) {
		getPeakAnnotations().add( peakAnnotation );
		peakAnnotation.setPeakPosition( this );
		return peakAnnotation;
	}

	public PeakAnnotation removePeakAnnotation( PeakAnnotation peakAnnotation ) {
		getPeakAnnotations().remove( peakAnnotation );
		peakAnnotation.setPeakPosition( null );
		return peakAnnotation;
	}
}
