package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;

/**
 * This class implements the ER/Studio Selected Object object
 * @author drothste
 *
 */
public class SelectedObject extends ModelingObject {
	/** empty object used for testing and initialization */
	static public SelectedObject nullObj = new SelectedObject();
		
	public SelectedObject() {
		this(null);
	}
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param selObject the ER/Studio object underlying this instance
	 */
	public SelectedObject (ActiveXComponent selObject) {
		super(selObject, false);
		
		if (null==getID())
			setName("NULL");
		else
			setName(getID());
		
		setSelfType("SELECTEDOBJECT");
	}
	
	public String getGUID() {
		return getName();
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public SelectedObject create(ActiveXComponent object) {
		SelectedObject so = find(object);
		
		return so;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<SelectedObject> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<SelectedObject> selectedObjects = new ArrayList<SelectedObject>();
		SelectedObject selectedObject = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(SelectedObject.nullObj, data);
				selectedObject = new SelectedObject(null);
				selectedObject.setObject(pax);
				selectedObjects.add(selectedObject);
				selectedObject.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				selectedObject.setName(data.get("SubmodelName"));
				selectedObject.setID(id);
				selectedObject.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				selectedObject.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				selectedObject.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				selectedObject.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				selectedObject.putProp("Note", HelperDictionary.getString(data, "Note"));
				selectedObject.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = selectedObject.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(selectedObject, objects, id));
				cl = selectedObject.getChildCollection("INDEX");
				cl.addAll(Index.create(selectedObject, objects, id));
				cl = selectedObject.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(selectedObject, objects, id));
				cl = selectedObject.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(selectedObject, objects, id));
			}
		}
		
		return selectedObjects;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public SelectedObject find(ActiveXComponent object) {
		return new SelectedObject(object);
	}

}
