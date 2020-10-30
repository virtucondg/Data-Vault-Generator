package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.collection.IndirectCollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Submodel object
 * @author drothste
 *
 */
public class SubModel extends ModelingObject {
	/** empty object used for testing and initialization */
	static public SubModel nullObj = new SubModel(null);
	static private CollectionLoader<SubModel> masterCO = new CollectionLoader<SubModel>(SubModel.nullObj, null, SubModel.nullObj);

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param subModel the ER/Studio object underlying this instance
	 */
	public SubModel(ActiveXComponent subModel) {
		super(subModel);
		setSelfType("SUBMODEL");
		
		if (null!=subModel) {
			addChild("SelectedObject", new CollectionLoader<SelectedObject>(this, "SelectedObjects", SelectedObject.nullObj, true));

			masterCO.add(this);
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param subModel the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public SubModel create(ActiveXComponent subModel) {
		SubModel sm = find(subModel);
		
		return sm;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<SubModel> create(ModelingObject parent, HashMap<String, RawDataObject> objects) {
		RawDataObject object = objects.get("Diagram");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<SubModel> submodels = new ArrayList<SubModel>();
		SubModel submodel = null;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			submodel = new SubModel(null);
			submodels.add(submodel);
			submodel.setParent(parent);
								
			submodel.setName(data.get("SubmodelName"));
			submodel.setID(data.get("Submodel_ID"));
			submodel.setGUID(HelperDictionary.getGUID("25", data.get("Submodel_ID"), "0", data.get("Model_ID")));
		
			cl = submodel.getChildCollection("SELECTEDOBJECT");
			cl.addAll(SelectedObject.create(submodel, objects, data.get("Submodel_ID")));
		}
		
		return submodels;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<SubModel> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("DataMovementColumnLink");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<SubModel> submodels = new ArrayList<SubModel>();
		SubModel submodel = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(SubModel.nullObj, data);
				submodel = new SubModel(null);
				submodel.setObject(pax);
				submodels.add(submodel);
				submodel.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("DataMovementColumnLink"));				
				if (null==id) id = getNextIDSequence();

				submodel.setName(data.get("SubmodelName"));
				submodel.setID(id);
				submodel.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Submodel"), id, "0", parentKey));

				cl = submodel.getChildCollection("SELECTEDOBJECT");
				cl.addAll(SelectedObject.create(submodel, objects, id));
			}
		}
		
		return submodels;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param subModel the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public SubModel find(ActiveXComponent subModel) {
		return new SubModel(subModel);
	}

	/**
	 * Set the parent of this submodel
	 * @param obj the {@link ModelingObject} subclass to set the parent to
	 */
	public void setParent(ModelingObject obj) {
		super.setParent(obj);

		addChild("Entity", new IndirectCollectionLoader<Entity>(this, "EntityDisplays", Entity.nullObj, "ParentEntity", getParent().getChildCollection("Entity"), true));
		addChild("View", new IndirectCollectionLoader<View>(this, "ViewDisplays", View.nullObj, "ParentView", getParent().getChildCollection("View"), true));
		addChild("BusinessDataObject", new IndirectCollectionLoader<BusinessDataObject>(this, "BusinessDataObjectDisplays", BusinessDataObject.nullObj, "LogicalName", getParent().getChildCollection("BusinessDataObject"), true));
	} 
	
	@SuppressWarnings("unchecked")
	private boolean isObjectSelected(String objectType) {
		ArrayList<SelectedObject> selectedObjects = (ArrayList<SelectedObject>) getChildren("SELECTEDOBJECT");
		
		for (int i = selectedObjects.size(); i-- > 0; )
			if (selectedObjects.get(i).getPropertyAsString("Type").equals(objectType))
				return true;
		
		return false;
	}
	
	/** 
	 * Does this submodel have an entity selected?
	 * @return <code>TRUE</code> if there is an Entity selected; <code>FALSE</code> otherwise
	 */
	public boolean isEntitySelected() {
		return isObjectSelected("1");
	}

	/** 
	 * Does this submodel have an business object selected?
	 * @return <code>TRUE</code> if there is a Business Object selected; <code>FALSE</code> otherwise
	 */
	public boolean isBusinessObjectSelected() {
		return isObjectSelected("108");
	}

	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param name one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public SubModel find(Variant... name) {
		SubModel result = masterCO.get(name);
		
		if (null==result)
			result = (SubModel) CollectionLoader.findGet(SubModel.nullObj.getSelfType(), name);
		
		return result;
	}
	
}
