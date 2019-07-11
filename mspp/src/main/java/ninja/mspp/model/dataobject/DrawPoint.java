package ninja.mspp.model.dataobject;

public class DrawPoint implements Comparable< DrawPoint > {

	private Double x;
	private Double minY;
	private Double maxY;
	private Double leftY;
	private Double rightY;

	public DrawPoint() {
		this( null, null );
	}

	public DrawPoint( Double x, Double y ) {
		this.setX( x );
		this.setMinY( y );
		this.setMaxY( y );
		this.setLeftY( y );
		this.setRightY( y );
	}

	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getMinY() {
		return minY;
	}
	public void setMinY(Double minY) {
		this.minY = minY;
	}
	public Double getMaxY() {
		return maxY;
	}
	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}
	public Double getLeftY() {
		return leftY;
	}
	public void setLeftY(Double leftY) {
		this.leftY = leftY;
	}
	public Double getRightY() {
		return rightY;
	}
	public void setRightY(Double rightY) {
		this.rightY = rightY;
	}

	@Override
	public int compareTo(DrawPoint o) {
		if( this.x < o.x ) {
			return -1;
		}
		if( this.x > o.x ) {
			return 1;
		}
		return 0;
	}



}
