package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


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
	private int id;

	//bi-directional many-to-one association to Peak
	@OneToMany(mappedBy="peakList")
	private List<Peak> peaks;

	public PeakList() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
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

}