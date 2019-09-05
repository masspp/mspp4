package ninja.mspp.model.dataobject;

import java.util.List;

import javafx.scene.paint.Color;
import ninja.mspp.model.entity.Sample;

/**
 * group information
 */
public class GroupInfo {
	private String name;
	private String description;
	private Color color;
	private List< Sample > samples;

	/**
	 * constructor
	 */
	public GroupInfo() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public List<Sample> getSamples() {
		return samples;
	}

	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}
}
