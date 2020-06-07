package ninja.mspp.model.dataobject;

public class Modification {
	private String modificationName;
	private String name;
	private String residue;
	private String position;
	private String classification;
	private double deltaMass;
	private boolean hidden;

	public String getModificationName() {
		return modificationName;
	}
	public void setModificationName(String modificationName) {
		this.modificationName = modificationName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResidue() {
		return residue;
	}
	public void setResidue(String residue) {
		this.residue = residue;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public double getDeltaMass() {
		return deltaMass;
	}
	public void setDeltaMass(double deltaMass) {
		this.deltaMass = deltaMass;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public String toString() {
		return this.getModificationName();
	}
}
