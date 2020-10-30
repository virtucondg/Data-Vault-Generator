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
 * This class implements the ER/Studio Domain object
 * @author drothste
 *
 */
public class Domain extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Domain nullObj = new Domain(null);
	static private CollectionLoader<Domain> masterCO = new CollectionLoader<Domain>(Domain.nullObj, null, Domain.nullObj);

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param domain the ER/Studio object underlying this instance
	 */
	public Domain (ActiveXComponent domain) {
		super(domain);
		setSelfType("DOMAIN");
		
		if (null!=domain) {
			masterCO.add(this);
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Domain create(ActiveXComponent object) {
		Domain dom = find(object);
		
		return dom;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<Domain> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Data_Dictionary");
//		CollectionLoader<? extends ModelingObject> mo;
		
		ArrayList<Domain> domains = new ArrayList<Domain>();
		Domain domain = null;
		String id;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			if(data.get("Data_Dictionary_ID").equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(DataMovementColumnLink.nullObj, data);
				domain = new Domain(null);
				domain.setObject(pax);
				domains.add(domain);
				domain.setParent(parent);
				
				id = data.get(HelperDictionary.getIdentityColumn("Diagram"));				
				if (null==id) id = getNextIDSequence();

				domain.setName(HelperDictionary.getString(data, "DomainName", "DomainNameId"));
				domain.setID(id);
				domain.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Domain"), id, "0", "0"));
				domain.putProp("DomainName", HelperDictionary.getString(data, "DomainName", "DomainNameId"));
				domain.putProp("ColumnName", HelperDictionary.getString(data, "ColumnName", "ColumnNameId"));
				domain.putProp("AttributeName", HelperDictionary.getString(data, "AttributeName"));
				domain.putProp("DataTypeId", data.get("DataTypeId"));
				domain.putProp("CheckConstraintId", data.get("CheckConstraintId"));
				domain.putProp("DeclaredDefaultId", data.get("DeclaredDefaultId"));
				domain.putProp("Length", data.get("Length"));
				domain.putProp("Scale", data.get("Scale"));
				domain.putProp("Nullable", data.get("Nullable"));
			}
		}
		
		return domains;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Domain find(ActiveXComponent object) {
		Domain dom = null;
		
		if (null!=object)
			dom = masterCO.get(object.getProperty("Name"));

		if (null==dom)
			dom = new Domain(object);
		
		return dom;
	}

	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param name one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public Domain find(Variant... name) {
		Domain result = masterCO.get(name);
		
		if (null==result)
			result = (Domain) CollectionLoader.findGet(Domain.nullObj.getSelfType(), name);
		
		return result;
	}
	
}
