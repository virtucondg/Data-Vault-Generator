package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Dictionary object
 * @author drothste
 *
 */
public class Dictionary extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Dictionary nullObj = new Dictionary(null);
	static private CollectionLoader<Dictionary> masterCO = new CollectionLoader<Dictionary>(Dictionary.nullObj, null, Dictionary.nullObj);
		
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param dictionary the ER/Studio object underlying this instance
	 */
	public Dictionary (ActiveXComponent dictionary) {
		super(dictionary, false);
		setSelfType("DICTIONARY");
		
		if (null!=dictionary) {
			addChild("Domain", new CollectionLoader<Domain>(this, "Domains", Domain.nullObj, true));
			
			masterCO.add(this);
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param dictionary the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Dictionary create(ActiveXComponent dictionary) {
		Dictionary dict = find(dictionary);
		
		return dict;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<Dictionary> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Data_Dictionary");
		CollectionLoader<? extends ModelingObject> mo;
		
		ArrayList<Dictionary> dictionaries = new ArrayList<Dictionary>();
		Dictionary dictionary = null;
		String id; 
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			PseudoActiveXComponent pax = new PseudoActiveXComponent(Dictionary.nullObj, data);
			dictionary = new Dictionary(null);
			dictionary.setObject(pax);
			dictionaries.add(dictionary);
			dictionary.setParent(parent);
			
			id = data.get(HelperDictionary.getIdentityColumn("Diagram"));				
			if (null==id) id = getNextIDSequence();

			dictionary.setName(HelperDictionary.getString(data, "Name"));
			dictionary.setID(id);
			dictionary.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Data Dictionary"), id, "0", "0"));
			dictionary.putProp("Name", HelperDictionary.getString(data, "Name"));
			dictionary.putProp("Description", HelperDictionary.getString(data, "Description"));
		
			mo = dictionary.getChildCollection("DOMAIN");
			mo.addAll(Domain.create(dictionary, objects, id));
	}
		
		return dictionaries;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param dictionary the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Dictionary find(ActiveXComponent dictionary) {
		Dictionary dict = null;
		
		if (null!=dictionary)
			dict = masterCO.get(dictionary.getProperty("Name"));
		
		if (null==dict)
			dict = new Dictionary(dictionary);
		
		return dict;
	}

	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param name one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public Dictionary find(Variant... name) {
		Dictionary result = masterCO.get(name);
		
		if (null==result)
			result = (Dictionary) CollectionLoader.findGet(Dictionary.nullObj.getSelfType(), name);
		
		return result;
	}
	
}
