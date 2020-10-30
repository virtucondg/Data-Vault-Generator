package com.biogen.generator.ertools.erstudio.raw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class reads the internal CSV files of an ER/Studio diagram file and sets them up for import.
 * @author drothste
 *
 */
public class RawDataObject {
	private String objectType;
	private ArrayList<HashMap<String, String>> properties = new ArrayList<HashMap<String, String>>();
	private ArrayList<String> header = new ArrayList<String>();
	
	/**
	 * Get the count of columns for the object
	 * @return the count of columns
	 */
	static public int getColumnCount() {
		return 1;
	}

	/**
	 * Constructor
	 * @param objectType the type of data that will be in this RawDataObject
	 */
	public RawDataObject(String objectType) {
		this.objectType = objectType;
	}
	
	/** 
	 * Get the name of this RawDataObject
	 * @return the name
	 */
	public String getName() {
		return this.objectType;
	}
	
	/**
	 * Set the header column list
	 * @param list the header column list
	 */
	@SuppressWarnings("unchecked")
	public void setHeader(ArrayList<String> list) {
		this.header = (ArrayList<String>) list.clone();
	}

	/**
	 * Get the header column list
	 * @return the header column list
	 */
	public ArrayList<String> getHeader() {
		return this.header;
	}
	
	/**
	 * Set additional properties to this object
	 * @param list the list of additional properties
	 */
	public void setProperties(ArrayList<String> list) {
		HashMap<String, String> result = new HashMap<String, String>();

		for (int i = header.size(); i-- > 0; )
			result.put(header.get(i), list.get(i));			
		
		properties.add(result);
	}
	
	/**
	 * Set additional properties to this object
	 * @param map the HashMap of additional properties
	 */
	public void setPropertyMap(HashMap<String, String> map) {
		properties.add(map);
	}
	
	/** 
	 * Get the number of properties
	 * @return the count
	 */
	public int getPropertiesCount() {
		return properties.size();
	}
	
	public void clear() {
		properties.clear();
	}
	
	private Iterator<HashMap<String, String>> it = null;
	
	/**
	 * Initialize the iterator for looping the CSV data
	 */
	public void initialize() {
		it = properties.iterator();
	}
	
	/**
	 * Are there more rows in this CSV?
	 * @return <code>TRUE</code> if there are more rows; <code>FALSE</code> if at the end of the data
	 */
	public boolean hasNext() {
		return it.hasNext();
	}
	
	/**
	 * Convert a CSV record into a HashMap with property names
	 * @return the map of properties
	 */
	public HashMap<String, String> getRecord() {		
		HashMap<String, String> data = it.next();
		if(null==data)
			it = null;
		
		return data;
	}
}
