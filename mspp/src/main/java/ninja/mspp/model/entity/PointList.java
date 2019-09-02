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

import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;


/**
 * The persistent class for the POINT_LISTS database table.
 *
 */
@Entity
@Table(name="POINT_LISTS")
@NamedQuery(name="PointList.findAll", query="SELECT p FROM PointList p")
public class PointList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column
	private Double maxX;

	@Column
	private Double maxY;

	@Column
	private Double minX;

	@Column
	private Double minY;

	@Column
	private Integer dataLength;

	@Lob
	@Column
	private byte[] xArray;

	@Lob
	@Column
	private byte[] yArray;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}

	public Double getMaxY() {
		return maxY;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}

	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	public Double getMinY() {
		return minY;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}

	public Integer getDataLength() {
		return dataLength;
	}

	public void setDataLength(Integer dataLength) {
		this.dataLength = dataLength;
	}

	public byte[] getxArray() {
		return xArray;
	}

	public void setxArray(byte[] xArray) {
		this.xArray = xArray;
	}

	public byte[] getyArray() {
		return yArray;
	}

	public void setyArray(byte[] yArray) {
		this.yArray = yArray;
	}

	/**
	 * gets the xy data;
	 * @return xy data
	 */
	public XYData getXYData() {
		DataInputStream xIn = new DataInputStream( new ByteArrayInputStream( this.xArray ) );
		DataInputStream yIn = new DataInputStream( new ByteArrayInputStream( this.yArray ) );

		List< Point< Double > > points = new ArrayList< Point< Double > >();

		try {
			for( int i = 0; i < this.dataLength; i++ ) {
				double x = xIn.readDouble();
				double y = yIn.readDouble();
				Point< Double > point = new Point< Double >( x, y );
				points.add( point );
			}
			xIn.close();
			yIn.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
			return null;
		}

		XYData xyData = new XYData( points, false );
		if( this.minX != null ) {
			xyData.setMinX( this.minX );
		}
		if( this.maxX != null ) {
			xyData.setMaxX( this.maxX );
		}
		if( this.minY != null ) {
			xyData.setMinY( this.minY );
		}
		if( this.maxY != null ) {
			xyData.setMaxY( this.maxY );
		}

		return xyData;
	}
}
