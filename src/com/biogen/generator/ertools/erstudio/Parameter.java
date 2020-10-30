package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.biogen.utils.Utility;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Parameter object
 * @author drothste
 *
 */
public class Parameter extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Parameter nullObj = new Parameter(null);
	private HashMap<String, String> map = new HashMap<String, String>();
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param object the ER/Studio object underlying this instance
	 */
	public Parameter(ActiveXComponent object) {
		super(null, false);
		setSelfType("PARAMETER");
		setGUID("XX");
		setID("XX");
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Parameter create(ActiveXComponent object) {
		return find(object);
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<Parameter> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		Parameter parameter = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Parameter.nullObj, data);
				parameter = new Parameter(null);
				parameter.setObject(pax);
				parameters.add(parameter);
				parameter.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				parameter.setName(data.get("SubmodelName"));
				parameter.setID(id);
				parameter.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				parameter.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				parameter.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				parameter.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				parameter.putProp("Note", HelperDictionary.getString(data, "Note"));
				parameter.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = parameter.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(parameter, objects, id));
				cl = parameter.getChildCollection("INDEX");
				cl.addAll(Index.create(parameter, objects, id));
				cl = parameter.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(parameter, objects, id));
				cl = parameter.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(parameter, objects, id));
			}
		}
		
		return parameters;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Parameter find(ActiveXComponent object) {
		return new Parameter(object);
	}
	
	@Override
	public Variant getDerivedProperty(String object, String name) {		
		return new Variant(getPropertyAsString(name));
	}
	
	@Override
	public String getPropertyAsString(String name) {
		String[] tokens = Utility.splitProperty(name);

		if (tokens[0].equalsIgnoreCase("PARAMETER"))
			return map.get(tokens[tokens.length - 1].replace("$", "").toUpperCase());
		
		return super.getPropertyAsString(name);
	}
	
	/**
	 * Put a parameter into the property list
	 * @param name the name of the property
	 * @param value the value for the property
	 */
	public void put(String name, String value) {
		map.put(name.toUpperCase(),  value);
		if (name.equalsIgnoreCase("NAME"))
			setName(value);
	}
}
