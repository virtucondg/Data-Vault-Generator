package com.biogen.generator.io;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * This class parses an XML file.  It must be subclassed with implementations for the abstract methods.
 * @author drothste
 *
 */
abstract public class XMLParser extends DefaultHandler {
	static private SAXParser sp = null;
	private XMLReader xmlReader;
	
	/** 
	 * Constructor
	 */
	public XMLParser() {	
		try {
			sp = SAXParserFactory.newInstance().newSAXParser();
			xmlReader = sp.getXMLReader();
			xmlReader.setContentHandler(this);
		} catch (Exception ignored) {}
	}
	
	/**
	 * Parse an XML file
	 * @param filename the filename to parse
	 */
	public void parseXML(String filename) {
		try {
			xmlReader.parse(filename);
		} catch(Exception ex) {ex.printStackTrace();}
	}
	
	/**
	 * Code to execute when a document is opened
	 */
	abstract public void startDocument() throws SAXException;

	/**
	 * Code to execute when an element is entered
	 * @param namespaceURI the path to the element
	 * @param localName the name of the element
	 * @param qName ??
	 * @param atts the list of attributes belonging to the element
	 */
	abstract public void startElement(String namespaceURI,
	                         String localName,
	                         String qName, 
	                         Attributes atts) throws SAXException;
	
	/**
	 * Code to execute when an element is exited
	 * @param namespaceURI the path to the element
	 * @param localName the name of the element
	 * @param qName ??
	 */
	abstract public void endElement(String namespaceURI,
				            String localName,
				            String qName) throws SAXException;
	
	/**
	 * Process a subset of a character array
	 * @param ch the array of characters
	 * @param start the starting position of the subset
	 * @param length the length of the subset
	 */
	abstract public void characters(char ch[], int start, int length) throws SAXException;
	
	/**
	 * Code to execute when a document is closed
	 */
	abstract public void endDocument() throws SAXException;

}
