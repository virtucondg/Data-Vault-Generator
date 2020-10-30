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
 * This class implements the ER/Studio Transformation Field object
 * @author drothste
 *
 */
public class TransformationField extends ModelingObject {
	/** empty object used for testing and initialization */
	static public TransformationField nullObj = new TransformationField(null);
	private Variant sourceObjectType;
	private Entity entity = null;
	private Attribute attribute = null;
	private View view = null;
	private ViewField viewField = null;
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param transformationField the ER/Studio object underlying this instance
	 */
	public TransformationField (ActiveXComponent transformationField) {
		super(transformationField, false);
		setSelfType("TRANSFORMATIONFIELD");
		
		if (null!=transformationField) {
			switch(getPropertyAsString("SourceObjectType")) {
			case "5" : sourceObjectType = getVariant("Attribute");
						
					   entity = Entity.find(getProperty("SourceParentID"));
					   if (null!=entity)
						   attribute = (Attribute) entity.getChild("ATTRIBUTE", getProperty("SourceObjectID"));
					   
					   break;
			case "17" : sourceObjectType = getVariant("ViewField");

						view = View.find(getProperty("SourceParentID"));
						if (null!=view)
							viewField = (ViewField) view.getChild("VIEWFIELD", getProperty("SourceObjectID"));
					   
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
	public TransformationField create(ActiveXComponent object) {
		TransformationField tff = find(object);
		
		return tff;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<TransformationField> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<TransformationField> tfs = new ArrayList<TransformationField>();
		TransformationField tf = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			String parID; 
			String rawType = data.get("RawType");

			if (rawType.equals("EXCEL"))
				parID = data.get("Model_ID");
			else
				parID = data.get("DiagramId") + "|" + data.get("Model_ID");
				
			if (parID.equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(TransformationField.nullObj, data);
				tf = new TransformationField(pax);
				tfs.add(tf);
				tf.setParent(parent);
				tf.setChildManual(true);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				tf.setName(data.get("SubmodelName"));
				tf.setID(id);
				tf.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				tf.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				tf.putProp("TableName", HelperDictionary.getString(data, "TableName"));
				tf.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				tf.putProp("Note", HelperDictionary.getString(data, "Note"));
				tf.putProp("RootEntity_Id", data.get("RootEntity_Id"));

				cl = tf.getChildCollection("ATTRIBUTE");
				cl.addAll(Attribute.create(tf, objects, id));
				cl = tf.getChildCollection("INDEX");
				cl.addAll(Index.create(tf, objects, id));
				cl = tf.getChildCollection("PARENTRELATIONSHIP");
				cl.addAll(Relationship.create(tf, objects, id));
				cl = tf.getChildCollection("CHILDRELATIONSHIP");
				cl.addAll(Relationship.create(tf, objects, id));
			}
		}
		
		return tfs;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public TransformationField find(ActiveXComponent object) {
		return new TransformationField(object);
	}
	
	public ModelingObject getModelingObject(String name) {
		ModelingObject ob = null;
		
		switch (name.toUpperCase()) {
		case "SOURCEPARENTOBJECT" :
			ob = (sourceObjectType.toString().equals("Attribute") ? entity : view);

			break;
		case "SOURCEOBJECT" :
			ob = (sourceObjectType.toString().equals("Attribute") ? attribute : viewField);

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
			case "Attribute" : value = attribute.getProperty("AttributeName");
								break;
			case "ViewField" : value = viewField.getProperty("Alias");
								break;
			default:
			}
			break;
		case "SOURCEOBJECTGUID":
			switch(sourceObjectType.toString()) {
			case "Attribute" : value = attribute.getProperty("GUID");
								break;
			case "ViewField" : value = viewField.getProperty("GUID");
								break;
			default:
			}
			break;
		case "SOURCEPARENTNAME":
			switch(sourceObjectType.toString()) {
			case "Attribute" : value = entity.getProperty("EntityName");
								break;
			case "ViewField" : value = view.getProperty("Name");
								break;
			default:
			}
			break;
		case "SOURCEPARENTGUID":
			switch(sourceObjectType.toString()) {
			case "Attribute" : value = entity.getProperty("GUID");
								break;
			case "ViewField" : value = view.getProperty("GUID");
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
