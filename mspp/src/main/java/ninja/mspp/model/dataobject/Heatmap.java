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
 * @since 2018
 *
 * Copyright (c) 2018 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.model.dataobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.service.RawDataService;

/**
 * heatmap
 */
public class Heatmap {
	public static final int RT_SIZE = 512;
	public static final int MZ_SIZE = 512;
	public static final double RT_DULATION = 1.0;
	public static final double MZ_DULATION = 0.1;

	private double[][] data;
	private Range< Double > mzRange;
	private Range< Double > rtRange;
	private double maxIntensity;

	private List< Spectrum > spectra;
	private RawDataService service;

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
	public Heatmap( List< Spectrum > spectra, RawDataService service ) {
		this.spectra = spectra;
		this.service = service;

		if( spectra == null || spectra.isEmpty() ) {
			return;
		}

		double startRt = spectra.get( 0 ).getStartRt();
		double endRt = spectra.get( spectra.size() - 1 ).getStartRt();
		endRt = Math.max( endRt,  startRt + 0.0001 );
		this.rtRange = new Range< Double >( startRt, endRt );

		double startMz = Double.POSITIVE_INFINITY;
		double endMz = Double.NEGATIVE_INFINITY;
		for( Spectrum spectrum : spectra ) {
			if( spectrum.getMsStage() == 1 ) {
				startMz = Math.min( startMz, spectrum.getLowerMz() );
				endMz = Math.max( endMz, spectrum.getUpperMz() );
			}
		}
		endMz = Math.max( endMz,  startMz + 0.0001 );
		this.mzRange = new Range< Double >( startMz, endMz );

		this.data = this.createData( spectra, this.rtRange, this.mzRange, service );
	}

	/**
	 * changes range
	 * @param startRt start RT
	 * @param endRt end RT
	 * @param startMz start m/z
	 * @param endMz end m/z
	 */
	public void changeRange( double startRt, double endRt, double startMz, double endMz ) {
		this.rtRange = new Range< Double >( startRt, endRt );
		this.mzRange = new Range< Double >( startMz, endMz );
		this.data = this.createData( this.spectra, this.rtRange, this.mzRange, this.service );
	}

