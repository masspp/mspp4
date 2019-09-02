package ninja.mspp.model.entity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ninja.mspp.model.dataobject.DrawPoint;


/**
 * The persistent class for the DRAW_ELEMENT_LISTS database table.
 *
 */
@Entity
@Table(name="DRAW_ELEMENT_LISTS")
@NamedQuery(name="DrawElementList.findAll", query="SELECT d FROM DrawElementList d")
public class DrawElementList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private Integer level;

	@Column
	private Long pointListId;

	@Column
	private Integer dataLength;

	@Lob
	@Column
	private byte[] xArray;

	@Lob
	@Column
	private byte[] minYArray;

	@Lob
	@Column
	private byte[] maxYArray;

	@Lob
	@Column
	private byte[] leftYArray;

	@Lob
	@Column
	private byte[] rightYArray;

	public DrawElementList() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getDataLength() {
		return dataLength;
	}

	public void setDataLength(Integer dataLength) {
		this.dataLength = dataLength;
	}

	public Long getPointListId() {
		return pointListId;
	}

	public void setPointListId(Long pointListId) {
		this.pointListId = pointListId;
	}

	public byte[] getxArray() {
		return xArray;
	}

	public void setxArray(byte[] xArray) {
		this.xArray = xArray;
	}

	public byte[] getMinYArray() {
		return minYArray;
	}

	public void setMinYArray(byte[] minYArray) {
		this.minYArray = minYArray;
	}

	public byte[] getMaxYArray() {
		return maxYArray;
	}

	public void setMaxYArray(byte[] maxYArray) {
		this.maxYArray = maxYArray;
	}

	public byte[] getLeftYArray() {
		return leftYArray;
	}

	public void setLeftYArray(byte[] leftYArray) {
		this.leftYArray = leftYArray;
	}

	public byte[] getRightYArray() {
		return rightYArray;
	}

	public void setRightYArray(byte[] rightYArray) {
		this.rightYArray = rightYArray;
	}


	/**
	 * gets draw points
	 * @return
	 */
	public List< DrawPoint > getDrawPoints() throws Exception {
		List< DrawPoint > list = new ArrayList< DrawPoint >();

		DataInputStream xIn = new DataInputStream( new ByteArrayInputStream( this.xArray ) );
		DataInputStream maxIn = new DataInputStream( new ByteArrayInputStream( this.maxYArray ) );
		DataInputStream minIn = new DataInputStream( new ByteArrayInputStream( this.minYArray ) );
		DataInputStream leftIn = new DataInputStream( new ByteArrayInputStream( this.leftYArray ) );
		DataInputStream rightIn = new DataInputStream( new ByteArrayInputStream( this.rightYArray ) );

		for( int i = 0; i < this.dataLength; i++ ) {
			double x = xIn.readDouble();
			double maxY = maxIn.readDouble();
			double minY = minIn.readDouble();
			double leftY = leftIn.readDouble();
			double rightY = rightIn.readDouble();

			DrawPoint point = new DrawPoint();
			point.setX( x );
			point.setMinY( minY );
			point.setMaxY( maxY );
			point.setLeftY( leftY );
			point.setRightY( rightY );
			list.add( point );
		}

		xIn.close();
		maxIn.close();
		minIn.close();
		leftIn.close();
		rightIn.close();

		return list;
	}
}
