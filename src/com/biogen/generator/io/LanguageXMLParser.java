package com.biogen.generator.io;

import java.util.HashMap;

import org.xml.sax.*;

import com.biogen.generator.io.language.LanguageElement;
import com.biogen.generator.io.language.Token;
import com.biogen.utils.Debug;

/**
 * This class loads an XML language file
 * TODO: add descriptions of valid elements/attributes
 * @author drothste
 *
 */
public class LanguageXMLParser extends XMLParser {
	private HashMap<String, LanguageElement> languageElements = new HashMap<String, LanguageElement>();
	private LanguageElement element = null;
	
	/**
	 * Constructor
	 */
	public LanguageXMLParser() {	
		super();
	}
	
	/**
	 * Parse the language XML file
	 * @param filename the name of the file to parse
	 * @return HashMap of the language elements
	 */
	public HashMap<String, LanguageElement> parse(String filename) {
		Debug.print("Loading Language file:" + filename + "...");

		parseXML(filename);

		Debug.println("Done");
		return languageElements;
	}
	
	@Override
	public void startDocument() throws SAXException {
		element = null;
	}

	@Override
	public void startElement(String namespaceURI,
	                         String localName,
	                         String qName, 
	                         Attributes atts)
	    throws SAXException {

	    switch(qName.toUpperCase()) {
	    case "TOKEN" :
	    	element = new Token();
	    	break;
	    default: 
	    	element = null;
	    	break;
	    }
	    
	    if (null!=element) {
		    for (int i = atts.getLength(); i-- > 0; ) {
		    	element.putProperty(atts.getLocalName(i), atts.getValue(i));
		    }
		    
		    languageElements.put(element.getProperty("name"), element);
	    }
	}
	
	@Override
	public void endElement(String namespaceURI,
				            String localName,
				            String qName)
				throws SAXException {
		element = null;
	}
	
	@Override
	public void endDocument() throws SAXException {
		element = null;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (null!=element) 
			element.putData(new String(ch, start, length).trim());
	}

}
