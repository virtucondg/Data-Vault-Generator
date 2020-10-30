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
 * This class implements the ER/Studio Data Flow object
 * @author drothste
 *
 */
public class DataFlow extends ModelingObject {
	/** empty object used for testing and initialization */
	static public DataFlow nullObj = new DataFlow(null);
		
	static private CollectionLoader<DataFlow> masterCO = new CollectionLoader<DataFlow>(DataFlow.nullObj, null, DataFlow.nullObj);

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param dataflow the ER/Studio object underlying this instance
	 */
	public DataFlow (ActiveXComponent dataflow) {
		super(dataflow, false);
		setSelfType("DATAFLOW");
		
		if (null!=dataflow) {
			setName(getPropertyAsString("ModelName"));
			addChild("Transformation", new CollectionLoader<Transformation>(this, "Transformations", Transformation.nullObj, true));

			masterCO.add(this);
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param dataFlow the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public DataFlow create(ActiveXComponent dataFlow) {
		DataFlow df = find(dataFlow);
		
		return df;
	}

	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<DataFlow> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Model");
		CollectionLoader<? extends ModelingObject> mo;
		
		ArrayList<DataFlow> dataflows = new ArrayList<DataFlow>();
		DataFlow dataflow = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			String rawType = data.get("RawType");

			if ("8".equals(data.get("ModelType")) && data.get("DiagramID").equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(DataFlow.nullObj, data);
				dataflow = new DataFlow(pax);
				dataflows.add(dataflow);
				dataflow.setParent(parent);
				dataflow.setChildManual(true);
			
				id = data.get(HelperDictionary.getIdentityColumn("Model"));				
				if (null==id) id = data.get("ModelName");

				dataflow.setName(HelperDictionary.getString(data, "ModelName", "ModelNameID"));
				dataflow.setID(id);
				if (!rawType.equals("EXCEL"))
					dataflow.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Data Dictionary"), id, "0", "0"));
				
				dataflow.putProp("DiagramID", data.get("DiagramID"));
				dataflow.putProp("ModelType", data.get("ModelType"));
			
				mo = dataflow.getChildCollection("TRANSFORMATION");
				mo.addAll(Transformation.create(dataflow, objects, id));
			}
		}
		
		return dataflows;
	}

	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param dataFlow the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public DataFlow find(ActiveXComponent dataFlow) {
		DataFlow df = null;
		if (null!=dataFlow)
			df = masterCO.get(dataFlow.getProperty("Name"));
		
		if (null==df)
			df = new DataFlow(dataFlow);
		
		return df;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param name one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public DataFlow find(Variant... name) {
		DataFlow result = masterCO.get(name);
		
		if (null==result)
			result = (DataFlow) CollectionLoader.findGet(DataFlow.nullObj.getSelfType(), name);
		
		return result;
	}
	
}
