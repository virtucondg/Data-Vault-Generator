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
 * This class implements the ER/Studio Relationship object
 * @author drothste
 *
 */
public class Relationship extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Relationship nullObj = new Relationship(null);
		
	private Entity parentEntity;
	private Entity childEntity;
	private boolean initChildren = false;
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param relationship the ER/Studio object underlying this instance
	 */
	public Relationship (ActiveXComponent relationship) {
		super(relationship);
		setSelfType("RELATIONSHIP");
		
		if (null!=relationship) {
			addChild("FKColumn", new CollectionLoader<FKColumn>(this, "FKColumnPairs", FKColumn.nullObj, true));
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Relationship create(ActiveXComponent object) {
		Relationship rel = find(object);
		
		return rel;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<Relationship> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<Relationship> relationships = new ArrayList<Relationship>();
		Relationship relationship = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Relationship.nullObj, data);
				relationship = new Relationship(null);
				relationship.setObject(pax);
				relationships.add(relationship);
				relationship.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				relationship.setName(data.get("SubmodelName"));
				relationship.setID(id);
				relationship.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				relationship.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				relationship.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				relationship.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				relationship.putProp("Note", HelperDictionary.getString(data, "Note"));
				relationship.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = relationship.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(relationship, objects, id));
				cl = relationship.getChildCollection("INDEX");
				cl.addAll(Index.create(relationship, objects, id));
				cl = relationship.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(relationship, objects, id));
				cl = relationship.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(relationship, objects, id));
			}
		}
		
		return relationships;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Relationship find(ActiveXComponent object) {
		return new Relationship(object);
	}
	
	private void deferredInitialize() {
		if (!initChildren) {
			initChildren = true;
			
			parentEntity = (Entity)Entity.find(getPropertyAsComponent("ParentEntity").getProperty("GUID"));
			childEntity = (Entity)Entity.find(getPropertyAsComponent("ChildEntity").getProperty("GUID"));
		}
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

		deferredInitialize();
		
		switch(object.toUpperCase()) {
		case "PARENTENTITY":
			switch(name.toUpperCase()) {
			case "OWNER" : 
				value = parentEntity.getProperty("Owner");
				break;
			case "TABLENAME" :
				value = parentEntity.getProperty("TableName");
				break;
			default:
			}
			
			break;
		case "CHILDENTITY":
			switch(name.toUpperCase()) {
			case "OWNER" : 
				value = childEntity.getProperty("Owner");
				break;
			case "TABLENAME" :
				value = childEntity.getProperty("TableName");
				break;
			default:
			}
			
			break;
		default:
			switch(name.toUpperCase()) {
			case "TYPE" :
				switch(getPropertyAsString("Type")) {
				case "0": 
					value = getVariant("Identifying");
					break;
				case "1":
					value = getVariant("NonIdentifying");
					break;
				case "2":
					value = getVariant("NonSpecific");
					break;
				case "3":
					value = getVariant("NonIdentifying Optional");
					break;
				default:
				}
				break;
			case "MANDATORY" :
				value = getVariant(getPropertyAsBoolean("Mandatory") ? "Mandatory" : "Optional");
				break;
			case "PARENTINDEX" :
				Variant key = getProperty("ParentKeyID");
				
				if ("0".equals(key.toString())) 
					value = getVariant("PK");
				else {
					Index ix = (Index)getParent().getChild("INDEX", key);
					if (null!=ix)
						value = getVariant(ix.getName());
				}
				
				break;
			default:
				value = super.getDerivedProperty(object, name);
			}
		}
		
		return value;
	}

}
