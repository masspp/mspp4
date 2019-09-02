package ninja.mspp.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the PEAKS database table.
 *
 */
@Entity
@Table(name="PEAKS")
@NamedQuery(name="Peak.findAll", query="SELECT p FROM Peak p")
public class Peak implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column
	private Double area;

	@Column
	private Double endX;

	@Column
	private Double endY;

	@Column
	private Double intensity;

	@Column
	private Double peakPosition;

	@Column
	private Double startx;

	@Column
	private Double starty;

	//bi-directional many-to-one association to Annotation
	@OneToMany(mappedBy="peak")
	private List<Annotation> annotations;

	//bi-directional many-to-one association to PeakList
	@ManyToOne
	@JoinColumn(name="PEAK_LIST_ID")
	private PeakList peakList;

	public Peak() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	public Double getEndX() {
		return endX;
	}

	public void setEndX(Double endX) {
		this.endX = endX;
	}

	public Double getEndY() {
		return endY;
	}

	public void setEndY(Double endY) {
		this.endY = endY;
	}

	public Double getIntensity() {
		return intensity;
	}

	public void setIntensity(Double intensity) {
		this.intensity = intensity;
	}

	public Double getPeakPosition() {
		return peakPosition;
	}

	public void setPeakPosition(Double peakPosition) {
		this.peakPosition = peakPosition;
	}

	public Double getStartx() {
		return startx;
	}

	public void setStartx(Double startx) {
		this.startx = startx;
	}

	public Double getStarty() {
		return starty;
	}

	public void setStarty(Double starty) {
		this.starty = starty;
	}

	public List<Annotation> getAnnotations() {
		return this.annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public Annotation addAnnotation(Annotation annotation) {
		getAnnotations().add(annotation);
		annotation.setPeak(this);

		return annotation;
	}

	public Annotation removeAnnotation(Annotation annotation) {
		getAnnotations().remove(annotation);
		annotation.setPeak(null);

		return annotation;
	}

	public PeakList getPeakList() {
		return this.peakList;
	}

	public void setPeakList(PeakList peakList) {
		this.peakList = peakList;
	}

}