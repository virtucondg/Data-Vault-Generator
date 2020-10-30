package com.biogen.generator.io.language;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

abstract public class LanguageElement {
	private HashMap<String, String> parameters = new HashMap<String, String>();
	private StringBuilder dataValue = new StringBuilder();

	public LanguageElement() {
	}

	abstract public String getLanguageTypeElement();

	public void putProperty(String key, String value) {
		parameters.put(key, value);
	}
	
	public String getProperty(String key) {
		String value = parameters.get(key);

		return value;
	}

	public void putData(String data) {
		dataValue.append(data);
	}
	
	public String getDataValue() {
		return dataValue.toString();
	}
	
	public HashMap<String, String> getProperties() {
		return parameters;
	}
	
	public String dumpXML() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<").append(getLanguageTypeElement());

		Iterator<Map.Entry<String, String>> it = parameters.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> me = (Map.Entry<String, String>)it.next();
	        sb.append(" " + me.getKey() + "=\"" + me.getValue() + "\"");
	    }
	    
		if (null==dataValue) {
			sb.append("/>\n");
		} else {
			sb.append(">\n").append(dataValue).append("</").append(getLanguageTypeElement()).append("\n");
		}

		return sb.toString();
	}
	
	public String parse(String data) {
		String value = data.replace("$", "");
		
		return value;
	}
	
}
