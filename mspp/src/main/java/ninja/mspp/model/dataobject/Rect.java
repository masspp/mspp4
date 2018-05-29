package ninja.mspp.model.dataobject;

/**
 * rectangle class
 */
public class Rect< T extends Number > implements Cloneable {
	T left;
	T right;
	T top;
	T bottom;

	/**
	 * constructor
	 * @param top top
	 * @param right right
	 * @param bottom bottom
	 * @param left left
	 */
	public Rect( T top, T right, T bottom, T left ) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	/**
	 * constructor
	 * @param top top
	 * @param leftRight left and right
	 * @param bottom bottom
	 */
	public Rect( T top, T leftRight, T bottom ) {
		this( top,  leftRight,  bottom,  leftRight );
	}

	/**
	 * constructor
	 * @param topBottom top and bottom
	 * @param leftRight left and right
	 */
	public Rect( T topBottom, T leftRight ) {
		this( topBottom, leftRight, topBottom );
	}

	/**
	 * @param value
	 */
	public Rect( T value ) {
		this( value, value );
	}

	public T getLeft() {
		return left;
	}

	public void setLeft(T left) {
		this.left = left;
	}

	public T getRight() {
		return right;
	}

	public void setRight(T right) {
		this.right = right;
	}

	public T getTop() {
		return top;
	}

	public void setTop(T top) {
		this.top = top;
	}

	public T getBottom() {
		return bottom;
	}

	public void setBottom(T bottom) {
		this.bottom = bottom;
	}
}
