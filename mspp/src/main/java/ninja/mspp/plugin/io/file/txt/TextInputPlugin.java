package ninja.mspp.plugin.io.file.txt;

import java.io.BufferedReader;
import java.io.FileReader;

import ninja.mspp.annotation.FileInput;
import ninja.mspp.annotation.Plugin;

@Plugin( name = "Text Input Plugin" )
public class TextInputPlugin {
	@FileInput( title = "Text File", ext = "txt" )
	public Object openFile( String path ) throws Exception {
		BufferedReader reader = new BufferedReader( new FileReader( path ) );
		String line;
		while( ( line = reader.readLine() ) != null ) {
			System.out.println( line );
		}
		reader.close();

		return "Sample Object";
	}
}
