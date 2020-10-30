package com.biogen.generator.ertools.erstudio.helpers;

import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.com.Variant;
/**
 * This class is used to resolve indirections when reading the raw CSV data from an ER/Studio file.
 * @author drothste
 *
 */
public class HelperDictionary {

	static private HashMap<String, String> strings = new HashMap<String, String>();
	static private HashMap<String, String> stringUsageTypes = new HashMap<String, String>();
	static private HashMap<String, String[]> guids = new HashMap<String, String[]>();
	static private HashMap<String, String> objectTypes = new HashMap<String, String>();
	static private HashMap<String, String> identities = new HashMap<String, String>();
	
	/**
	 * Process the raw data and initialize all of the helper objects
	 * @param objects the raw CSV data sheets
	 */
	static public void addObjects(HashMap<String, RawDataObject> objects) {
		HelperDictionary.addStringUsageTypes(objects.get("StringUsageType"));
		HelperDictionary.addIdentities(objects.get("Identity_Value"));
		HelperDictionary.addStrings(objects.get("SmallString"));
		HelperDictionary.addStrings(objects.get("LargeString"));
		HelperDictionary.addGUIDs(objects.get("UniqueObject"));
		HelperDictionary.addObjectTypes(objects.get("ObjectDefinition"));
	}
	
	/**
	 * Add strings to the helper class
	 * @param obj the raw CSV data sheets
	 */
	static public void addStrings(RawDataObject obj) {
		obj.initialize();
		while (obj.hasNext()) {
			HashMap<String, String> data = obj.getRecord();
			strings.put(data.get("String_Id"), data.get("Data"));
		}
	}

	/**
	 * Add identities to the helper class
	 * @param obj the raw CSV data sheets
	 */
	static public void addIdentities(RawDataObject obj) {
		obj.initialize();
		while (obj.hasNext()) {
			HashMap<String, String> data = obj.getRecord();
			identities.put(data.get("Table_Name"), data.get("Column_Name"));
		}
	}

	/**
	 * Add object types to the helper class
	 * @param obj the raw CSV data sheets
	 */
	static public void addObjectTypes(RawDataObject obj) {
		obj.initialize();
		while (obj.hasNext()) {
			HashMap<String, String> data = obj.getRecord();
			strings.put(data.get("ObjectName"), data.get("ObjectType_ID"));
		}
	}

	/**
	 * Add string usage types to the helper class
	 * @param obj the raw CSV data sheets
	 */
	static public void addStringUsageTypes(RawDataObject obj) {
		obj.initialize();
		while (obj.hasNext()) {
			HashMap<String, String> data = obj.getRecord();
			stringUsageTypes.put(data.get("String_Id"), data.get("Data"));
		}
	}

	/**
	 * Get a string value
	 * @param id the id of the string
	 * @return the string value
	 */
	static public String getString(HashMap<String, String> data, String key) {
		return getString(data, key, key + "_Id");
	}
	
	/**
	 * Get a string value
	 * @param id the id of the string
	 * @param lookupId the id to look up in the strings table
	 * @return the string value
	 */
	static public String getString(HashMap<String, String> data, String key, String lookupKey) {
		String result = data.get(key);
		if (null==result)
			result = strings.get(key + "_Id");
		
		return result;
	}

	/**
	 * Get an object type
	 * @param objectName the name of the object
	 * @return the object type
	 */
	static public String getObjectType(String objectName) {
		return objectTypes.get(objectName);
	}

	/**
	 * Get an identity column
	 * @param objectName the name of the object
	 * @return the identity column
	 */
	static public String getIdentityColumn(String objectName) {
		return identities.get(objectName);
	}

	/**
	 * Add GUIDs to the helper class
	 * @param obj the raw CSV data sheets
	 */
	static public void addGUIDs(RawDataObject obj) {
		obj.initialize();
		while (obj.hasNext()) {
			HashMap<String, String> data = obj.getRecord();
			guids.put(data.get("ObjectType_Id") + "|" + data.get("InterModel_Id") + "|" + data.get("IntraModeLObject_Id") + "|" + data.get("Model_Id"), 
					new String[]{data.get("GUID"), data.get("InterModel_Id"), data.get("IntraModelObject_Id")});
		}
	}

	/**
	 * Get a GUID
	 * @param objectTypeID the object type id
	 * @param id the id
	 * @param objectID the object id
	 * @param modelID the model id
	 * @return the GUID
	 */
	static public String getGUID(String objectTypeID, String id, String objectID, String modelID) {
		return guids.get(objectTypeID + "|" + id + "|" + objectID + "|" + modelID)[0];
	}

	static public boolean isTrue(Variant value) {
		switch (value.changeType(Variant.VariantString).getString().toUpperCase()) {
		case "Y" : 
		case "TRUE" : return true;
		default: return false;
		}
	}
}
