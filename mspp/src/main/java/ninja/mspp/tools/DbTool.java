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
 * @since 2019
 *
 * Copyright (c) Mon Sep 23 19:52:11 JST 2019 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

public class DbTool {
	/**
	 * creates blob from double array
	 * @param array double array
	 * @return blob
	 * @throws IOException
	 * @throws SerialException
	 * @throws SQLException
	 */
	public static Blob createBlobFromDoubleArray( double[] array ) throws IOException, SerialException, SQLException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream( byteStream );
		dataStream.writeInt( array.length );
		for( double value : array ) {
			dataStream.writeDouble( value );
		}
		dataStream.close();

		Blob blob = new SerialBlob( byteStream.toByteArray() );

		return blob;
	}

	/**
	 * creates bytes from double array
	 * @param array
	 * @return
	 * @throws IOException
	 * @throws SerialException
	 * @throws SQLException
	 */
	public static byte[] createBytesFromDoubleArray( double[] array ) throws IOException, SerialException, SQLException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream( byteStream );
		dataStream.writeInt( array.length );
		for( double value : array ) {
			dataStream.writeDouble( value );
		}
		dataStream.close();

		byte[] bytes = byteStream.toByteArray();
		return bytes;
	}


	/**
	 * creates double array from blob
	 * @param blob blob
	 * @return double array
	 * @throws SQLException
	 * @throws IOException
	 */
	public static double[] createDoubleArrayFromBlob( Blob blob ) throws SQLException, IOException {
		DataInputStream dataStream = new DataInputStream( blob.getBinaryStream() );
		int length = dataStream.readInt();
		double[] values = new double[ length ];
		for( int i = 0; i < length; i++ ) {
			values[ i ] = dataStream.readDouble();
		}
		return values;
	}

	/**
	 * creates double array from byte array
	 * @param bytes byte array
	 * @return double array
	 * @throws IOException
	 */
	public static double[] createDoubleArrayFromBytes( byte[] bytes ) throws IOException {
		DataInputStream dataStream = new DataInputStream( new ByteArrayInputStream( bytes ) );
		int length = dataStream.readInt();
		double[] values = new double[ length ];
		for( int i = 0; i < length; i++ ) {
			values[ i ] = dataStream.readDouble();
		}
		return values;
	}

}
