package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;

/**
 * This class implements the ER/Studio Model object
 * @author drothste
 *
 */
public class Model extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Model nullObj = new Model(null);
			
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param model the ER/Studio object underlying this instance
	 */
	public Model(ActiveXComponent model) {
		super(model);
		setSelfType("MODEL");
		
		if (null!=model) {
			addChild("Entity", new CollectionLoader<Entity>(this, "Entities", Entity.nullObj, true));
			addChild("BusinessDataObject", new CollectionLoader<BusinessDataObject>(this, "BusinessDataObjects", BusinessDataObject.nullObj, true));
			addChild("SubModel", new CollectionLoader<SubModel>(this, "SubModels", SubModel.nullObj, true));
			addChild("View", new CollectionLoader<View>(this, "Views", View.nullObj, true));
			addChild("Relationship", new CollectionLoader<Relationship>(this, "Relationships", Relationship.nullObj, true));
		}
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param model the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Model create(ActiveXComponent model) {
		Model mdl = find(model);
		
		return mdl;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param model the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Model find(ActiveXComponent model) {
		return new Model(model);
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<Model> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Model");
		CollectionLoader<? extends ModelingObject> mo;
		
		ArrayList<Model> models = new ArrayList<Model>();
		Model model = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ("1".equals(data.get("ModelType")) && data.get("DiagramID").equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Model.nullObj, data);
				model = new Model(null);
				model.setObject(pax);
				models.add(model);
				model.setParent(parent);
				
				id = data.get(HelperDictionary.getIdentityColumn("Model"));				
				if (null==id) id = getNextIDSequence();

				model.setName(HelperDictionary.getString(data, "ModelName", "ModelNameID"));
				model.setID(id);
				model.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Model"), id, "0", "0"));
				model.putProp("DiagramID", data.get("DiagramID"));
				model.putProp("ModelType", data.get("ModelType"));
			
				mo = model.getChildCollection("ENTITY");
				mo.addAll(Entity.create(model, objects, parentKey + "|" + id));
				mo = model.getChildCollection("VIEW");
				mo.addAll(View.create(model, objects, parentKey + "|" + id));
				mo = model.getChildCollection("RELATIONSHIP");
				mo.addAll(Relationship.create(model, objects, parentKey + "|" + id));
				mo = model.getChildCollection("SUBMODEL");
				mo.addAll(SubModel.create(model, objects, parentKey + "|" + id));
				mo = model.getChildCollection("BUSINESSDATAOBJECT");
				mo.addAll(BusinessDataObject.create(model, objects, id));
			}
		}
		
		return models;
	}
	
	/**
	 * Get the active submodel
	 * @return the active {@link SubModel} object
	 */
	public SubModel getActiveSubModel() {
		return (SubModel) getChild("SUBMODEL", getPropertyAsComponent("ActiveSubModel").getProperty("Name"));
	}

}
