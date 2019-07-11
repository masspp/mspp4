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

import ninja.mspp.model.dataobject.DrawPoint;
import ninja.mspp.tools.DbTool;


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
	private int id;

	private int level;

	@Column(name="POINT_LIST_ID")
	private int pointListId;

	@Lob
	@Column( name = "X_ARRAY" )
	private byte[] xArray;

	@Lob
	@Column( name = "MIN_Y_ARRAY" )
	private byte[] minYArray;

	@Lob
	@Column( name = "MAX_Y_ARRAY" )
	private byte[] maxYArray;

	@Lob
	@Column( name = "LEFT_Y_ARRAY" )
	private byte[] leftYArray;

	@Lob
	@Column( name = "RIGHT_Y_ARRAY" )
	private byte[] rightYArray;

	public DrawElementList() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPointListId() {
		return this.pointListId;
	}

	public void setPointListId(int pointListId) {
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
	 * gets the draw points
	 * @return
	 */
	public List< DrawPoint > getDrawPoints() {
		List< DrawPoint > points = new ArrayList< DrawPoint >();
		try {
			double[] xArray = DbTool.createDoubleArrayFromBytes( this.getxArray() );
			double[] minYArray = DbTool.createDoubleArrayFromBytes( this.getMinYArray() );
			double[] maxYArray = DbTool.createDoubleArrayFromBytes( this.getMaxYArray() );
			double[] leftYArray = DbTool.createDoubleArrayFromBytes( this.getLeftYArray() );
			double[] rightYArray = DbTool.createDoubleArrayFromBytes( this.getRightYArray() );

			for(
					int i = 0;
					i < xArray.length && i < minYArray.length && i < maxYArray.length
						&& i < leftYArray.length && i < rightYArray.length;
					i++
			) {
				DrawPoint point = new DrawPoint();
				point.setX( xArray[ i ] );
				point.setMinY( minYArray[ i ] );
				point.setMaxY( maxYArray[ i ] );
				point.setLeftY( leftYArray[ i ] );
				point.setRightY( rightYArray[ i ] );
				points.add( point );
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return points;
	}

	/**
	 * sets the draw points
	 * @param points draw points
	 */
	public void setDrawPoints( List< DrawPoint > points ) {
		int length = points.size();
		double[] xArray = new double[ length ];
		double[] minYArray = new double[ length ];
		double[] maxYArray = new double[ length ];
		double[] leftYArray = new double[ length ];
		double[] rightYArray = new double[ length ];

		int index = 0;
		for( DrawPoint point : points ) {
			xArray[ index ] = point.getX();
			minYArray[ index ] = point.getMinY();
			maxYArray[ index ] = point.getMaxY();
			leftYArray[ index ] = point.getLeftY();
			rightYArray[ index ] = point.getRightY();
			index++;
		}


		try {
			this.setxArray( DbTool.createBytesFromDoubleArray( xArray ) );
			this.setMinYArray( DbTool.createBytesFromDoubleArray( minYArray ) );
			this.setMaxYArray( DbTool.createBytesFromDoubleArray( maxYArray ) );
			this.setLeftYArray( DbTool.createBytesFromDoubleArray( leftYArray ) );
			this.setRightYArray( DbTool.createBytesFromDoubleArray( rightYArray ) );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
