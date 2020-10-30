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
 * This class implements the ER/Studio Data Movement Column Link object
 * @author drothste
 *
 */
public class DataMovementColumnLink extends ModelingObject {
	/** empty object used for testing and initialization */
	static public DataMovementColumnLink nullObj = new DataMovementColumnLink(null);
	private Attribute parentAttribute = null;
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param dataMovementColumnLink the ER/Studio object underlying this instance
	 */
	public DataMovementColumnLink (ActiveXComponent dataMovementColumnLink) {
		super(dataMovementColumnLink, false);
		setSelfType("DATAMOVEMENTCOLUMNLINK");
		
		if (null!=dataMovementColumnLink) {
			setName(getPropertyAsString("GUID"));

			if (null!=getProperty("SrcTrgtTableName")) {
				Entity ent = Entity.find(getProperty("SrcTrgtTableName"));
				if (null!=ent)
					parentAttribute = (Attribute)ent.getChild("ATTRIBUTE", getProperty("SrcTrgtColumnName"));
			}
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public DataMovementColumnLink create(ActiveXComponent object) {
		DataMovementColumnLink dmcl = find(object);
		
		return dmcl;
	}

	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<DataMovementColumnLink> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
//		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<DataMovementColumnLink> dmcls = new ArrayList<DataMovementColumnLink>();
		DataMovementColumnLink dmcl = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			String rawType = data.get("RawType");
			String parentID = data.get("Owner") + "|" + data.get("EntityName") + "|" + data.get("AttributeName");
			
			if (parentID.equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(DataMovementColumnLink.nullObj, data);
				dmcl = new DataMovementColumnLink(pax);
				dmcls.add(dmcl);
				dmcl.setParent(parent);
				dmcl.setChildManual(true);

				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				dmcl.setName(id);
				dmcl.setID(id);
			}
		}
		
		return dmcls;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public DataMovementColumnLink find(ActiveXComponent object) {
		return new DataMovementColumnLink(object);
	}
	
	/**
	 * Get the {@link ModelingObject} associated with one side of the link
	 * @param name the name of the side to get the object for
	 * @return the associated {@link ModelingObject} 
	 */
	public ModelingObject getModelingObject(String name) {
		ModelingObject ob = null;
		
		switch (name.toUpperCase()) {
		case "SOURCETARGETCOLUMNOBJECT" :
			ob = parentAttribute;

			break;
		case "SOURCETARGETTABLEOBJECT" :
			ob = parentAttribute.getParent();

			break;
		default:	
			ob = super.getModelingObject(name);
		}

		return ob;
	}
	
	/**
	 * Get a property value.  This method will be overridden in each subclass to handle specific derived properties.  These
	 * could be either calculated values or references to parent or other related objects
	 * @param object the object to get the property from
	 * @param name the name of the property
	 * @return the value of the property
	 */
	public Variant getDerivedProperty(String object, String name) {
		Variant value = null;

		switch(object.toUpperCase()) {
		case "SOURCEATTRIBUTE" :
			if (null!=parentAttribute) {
				if (name.toUpperCase().contains("NAME") || name.startsWith("$")) 
					value = parentAttribute.getDerivedProperty("", name.replace("$", ""));
				if (null==value)
					value = parentAttribute.getProperty(name.replace("$", ""));
			}
			
			break;
		case "SOURCEENTITY" :
			if (null!=parentAttribute) {
				if (name.startsWith("$"))
					value = parentAttribute.getParent().getDerivedProperty("", name.replace("$", ""));				
				if (null==value)
					value = parentAttribute.getParent().getProperty(name.replace("$",  ""));
			}
	
			break;
		default:
			value = super.getDerivedProperty(object, name);
		}

		return value;
	}
	
}
