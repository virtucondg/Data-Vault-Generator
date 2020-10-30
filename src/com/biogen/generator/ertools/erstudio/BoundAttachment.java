package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.jacob.activeX.ActiveXComponent;

/**
 * This class implements the ER/Studio Bound Attachment object
 * @author drothste
 *
 */
public class BoundAttachment extends ModelingObject {
	/** empty object used for testing and initialization */
	static public BoundAttachment nullObj = new BoundAttachment(null);
		
	static private String X = "X";

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param attachment the ER/Studio object underlying this instance
	 */
	public BoundAttachment (ActiveXComponent attachment) {
		super(attachment, false);
		setSelfType("BOUNDATTACHMENT");
		
		if(null!=attachment) {
			setName(getPropertyAsComponent("Attachment").getPropertyAsString("Name"));
			setID(X);
			setGUID(X);
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param attachment the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public BoundAttachment create(ActiveXComponent attachment) {
		BoundAttachment ba = find(attachment);
		
		return ba;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<BoundAttachment> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("BoundAttachment");

		ArrayList<BoundAttachment> boundAttachments = new ArrayList<BoundAttachment>();
		BoundAttachment boundAttachment = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if ((data.get("DiagramId") + "|" + data.get("Model_ID")).equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Entity.nullObj, data);
				boundAttachment = new BoundAttachment(null);
				boundAttachment.setObject(pax);
				boundAttachments.add(boundAttachment);
				boundAttachment.setParent(parent);
								
				id = data.get(HelperDictionary.getIdentityColumn("BoundAttachment"));				
				if (null==id) id = getNextIDSequence();

				boundAttachment.setName(data.get("SubmodelName"));
				boundAttachment.setID(id);
				boundAttachment.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Entity"), id, "0", parentKey));
				boundAttachment.putProp("EntityName", HelperDictionary.getString(data, "EntityName"));
				boundAttachment.putProp("TableName", HelperDictionary.getString(data, "TableNam"));
				boundAttachment.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				boundAttachment.putProp("Note", HelperDictionary.getString(data, "Note"));
				boundAttachment.putProp("RootEntity_Id", data.get("RootEntity_Id"));
			}
		}
		
		return boundAttachments;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param attachment the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public BoundAttachment find(ActiveXComponent attachment) {
		return new BoundAttachment(attachment);
	}
	
}
