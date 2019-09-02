package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


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
	private int id;

	private double area;

	private double endx;

	private double endy;

	private double intensity;

	@Column(name="PEAK_POSITION")
	private double peakPosition;

	private double startx;

	private double starty;

	//bi-directional many-to-one association to Annotation
	@OneToMany(mappedBy="peak")
	private List<Annotation> annotations;

	//bi-directional many-to-one association to PeakList
	@ManyToOne
	@JoinColumn(name="PEAK_LIST_ID")
	private PeakList peaklist;

	public Peak() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getArea() {
		return this.area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public double getEndx() {
		return this.endx;
	}

	public void setEndx(double endx) {
		this.endx = endx;
	}

	public double getEndy() {
		return this.endy;
	}

	public void setEndy(double endy) {
		this.endy = endy;
	}

	public double getIntensity() {
		return this.intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public double getPeakPosition() {
		return this.peakPosition;
	}

	public void setPeakPosition(double peakPosition) {
		this.peakPosition = peakPosition;
	}

	public double getStartx() {
		return this.startx;
	}

	public void setStartx(double startx) {
		this.startx = startx;
	}

	public double getStarty() {
		return this.starty;
	}

	public void setStarty(double starty) {
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
		return this.peaklist;
	}

	public void setPeakList(PeakList peaklist) {
		this.peaklist = peaklist;
	}

}