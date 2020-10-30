package com.biogen.generator.io.language;

import com.biogen.generator.ertools.erstudio.ModelingObject;
import com.biogen.utils.Utility;

public class Token extends LanguageElement{
	
	public Token() {
		super();
	}
	
	public String getTokenProperty(String key) {
		String value = getProperty(key);
		
		if (null!=value)
			if (!value.startsWith("!")) {
				if (value.contains("{") || value.contains("["))
					value = parseTokens(value);
				else value = parseTokens("{" + value + "}");
			} else {
				value = value.substring(1);
			}
			
		return value;
	}
	
	String parseTokens(String data) {
		StringBuilder result = new StringBuilder();
		String[] components = data.split("\\{|\\}");
		
		for (int i = 0; i < components.length; i++) {
			if (i%2==0)
				result.append(components[i]);
				else {
					String[] fw = components[i].split(":");
					String[] tok = Utility.splitProperty(fw[0]);
					ModelingObject mo = ModelingObject.getCurrentObject(tok[0]);
					
					if (null!=mo) {
						String value = mo.getPropertyAsString(fw[0]);
						if (1 < fw.length) {
							int len = Integer.parseInt(fw[1]);
							if (len < value.length())
								value = value.substring(0,  len);
						}
						if (null!=value)	
							result.append(value);
					} else {
						result.append(components[i]);
					}
				}
		}
		
		return result.toString();
	}	
	
	@Override
	public String getLanguageTypeElement() {
		return "token";
	}	
}