	/**
	 * creates data
	 * @param spectra spectra
	 * @param rtRange rt range
	 * @param mzRange mz range
	 * @return data
	 */
	protected double[][] createData(
			List< Spectrum > spectra,
			Range< Double > rtRange,
			Range< Double > mzRange,
			RawDataService service
	) {
		System.out.println( "Creating Heatmap....." );
		double[][] data = new double[ RT_SIZE ][ MZ_SIZE ];
		for( double[] array : data ) {
			Arrays.fill( array,  0.0 );
		}

		double rtUnit = ( rtRange.getEnd() - rtRange.getStart() ) / (double)( RT_SIZE - 1 );
		double mzUnit = ( mzRange.getEnd() - mzRange.getStart() ) / (double)( MZ_SIZE - 1 );
		int rtDulationRange = ( int )Math.max( 0, Math.round( RT_DULATION / rtUnit ) );
		int mzDulationRange = ( int )Math.max( 0, Math.round( MZ_DULATION / mzUnit ) );

		List< Spectrum > msList = new ArrayList< Spectrum >();
		for( Spectrum spectrum : spectra ) {
			if( spectrum.getMsStage() == 1 ) {
				msList.add( spectrum );
			}
		}

		List< Spectrum > spectrumList = new ArrayList< Spectrum >();
		Spectrum prevSpectrum = null;
		for( Spectrum spectrum : msList ) {
			double rt = spectrum.getStartRt();
			int rtIndex = (int)Math.round( ( rt - rtRange.getStart() ) / rtUnit );
			if( rtIndex >= 0 && rtIndex < RT_SIZE ) {
				if( prevSpectrum != null ) {
					spectrumList.add( prevSpectrum );
					prevSpectrum = null;
				}
				spectrumList.add( spectrum );
			}
			else {
				if( rtIndex >= RT_SIZE && prevSpectrum == null ) {
					spectrumList.add( spectrum );
				}
				prevSpectrum = spectrum;
			}
		}

		int prevRtIndex = - 1;
		double[] prevMzArray = new double[ MZ_SIZE ];
		Arrays.fill( prevMzArray, 0.0 );
		double maxIntensity = 1.0;
		for( Spectrum spectrum : spectrumList ) {
			System.out.println( "    Reading Spectrum [" + spectrum.getName() + "]....." );
			double rt = spectrum.getStartRt();
			int rtIndex = (int)Math.round( ( rt - rtRange.getStart() ) / rtUnit );

			XYData xyData = service.findDataPoints( spectrum.getPointListId() );
			double[] mzArray = new double[ MZ_SIZE ];
			Arrays.fill( mzArray,  0.0 );

			double prevValue = 0.0;
			int prevMzIndex = -1;
			for( Point< Double > point : xyData ) {
				double mz = point.getX();
				double intensity = point.getY();
				int mzIndex = ( int )Math.round( ( mz - mzRange.getStart() ) / mzUnit );
				if( mzIndex >= 0 && mzIndex < MZ_SIZE ) {
					mzArray[ mzIndex ] = Math.max( mzArray[ mzIndex ], intensity );
					if( rtIndex >= 0 && rtIndex < RT_SIZE ) {
						maxIntensity  = Math.max( maxIntensity, intensity );
					}
				}

				if( ( prevMzIndex >= 0 || mzIndex >= 0 ) && ( prevMzIndex < MZ_SIZE || mzIndex < MZ_SIZE ) ) {
					int diff = mzIndex - prevMzIndex;
					if( diff > 1 ) {
						if( diff <= mzDulationRange ) {
							for( int i = 1; i < diff; i++ ) {
								int index = prevMzIndex + i;
								if( index >= 0 && index < MZ_SIZE ) {
									mzArray[ index ] = prevValue + ( intensity - prevValue ) * ( double )i / ( double )diff;
								}
							}
						}
						else {
							for( int i = 1; i < mzDulationRange; i++ ) {
								int index = mzIndex - i;
								if( index >= 0 && index < MZ_SIZE ) {
									mzArray[ index ] = intensity * ( double )( mzDulationRange - i ) / ( double )mzDulationRange;
								}
								index = prevMzIndex + i;
								if( index >= 0 && index < MZ_SIZE ) {
									mzArray[ index ] = Math.max( mzArray[ index ], prevValue * ( double )( mzDulationRange - i ) / ( double )mzDulationRange );
								}
							}
						}
					}
				}
				prevMzIndex = mzIndex;
				prevValue = intensity;
			}
			if( ( rtIndex >= 0 || prevRtIndex >= 0 ) && ( rtIndex < RT_SIZE || prevRtIndex < RT_SIZE ) ) {
				int diff = rtIndex - prevRtIndex;
				if( diff > 1 ) {
					if( diff < rtDulationRange ) {
						for( int i = 1; i < diff; i++ ) {
							int index = prevRtIndex + i;
							if( index >= 0 && index < RT_SIZE ) {
								if( prevMzArray != null ) {
									for( int j = 0; j < mzArray.length; j++ ) {
										data[ index ][ j ] = prevMzArray[ j ] + ( mzArray[ j ] - prevMzArray[ j ] ) * ( double )i / ( double )diff;
									}
								}
							}
						}
					}
					else {
						for( int i = 1; i < rtDulationRange; i++ ) {
							int index = rtIndex - i;
							if( index >= 0 && index < RT_SIZE ) {
								for( int j = 0; j < mzArray.length; j++ ) {
									data[ index ][ j ] = mzArray[ j ] * ( double )( rtDulationRange - i ) / ( double )rtDulationRange;
								}
							}
							index = prevRtIndex + i;
							if( index >= 0 && index < RT_SIZE ) {
								if( prevMzArray != null ) {
									for( int j = 0; j < mzArray.length; j++ ) {
										data[ index ][ j ] = Math.max( data[ index ][ j ],  prevMzArray[ j ] * ( double )( rtDulationRange - i ) / ( double )rtDulationRange );
									}
								}
							}
						}
					}
				}
			}
			if( rtIndex >= 0 && rtIndex < RT_SIZE ) {
				for( int i = 0; i < mzArray.length; i++ ) {
					data[ rtIndex ][ i ] = Math.max( mzArray[ i ],  data[ rtIndex ][ i ] );
				}
			}
			prevMzArray = mzArray;
			prevRtIndex = rtIndex;
		}
		this.maxIntensity = maxIntensity;

		for( double[] mzArray : data ) {
			for( int i = 0; i < mzArray.length; i++ ) {
				mzArray[ i ] = Math.min( 1.0, mzArray[ i ] / maxIntensity );
			}
		}

		return data;
	}

}
