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
 * This class implements the ER/Studio FK Column object
 * @author drothste
 *
 */
public class FKColumn extends ModelingObject {
	/** empty object used for testing and initialization */
	static public FKColumn nullObj = new FKColumn(null);
		
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param fkColumn the ER/Studio object underlying this instance
	 */
	public FKColumn (ActiveXComponent fkColumn) {
		super(fkColumn, false);
		setSelfType("FKCOLUMN");
		
		if (null!=fkColumn) {
			setName(getPropertyAsString("GUID"));
			
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param fkColumn the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public FKColumn create(ActiveXComponent fkColumn) {
		FKColumn fkc = find(fkColumn);
		
		return fkc;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<FKColumn> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<FKColumn> fkColumns = new ArrayList<FKColumn>();
		FKColumn fkColumn = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(FKColumn.nullObj, data);
				fkColumn = new FKColumn(null);
				fkColumn.setObject(pax);
				fkColumns.add(fkColumn);
				fkColumn.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				fkColumn.setName(data.get("SubmodelName"));
				fkColumn.setID(id);
				fkColumn.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				fkColumn.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				fkColumn.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				fkColumn.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				fkColumn.putProp("Note", HelperDictionary.getString(data, "Note"));
				fkColumn.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = fkColumn.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(fkColumn, objects, id));
				cl = fkColumn.getChildCollection("INDEX");
				cl.addAll(Index.create(fkColumn, objects, id));
				cl = fkColumn.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(fkColumn, objects, id));
				cl = fkColumn.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(fkColumn, objects, id));
			}
		}
		
		return fkColumns;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param fkColumn the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public FKColumn find(ActiveXComponent fkColumn) {
		return new FKColumn(fkColumn);
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
		case "PARENTATTRIBUTENAME":
				value = getPropertyAsComponent("ParentAttribute").getProperty("ColumnName");
				break;
		case "CHILDATTRIBUTENAME":
			value = getPropertyAsComponent("ChildAttribute").getProperty("ColumnName");
			break;
		default:
			value = super.getDerivedProperty(object, name);
		}
		
		return value;
	}
	
}
