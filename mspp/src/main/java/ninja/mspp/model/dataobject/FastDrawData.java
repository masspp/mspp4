/**
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group
 * @author Satoshi Tanaka
 * @since 2018-05-12 16:54:23+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.model.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class FastDrawData {
	/** min range */
	private static Double MIN_RANGE = 0.01;

	/** max level */
	private static Integer MAX_LEVEL = 10;

	/** files */
	private ArrayList< ArrayList< FastDrawData.Element > > points;

	/**
	 * constructor
	 * @param xyData xyData
	 */
	public FastDrawData( XYData xyData ) {
		this.points = new ArrayList< ArrayList< FastDrawData.Element > >();
		this.createData( xyData );
	}

	/**
	 * gets the poibnts
	 * @param level level
	 * @return points
	 */
	@SuppressWarnings("unchecked")
	public ArrayList< FastDrawData.Element > getPoints( Integer level ) {
		if( level < 0 || level >= this.points.size() ) {
			return null;
		}

		return this.points.get( level );
	}

	/**
	 *
	 * @param xyData
	 */
	private void createData( XYData xyData ) {
		if( this.points.size() > 0 ) {
			return;
		}
		for( int i = 0; i <= FastDrawData.MAX_LEVEL; i++ ) {
			ArrayList< FastDrawData.Element > array = this.createEachData( xyData );
			this.points.add( array );
		}
	}

	/**
	 * creates data
	 * @param xyData xy data
	 * @param level level
	 * @return temporafy file
	 */
	private ArrayList< FastDrawData.Element > createEachData( XYData xyData ) {
		ArrayList< FastDrawData.Element > points = new ArrayList< FastDrawData.Element >();

		if( this.points.size() == 0 ) {
			for( Point< Double > point: xyData ) {
				FastDrawData.Element currentPoint = new FastDrawData.Element( point.getX(), point.getY() );
				points.add( currentPoint );
			}
		}
		else {
			Integer level = this.points.size();
			Double range = FastDrawData.MIN_RANGE * Math.pow( 2.0,  level.doubleValue() );

			Long currentIndex = -1L;
			FastDrawData.Element currentPoint = null;

			ArrayList< FastDrawData.Element > array = this.points.get( level - 1 );
			for( FastDrawData.Element point : array ) {
				Double x = point.getX();
				Long index = Math.round( x / range );

				if( index > currentIndex ) {
					currentIndex = index;
					currentPoint = new FastDrawData.Element();

					currentPoint.setLeft( point.getLeft() );
					currentPoint.setRight( point.getRight() );
					currentPoint.setMin( point.getMin() );
					currentPoint.setMax( point.getMax() );
					currentPoint.setX( range * index.doubleValue() );

					points.add( currentPoint);
				}
				else {
					currentPoint.setRight( point.getRight() );
					if( point.getMax().getY() > currentPoint.getMax().getY() ) {
						currentPoint.setMax( point.getMax() );
					}
					if( point.getMin().getY() < currentPoint.getMin().getY() ) {
						currentPoint.setMin( point.getMin() );
					}
				}
			}
		}

		return points;
	}

	/**
	 * gets the level
	 * @param left left position
	 * @param right right position
	 * @param minX min x
	 * @param maxX max x
	 * @return level
	 */
	public static Integer getLevel( Integer left, Integer right, Double minX, Double maxX ) {
		return getLevel( right - left, maxX - minX );
	}

	/**
	 * gets the level
	 * @param width graph width
	 * @param range data range
	 * @return level
	 */
	public static Integer getLevel( Integer width, Double range ) {
		Double rangePerPixel = range / (double)Math.max( 1,  width );
		Double log = Math.log( rangePerPixel / FastDrawData.MIN_RANGE ) / Math.log( 2.0 );
		Integer level = (int)Math.floor( log );
		level = Math.max( 0,  Math.min( FastDrawData.MAX_LEVEL,  level ) );
		return level;
	}

	/**
	 * fast draw data point
	 */
	public static class Element implements Serializable, Comparable< Element > {
		private Double x;
		private Point< Double > left;
		private Point< Double > right;
		private Point< Double > max;
		private Point< Double > min;

		public Element() {
		}

		public Element( Double x, Double y ) {
			this.setX( x );
			this.setLeft( new Point< Double >( x, y ) );
			this.setRight( new Point< Double >( x, y ) );
			this.setMax( new Point< Double >( x, y ) );
			this.setMin( new Point< Double >( x, y ) );
		}

		public void setX( Double x ) {
			this.x = x;
		}

		public Double getX() {
			return this.x;
		}

		public Point<Double> getLeft() {
			return left;
		}
		public void setLeft(Point<Double> left) {
			this.left = left;
		}
		public Point<Double> getRight() {
			return right;
		}
		public void setRight(Point<Double> right) {
			this.right = right;
		}
		public Point<Double> getMax() {
			return max;
		}
		public void setMax(Point<Double> max) {
			this.max = max;
		}
		public Point<Double> getMin() {
			return min;
		}
		public void setMin(Point<Double> min) {
			this.min = min;
		}

		@Override
		public int compareTo(Element o) {
			if( this.getX() < o.getX() ) {
				return -1;
			}
			if( this.getX() > o.getX() ) {
				return 1;
			}
			return 0;
		}
	}
}
