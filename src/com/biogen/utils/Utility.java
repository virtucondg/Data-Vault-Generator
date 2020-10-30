package com.biogen.utils;

import java.util.HashMap;

import com.biogen.generator.io.LanguageXMLParser;
import com.biogen.generator.io.language.LanguageElement;
import com.biogen.generator.io.language.Token;

/**
 * Utility functions for the Generator
 * @author drothste
 *
 */
public class Utility {
	static private HashMap<String, LanguageElement> tokens = new HashMap<String, LanguageElement>();
	static private HashMap<String, String> datatypeClasses = new HashMap<String, String>();
	/** categories of data types initializer */
	static {
		datatypeClasses.put("DATE", "DATE");
		datatypeClasses.put("TIMESTAMP/DATE", "TIME");
		datatypeClasses.put("TIMESTAMP", "TIME");
		datatypeClasses.put("DATETIME", "TIME");
		datatypeClasses.put("DATETIMN", "TIME");
		datatypeClasses.put("SMALLDATETIME", "TIME");
		datatypeClasses.put("TIME/DATETIME", "TIME");
		datatypeClasses.put("TEXT", "LOB");
		datatypeClasses.put("CLOB", "LOB");
		datatypeClasses.put("NTEXT/LONG NVARCHAR", "LOB");
		datatypeClasses.put("LONG VARCHAR", "LOB");
		datatypeClasses.put("PICTURE", "LOB");
		datatypeClasses.put("ID", "NUMERIC");
		datatypeClasses.put("NUMERIC", "NUMERIC");
		datatypeClasses.put("NUMBER", "NUMERIC");
		datatypeClasses.put("DECIMAL", "NUMERIC");
		datatypeClasses.put("MONEY", "NUMERIC");
		datatypeClasses.put("SMALLMONEY", "NUMERIC");
		datatypeClasses.put("MONEYN", "NUMERIC");
		datatypeClasses.put("DECIMALN", "NUMERIC");
		datatypeClasses.put("FLOAT", "NUMERIC");
		datatypeClasses.put("SINGLE", "NUMERIC");
		datatypeClasses.put("REAL/SMALLFLOAT", "NUMERIC");
		datatypeClasses.put("FLOATN", "NUMERIC");
		datatypeClasses.put("NUMBER", "NUMERIC");
		datatypeClasses.put("DOUBLE", "NUMERIC");
		datatypeClasses.put("DOUBLE PRECISION", "NUMERIC");
		datatypeClasses.put("INTEGER", "NUMERIC");
		datatypeClasses.put("INT", "NUMERIC");
		datatypeClasses.put("INTN", "NUMERIC");
		datatypeClasses.put("SERIAL/INTEGER", "NUMERIC");
		datatypeClasses.put("COUNTER", "NUMERIC");
		datatypeClasses.put("UNIQUEID", "NUMERIC");
		datatypeClasses.put("SMALLINT", "NUMERIC");
		datatypeClasses.put("TINYINT", "NUMERIC");
	}
	
	/**
	 * Format a line of text for special characters
	 * @param data the data to process
	 * @param multiline if there can be multiple lines in the output
	 * @return the processed data
	 */
	static public String textToFormattedText(String data, boolean multiline) {
		String result = data.replace("\\n", (multiline ? System.getProperty("line.separator") : " "));
		result = result.replace("\\t", "\t");
		
		return result;
	}
	
	/**
	 * Switch two objects
	 * @param one the first object
	 * @param two the second object
	 */
	static public void swap(Object one, Object two) {
		Object temp = one;
		one = two;
		two = temp;
	}

	/** 
	 * Change static schemas to referenceable ones
	 * @param name the actual schema name
	 * @return the referenceable one
	 */
	static public String getSchemaReference(String name) {
		return "$$SCHEMA_" + name.replace("_DEV3",  "");
	}
	
	/**
	 * Load a language file
	 * @param language the language to load
	 */
	static public void loadLanguage(String language) {
		LanguageXMLParser lp = new LanguageXMLParser();
		tokens = lp.parse("C:\\Users\\drothste\\eclipse-workspace\\Data Vault Generator\\src\\com\\biogen\\generator\\io\\language\\Language_" + language + ".xml");

	}
	
	/**
	 * Get the language variant of a token
	 * @param name the token
	 * @return the language variant of the token
	 */
	static public String getLanguageOf(String name) {
		Token token = (Token) tokens.get(name);
		
		return (null==token ? null : token.getDataValue());
	}

	/**
	 * Convert a batch name to a source
	 * @param batchName the batch name to get the source for
	 * @return the source
	 */
	static public String getSourceFromBatch(String batchName) {
		if (batchName.contains("INTL")) {
			String sourceName = batchName.substring(0, batchName.indexOf("_BATCH") - 1);
			sourceName = sourceName.substring(batchName.indexOf("LOAD_") + 5);
			switch (sourceName) {
			case "CRM" : 
				return "SFDC_EU";
			case "OM" :
				return "ORDER_MANAGEMENT";
			case "ERP" :
				return "ORDER_MANAGEMENT";
			case "REF" :
				return "GLOBAL";
			case "MD" :
				return "GLOBAL";
			case "FD" :
				return "FIELD_DIRECTORY";
			default :
				return sourceName;
			}
		}
		
		String sourceName = batchName.substring(0, batchName.indexOf("_LOAD") - 1);
		if (sourceName.equals("TOUCH_PS"))
			sourceName = "TOUCH";

		return sourceName;
	}
	
	/**
	 * Get the data type class of a data type
	 * @param datatype the data type
	 * @return the data type class
	 */
	static public String getDataTypeClass(String datatype) {
		String result = datatypeClasses.get(datatype);
		
		return (null==result ? "CHAR" : result);
	}

	/**
	 * Get the type of template 
	 * @param name the object name
	 * @param templateType the default template type; return if not <code>NULL</code>
	 * @return the template type
	 */
	static public String getTemplateType(String name, String templateType) {
		if (null!=templateType) {
			return templateType;
		} else if (name.substring(0,  3).equalsIgnoreCase("BI_")) {
			return "BRG_TEMPLATE";
		} else {
			switch(name.substring(name.length() - 4).toUpperCase()) {
			case "_HUB" :
				return "HUB_TEMPLATE";
			case "_LNK" :
				return "LINK_TEMPLATE";
			case "_SAT" :
				return "SAT_TEMPLATE";
			case "_BRG" :
				return "BRG_TEMPLATE";
			default :
				return "";
			}
		}
	}

	/**
	 * Split a string (usually property name) into tokens separated by ".".  This allows easy categorization
	 * @param name the name to split
	 * @return the split property names
	 */
	static public String[] splitProperty(String name) {
		String[] tokens = name.split("\\.");
		
		return tokens;
	}
	
}
