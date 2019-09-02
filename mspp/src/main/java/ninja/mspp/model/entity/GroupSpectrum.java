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
	@JoinColumn(name="SPECTRUM_ID")
	private Spectrum spectra;

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

	public Spectrum getSpectra() {
		return this.spectra;
	}

	public void setSpectra(Spectrum spectra) {
		this.spectra = spectra;
	}

}