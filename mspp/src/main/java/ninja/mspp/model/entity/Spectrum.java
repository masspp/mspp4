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


/**
 * The persistent class for the SPECTRA database table.
 *
 */
@Entity
@Table(name="SPECTRA")
@NamedQuery(name="Spectrum.findAll", query="SELECT s FROM Spectrum s")
public class Spectrum implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private Double bpi;

	private Double bpm;

	@Column
	private Integer centroidMode;

	@Column
	private Double endRt;

	@Column
	private Integer msStage;

	@Column
	private String name;

	@Column
	private Long parentSpectrumId;

	@Column
	private Double precursor;

	@Column
	private Long pointListId;

	@Column
	private Integer polarity;

	@Column
	private String spectrumId;

	@Column
	private double startRt;

	@Column
	private double tic;

	@Column
	private String title;

	@Column
	private Double lowerMz;

	@Column
	private Double upperMz;

	@Column
	private Double maxIntensity;

	//bi-directional many-to-one association to Sample
	@ManyToOne
	private Sample sample;

	public Spectrum() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getBpi() {
		return bpi;
	}

	public void setBpi(Double bpi) {
		this.bpi = bpi;
	}

	public Double getBpm() {
		return bpm;
	}

	public void setBpm(Double bpm) {
		this.bpm = bpm;
	}

	public Integer getCentroidMode() {
		return centroidMode;
	}

	public void setCentroidMode(Integer centroidMode) {
		this.centroidMode = centroidMode;
	}

	public Double getEndRt() {
		return endRt;
	}

	public void setEndRt(Double endRt) {
		this.endRt = endRt;
	}

	public Integer getMsStage() {
		return msStage;
	}

	public void setMsStage(Integer msStage) {
		this.msStage = msStage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentSpectrumId() {
		return parentSpectrumId;
	}

	public void setParentSpectrumId(Long parentSpectrumId) {
		this.parentSpectrumId = parentSpectrumId;
	}

	public Double getPrecursor() {
		return precursor;
	}

	public void setPrecursor(Double precursor) {
		this.precursor = precursor;
	}

	public Long getPointListId() {
		return pointListId;
	}

	public void setPointListId(Long pointListId) {
		this.pointListId = pointListId;
	}

	public Integer getPolarity() {
		return polarity;
	}

	public void setPolarity(Integer polarity) {
		this.polarity = polarity;
	}

	public String getSpectrumId() {
		return spectrumId;
	}

	public void setSpectrumId(String spectrumId) {
		this.spectrumId = spectrumId;
	}

	public double getStartRt() {
		return startRt;
	}

	public void setStartRt(double startRt) {
		this.startRt = startRt;
	}

	public double getTic() {
		return tic;
	}

	public void setTic(double tic) {
		this.tic = tic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getLowerMz() {
		return lowerMz;
	}

	public void setLowerMz(Double lowerMz) {
		this.lowerMz = lowerMz;
	}

	public Double getUpperMz() {
		return upperMz;
	}

	public void setUpperMz(Double upperMz) {
		this.upperMz = upperMz;
	}

	public Double getMaxIntensity() {
		return maxIntensity;
	}

	public void setMaxIntensity(Double maxIntensity) {
		this.maxIntensity = maxIntensity;
	}

	public Sample getSample() {
		return this.sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

}