package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Business Data Object object
 * @author drothste
 *
 */
public class BusinessDataObject extends ModelingObject {
	/** empty object used for testing and initialization */
	static public BusinessDataObject nullObj = new BusinessDataObject(null);
	static private CollectionLoader<BusinessDataObject> masterCO = new CollectionLoader<BusinessDataObject>(BusinessDataObject.nullObj, null, BusinessDataObject.nullObj);

	private ArrayList<String> refIDs;
	private ArrayList<Entity> refObjs;

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param busDataObj the ER/Studio object underlying this instance
	 */
	public BusinessDataObject(ActiveXComponent busDataObj) {
		super(busDataObj);
		setSelfType("BUSINESSDATAOBJECT");

		if (null!=busDataObj) {
			setName(getPropertyAsString("LogicalName"));

			masterCO.add(this);
		}
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param busDataObj the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public BusinessDataObject create(ActiveXComponent busDataObj) {
		BusinessDataObject bdo = find(busDataObj);
		
		return bdo;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<BusinessDataObject> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("BusDataObject");
//		CollectionLoader<? extends ModelingObject> cl;

		ArrayList<BusinessDataObject> bdos = new ArrayList<BusinessDataObject>();
		BusinessDataObject bdo = null;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if (data.get("Model_ID").equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(BusinessDataObject.nullObj, data);
				bdo = new BusinessDataObject(null);
				bdo.setObject(pax);
				bdos.add(bdo);
				bdo.setParent(parent);
									
				bdo.setName(data.get("SubmodelName"));
				bdo.setID(data.get("Submodel_ID"));
				bdo.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Business Data Object"), data.get("Submodel_ID"), "0", data.get("Model_ID")));
				bdo.putProp("LogicalName", HelperDictionary.getString(data, "LogicalName"));
				bdo.putProp("PhysicalName", HelperDictionary.getString(data, "PhysicalName"));
				bdo.putProp("RootEntity_Id", data.get("RootEntity_Id"));
			}
		}
		
		return bdos;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param busDataObj the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public BusinessDataObject find(ActiveXComponent busDataObj) {
		BusinessDataObject bdo = null;
		
		if (null!=busDataObj)
			bdo = masterCO.get(busDataObj.getProperty("LogicalName"));
			
		if (null==bdo)
			bdo = new BusinessDataObject(busDataObj);
		
		return bdo;
	}
	
	/**
	 * Initialize the list of objects that are in this BusinessDataObject
	 */
	private void initRefObjs() {
		if (null==refObjs) {
			refObjs = new ArrayList<Entity>();
			
			ActiveXComponent refs = getPropertyAsComponent("ContainedObjects");
			EnumVariant ev = new EnumVariant(refs);
			Dispatch item;
			Variant id;
			
			while (ev.hasMoreElements()) {
				item = ev.nextElement().getDispatch();
				id = Dispatch.get(item, "ID");
				Entity ent = Entity.find(id);
				if (null!= ent) 
					refObjs.add(ent);
			}
		}
	}

	/**
	 * Get the list of objects that are in this BusinessDataObject
	 * @return the list of objects
	 */
	public ArrayList<Entity> getReferencedObjects() {
		initRefObjs();

		return refObjs;
	}
	
	/**
	 * Initialize the list of object IDs that are in this BusinessDataObject
	 */
	private void initRefIDs() {
		if (null==refIDs) {
			refIDs = new ArrayList<String>();
			
			ActiveXComponent refs = getPropertyAsComponent("ContainedObjects");
			EnumVariant ev = new EnumVariant(refs);
			Dispatch item;
			Variant id;
			
			while (ev.hasMoreElements()) {
				item = ev.nextElement().getDispatch();
				id = Dispatch.get(item, "ID");
				refIDs.add(String.valueOf(id.getInt()));
			}
		}
	}

	/**
	 * Is an object a part of this BusinessDataObject?
	 * @param entity the entity to check
	 * @return <code>TRUE</code> if this entity is in this BusinessDataObject; <code>FALSE</code> otherwise
	 */
	public boolean isContained(Entity entity) {
		initRefIDs();
		return refIDs.contains(entity.getPropertyAsString("ID"));
	}
	
	/**
	 * Get the list of additional properties that can be searched.
	 * This can be overridden to set additional searchable properties
	 * @return the sort key for this object
	 */
	public String[] getCollectionKeys() {
		return new String[] {"PhysicalName"};
	}
		
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param name one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public BusinessDataObject find(Variant... name) {
		BusinessDataObject result = masterCO.get(name);
		
		if (null==result)
			result = (BusinessDataObject) CollectionLoader.findGet(BusinessDataObject.nullObj.getSelfType(), name);
		
		return result;
	}
	
	/**
	 * Get the BusinessDataObject that a given entity is in
	 * @param entity the Entity to find the BusinessDataObject for
	 * @return the BusinesDataObject parent of the Entity
	 */
	static public BusinessDataObject which(Entity entity) {
		BusinessDataObject result = null;
		
		for (BusinessDataObject bdo: masterCO.getList())
			if (bdo.isContained(entity))
				result = bdo;
		
		return result;
	}
}
