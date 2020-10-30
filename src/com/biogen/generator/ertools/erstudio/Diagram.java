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
 * This class implements the ER/Studio Diagram object
 * @author drothste
 *
 */
public class Diagram extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Diagram nullObj = new Diagram(null);
			
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param diagram the ER/Studio object underlying this instance
	 */
	public Diagram(ActiveXComponent diagram) {
		super(diagram);
		setSelfType("DIAGRAM");
		
		if(null!=diagram) {
			setName(getPropertyAsString("FileName"));
			
			if (null!=diagram) {
				addChild("Model", new CollectionLoader<Model>(this, "Models", Model.nullObj));
				addChild("EnterpriseDataDictionary", new CollectionLoader<Dictionary>(this, "EnterpriseDataDictionaries", Dictionary.nullObj, true));
				addChild("DataFlow", new CollectionLoader<DataFlow>(this, "DataFlows", DataFlow.nullObj, true));
			}			
			setCurrentObject();
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param diagram the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Diagram create(ActiveXComponent diagram) {
		Diagram diag = find(diagram);
		
		return diag;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param diagram the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Diagram find(ActiveXComponent diagram) {
		return new Diagram(diagram);
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<Diagram> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Diagram");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<Diagram> diagrams = new ArrayList<Diagram>();
		Diagram diagram = null;
		String id;
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			PseudoActiveXComponent pax = new PseudoActiveXComponent(Diagram.nullObj, data);
			diagram = new Diagram(null);
			diagram.setObject(pax);
			diagrams.add(diagram);
			diagram.setParent(parent);
			
			id = data.get(HelperDictionary.getIdentityColumn("Diagram"));				
			if (null==id) id = getNextIDSequence();

			diagram.setName(data.get("DiagramName"));
			diagram.setID(data.get("DiagramID"));
			diagram.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Diagram"), id, "0", "0"));
			diagram.putProp("ActiveModel", data.get("ActiveModel"));
			diagram.putProp("ActiveSubModel", data.get("ActiveSubModel"));
			diagram.putProp("DiagramName", HelperDictionary.getString(data, "Diagram", "DiagramNameId"));
			diagram.putProp("FileName", HelperDictionary.getString(data, "FileName", "FileNameId"));
		
			cl = diagram.getChildCollection("MODEL");
			cl.addAll(Model.create(diagram, objects, id));
			ArrayList<Dictionary> dictionaries = (ArrayList<Dictionary>) Dictionary.create(diagram, objects, id);
			cl = diagram.getChildCollection("ENTERPRISEDATADICTIONARY");
			for (Dictionary dictionary : dictionaries)
				if ("0".equals(dictionary.getPropertyAsString("Flag")))
					((CollectionLoader<Dictionary>)cl).add(dictionary);
			cl = diagram.getChildCollection("DATAFLOW");
			cl.addAll(Model.create(diagram, objects, id));
		}
		
		return diagrams;
	}
	
	/**
	 * Get the active model of this diagram
	 * @return the active Model
	 */
	public Model getActiveModel() {
		Model model = new Model(getPropertyAsComponent("ActiveModel"));
		model.setParent(this);
		
		return model;
	}
	
	/**
	 * Set the active model for this diagram
	 * @param name the name of the model to set as active
	 */
	public void setActiveModel(String name) {
		setPropertyAsString("SetActiveModel", name);
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
		case "FILENAME":
			value = getVariant(getPropertyAsString("FileName").replaceAll("\\.dm1$", ""));
				
			break;
		case "ABBREVIATION":
			if (getPropertyAsString("FileName").contains("Coordinated Care"))
				value = getVariant("CCS");
			else if (getPropertyAsString("FileName").contains("International Data"))
				value = getVariant("IDF");
			else
				value = getVariant("OTH");
			
			break;
		default:
			value = super.getDerivedProperty(object, name);
		}
		
		return value;
	}
	
}
