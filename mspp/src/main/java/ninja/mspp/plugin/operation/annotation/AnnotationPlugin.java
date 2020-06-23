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
package ninja.mspp.plugin.operation.annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ninja.mspp.annotation.method.DrawSpectrumForeground;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.dataobject.DrawPoint;
import ninja.mspp.model.dataobject.Range;
import ninja.mspp.model.dataobject.Rect;

@Plugin( "annotation")
public class AnnotationPlugin {
	ArrayList< Annotation > annotations;

	/**
	 * constructor
	 */
	public AnnotationPlugin() {
		this.annotations  = new ArrayList< Annotation >();
	}

	@DrawSpectrumForeground
	public void drawAnnotation(
			GraphicsContext g,
			ArrayList< DrawPoint > points,
			Range< Double > xRange,
			Range< Double > yRange,
			Integer width,
			Integer height,
			Rect< Integer > margin,
			RealMatrix drawMatrix
	) {
		g.setGlobalAlpha( 0.3 );

		for( int i = 0; i < this.annotations.size(); i++ ) {
			g.beginPath();
			g.setStroke( Color.CYAN );
			Annotation annotation = this.annotations.get( i );
			Integer posX = (int)Math.round( annotation.getPosition() * drawMatrix.getEntry( 0,  0 ) + drawMatrix.getEntry( 0,  2 ) );
			g.moveTo( (double)posX,  margin.getTop() );
			g.lineTo( (double)posX,  height - margin.getBottom() );
			g.closePath();
			g.stroke();

			g.beginPath();
			g.setStroke( Color.DARKBLUE );
			Integer posY = i % 3 + 1;
			posY = margin.getTop() + (int)Math.round( g.getFont().getSize() * 3.0 * posY / 2.0 );
			g.strokeText( annotation.getAnnotation(),  (double)( posX + 3 ),  (double)posY );
			g.closePath();
			g.stroke();
		}

		g.setGlobalAlpha( 1.0 );
	}

	/**
	 * reads annotations
	 * @param file file
	 * @return annotations
	 */
	ArrayList< Annotation > readAnnotations( File file ) throws Exception {
		ArrayList< Annotation > annotations = new ArrayList< Annotation >();

		BufferedReader reader = new BufferedReader( new FileReader( file ) );
		String line = null;
		while( ( line = reader.readLine() ) != null ) {
			StringTokenizer tokenizer = new StringTokenizer( line, "," );
			try {
				Double position = Double.parseDouble( tokenizer.nextToken() );
				String text = tokenizer.nextToken();

				Annotation annotation = new Annotation();
				annotation.setPosition( position );
				annotation.setAnnotation( text );
				annotations.add( annotation );
			}
			catch( Exception e ) {
			}
		}
		reader.close();

		annotations.sort(
			( anno0, anno1 ) -> {
				if( anno0.getPosition() < anno1.getPosition() ) {
					return -1;
				}
				if( anno0.getPosition() > anno1.getPosition() ) {
					return 1;
				}
				return 0;
			}
		);

		return annotations;
	}
}
