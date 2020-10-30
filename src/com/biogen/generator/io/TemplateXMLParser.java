package com.biogen.generator.io;

import org.xml.sax.*;

import com.biogen.generator.io.output.*;
import com.biogen.generator.template.*;
import com.biogen.utils.Debug;

/**
 * This class processes XML generator templates.
 * TODO: add descriptions of the valid elements/attributes
 * @author drothste
 * 
 */
public class TemplateXMLParser extends XMLParser {
	private Operation parentOperation = null;
	private Operation operation = null;
	private Operation rootOperation = null;
	
	/**
	 * Constructor
	 */
	public TemplateXMLParser() {	
		super();
	}
	
	/**
	 * Parse the template file
	 * @param filename the file to parse
	 * @return a {@link TemplateOperation} structure of the generator template
	 */
	public TemplateOperation parse(String filename) {
		Debug.print("Loading Template:" + filename + "...");

		parseXML(filename);

		Debug.println("Done");
		TemplateOperation op = (TemplateOperation) rootOperation.getNextOperation();
		
		return op;
	}
	
	@Override
	public void startDocument() throws SAXException {
		rootOperation = new TemplateOperation(false);
	    parentOperation = rootOperation;
	}

	@Override
	public void startElement(String namespaceURI,
	                         String localName,
	                         String qName, 
	                         Attributes atts)
	    throws SAXException {
		boolean isTemplate = false;
		
	    switch(qName.toUpperCase()) {
	    case "TEMPLATE" :
	    	operation = new TemplateOperation();
	    	isTemplate = true;
	    	break;
	    case "WORKBOOK" :
	    	operation = new WorkbookOperation();
	    	break;
	    case "FILE" :
	    	operation = new FileOperation();
	    	break;
	    case "SHEET" :
	    	operation = new SheetOperation();
	    	break;
	    case "GRID" :
	    	operation = new GridOperation();
	    	break;
	    case "DATA" :
	    	operation = new DataOperation();
	    	break;
	    case "LIST" :
	    	operation = new ListOperation();
	    	break;
	    case "PRINT" :
	    	operation = new PrintOperation();
	    	break;
	    case "TEXTFORMAT" :
	    	operation = new TextFormatOperation();
	    	break;
	    default: 
	    	operation = null;
	    	break;
	    }
	    
	    operation.setParent(parentOperation);
	    
	    for (int i = atts.getLength(); i-- > 0; ) {
	    	operation.putProperty(atts.getLocalName(i), atts.getValue(i));
	    }
	    
	    if (isTemplate) {
	    	switch (operation.getProperty("fileType")) {
	    	case "Excel" :
	    		((TemplateOperation)operation).setOutput(new ExcelOutput());
	    		
	    		break;
	    	case "CSV" :
	    		((TemplateOperation)operation).setOutput(new CSVOutput());
	    		
	    		break;
	    	default:
	    		((TemplateOperation)operation).setOutput(new ExcelOutput());
	    	}
	    }
	    
	    parentOperation = operation;
	}
	
	@Override
	public void endElement(String namespaceURI,
				            String localName,
				            String qName)
				throws SAXException {
		parentOperation = parentOperation.getParent();
	}
	
	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

}
