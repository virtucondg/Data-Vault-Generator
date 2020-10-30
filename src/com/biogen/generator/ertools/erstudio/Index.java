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
 * This class implements the ER/Studio Index object
 * @author drothste
 *
 */
public class Index extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Index nullObj = new Index(null);
		
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param index the ER/Studio object underlying this instance
	 */
	public Index (ActiveXComponent index) {
		super(index);
		setSelfType("INDEX");
		
		if (null!=index) {
			addChild("IndexColumn", new CollectionLoader<IndexColumn>(this, "IndexColumns", IndexColumn.nullObj, true));
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param index the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Index create(ActiveXComponent index) {
		Index ix = find(index);
		
		return ix;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<Index> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<Index> indexes = new ArrayList<Index>();
		Index index = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Index.nullObj, data);
				index = new Index(null);
				index.setObject(pax);
				indexes.add(index);
				index.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				index.setName(data.get("SubmodelName"));
				index.setID(id);
				index.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				index.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				index.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				index.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				index.putProp("Note", HelperDictionary.getString(data, "Note"));
				index.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = index.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(index, objects, id));
				cl = index.getChildCollection("INDEX");
				cl.addAll(Index.create(index, objects, id));
				cl = index.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(index, objects, id));
				cl = index.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(index, objects, id));
			}
		}
		
		return indexes;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param index the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Index find(ActiveXComponent index) {
		return new Index(index);
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

		switch(name.toUpperCase()) {
		case "KEYTYPE": 
			switch(getPropertyAsString("KeyType")) {
			case "P": value = getVariant("PK"); break;
			case "I": value = getVariant("Inversion"); break;
			case "A": value = getVariant("Alternate"); break;
			default:
			}
			break;
		default:
			value = super.getDerivedProperty(object, name);
		}
		
		return value;
	}
	
}
