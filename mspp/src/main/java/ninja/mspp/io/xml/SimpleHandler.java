package ninja.mspp.io.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * simple XML handler
 */
public abstract class SimpleHandler extends DefaultHandler {
	/** text */
	private String text;

	/**
	 * on start document
	 */
	abstract protected void onStartDocument();

	/**
	 * on start element
	 * @param name element name
	 * @param attributes attributes
	 */
	abstract protected void onStartElement( String name, Attributes attributes );

	/**
	 * on end element
	 * @param name element name
	 * @param text text
	 */
	abstract protected void onEndElement( String name, String text );

	/**
	 * on end element
	 */
	abstract protected void onEndDocument();

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		this.onStartDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		this.onEndDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		this.text = null;
		this.onStartElement( qName,  attributes );
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		this.onEndElement( qName,  this.text );
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);

		String string = new String( ch, start, length );
		string = string.trim();

		if( this.text == null ) {
			this.text = string;
		}
		else {
			this.text = this.text + string;
		}
	}
}
