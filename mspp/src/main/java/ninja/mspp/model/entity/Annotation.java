package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ANNOTATIONS database table.
 * 
 */
@Entity
@Table(name="ANNOTATIONS")
@NamedQuery(name="Annotation.findAll", query="SELECT a FROM Annotation a")
public class Annotation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String description;

	//bi-directional many-to-one association to Peak
	@ManyToOne
	private Peak peak;

	public Annotation() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Peak getPeak() {
		return this.peak;
	}

	public void setPeak(Peak peak) {
		this.peak = peak;
	}

}