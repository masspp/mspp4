package ninja.mspp.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table( name = "PEAK_ANNOTATIONS" )
@NamedQuery(name="PeakAnnotation.findAll", query="SELECT p FROM PeakAnnotation p")
public class PeakAnnotation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column
	private String description;

	@Column
	private Double score;

	@Column
	private String peptide;

	@Column
	private String protein;

	@ManyToOne
	private PeakPosition peakPosition;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getPeptide() {
		return peptide;
	}

	public void setPeptide(String peptide) {
		this.peptide = peptide;
	}

	public String getProtein() {
		return protein;
	}

	public void setProtein(String protein) {
		this.protein = protein;
	}

	public PeakPosition getPeakPosition() {
		return peakPosition;
	}

	public void setPeakPosition(PeakPosition peakPosition) {
		this.peakPosition = peakPosition;
	}
}
