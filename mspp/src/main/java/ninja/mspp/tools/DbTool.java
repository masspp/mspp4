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
