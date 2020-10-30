package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;

/**
 * This class implements the ER/Studio View Field object
 * @author drothste
 *
 */
public class ViewField extends ModelingObject {
	/** empty object used for testing and initialization */
	static public ViewField nullObj = new ViewField(null);

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param viewField the ER/Studio object underlying this instance
	 */
	public ViewField(ActiveXComponent viewField) {
		super(viewField, false);
		setSelfType("VIEWFIELD");
		
		if (null != viewField) {
			setName(getPropertyAsString("Alias"));
		}
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public ViewField create(ActiveXComponent object) {
		ViewField vwf = find(object);
		
		return vwf;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<ViewField> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<ViewField> viewFields = new ArrayList<ViewField>();
		ViewField viewField = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(ViewField.nullObj, data);
				viewField = new ViewField(null);
				viewField.setObject(pax);
				viewFields.add(viewField);
				viewField.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				viewField.setName(data.get("SubmodelName"));
				viewField.setID(id);
				viewField.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				viewField.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				viewField.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				viewField.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				viewField.putProp("Note", HelperDictionary.getString(data, "Note"));
				viewField.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = viewField.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(viewField, objects, id));
				cl = viewField.getChildCollection("INDEX");
				cl.addAll(Index.create(viewField, objects, id));
				cl = viewField.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(viewField, objects, id));
				cl = viewField.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(viewField, objects, id));
			}
		}
		
		return viewFields;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public ViewField find(ActiveXComponent object) {
		return new ViewField(object);
	}
	
}
