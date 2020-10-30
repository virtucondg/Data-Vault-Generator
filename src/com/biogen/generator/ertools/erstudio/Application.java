package com.biogen.generator.ertools.erstudio;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.biogen.utils.Debug;
import com.biogen.utils.Utility;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;
/**
 * This class represents an ER/Studio Application object
 * @author drothste
 *
 */
public class Application extends ModelingObject {
	/** reference to a singular Application object */
	static Application app = new Application(null);
	
	private ArrayList<String> filters = new ArrayList<String>();
	private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	/** 
	 * Constructor based on an underlying ActiveX component
	 * @param object
	 */
	private Application(ActiveXComponent object) {
		super(object, false);
		setSelfType("APP");
		setCurrentObject();
	}

	/**
	 * Get the application instance
	 * @return the static Application instance
	 */
	static public Application getInstance() {
		if (null==app) app = new Application(null);
		
		return app;
	}
	
	/** 
	 * Load the properties for the application
	 * @param propertyFile the properties file name
	 */
	public void loadProperties(String propertyFile) {
		try {
			File file = new File(propertyFile);
			FileInputStream fileInput = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(fileInput);
			fileInput.close();
			
			for (Object key : prop.keySet())
				putProp((String) key, prop.getProperty((String) key));
				
			for (String filter : prop.getProperty("filter").split(";"))
				addFilter(filter.trim());
			
		} catch(Exception e) {
			Debug.printError("Error reading properties file: " + propertyFile);
			Debug.printStackTrace(e);
		}
	}
		
	/** 
	 * Add a new filter to the filters list
	 * @param value the name of the filter to add
	 */
	public void addFilter(String value) {
		filters.add(value);
	}
	
	/**
	 * Is this value in the list of filters?
	 * @param value the value to check
	 * @return <code>TRUE</code> if it is in the list, <code>FALSE</code> otherwise
	 */
	public boolean hasFilter(String value) {
		return filters.contains(value) || (null==value);
	}

	/**
	 * Get the value for an ActiveX component property
	 * @param name the name of the property
	 * @return the Variant property value
	 */
	public Variant getProperty(String name) {
		return getVariant(getPropertyAsString(name));
	}
	
	/**
	 * Get a property value as a string.
	 * @param name the name of the property
	 * @return the value of the property, or defaultValue if value was <code>NULL</code>
	 */
	public String getPropertyAsString(String name) {
		String value = null;
		String[] tokens = Utility.splitProperty(name);

		switch(tokens[tokens.length - 1].toUpperCase()) {
		case "NULL": //  derived property
			value = null;
			break;
		case "DATE": //  derived property
			value = df.format(new Date());
			break;
		case "FILENAMEBASE" :
			value = getProperty("FileName").getString();
			value = value.substring(0, value.lastIndexOf("."));
			break;
		default:
			value = getProp(name.toUpperCase()); 
		}
				
		return value;
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public ModelingObject create(ActiveXComponent object) {
		return null;
	}

	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public ModelingObject find(ActiveXComponent object) {
		return null;
	}

}
