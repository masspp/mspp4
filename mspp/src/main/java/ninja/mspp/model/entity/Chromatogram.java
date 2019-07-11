package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the CHROMATOGRAMS database table.
 * 
 */
@Entity
@Table(name="CHROMATOGRAMS")
@NamedQuery(name="Chromatogram.findAll", query="SELECT c FROM Chromatogram c")
public class Chromatogram implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="MS_STAGE")
	private int msStage;

	private double mz;

	private String name;

	@Column(name="POINT_LIST_ID")
	private int pointListId;

	private String title;

	//bi-directional many-to-one association to Sample
	@ManyToOne
	private Sample sample;

	public Chromatogram() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMsStage() {
		return this.msStage;
	}

	public void setMsStage(int msStage) {
		this.msStage = msStage;
	}

	public double getMz() {
		return this.mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPointListId() {
		return this.pointListId;
	}

	public void setPointListId(int pointListId) {
		this.pointListId = pointListId;
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

}