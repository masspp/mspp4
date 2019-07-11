package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;


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
	private int id;

	@Column(name="PEAK_LIST_ID")
	private int peakListId;

	@Column(name="POINT_LIST_ID")
	private int pointListId;

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

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPeakListId() {
		return this.peakListId;
	}

	public void setPeakListId(int peakListId) {
		this.peakListId = peakListId;
	}

	public int getPointListId() {
		return this.pointListId;
	}

	public void setPointListId(int pointListId) {
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