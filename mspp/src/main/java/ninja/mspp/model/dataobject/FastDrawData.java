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

import java.util.ArrayList;
import java.util.List;

import ninja.mspp.model.entity.DrawElementList;

public class FastDrawData {
	private List< List< DrawPoint > > array;

	public static final double MIN_RANGE = 0.01;
	public static final int MAX_LEVEL = 10;

	/**
	 * constructor
	 * @param xyData xyData
	 */
	public FastDrawData( List< DrawElementList > lists ) {
		lists.sort(
			( list1, list2 ) -> {
				return ( list1.getLevel() - list2.getLevel() );
			}
		);

		this.array = new ArrayList< List< DrawPoint > >();
		try {
			for( DrawElementList list : lists ) {
				this.array.add( list.getDrawPoints() );
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
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
