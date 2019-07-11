package ninja.mspp.model.dataobject;

/**
 * constructor
 */
public abstract class DrawObject {
	/** sample */
	private SampleObject sample;

	/** name */
	private String name;

	/**
	 * constructor
	 * @param sample sample
	 */
	public DrawObject( SampleObject sample ) {
		this.setSample( sample );
		this.setName( "" );
	}

	public SampleObject getSample() {
		return sample;
	}

	public void setSample(SampleObject sample) {
		this.sample = sample;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	abstract public XYData getXYData();
}
