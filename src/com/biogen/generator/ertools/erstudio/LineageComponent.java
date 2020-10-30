package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Lineage Transformation Field object
 * @author drothste
 *
 */
public class LineageComponent extends ModelingObject {
	/** empty object used for testing and initialization */
	static public LineageComponent nullObj = new LineageComponent(null);
	private Variant sourceObjectType;
	private Entity entity = null;
	private View view = null;
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param lineageComponent the ER/Studio object underlying this instance
	 */
	public LineageComponent (ActiveXComponent lineageComponent) {
		super(lineageComponent, false);
		setSelfType("LINEAGECOMPONENT");
		
		if (null!=lineageComponent) {
			switch(getPropertyAsString("SourceObjectType")) {
			case "1" : sourceObjectType = getVariant("Entity");
					   entity = Entity.find(getProperty("SourceObjectID"));

					   break;
			case "16" : sourceObjectType = getVariant("View");
					   view = View.find(getProperty("SourceObjectID"));
					   
					   break;
			default: 
			}
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public LineageComponent create(ActiveXComponent object) {
		LineageComponent lc = find(object);
		
		return lc;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<LineageComponent> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		
		ArrayList<LineageComponent> lineageComponents = new ArrayList<LineageComponent>();
		LineageComponent lineageComponent = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(LineageComponent.nullObj, data);
				lineageComponent = new LineageComponent(null);
				lineageComponent.setObject(pax);
				lineageComponents.add(lineageComponent);
				lineageComponent.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				lineageComponent.setName(data.get("SubmodelName"));
				lineageComponent.setID(id);
				lineageComponent.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				lineageComponent.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				lineageComponent.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				lineageComponent.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				lineageComponent.putProp("Note", HelperDictionary.getString(data, "Note"));
				lineageComponent.putProp("RootEntity_Id", data.get("RootEntity_Id"));
			}
		}
		
		return lineageComponents;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public LineageComponent find(ActiveXComponent object) {
		return new LineageComponent(object);
	}
	
	/**
	 * Get the modeling object that is referenced in the {@link Transformation} field
	 * @param name the side of the association to get
	 * @return the associated {@link ModelingObject}
	 */
	public ModelingObject getModelingObject(String name) {
		ModelingObject ob = null;
		
		switch (name.toUpperCase()) {
		case "SOURCEOBJECT" :
			ob = (sourceObjectType.toString().equals("Attribute") ? entity : view);

			break;
		default:	
			ob = super.getModelingObject(name);
		}

		return ob;
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
		case "SOURCEOBJECTTYPE":
			value = sourceObjectType;
			
			break;
		case "SOURCEOBJECTNAME":
			switch(sourceObjectType.toString()) {
			case "Attribute" : value = getVariant(entity.getName());
								break;
			case "ViewField" : value = getVariant(view.getName());
								break;
			default:
			}
			break;
		case "SOURCEOBJECTGUID":
			switch(sourceObjectType.toString()) {
			case "Attribute" : value = getVariant(entity.getGUID());
								break;
			case "ViewField" : value = getVariant(view.getGUID());
								break;
			default:
			}
			break;
		default:
			value = super.getDerivedProperty(object, name);
		}
		
		return value;
	}
	
}
