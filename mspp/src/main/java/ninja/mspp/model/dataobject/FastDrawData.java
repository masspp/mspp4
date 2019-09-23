/*
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
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author Satoshi Tanaka
 * @since Sat May 12 16:54:23 JST 2018
 *
 * Copyright (c) 2018 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.model.dataobject;

import java.util.ArrayList;
import java.util.List;

public class FastDrawData {
	private List< List< DrawPoint > > array;

	public static final double MIN_RANGE = 0.01;
	public static final int MAX_LEVEL = 10;

	/**
	 * constructor
	 * @param data xyData
	 */
	public FastDrawData( XYData data ) {
		array = createArray( data );
	}

	/**
	 * creates array
	 * @param points data points
	 * @return data array
	 */
	List< List< DrawPoint > > createArray( XYData points ) {
		List< List< DrawPoint > > list = new ArrayList< List< DrawPoint > >();

		List< DrawPoint > prevArray = this.createFirstArray( points );
		list.add( prevArray );

		double range = MIN_RANGE;
		for( int level = 1; level <= MAX_LEVEL; level++ ) {
			int prevIndex = -1;
			DrawPoint prevPoint = null;
			List< DrawPoint > array = new ArrayList< DrawPoint >();

			for( DrawPoint element : prevArray ) {
				double x = element.getX();
				int index = ( int )Math.round( x / range );

				if( index > prevIndex ) {
					DrawPoint point = new DrawPoint();
					point.setX( range * ( double )index );
					point.setMinY( element.getMinY() );
					point.setMaxY( element.getMaxY() );
					point.setLeftY( element.getLeftY() );
					point.setRightY( element.getRightY() );

					array.add( point );
					prevPoint = point;
					prevIndex = index;
				}
				else {
					if( element.getMinY() < prevPoint.getMinY() ) {
						prevPoint.setMinY( element.getMinY() );
					}
					if( element.getMaxY() > prevPoint.getMaxY() ) {
						prevPoint.setMaxY( element.getMaxY() );
					}
					prevPoint.setRightY( element.getRightY() );
				}
			}

			list.add( array );
			prevArray = array;
			range *= 2.0;
		}

		return list;
	}

	/**
	 * creates first array
	 * @param points points
	 * @return array
	 */
	List< DrawPoint > createFirstArray( XYData points ) {
		List< DrawPoint > array = new ArrayList< DrawPoint >();

		for( Point< Double > point : points ) {
			DrawPoint element = new DrawPoint( point.getX(), point.getY() );
			array.add( element );
		}
		return array;
	}

	/**
	 * gets the poibnts
	 * @param level level
	 * @return points
	 */
	public List< DrawPoint > getPoints( Integer level ) {
		if( level < 0 || level >= this.array.size() ) {
			return null;
		}

		return this.array.get( level );
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
		Double log = Math.log( rangePerPixel / MIN_RANGE / Math.log( 2.0 ) );
		Integer level = (int)Math.floor( log );
		level = Math.max( 0,  Math.min( MAX_LEVEL,  level ) );
		return level;
	}
}
