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
 * @since 2018-06-01 05:51:10+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.model.dataobject;

import java.util.Arrays;
import java.util.List;

/**
 * heatmap
 */
public class Heatmap {
	public static final int RT_SIZE = 256;
	public static final int MZ_SIZE = 256;
	public static final double RT_DULATION = 1.0;
	public static final double MZ_DULATION = 0.1;

	private double[][] data;
	private Range< Double > mzRange;
	private Range< Double > rtRange;
	private double maxIntensity;

	public double[][] getData() {
		return data;
	}

	public void setData(double[][] data) {
		this.data = data;
	}

	public Range<Double> getMzRange() {
		return mzRange;
	}

	public void setMzRange(Range<Double> mzRange) {
		this.mzRange = mzRange;
	}

	public Range<Double> getRtRange() {
		return rtRange;
	}

	public void setRtRange(Range<Double> rtRange) {
		this.rtRange = rtRange;
	}

	public double getMaxIntensity() {
		return maxIntensity;
	}

	public void setMaxIntensity(double maxIntensity) {
		this.maxIntensity = maxIntensity;
	}

	/**
	 * constructor
	 * @param spectra spectra
	 */
	public Heatmap( List< SpectrumObject > spectra ) {
		if( spectra == null || spectra.isEmpty() ) {
			return;
		}

		double startRt = spectra.get( 0 ).getRt();
		double endRt = spectra.get( spectra.size() - 1 ).getRt();
		endRt = Math.max( endRt,  startRt + 0.0001 );
		this.rtRange = new Range< Double >( startRt, endRt );


		double startMz = Double.POSITIVE_INFINITY;
		double endMz = Double.NEGATIVE_INFINITY;
		double maxIntensity = 0.001;
		for( SpectrumObject spectrum : spectra ) {
			XYData xyData = spectrum.getXYData();
			startMz = Math.min( startMz,  xyData.getMinX() );
			endMz = Math.max( endMz,  xyData.getMaxX() );
			maxIntensity = Math.max( maxIntensity,  xyData.getMaxY() );
		}
		endMz = Math.max( endMz,  startMz + 0.0001 );
		this.mzRange = new Range< Double >( startMz, endMz );
		this.maxIntensity = maxIntensity;

		this.data = this.createData( spectra, this.rtRange, this.mzRange, maxIntensity );
	}

	/**
	 * creates data
	 * @param spectra spectra
	 * @param rtRange rt range
	 * @param mzRange mz range
	 * @return data
	 */
	protected double[][] createData( List< SpectrumObject > spectra, Range< Double > rtRange, Range< Double > mzRange, double maxIntensity ) {
		double[][] data = new double[ MZ_SIZE ][ RT_SIZE ];
		for( double[] array : data ) {
			Arrays.fill( array,  0.0 );
		}

		double rtUnit = ( rtRange.getEnd() - rtRange.getStart() ) / (double)( RT_SIZE - 1 );
		double mzUnit = ( mzRange.getEnd() - mzRange.getStart() ) / (double)( MZ_SIZE - 1 );

		int prevRtIndex = -1;
		double prevRt = -100.0;
		for( SpectrumObject spectrum : spectra ) {
			double rt = spectrum.getRt();
			int rtIndex = (int)Math.round( ( rt - rtRange.getStart() ) / rtUnit );

			XYData xyData = spectrum.getXYData();
			double[] mzArray = new double[ MZ_SIZE ];
			Arrays.fill( mzArray,  0.0 );

			int prevMzIndex = -1;
			double prevMz = -100.0;
			for( Point< Double > point : xyData.getPoints() ) {
				double mz = point.getX();
				double intensity = point.getY() / maxIntensity;

				int mzIndex = (int)Math.round( ( mz - mzRange.getStart()  ) / mzUnit );
				mzArray[ mzIndex ] = Math.max( intensity,  mzArray[ mzIndex ] );

				int diff = mzIndex - prevMzIndex;
				if( diff > 1 && ( mz - prevMz ) <= MZ_DULATION ) {
					for( int i = 1; i < diff; i++ ) {
						double prevIntensity = mzArray[ prevMzIndex ];
						mzArray[ prevMzIndex + i ] = prevIntensity + ( intensity - prevIntensity ) * (double)i / (double)diff;
					}
				}

				prevMz = mz;
				prevMzIndex = mzIndex;
			}

			for( int i = 0; i < MZ_SIZE; i++ ) {
				data[ i ][ rtIndex ] = Math.max( data[ i ][ rtIndex ], mzArray[ i ] );
			}

			int diff = rtIndex - prevRtIndex;
			if( diff > 1 && ( rt - prevRt ) <= RT_DULATION ) {
				for( int i = 1; i < diff; i++ ) {
					for( int j = 0; j < MZ_SIZE; j++ ) {
						double prevIntensity = data[ j ][ prevRtIndex ];
						double intensity = mzArray[ j ];
						data[ j ][ prevRtIndex + i ] = prevIntensity + ( intensity - prevIntensity ) * (double)i / (double)diff;
					}
				}
			}

			prevRtIndex = rtIndex;
			prevRt = rt;
		}

		return data;
	}

}
