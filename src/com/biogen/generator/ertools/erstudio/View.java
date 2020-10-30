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
 * This class implements the ER/Studio View object 
 * @author drothste
 *
 */
public class View extends ModelingObject {
	/** empty object used for testing and initialization */
	static public View nullObj = new View(null);
	static private CollectionLoader<View> masterCO = new CollectionLoader<View>(View.nullObj, null, View.nullObj);

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param view the ER/Studio object underlying this instance
	 */
	public View(ActiveXComponent view) {
		super(view);
		setSelfType("VIEW");
		
		if (null!=view) {
			setName(getPropertyAsString("Name"));
			addChild("ViewField", new CollectionLoader<ViewField>(this, "ViewFields", ViewField.nullObj));

			masterCO.add(this);
		}
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public View create(ActiveXComponent object) {
		View vw = find(object);
		
		return vw;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<View> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<View> views = new ArrayList<View>();
		View view = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(View.nullObj, data);
				view = new View(null);
				view.setObject(pax);
				views.add(view);
				view.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				view.setName(data.get("SubmodelName"));
				view.setID(id);
				view.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				view.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				view.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				view.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				view.putProp("Note", HelperDictionary.getString(data, "Note"));
				view.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = view.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(view, objects, id));
				cl = view.getChildCollection("INDEX");
				cl.addAll(Index.create(view, objects, id));
				cl = view.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(view, objects, id));
				cl = view.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(view, objects, id));
			}
		}
		
		return views;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public View find(ActiveXComponent object) {
		View vw = null;
		
		if (null!=object)
			vw = masterCO.get(object.getProperty("Name"));
		
		if (null==vw)
			vw = new View(object);
		
		return vw;
	}

	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param name one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public View find(Variant... name) {
		View result = masterCO.get(name);
		
		if (null==result)
			result = (View) CollectionLoader.findGet(View.nullObj.getSelfType(), name);
		
		return result;
	}
	
}
