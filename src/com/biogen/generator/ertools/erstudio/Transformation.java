package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.collection.CollectionLoaderInit;
import com.biogen.generator.ertools.erstudio.collection.ManualCollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.NullActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.biogen.utils.Utility;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Transformation object
 * @author drothste
 *
 */
public class Transformation extends ModelingObject {
	/** empty object used for testing and initialization */
	static public Transformation nullObj = new Transformation(null);
	static private CollectionLoader<Transformation> masterCO = new CollectionLoader<Transformation>(Transformation.nullObj, null, Transformation.nullObj);
	static private Pattern sqlTablePattern = Pattern.compile("\\s+(?:FROM|JOIN)\\s+(?:\\$\\$SCHEMA_)?(\\S+)(?:$|\\s+|(WHERE|ON))");
	static private String[] sqlBoundAttachments = new String[] {"#Source Select Query Override#", "#Target Insert Query Override#"};

	private String sourceEntityName;
	private DataFlow dataflow = null;
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param transformation the ER/Studio object underlying this instance
	 */
	public Transformation (ActiveXComponent transformation) {
		super(transformation);
		setSelfType("TRANSFORMATION");
		
		if (null!=transformation) {
			setName(getPropertyAsString("Name"));
			addChild("LineageInput", new CollectionLoader<TransformationField>(this, "LineageInputs", TransformationField.nullObj, true));
			addChild("LineageOutput", new CollectionLoader<TransformationField>(this, "LineageOutputs", TransformationField.nullObj, true));
			addChild("CustomParameter", new ManualCollectionLoader<Parameter>(this, Parameter.nullObj, new CustomParameterInit()));
			addChild("FilteredCustomParameter", new ManualCollectionLoader<Parameter>(this, Parameter.nullObj, new FilteredCustomParameterInit()));
			
			masterCO.add(this);
		}
	}
	
	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	public Transformation create(ActiveXComponent object) {
		Transformation tf = find(object);
		
		return tf;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	//TODO:
	static public ArrayList<Transformation> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Transformation");
//		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();
		Transformation transformation = null;
		String id;
		String parID;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			String rawType = data.get("RawType");

			if (rawType.equals("EXCEL"))
				parID = data.get("Model_ID");
			else
				parID = data.get("DiagramId") + "|" + data.get("Model_ID");
				
			if (parID.equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Transformation.nullObj, data);
				transformation = new Transformation(pax);
				transformations.add(transformation);
				transformation.setParent(parent);
				transformation.setChildManual(true);
								
				id = data.get(HelperDictionary.getIdentityColumn("Transformation"));				
				if (null==id) id = getNextIDSequence();

				transformation.setID(id);

//				cl = transformation.getChildCollection("LINEAGEINPUT");
//				cl.addAll(TransformationField.create(transformation, objects, id));
//				cl = transformation.getChildCollection("LINEAGEOUTPUT");
//				cl.addAll(TransformationField.create(transformation, objects, id));
				transformation.createCustomParameters();
				transformation.createFilteredCustomParameters();
			}
		}
		
		return transformations;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<Transformation> create(ModelingObject parent, HashMap<String, RawDataObject> objects) {
		RawDataObject object = objects.get("Transformation");
		
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();
		Transformation transformation = null;
		
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			PseudoActiveXComponent pax = new PseudoActiveXComponent(Transformation.nullObj, data);
			
			transformation = new Transformation(new NullActiveXComponent());
			transformation.setObject(pax);
			transformations.add(transformation);
			transformation.setParent(parent);

			transformation.setName(HelperDictionary.getString(data, "Name"));
			transformation.setID(data.get("Transformation_ID"));
			transformation.setGUID(HelperDictionary.getGUID("90", data.get("Transformation_ID"), "0", "0"));
			transformation.putProp("DiagramID", data.get("DiagramID"));
			transformation.putProp("ModelType", data.get("ModelType"));
		}
		
		return transformations;
	}

	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Transformation find(ActiveXComponent object) {
		Transformation tf = null;
		
		if (null!=object)
			tf = masterCO.get(object.getProperty("Name"));
		
		if (null==tf)
			tf = new Transformation(object);
		
		return tf;
	}
	
	private void addParameter(CollectionLoader<ModelingObject> co, String name, String value) {
		Parameter parm = new Parameter(null);
		parm.put("name", name.toUpperCase());
		parm.put("value", value);
		co.add(parm);
	}
	
	/**
	 * Set the source table for this Transformation; used in filtering
	 * @param tableName the table name to filter
	 */
	public void setSourceTable(String tableName) {
		sourceEntityName = tableName;
	}
	
	/**
	 * Clear the source table used for filtering
	 */
	public void clearSourceTable() {
		sourceEntityName = null;
	}
	
	@SuppressWarnings("unchecked")
	private void createCustomParameters() {
		CollectionLoader<ModelingObject> co = (CollectionLoader<ModelingObject>) getChildCollection("CUSTOMPARAMETER");
		if (0==co.getList().size()) {
			String data = getPropertyAsString("#Custom Parameters Override#");
			
			if (null!=data) {
				for (String item : data.split(";")) {
					String[] parameter = item.split("=");
					addParameter(co, parameter[0], (1==parameter.length ? "Y" : parameter[1]));
				}
			}
		}
	}

	/**
	 * Get the list of user custom parameters for this Transformation
	 * This removes custom parameters used internally
	 */
	@SuppressWarnings("unchecked")
	public void createFilteredCustomParameters() {
		CollectionLoader<ModelingObject> co = (CollectionLoader<ModelingObject>) getChildCollection("FILTEREDCUSTOMPARAMETER");
		
		for (Parameter param : (ArrayList<Parameter>) getChildren("CUSTOMPARAMETER"))
			switch(param.getPropertyAsString("name")) {
				case "MULTIACTIVESATELLITE" :
				case "NODELETION" :
				case "SELECTQUERYTYPE" :
				case "SELECTQUERYFILTER" :
				case "CUSTOMSOURCEQUERYALIAS" :	
					break;
			default:
				co.add(param);
			}
		
		if (!co.contains("SOURCE_TABLE")) {
			addParameter(co, "SOURCE_TABLE", "SOURCE_TABLE");
		}
		
		if (!co.contains("RECORD_SOURCE")) {
			addParameter(co, "RECORD_SOURCE", Utility.getSourceFromBatch(getPropertyAsString("Name")));
		}
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

		switch(object.toUpperCase()) {
		case "CUSTOMPARAMETER" :
			createCustomParameters();
			Parameter param = (Parameter) getChild("CustomParameter", name);
			if (null!=param)
				value = param.getProperty("value");
			
			break;
		case "SOURCEENTITY" :
			String[] parts = Utility.splitProperty(sourceEntityName);

			switch(name.toUpperCase()) {
			case "OWNER" :
				if (1 < parts.length)
					value = new Variant(parts[0]);
				
				break;
			case "NAME" :
				value = new Variant(parts[parts.length - 1]);
				
				break;
			default:
			}
			
			break;
		default:
			switch(name.toUpperCase()) {
			case "QUALIFIEDNAME":
				value = getVariant(getParent().getName() + ":" + getName());
				
				break;
			case "WORKFLOWNAME":
				String item = getPropertyAsString("#Active Batch Name#");
				if (null==item) item = getName();
				value = getVariant(item);
				
				break;
			case "TEMPLATETYPE" :
				ModelingObject mo = getChildren("LINEAGEOUTPUT").get(0);
				if (mo instanceof Entity) 
					value = getVariant(Utility.getTemplateType(mo.getPropertyAsString("TableName"), getPropertyAsString("#Template Type#")));
	
				break;
			case "DATAFLOW" :
				if (null==dataflow) {
					for (ModelingObject df : getCurrentObject("DIAGRAM").getChildren("DATAFLOW"))
						if (null!=df.getChild("TRANSFORMATION", getName())) {
							dataflow = (DataFlow) df;				
							
							break;
						}
				}
				
				if (null!=dataflow) 
					value = new Variant(dataflow.getName());
				
				break;
			case "SOURCEENTITY" :
				value = new Variant(sourceEntityName);
				
				break;
			case "SOURCETABLES" :
				HashMap<String, String> hm = new HashMap<String, String>();
				Matcher matcher;
				
				String valueS = null;
				
				for (int which = sqlBoundAttachments.length; --which > 0; ) {
					valueS = getPropertyAsString(sqlBoundAttachments[which]);
					
					if (null!=valueS && !"".equals(valueS)) {
						matcher = sqlTablePattern.matcher(valueS.toString());
						while (matcher.find()) {
							String tabl = matcher.group(1).trim().toUpperCase();
							
							if (!tabl.startsWith("("))
								hm.put(tabl, tabl);
						}
					}
				}

				StringBuilder sb = new StringBuilder();
				
				if (0 < hm.size()) {
					for (String v : hm.values())
						sb.append(";").append(v);
					
					value = getVariant(sb.toString().substring(1));
				} else {
					value = null;
				}
				
				break;
			default:
				value = super.getDerivedProperty(object, name);
			}
	
			}
		
		return value;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param name one or more search parameters to locate the object
	 * @return the object if found; otherwise instantiate it and return
	 */
	static public Transformation find(Variant... name) {
		Transformation result = masterCO.get(name);
		
		if (null==result)
			result = (Transformation) CollectionLoader.findGet(Transformation.nullObj.getSelfType(), name);
		
		return result;
	}
	
	private class CustomParameterInit implements CollectionLoaderInit {

		@Override
		public void init() {
			createCustomParameters();
		}
		
	}

	private class FilteredCustomParameterInit implements CollectionLoaderInit {

		@Override
		public void init() {
			createFilteredCustomParameters();
		}
		
	}

}
