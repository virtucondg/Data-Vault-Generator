package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.collection.CollectionLoaderInit;
import com.biogen.generator.ertools.erstudio.collection.ManualCollectionLoader;
import com.biogen.generator.ertools.erstudio.helpers.HelperDictionary;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.biogen.utils.Utility;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This class implements the ER/Studio Attribute object
 * @author drothste
 *
 */
public class Attribute extends ModelingObject {
	
	/** empty object used for testing and initialization */
	static public Attribute nullObj = new Attribute(null);
	private String filterTransformationName = ".";
	private boolean upperCase = false;

	/**
	 * Constructor with an ER/Studio component as a base
	 * @param attribute the ER/Studio object underlying this instance
	 */
	public Attribute (ActiveXComponent attribute) {
		super(attribute);
		setSelfType("ATTRIBUTE");
		
		if (null!=attribute) {
			setName((getPropertyAsBoolean("HasLogicalRoleName") ? getPropertyAsString("LogicalRoleName") : getPropertyAsString("AttributeName")));
			addChild("DataMovementColumnLink", new CollectionLoader<DataMovementColumnLink>(this, "DataMovementColumnLinks", DataMovementColumnLink.nullObj));
			addChild("SourceAttribute", new ManualCollectionLoader<Attribute>(this, Attribute.nullObj, new SourceAttributeInit(), true));
			addChild("FilteredDataMovementColumnLink", new ManualCollectionLoader<DataMovementColumnLink>(this, DataMovementColumnLink.nullObj, new FilteredDmclInit(), true));
		}
	}
	
	@Override
	public Attribute create(ActiveXComponent attribute) {
		Attribute attr = find(attribute);
		
		return attr;
	}
	
	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<Attribute> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		RawDataObject object = objects.get("Attribute");
		CollectionLoader<? extends ModelingObject> cl;
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = null;
		String id;
		object.initialize();
		while (object.hasNext()) {
			HashMap<String, String> data = object.getRecord();
			String rawType = data.get("RawType");
			String parentID; 

			if (rawType.equals("EXCEL")) {
				parentID = data.get("EntityOwner") + "|" + data.get("EntityName");
			} else {
				parentID = (data.get("DiagramID") + "|" + data.get("ModelID") + data.get("Entity_ID"));
			} 
			if (parentID.equals(parentKey)) {
				PseudoActiveXComponent pax = new PseudoActiveXComponent(Attribute.nullObj, data);
				attribute = new Attribute(pax);
				attributes.add(attribute);
				attribute.setParent(parent);
				attribute.setChildManual(true);
				
				if (rawType.equals("EXCEL")) {
					id = parentID + "|" + ("".equals(data.get("LogicalRoleName")) ? data.get("AttributeName") : data.get("LogicalRoleName"));
					attribute.setGUID(data.get("GUID"));
					attribute.putProp("EntityGUID", data.get("EntityGUID"));
					attribute.putProp("EntityOwner", data.get("EntityOwner"));
					attribute.putProp("EntityName", data.get("EntityName"));
					attribute.putProp("TableName", data.get("TableName"));
					attribute.putProp("DomainName", data.get("DomainName"));
					attribute.putProp("AttributeType", data.get("AttributeType"));
					attribute.putProp("Usage", data.get("Usage"));
					attribute.putProp("TableName", data.get("TargetTable"));
					attribute.putProp("DataType", data.get("DataType"));
					attribute.putProp("PrimaryKey", data.get("PrimaryKey"));
					attribute.putProp("ForeignKey", data.get("ForeignKey"));
					attribute.putProp("Rate of Change", data.get("Rate of Change"));
					attribute.putProp("Completeness", data.get("Completeness"));
					attribute.putProp("HasRoleName", ("".equals(data.get("RoleName")) ? "N" : "Y"));
					attribute.putProp("HasLogicalRoleName", ("".equals(data.get("LogicalRoleName")) ? "N" : "Y"));
				} else {
					id = data.get(HelperDictionary.getIdentityColumn("Attribute"));
					attribute.setGUID(HelperDictionary.getGUID(HelperDictionary.getObjectType("Attribute"), id, data.get("Entity_ID"), data.get("ModelID")));
					attribute.putProp("EntityId", data.get("EntityID"));
					attribute.putProp("UserDataTypeId", data.get("UserDataTypeId"));
					attribute.putProp("DomainId", data.get("DomainId"));
					attribute.putProp("DefaultId", data.get("DefaultId"));
					attribute.putProp("DataTypeId", data.get("DataTypeId"));
					attribute.putProp("HasRoleName", ("0".equals(data.get("RoleNameId")) ? "N" : "Y"));
					attribute.putProp("HasLogicalRoleName", ("0".equals(data.get("LogicalRoleNameId")) ? "N" : "Y"));
				}

				if ("N".equals(attribute.getPropertyAsString("HasLogicalRoleName")))
					attribute.setName(HelperDictionary.getString(data, "AttributeName", "AttributeNameId"));
				else
					attribute.setName(HelperDictionary.getString(data, "LogicalRoleName", "LogicalRoleNameId"));
				attribute.setID(id);
				
				attribute.putProp("AttributeName", HelperDictionary.getString(data, "Attribute", "AttributeNameId"));
				attribute.putProp("ColumnName", HelperDictionary.getString(data, "ColumnName", "ColumnNameId"));
				attribute.putProp("Definition", HelperDictionary.getString(data, "Definition", "DefinitionId"));
				attribute.putProp("Note", HelperDictionary.getString(data, "Note", "NoteId"));
				attribute.putProp("RoleName", HelperDictionary.getString(data, "RoleName", "RoleNameId"));
				attribute.putProp("LogicalRoleName", HelperDictionary.getString(data, "LogicalRoleName", "LogicalRoleNameId"));
				attribute.putProp("Nullable", data.get("Nullable"));
				attribute.putProp("Length", data.get("Length"));
				attribute.putProp("Scale", data.get("Scale"));
				attribute.putProp("SequenceNo", data.get("SequenceNo"));
				attribute.putProp("DefaultText", HelperDictionary.getString(data, "DefaultText", "DefaultTextId"));
				attribute.putProp("UserDataType", data.get("UserDataType"));
				attribute.putProp("DefaultName", HelperDictionary.getString(data, "DefaultText", "DefaultNameId"));
			
				cl = attribute.getChildCollection("DATAMOVEMENTCOLUMNLINK");
				if (null!=objects.get("DataMovementColumnLink"))
					cl.addAll(DataMovementColumnLink.create(attribute, objects, id));
			}
		}
		
