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
