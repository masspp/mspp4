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
 * @author: Mass++ User Group
 * @since: 2018
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.tools;

import java.io.File;

import ninja.mspp.MsppManager;

/**
 * file tool
 */
public class FileTool {
	/**
	 * gets the extension
	 * @param path file path
	 * @return extension
	 */
	public static String getExtension( String path ) {
		if( path == null ) {
			return null;
		}

		int index = path.lastIndexOf( "." );
		if( index >= 0 ) {
			return path.substring( index + 1 );
		}
		return path;
	}

	/**
	 * creates tmp file
	 * @param folder folder
	 * @param prefix prefix
	 * @param suffix suffix
	 * @return tmp file
	 */
	public static File createTmpFile( String folder, String prefix, String suffix ) {
		MsppManager msppManager = MsppManager.getInstance();
		File dir = new File( msppManager.getTmpDir(), folder );
		if( !dir.exists() ) {
			dir.mkdirs();
		}

		File file = null;
		try{
			file = File.createTempFile( prefix,  suffix, dir );
		}
		catch( Exception e ) {
			System.out.println( msppManager.getTmpDir() );
			System.out.println( dir );
			e.printStackTrace();
		}

		return file;
	}
}
