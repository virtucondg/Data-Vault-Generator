package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.collection.CollectionLoaderInit;
import com.biogen.generator.ertools.erstudio.collection.ManualCollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Entity object
 * @author drothste
 *
 */
public class Entity extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Entity nullObj = new Entity(null);
	static private CollectionLoader<Entity> masterCO = new CollectionLoader<Entity>(Entity.nullObj, null, Entity.nullObj);
	
	private BusinessDataObject parentBDO = null;
	private boolean initRefs = false;
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param entity the ER/Studio object underlying this instance
	 */
	public Entity (ActiveXComponent entity) {
		super(entity);
		setSelfType("ENTITY");
		
		if (null!=entity) {
			setName(getPropertyAsString("EntityName"));
			addChild("Attribute", new CollectionLoader<Attribute>(this, "Attributes", Attribute.nullObj, true));
			addChild("Index", new CollectionLoader<Index>(this, "Indexes", Index.nullObj, true));
			addChild("ParentRelationsip", new CollectionLoader<Relationship>(this, "ParentRelationships", Relationship.nullObj, true));
			addChild("ChildRelationship", new CollectionLoader<Relationship>(this, "ChildRelationships", Relationship.nullObj, true));
			addChild("Transformation", new ManualCollectionLoader<Transformation>(this, Transformation.nullObj, new TransformationInit()));
	
			masterCO.add(this);
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param entity the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Entity create(ActiveXComponent entity) {
		Entity ent = find(entity);
		
		return ent;
	}

	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<Entity> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Entity");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		Entity entity = null;
		String id;
		String parentID;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			String rawType = data.get("RawType");

			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey) || rawType.equals("EXCEL")) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Entity.nullObj, data);
				
				entity = new Entity(pax);
				entities.add(entity);
				entity.setParent(parent);
				entity.setChildManual(true);
								
				entity.setName(HelperDictionary.getString(data, "EntityName"));
				entity.putProp("Owner", HelperDictionary.getString(data, "Owner", "OwnerID"));
				entity.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				entity.putProp("TableName", HelperDictionary.getString(data, "TableName"));

				if (rawType.equals("EXCEL")) {
					parentID = entity.getProp("Owner") + "|" + entity.getProp("EntityName");
					id = entity.getProp("EntityName");
				} else {
					id = data.get(HelperDictionary.getIdentityColumn("Entity"));
					parentID = id;
				} 
				
				entity.setID(id);

				if (rawType.equals("EXCEL")) {
					entity.setGUID(data.get("GUID"));

					cl = entity.getChildCollection("ATTRIBUTE");
					cl.addAll(Attribute.create(entity, objects, parentID));
				} else {
					entity.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
					entity.putProp("RootEntity_Id", data.get("RootEntity_Id"));

					cl = entity.getChildCollection("ATTRIBUTE");
					cl.addAll(Attribute.create(entity, objects, id));
					cl = entity.getChildCollection("INDEX");
					cl.addAll(Index.create(entity, objects, id));
					cl = entity.getChildCollection("PARENTRELATIONSHIP");
					cl.addAll(Relationship.create(entity, objects, id));
					cl = entity.getChildCollection("CHILDRELATIONSHIP");
					cl.addAll(Relationship.create(entity, objects, id));
					entity.createTransformation();
				}
				entity.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				entity.putProp("Note", HelperDictionary.getString(data, "Note"));
			}
		}
		
		return entities;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param entity the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Entity find(ActiveXComponent entity) {
		Entity ent = null;
		
		if (null!=entity)
			ent = masterCO.get(entity.getProperty("EntityName"));
		
		if (null==ent)
			ent = new Entity(entity);
		
		return ent;
	}

	@SuppressWarnings("unchecked")
	private void initRefs() {
		if(!initRefs) {
			initRefs = true;

			for (BusinessDataObject bdo:(ArrayList<BusinessDataObject>)((Model)getParent()).getChildren("BUSINESSDATAOBJECT")) {
				if (bdo.isContained(this))
					parentBDO = bdo;
			}
		}
	}

	/**
	 * Get the list of additional properties that can be searched.
	 * This can be overridden to set additional searchable properties
	 * @return the sort key for this object
	 */
	public String[] getCollectionKeys() {		
		return new String[] {"TableName", "Owner||TableName"};
	}
		
	/**
	 * Initialize the {@link Transformation}s that this entity is a target of
	 */
	@SuppressWarnings("unchecked")
	public void createTransformation() {
		CollectionLoader<ModelingObject> cl = (CollectionLoader<ModelingObject>) getChildCollection("TRANSFORMATION");
		String workflows = getPropertyAsString("ENTITY.#Workflow Name#");
		
		if (null!=workflows && (0 < workflows.length())) {
			for (String wf: workflows.trim().split(";")) {
				String[] wfs = wf.split(":");
				
				DataFlow df = DataFlow.find(new Variant(wfs[0].trim()));
						//(DataFlow)(getCurrentObject("DIAGRAM").getChild("DATAFLOW", wfs[0].trim()));
				
				if (null!=df) {
					Transformation tf = (Transformation)(df.getChild("TRANSFORMATION", wfs[1].trim()));
					
					if (null!=tf)
						cl.add(tf);
				}
			}
			
			cl.sort();
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

		switch(object.toUpperCase()) {
		case "BUSINESSDATAOBJECT":
			if (null==parentBDO)
				initRefs();
			
			if (null!=parentBDO) 
				value = parentBDO.getProperty(name);
				
			break;
		default:
			switch(name.toUpperCase()) {
			case "TABLENAMESEQUENCE" :
				String data = getPropertyAsString("TABLENAME");
				if (null != getSequence() && (data.length() > 30 - getSequence().length())) {
					data = data.substring(0,  30 - getSequence().length()) + getSequence();
				} else {
					data += getSequence();
				}
				
				value = getVariant(data);
				
				break;
			default:
				value = super.getDerivedProperty(object, name);
			}
		}		

		return value;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param id one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public Entity find(Variant... id) {
		Entity result = masterCO.get(id);
		
		if (null==result)
			result = (Entity) CollectionLoader.findGet(Entity.nullObj.getSelfType(), id);
		
		return result;
	}
	
	private class TransformationInit implements CollectionLoaderInit {
		@Override
		public void init() {
			createTransformation();
		}
	}

}
