package ninja.mspp.model.entity;

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
import ninja.mspp.tools.DbTool;


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
	private int id;

	@Column(name="MAX_X")
	private double maxX;

	@Column(name="MAX_Y")
	private double maxY;

	@Column(name="MIN_X")
	private double minX;

	@Column(name="MIN_Y")
	private double minY;

	@Lob
	@Column( name = "X_ARRAY" )
	private byte[] xArray;

	@Lob
	@Column( name = "Y_ARRAY" )
	private byte[] yArray;

	private String xunit;

	private String yunit;

	public PointList() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getMaxX() {
		return this.maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMaxY() {
		return this.maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double getMinX() {
		return this.minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMinY() {
		return this.minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public String getXunit() {
		return this.xunit;
	}

	public void setXunit(String xunit) {
		this.xunit = xunit;
	}

	public String getYunit() {
		return this.yunit;
	}

	public void setYunit(String yunit) {
		this.yunit = yunit;
	}

	public byte[] getxArray() {
		return xArray;
	}

	public void setxArray( byte[] xArray) {
		this.xArray = xArray;
	}

	public byte[] getyArray() {
		return yArray;
	}

	public void setyArray( byte[] yArray) {
		this.yArray = yArray;
	}

	/**
	 * gets the xy data
	 * @return xy data
	 */
	public XYData getXYData() {
		List< Point< Double > > points = new ArrayList< Point< Double > >();
		try {
			double[] xArray = DbTool.createDoubleArrayFromBytes( this.getxArray() );
			double[] yArray = DbTool.createDoubleArrayFromBytes( this.getyArray() );

			for( int i = 0; i < xArray.length && i < yArray.length; i++ ) {
				double x = xArray[ i ];
				double y = yArray[ i ];
				Point< Double > point = new Point< Double >( x, y );
				points.add( point );
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		XYData xyData = new XYData( points );
		return xyData;
	}

	/**
	 * sets the xy data
	 * @param xyData xy data
	 */
	public void setXYData( XYData xyData ) {
		int length = xyData.getPoints().size();

		double[] xArray = new double[ length ];
		double[] yArray = new double[ length ];

		int index = 0;
		for( Point< Double > point : xyData ) {
			xArray[ index ] = point.getX();
			yArray[ index ] = point.getY();
			index++;
		}

		try {
			this.setxArray( DbTool.createBytesFromDoubleArray( xArray ) );
			this.setyArray( DbTool.createBytesFromDoubleArray( yArray ) );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