		return attributes;
	}
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param attribute the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	public Attribute find(ActiveXComponent attribute) {
		return new Attribute(attribute);
	}
	
	@Override
	public String getSortKeyValue() {
		return String.format("%05d", getPropertyAsInt("SequenceNumber"));
	}
	
	/**
	 * Standardize the data type
	 * @return the standardized data type
	 */
	public String getStandardizedDataType() {
		String test = getPropertyAsString("DataType").toUpperCase();
		
		if (test.equals("VARCHAR2") || test.startsWith("NVARCHAR"))
			return "VARCHAR";
		else if (test.equals("CLOB")||test.startsWith("NTEXT"))
			return "TEXT";
		
		return test;
	}
	
	@Override
	public String[] getCollectionKeys() {		
		ArrayList<String> al = new ArrayList<String>();

		if ("Y".equals(getProperty("HasRoleName"))) 
			al.add("RoleName");
		else
			al.add("ColumnName");

		return al.toArray(new String[al.size()]);
	}
			
	/**
	 * Create the collection of source attributes for this object
	 */
	@SuppressWarnings("unchecked")
	private void createSourceAttribute() {
		CollectionLoader<ModelingObject> cl = (CollectionLoader<ModelingObject>) getChildCollection("SOURCEATTRIBUTE");
		
		try {
			for (DataMovementColumnLink dmcl : (ArrayList<DataMovementColumnLink>) getChildren("DataMovementColumnLink")) {
				cl.add((Attribute)dmcl.getModelingObject("SOURCETARGETCOLUMNOBJECT"));
			}

			cl.sort();
		} catch (Exception ignored) {}
	}

	/**
	 * Generate a collection of {@link DataMovementColumnLink}s that are filtered by the current {@link Transformation} object
	 */
	@SuppressWarnings("unchecked")
	private void createFilteredDmcl() {
		CollectionLoader<ModelingObject> cl = (CollectionLoader<ModelingObject>) getChildCollection("FilteredDataMovementColumnLink");
		ArrayList<String> lineageInputs = new ArrayList<String>();

		try {
			ModelingObject tf = getCurrentObject("TRANSFORMATION");
			if (null==tf && !filterTransformationName.equals("")) {
				filterTransformationName = "";
				cl.clear();
			} else if (!filterTransformationName.equals(tf.getPropertyAsString("Name"))) {
				cl.clear();
	
				ArrayList<DataMovementColumnLink> dmcl = (ArrayList<DataMovementColumnLink>) getChildren("DATAMOVEMENTCOLUMNLINK");
	
				if (0 < dmcl.size()) {
					for (ModelingObject tff : tf.getChildren("LineageInput")) 
						lineageInputs.add(tff.getPropertyAsString("LINEAGEINPUT.SourceObjectID"));
						
					for (DataMovementColumnLink dm : dmcl) 
						if (lineageInputs.contains(dm.getPropertyAsString("DATAMOVEMENTCOLUMNLINK.$SourceAttribute.ID")))  
							cl.add(dm);
				}
				
				cl.sort();
			}
		} catch (Exception ignored) {}
	}
	
	@Override
	public Variant getDerivedProperty(String object, String name) {
		Variant value = null;
		String item;
		String sdtl = "";
		
		StringBuilder sb = new StringBuilder();
		boolean postProcess = false;
		boolean concatenate = false;
		String concatenator = System.getProperty("line.separator");;
		String md5Code = "";
		
		switch(object) {
		case "CONCATENATEDSOURCETABLES" :
			concatenate = true;
			concatenator = Utility.getLanguageOf("MD5_TOKEN");
		case "SOURCETABLES":
			for (ModelingObject dmcl : getChildren("FilteredDataMovementColumnLink")) {
				item = dmcl.getPropertyAsString("DATAMOVEMENTCOLUMNLINK.$SOURCEENTITY." + name);
				if (null==item) item = "";
				sb.append(concatenator).append(item);
			}
			
			if (sb.length() > concatenator.length())
				value = getVariant(sb.toString().substring(concatenator.length()));

			break;
		case "CONCATENATEDSOURCECOLUMNS" :
			concatenate = true;
			concatenator = Utility.getLanguageOf("MD5_TOKEN");
		case "SOURCECOLUMNS":
			sdtl = getPropertyAsString("SourceDirectTransformationLogic");
			if (upperCase = sdtl.startsWith("^")) 
				sdtl = sdtl.substring(1);
			
			if (concatenate) {
				String[] cols = new String[0];
				
				if (sdtl.startsWith("MD5")) {
					md5Code = sdtl;
					int cp = sdtl.indexOf(":");
			
					if (0 < cp) {
						md5Code = sdtl.substring(0,  cp);
						cols = sdtl.substring(cp + 1).split(",");
						
						for (int i = 0; i < cols.length; i++)
							if (cols[i].equals("$$RECORD_SOURCE$$")) {
								md5Code = "MD5RS";
								cols[i] = "";
								break;
							}
					}
					
					sb.append(concatenator).append(Utility.getLanguageOf(md5Code));
					StringBuilder sb2 = new StringBuilder();
					for (String col : cols) {
						if (0 < col.length())
							sb2.append(concatenator).append(col);
					}
					if (0==sb2.length())
						sb.append("''");
					else 
						sb.append(sb2.toString().substring(concatenator.length()));
				}
			}
			
			for (ModelingObject dmcl : getChildren("FilteredDataMovementColumnLink")) {
				item = dmcl.getPropertyAsString("DATAMOVEMENTCOLUMNLINK.$SOURCEATTRIBUTE." + name);
				if (null==item) item = "";
				sb.append(concatenator).append(item);
			}		

			if (concatenate) {
				if (0==sb.length()) {
					if (0 < sdtl.length())
						sb.append(concatenator).append(sdtl);
					else
						sb.append(concatenator).append("No Mapping");
				} else {
					if (0 < md5Code.length())
						sb.append(")");
				}
			} else {
				if ((0==sb.length()) && (0 < sdtl.length()) && name.toUpperCase().contains("DATATYPE"))
					sb.append(concatenator).append("Derived Column");
			}
			
			if (sb.length() >= concatenator.length())
				value = getVariant(sb.toString().substring(concatenator.length()).replace("''" + concatenator, ""));
			
			upperCase = false;
			
			break;
		default:
			switch(name.toUpperCase()) {
			case "DATALENGTHSCALE" :
				value = getDerivedProperty("", "DataLength");
				Variant v2 = getDerivedProperty("", "DataScale");
				if (null != value) {
					if (null != v2) {
						item = value.toString() + "," + v2.toString();
						value = getVariant(item);
					}
				}
				
				break;
			case "DATALENGTH" :
			case "DATASCALE" :
				value = getProperty(name);
				if (value.getInt() < 0)
					value = null;
				
				break;
			case "ATTRIBUTENAME" :
				value = getProperty("HasLogicalRoleName");
				if (HelperDictionary.isTrue(value))
					value = getProperty("LogicalRoleName");
				else
					value = getProperty("AttributeName");
				
				break;
			case "FORMATTEDCOLUMNNAME" :
				postProcess=true;
				
			case "COLUMNNAME" :
				item = getPropertyAsString("HasRoleName");
				if ("true".equals(item))
					item = getPropertyAsString("RoleName");
				else
					item = getPropertyAsString("ColumnName");
				
				if (postProcess) {
					switch (Utility.getDataTypeClass(getPropertyAsString("DataType"))) {
					case "DATE" :
						item = "TO_CHAR(" + item + ", 'DD-MON-YYYY')";
						break;
					case "TIME" :
						item = "TO_CHAR(" + item + ", 'DD-MON-YYYY HH24:MI:SS')";
						break;
					case "LOB" :
						item = Utility.getSchemaReference("DH_ABC") + ".WORKFLOW_PROCESSING_PKG.FNC_GET_HASH_VAL_CLOB(" + item + ")";
						break;
					case "NUMERIC" :
						item = "TO_CHAR(" + item + ")";
						break;
					case "CHAR" :
						if (getPropertyAsString("DataType").equals("NVARCHAR"))
							if (upperCase)
								item = "CAST(UPPER(" + item + ") AS VARCHAR2(" + getPropertyAsString("DataLength") + " CHAR))";
							else
								item = "CAST(" + item + " AS VARCHAR2(" + getPropertyAsString("DataLength") + " CHAR))";
						else
							if (upperCase) item = "UCASE(" + item + ")";
					}					
				}
				
				value = getVariant(item);
				
				break;
			case "LOGICALROLENAME" :
				value = getProperty("HasLogicalRoleName");
				if (HelperDictionary.isTrue(value))
					value = getProperty("LogicalRoleName");
				else
					value = null;

				break;
			case "ROLENAME" :
				value = getProperty("HasRoleName");
				if (HelperDictionary.isTrue(value))
					value = getProperty("RoleName");
				else
					value = null;

				break;
			case "DOMAINNAME": 
				Domain dom = Domain.find(getProperty("DomainID"));
				if (null!=dom)
					value = dom.getProperty("Name");
				
				break;
			case "NULLABBLE" :
				String boValue = getPropertyAsString("#Not Null DQM");
				if ("Y".equals(boValue))
					value = getVariant(boValue);

				break;
			case "NULLOPTION" :
				if ("NULL".equals(getPropertyAsString("NULLOPTION")))
					value = getVariant("Y");
				else
					value = getVariant("N");
				
				break;
			case "DATATYPE" :
				value = getVariant(getStandardizedDataType());
				
				break;
			default:
				value = super.getDerivedProperty(object, name);
			}
		}
		
		return value;
	}
	
	
	private class SourceAttributeInit implements CollectionLoaderInit {

		@Override
		public void init() {
			createSourceAttribute();
		}
		
	}

	private class FilteredDmclInit implements CollectionLoaderInit {

		@Override
		public void init() {
			createFilteredDmcl();
		}
		
	}

}
