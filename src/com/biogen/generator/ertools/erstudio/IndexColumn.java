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
 * This class implements the ER/Studio Index Column object
 * @author drothste
 *
 */
public class IndexColumn extends ModelingObject {
	/** empty object used for testing and initialization */
	static public IndexColumn nullObj = new IndexColumn(null);
		
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param indexColumn the ER/Studio object underlying this instance
	 */
	public IndexColumn (ActiveXComponent indexColumn) {
		super(indexColumn, false);
		setSelfType("INDEXCOLUMN");

		if (null!=indexColumn) {
			setName(getPropertyAsString("ColumnName"));
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param indexColumn the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public IndexColumn create(ActiveXComponent indexColumn) {
		IndexColumn ixc = find(indexColumn);
		
		return ixc;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<IndexColumn> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<IndexColumn> indexes = new ArrayList<IndexColumn>();
		IndexColumn index = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(IndexColumn.nullObj, data);
				index = new IndexColumn(null);
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
	 * @param indexColumn the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public IndexColumn find(ActiveXComponent indexColumn) {
		return new IndexColumn(indexColumn);
	}
	
	public String getSortKeyValue() {
		return String.format("%03d", getPropertyAsInt("SequenceNo"));
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
		case "SORTORDER": 
			switch(getPropertyAsString("SortOrder")) {
			case "A": value = getVariant("ASC"); break;
			case "D": value = getVariant("DESC"); break;
			default:
			}
			break;
		default:
			value = super.getDerivedProperty(object, name);
		}
		
		return value;
	}
	
}
