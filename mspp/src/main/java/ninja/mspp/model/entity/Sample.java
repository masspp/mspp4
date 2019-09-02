package ninja.mspp.model.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the SAMPLES database table.
 *
 */
@Entity
@Table(name="SAMPLES")
@NamedQuery(name="Sample.findAll", query="SELECT s FROM Sample s")
public class Sample implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private Integer spectrumCount;

	private String acquisitionsoftware;

	private String filename;

	private String filepath;

	private String instrumentVendor;

	private String instrumentModel;

	private String instrumentAnalyzer;

	private String instrimentDetector;

	private String ionization;

	private String md5;

	private String name;

	@Column(name="REGISTRATION_DATE")
	private Timestamp registrationDate;

	@Column(name="USER_COMMENT")
	private String userComment;

	//bi-directional many-to-one association to Chromatogram
	@OneToMany(mappedBy="sample")
	private List<Chromatogram> chromatograms;

	//bi-directional many-to-one association to Spectrum
	@OneToMany(mappedBy="sample", fetch = FetchType.EAGER )
	private List<Spectrum> spectras;

	public Sample() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSpectrumCount() {
		return spectrumCount;
	}

	public void setSpectrumCount(Integer spectrumCount) {
		this.spectrumCount = spectrumCount;
	}

	public String getAcquisitionsoftware() {
		return this.acquisitionsoftware;
	}

	public void setAcquisitionsoftware(String acquisitionsoftware) {
		this.acquisitionsoftware = acquisitionsoftware;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getInstrumentVendor() {
		return instrumentVendor;
	}

	public void setInstrumentVendor(String instrumentVendor) {
		this.instrumentVendor = instrumentVendor;
	}

	public String getInstrumentModel() {
		return instrumentModel;
	}

	public void setInstrumentModel(String instrumentModel) {
		this.instrumentModel = instrumentModel;
	}

	public String getInstrumentAnalyzer() {
		return instrumentAnalyzer;
	}

	public void setInstrumentAnalyzer(String instrumentAnalyzer) {
		this.instrumentAnalyzer = instrumentAnalyzer;
	}

	public String getInstrimentDetector() {
		return instrimentDetector;
	}

	public void setInstrimentDetector(String instrimentDetector) {
		this.instrimentDetector = instrimentDetector;
	}

	public String getIonization() {
		return ionization;
	}

	public void setIonization(String ionization) {
		this.ionization = ionization;
	}

	public String getMd5() {
		return this.md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getRegistrationDate() {
		return this.registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getUserComment() {
		return this.userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public List<Chromatogram> getChromatograms() {
		return this.chromatograms;
	}

	public void setChromatograms(List<Chromatogram> chromatograms) {
		this.chromatograms = chromatograms;
	}

	public Chromatogram addChromatogram(Chromatogram chromatogram) {
		getChromatograms().add(chromatogram);
		chromatogram.setSample(this);

		return chromatogram;
	}

	public Chromatogram removeChromatogram(Chromatogram chromatogram) {
		getChromatograms().remove(chromatogram);
		chromatogram.setSample(null);

		return chromatogram;
	}

	public List<Spectrum> getSpectras() {
		return this.spectras;
	}

	public void setSpectras(List<Spectrum> spectras) {
		this.spectras = spectras;
	}

	public Spectrum addSpectra(Spectrum spectra) {
		getSpectras().add(spectra);
		spectra.setSample(this);

		return spectra;
	}

	public Spectrum removeSpectra(Spectrum spectra) {
		getSpectras().remove(spectra);
		spectra.setSample(null);

		return spectra;
	}

}