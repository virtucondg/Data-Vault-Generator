package com.biogen.generator.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.biogen.generator.ertools.erstudio.Application;
import com.biogen.generator.ertools.erstudio.BusinessDataObject;
import com.biogen.generator.ertools.erstudio.DataMovementColumnLink;
import com.biogen.generator.ertools.erstudio.Entity;
import com.biogen.generator.ertools.erstudio.ModelingObject;
import com.biogen.generator.ertools.erstudio.Transformation;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.io.output.Output;
import com.biogen.utils.Debug;
import com.biogen.utils.Utility;

/**
 * This class is the parent of generator operations to execute.  These commands come from the template files, and there is 
 * one subclass for each element type supported in templates.
 * @author drothste
 * 
 * <p>
 * </div>
 *<h3>Tokenized Strings</h3>
 *<div class="block">
 * Some of the values can be tokenized.  This allows them to be customized by values in the ER/Studio diagram.  Parameters to be replaced 
 * are delineated by curly braces “{}”.  In addition to the standard ER/Studio object parameters there are additional custom parameters unique 
 * to each object.  Custom parameters are enclosed by dollar signs “$”.
 * <p>
 * For example: <code>STTM_{DIAGRAM.$FILENAME$}_{SUBMODEL.Name}_{APP.DATE}.xlsx</code> <p>
 * This will take “STTM_”, append the filename, append “_”, append the submodel name, append “_”, append today’s date in YYYYMMDD format, and 
 * finally append “.xlsx”.  So if you were in IDF/DI Layer – CRM the value would be “STTM_International Data Hub_DI Layer - CRM_20190621.xlsx”.
 * <p>
 * If there is only one token for a value calculation then the curly braces can be omitted (<code>ENTITY.Note</code> or <code>{ENTITY.Note}</code> are treated the same).
 * <p>
 * <!-- code-readable version
 * Special Character	Description
 * 		:				This allows you to set a maximum size of returned data
 *						Ex. you need to take the table name but create it as a PK index name.  Oracle has a maximum of 30 characters, 
 *						so {ENTITY.TableName:27}_PK</code> would guarantee legal index names.
 *		$				This specifies custom parameters for an object.  These usually are transformations of standard ER/Studio paraemters.  The custom parameter 
 *						needs to be bracketed by ‘$” so the parser can recognize the custom portion.
 *						Ex.{DIAGRAM.FileName} gives you the actual document filename, including the extension.  That’s a problem when you want to use it for another filename.  
 *						So {DIAGRAM.$FileName$} returns the filename without an extension.
 *						Ex.{DATAMOVEMENTCOLUMNLINK.$SOURCEENTITY.Owner$} finds the source entity for the current DataMovementColumnLink object and 
 *						returns its owner parameter.
 *		#				This specifies a bound attachment column instead of an actual property. # must bracket the bound attachment column name
 *						Ex. {ENTITY.#Workflow Name#} will return the value of the attachment “Workflow Name” from the current Entity object.
 *	  [[s,t]]			This will replace a character or string with another character or string.
 *						Ex. {TRANSFORMATION.$SOURCEENTITY$[[\.,/]]} will take the source entity value for the transformation (say DI_PS.DI_ACCOUNT_1_SAT) and replace 
 *						all “.” with “/”, returning DI_PS/DI_ACCOUNT_1_SAT
 * 
 * HTML-formatted text below:
 * -->
 * </div>
 *<ul class="blockList">
 *<li class="blockList">
 *<h3>Special Characters</h3>
 *<table class="tokenizedSpecialCharacters" border="0" cellpadding="3" cellspacing="0" summary="Special Characters for Tokenized Strings, listing characters, and an explanation">
 *<tr><th>Special Character</th><th>Description</th></tr>
 *<tr class="rowColor" style="text-align:center"><td><b>:</b></td><td style="text-align:left">This allows you to set a maximum size of returned data<p>
 *Ex. you need to take the table name but create it as a PK index name.  Oracle has a maximum of 30 characters, so <code>{ENTITY.TableName:27}_PK</code> would guarantee legal index names.</td></tr>
 *<tr class="altColor" style="text-align:center"><td>$</td><td style="text-align:left">This specifies custom parameters for an object.  These usually are transformations of standard ER/Studio paraemters.  The custom parameter 
 *needs to be bracketed by ‘$” so the parser can recognize the custom portion.<p>
 *Ex.<code>{DIAGRAM.FileName}</code> gives you the actual document filename, including the extension.  That’s a problem when you want to use it for another filename.  
 *So <code>{DIAGRAM.$FileName$}</code> returns the filename without an extension.<p>
 *Ex. <code>{DATAMOVEMENTCOLUMNLINK.$SOURCEENTITY.Owner$}</code> finds the source entity for the current {@link com.biogen.generator.ertools.erstudio.DataMovementColumnLink DataMovementColumnLink}
 * object and returns its owner parameter.</td></tr>
 *<tr class="rowColor" style="text-align:center"><td>#</td><td style="text-align:left">This specifies a bound attachment column instead of an actual property. # must bracket the bound attachment column name<p>
 *Ex. <code>{ENTITY.#Workflow Name#}</code> will return the value of the attachment “Workflow Name” from the current {@link com.biogen.generator.ertools.erstudio.Entity Entity} object.</td></tr>
 *<tr class="altColor" style="text-align:center"><td>[[s,t]]</td><td style="text-align:left">This will replace a character or string with another character or string.<p>
 *Ex. <code>{TRANSFORMATION.$SOURCEENTITY$[[\.,/]]}</code> will take the source entity value for the transformation (say DI_PS.DI_ACCOUNT_1_SAT) and replace all “.” with “/”, 
 *returning DI_PS/DI_ACCOUNT_1_SAT</td></tr>
 *</table></li></ul>
 *<div class="summary">
 *
 */
abstract public class Operation {
	private ArrayList<Operation> operationList = new ArrayList<Operation>();
	private HashMap<String, String> parameters = new HashMap<String, String>();
	static private HashMap<String, String> sharedParameters = new HashMap<String, String>();
	/** the ER/Studio application instance */
	static Application app = Application.getInstance();

	private Iterator<Operation> it = null;
	private Operation parent = null;
	static private ModelingObject currentObject = null;
	static private HashMap<String, ArrayList<? extends ModelingObject>> currentModelingObjects = new HashMap<String, ArrayList<? extends ModelingObject>>();
	/** the current text format */
	static String textFormat = null;
	/** the current header text format */
	static String headerTextFormat = null;
	
	private String priorDistinct;
	private String priorSorted;
	
	/**
	 * Constructor
	 */
	public Operation() {
	}
	
	/** 
	 * Set the parent Operation of this operation.  This corresponds to the XML template hierarchy.
	 * @param parent the parent Operation
	 */
	public void setParent(Operation parent) {
		this.parent = parent;
		parent.addOperation(this);
	}
	
	/**
	 * Get this Operation's parent
	 * @return the parent Operation
	 */
	public Operation getParent() {
		return this.parent;
	}
	
	/** 
	 * Set the list of current {@link ModelingObject}s
	 * @param name the object type (Ex. ENTITY)
	 * @param arrayList the list of current objects of this type
	 */
	public void putCurrentObjectsList(String name, ArrayList<? extends ModelingObject> arrayList) {
		currentModelingObjects.put(name.toUpperCase(), arrayList);
	}
	
	/**
	 * Get the list of current objects of a type
	 * @param name the object type (Ex. ENTITY)
	 * @return the list of current objects of this type
	 */
	public ArrayList<? extends ModelingObject> getCurrentObjectsList(String name) {
		return currentModelingObjects.get(name.toUpperCase());
	}
	
	/**
	 * Get the current {@link ModelingObject} being operated on
	 * @return the current {@link ModelingObject}
	 */
	public ModelingObject getCurrentObject() {
		return currentObject;
	}
	
	/** Load an array of properties for this Operation
	 * @param properties the array of properties to load
	 */
	public void loadProperties(String[] properties) {
		for (int i = properties.length; i-- > 0; ) {
			String[] prop = properties[i].split("=", 2);
			if (2==prop.length)
				putProperty(prop[0], prop[1]);
		}
	}
	
	/**
	 * Set a property for this Operation
	 * @param key the name of the property
	 * @param value the property value
	 */
	public void putProperty(String key, String value) {
		parameters.put(key, value);
	}
	
	/**
	 * Get a property
	 * @param key the name of the property
	 * @return the property value
	 */
	public String getProperty(String key) {
		String value = parameters.get(key);
		if (null==value) 
			value = sharedParameters.get(key);

		return value;
	}

	/**
	 * Get a property.  If the property is not set then return a default value.
	 * @param key the name of the property
	 * @param defaultValue the default value if the property is <code>NULL</code>
	 * @return the property value
	 */
	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		
		return (null==value ? defaultValue : value);
	}
	
	/**
	 * Get a property, replacing tokens with their actual values
	 * @param key the name of the property
	 * @return the property value
	 */
	public String getTokenProperty(String key) {
		String value = getProperty(key);
		
		if (null!=value)
			if (!value.startsWith("!")) {
				if (value.contains("{")) // || value.contains("[")
					value = parseTokens(value);
				else value = parseTokens("{" + value + "}");
			} else {
				value = value.substring(1);
			}
			
		return value;
	}
	
	/**
	 * Get a property as an number.
	 * @param key the name of the property
	 * @return the property value
	 */
	public int getPropertyAsInt(String key) {
		String value = getProperty(key);
		
		if (null!=value)
			return Integer.valueOf(value);
		else
			return -1;
	}
	
	/** 
	 * Set a shared property.  These property values are shared across all Operations.
	 * @param key the name of the property
	 * @param value the property value
	 */
	public void putSharedProperty(String key, String value) {
		if (null==value)
			sharedParameters.remove(key);
		else
			sharedParameters.put(key, value);
	}
	
	/**
	 * Get a shared property
	 * @param key the name of the property
	 * @return the property value
	 */
	public String getSharedProperty(String key) {
		String value = sharedParameters.get(key);

		return value;
	}
	
	/**
	 * Get the map of all properties
	 * @return the Map of properties
	 */
	public HashMap<String, String> getProperties() {
		return parameters;
	}
	
	/**
	 * Parse a string and replace tokens with their actual values
	 * @param data the string to parse
	 * @return the parsed string
	 */
	String parseTokens(String data) {
		return parseTokens(data, true);
	}
	
	/**
	 * Parse a string and replace tokens with their actual values
	 * @param data the string to parse
	 * @param defaultOps if <code>TRUE</code> and the token is not found put the token entry in the string; if <code>FALSE</code> put "" in instead
	 * @return the parsed string
	 */
	String parseTokens(String data, boolean defaultOps) {
		StringBuilder result = new StringBuilder();
		String[] components = data.split("\\{|\\}");
		
		for (int i = 0; i < components.length; i++) {
			if (i%2==0)
				result.append(components[i]);
				else {
					String[] fw = components[i].split(":");
					String[] tok = Utility.splitProperty(fw[0]);
					ModelingObject mo = ModelingObject.getCurrentObject(tok[0]);
					
					if (null!=mo) {
						String value = mo.getPropertyAsString(fw[0]);
						if (1 < fw.length) {
							int len = Integer.parseInt(fw[1]);
							if (len < value.length())
								value = value.substring(0,  len);
						}
						if (null!=value)	
							result.append(value);
					} else {
						if (defaultOps)
							result.append(components[i]);
					}
				}
		}
		
		return result.toString();
	}
	
	/**
	 * If the position needs to be changed in the output move to the new position
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to move the position in
	 */
	public void moveIfNeeded(Output output) {
		int row = getPropertyAsInt("row");
		int col = getPropertyAsInt("col");
		if (-2 < row + col)
			output.moveTo(row, col);
	}
	
	/**
	 * Get the hyperlink value
	 * @return the hyperlink value, or <code>NULL</code> if none
	 */
	public String getHyperlink() {
		String hyperlink = getTokenProperty("hyperlink");
		if ((null!=hyperlink) && (null!=getCurrentObject())) {
			hyperlink = parseTokens(hyperlink);
		} else
			hyperlink = null;

		return hyperlink;
	}
	
	/**
	 * Execute the Operation
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to execute into
	 */
	@SuppressWarnings("unchecked")
	public void executeOperation(Output output) {
		ArrayList<ModelingObject> mos = new ArrayList<ModelingObject>();
		ArrayList<ModelingObject> storage = new ArrayList<ModelingObject>();
		String objects = getProperty("objects");
		String currentTF = textFormat;
		String currentHTF = headerTextFormat;
		String validation = "{" + getProperty("validation", "") + "}";
		
		textFormat = getProperty("textFormat", textFormat);
		headerTextFormat = getProperty("headerTextFormat", headerTextFormat);
				
		if (null==objects) {
			loopChildOperation(output);
		} else {
			executeObjectPreLoop(output);

			switch(objects.toUpperCase()) {
			case "BUSINESSDATAOBJECT" :
				storage = (ArrayList<ModelingObject>) getCurrentObjectsList("ENTITY");
				
				for (ModelingObject bdo : getCurrentObjectsList("BUSINESSDATAOBJECT")) {
					{
						BusinessDataObject bdo1 = (BusinessDataObject) bdo;
						bdo1.setCurrentObject();
						
						currentObject = bdo;
						Debug.println("-->BusinessDataObject:" + bdo1.getName(), true);
						mos.clear();
						
						for (ModelingObject ent : bdo1.getReferencedObjects()) {
							if (storage.contains(ent))
								mos.add(ent);
						}
					
						putCurrentObjectsList("ENTITY", mos);
						
						if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
							loopChildOperation(output);
						bdo.clearCurrentObject();
				    }
				}
				
				putCurrentObjectsList("ENTITY", storage);

				break;
			case "ATTRIBUTE" :
				if (null==Entity.getCurrentObject("ENTITY")) {
					for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
						ent.setCurrentObject();
						ent.resetSequence();
						Debug.println("-->Entity:" + ent.getName(), true);
	
						for (ModelingObject attr : ent.getChildren("ATTRIBUTE")) {
							attr.setCurrentObject();
							currentObject = attr;
							if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
								loopChildOperation(output);
							attr.clearCurrentObject();
							
							ent.nextSequence();
						}
						ent.clearCurrentObject();
					}
				} else {
					ModelingObject ent = Entity.getCurrentObject("ENTITY");
					Debug.println("-->Entity:" + ent.getName(), true);

					for (ModelingObject attr : ent.getChildren("ATTRIBUTE")) {
						Debug.println("\t-->Attribute:" + attr.getName(), true);
						attr.setCurrentObject();
						currentObject = attr;
						if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
							loopChildOperation(output);
						attr.clearCurrentObject();
					}
				}
				
				break;
			case "ENTITY" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					Debug.println("-->Entity:" + ent.getName(), true);
					ent.setCurrentObject();
					currentObject = ent;
					if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
						loopChildOperation(output);
					ent.clearCurrentObject();
				}
				
				break;
			case "INDEX" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);

					for (ModelingObject idx : ent.getChildren("INDEX")) {
						idx.setCurrentObject();
						currentObject = idx;
						if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
							loopChildOperation(output);
						idx.clearCurrentObject();
						
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
							
				break;
			case "INDEXCOLUMN" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject idx : ent.getChildren("INDEX")) { 
						idx.setCurrentObject();
						idx.resetSequence();
						Debug.println("\t-->Index:" + idx.getName(), true);

						for (ModelingObject idxc : idx.getChildren("INDEXCOLUMN")) {
							idxc.setCurrentObject();
							currentObject = idxc;
							if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
								loopChildOperation(output);
							idxc.clearCurrentObject();
							
							ent.nextSequence();
						}
						idx.clearCurrentObject();
					}
					ent.clearCurrentObject();
				}
				break;
			case "CHILDFKCOLUMN" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.nextSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject rel : ent.getChildren("CHILDRELATIONSHIP")) { 
						rel.setCurrentObject();
						rel.nextSequence();
						Debug.println("\t-->Relationship:" + rel.getName(), true);
						
						for (ModelingObject fkc : rel.getChildren("FKCOLUMN")) {
							fkc.setCurrentObject();
							currentObject = fkc;
							if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
								loopChildOperation(output);
							fkc.clearCurrentObject();
							
							rel.nextSequence();
						}

						ent.nextSequence();
					}
				}
				break;
			case "PARENTFKCOLUMN" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject rel : ent.getChildren("PARENTRELATIONSHIP")) { 
						rel.setCurrentObject();
						rel.resetSequence();
						Debug.println("\t-->Relationship:" + rel.getName(), true);

						for (ModelingObject fkc : rel.getChildren("FKCOLUMN")) {
							fkc.setCurrentObject();
							currentObject = fkc;
							if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
								loopChildOperation(output);
							fkc.clearCurrentObject();
							
							rel.nextSequence();
						}
						rel.clearCurrentObject();
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
				break;
			case "CHILDRELATIONSHIP" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject rel : ent.getChildren("CHILDRELATIONSHIP")) {
						rel.setCurrentObject();
						currentObject = rel;
						if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
							loopChildOperation(output);
						rel.clearCurrentObject();
						
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
							
				break;
			case "PARENTRELATIONSHIP" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
	
					for (ModelingObject rel : ent.getChildren("PARENTRELATIONSHIP")) {
						rel.setCurrentObject();
						currentObject = rel;
						if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
							loopChildOperation(output);
						rel.clearCurrentObject();
						
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
							
				break;
			case "TRANSFORMATION" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject tf : ent.getChildren("TRANSFORMATION")) {
						tf.setCurrentObject();
						currentObject = tf;
						if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
							loopChildOperation(output);
						tf.clearCurrentObject();
						
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
				
				break;
			case "TRANSFORMATION/ENTITY" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject tf : ent.getChildren("TRANSFORMATION")) {
						tf.setCurrentObject();
						currentObject = tf;
						
						String sourceTableList = tf.getPropertyAsString("TRANSFORMATION.$SOURCETABLES$");
						
						if (null != sourceTableList) {
							for (String table : sourceTableList.split(";")) {
								if (0 < table.length()) {
									((Transformation) tf).setSourceTable(table);
									if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
										loopChildOperation(output);
								}
							}
							((Transformation) tf).clearSourceTable();
						}
						
						tf.clearCurrentObject();
						
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
				
				break;
			case "CUSTOMPARAMETER" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject tf : ent.getChildren("TRANSFORMATION")) {
						tf.setCurrentObject();
						tf.resetSequence();
						Debug.println("\t-->Transformation:" + tf.getName(), true);

						for (ModelingObject cp : tf.getChildren("FILTEREDCUSTOMPARAMETER")) {
							cp.setCurrentObject();
							currentObject = cp;
							if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
								loopChildOperation(output);
							
							tf.nextSequence();
						}
						tf.clearCurrentObject();
						
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
				
				break;
			case "DATAMOVEMENTCOLUMNLINK" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject tf : ent.getChildren("TRANSFORMATION")) {
						tf.setCurrentObject();
						tf.resetSequence();
						Debug.println("\t-->Transformation:" + tf.getName(), true);

						for (ModelingObject attr : ent.getChildren("ATTRIBUTE")) {
							attr.setCurrentObject();
							ArrayList<DataMovementColumnLink> dmcl = (ArrayList<DataMovementColumnLink>) attr.getChildren("FilteredDataMovementColumnLink");
							attr.resetSequence();
							Debug.println("\t\t-->Attribute:" + attr.getName(), true);
						
							if (0==dmcl.size()) {
								if (!"".equals(attr.getPropertyAsString("SourceDirectTransformationLogic"))) {
									currentObject = new DataMovementColumnLink(null);
									currentObject.setParent(attr);
									currentObject.setCurrentObject();
									if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
										loopChildOperation(output);
									currentObject.clearCurrentObject();
									
									attr.nextSequence();
								}
							} else {
								for (ModelingObject dm : dmcl) {
									dm.setCurrentObject();
									currentObject = dm;
									if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
										loopChildOperation(output);
									dm.clearCurrentObject();
									attr.nextSequence();
								}
							}
							attr.clearCurrentObject();
							tf.nextSequence();
						}
						tf.clearCurrentObject();
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
				
				break;
			case "DATAMOVEMENTCOLUMNLINK_VAULT" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject attr : ent.getChildren("ATTRIBUTE")) {
						attr.setCurrentObject();
						ArrayList<DataMovementColumnLink> dmcl = (ArrayList<DataMovementColumnLink>) attr.getChildren("DataMovementColumnLink");
						attr.resetSequence();
						Debug.println("\t\t-->Attribute:" + attr.getName(), true);
					
						if (0==dmcl.size()) {
							if (null!=attr.getPropertyAsString("SourceDirectTransformationLogic") && (0 < attr.getPropertyAsString("SourceDirectTransformationLogic").length())) {
								HashMap<String, String> data = new HashMap<String, String>();
								data.put("QualifiedName", ent.getPropertyAsString("Workflow Name"));
								PseudoActiveXComponent psx = new PseudoActiveXComponent(DataMovementColumnLink.nullObj, data);
								currentObject = new DataMovementColumnLink(psx);
								currentObject.setParent(attr);
								currentObject.setCurrentObject();
								if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
									loopChildOperation(output);
								currentObject.clearCurrentObject();
								
								attr.nextSequence();
							}
						} else {
							for (ModelingObject dm : dmcl) {
								dm.setCurrentObject();
								currentObject = dm;
								if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
									loopChildOperation(output);
								dm.clearCurrentObject();
								attr.nextSequence();
							}
						}
						attr.clearCurrentObject();
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
				
				break;
			case "DATAMOVEMENTCOLUMNLINK/ENTITY" :
				for (ModelingObject ent : getCurrentObjectsList("ENTITY")) {
					ent.setCurrentObject();
					ent.resetSequence();
					Debug.println("-->Entity:" + ent.getName(), true);
					
					for (ModelingObject tf : ent.getChildren("TRANSFORMATION")) {
						tf.setCurrentObject();
						tf.resetSequence();
						Debug.println("\t-->Transformation:" + tf.getName(), true);
						HashMap<String, String> hm = new HashMap<String, String>();

						for (ModelingObject attr : ent.getChildren("ATTRIBUTE")) {
							attr.setCurrentObject();
							ArrayList<DataMovementColumnLink> dmcl = (ArrayList<DataMovementColumnLink>) attr.getChildren("FilteredDataMovementColumnLink");
							attr.resetSequence();
							Debug.println("\t\t-->Attribute:" + attr.getName(), true);
						
							for (ModelingObject dm : dmcl) {
								dm.setCurrentObject();
								currentObject = dm;
								String srcTable = dm.getPropertyAsString("DATAMOVEMENTCOLUMNLINK.$SOURCEENTITY.Owner$") + "." + dm.getPropertyAsString("DATAMOVEMENTCOLUMNLINK.$SOURCEENTITY.TableName$");
								if (null==hm.get(srcTable.toUpperCase())) {
									hm.put(srcTable.toUpperCase(), srcTable);
									if (("{}".equals(validation)) || (0 < parseTokens(validation, false).length())) 
										loopChildOperation(output);
								}
								dm.clearCurrentObject();
								attr.nextSequence();
							}
							attr.clearCurrentObject();
							tf.nextSequence();
						}
						tf.clearCurrentObject();
						ent.nextSequence();
					}
					ent.clearCurrentObject();
				}
				
				break;
			default:
			}
			
			executeObjectPostLoop(output);
		}
		
		textFormat = currentTF;
		headerTextFormat = currentHTF;
	}
	
	/**
	 * Execute the Operation
	 * @param function the function to execute
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to execute into
	 */
	public void executeOperation(String function, Output output) {
		loopChildOperation(function, output);
	}

	/**
	 * Tasks to perform before looping the child Operations
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to execute into
	 */
	void executeObjectPreLoop(Output output) {	
	}
	
	/**
	 * Tasks to perform after looping the child Operations
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to execute into
	 */
	void executeObjectPostLoop(Output output) {	
	}
	
	/**
	 * Execute all child Operations of this Operation
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to execute into
	 */
	void loopChildOperation(Output output) {
		for (Operation op: getOperationList())
			if (app.hasFilter(op.getProperty("filter")))
					op.executeOperation(output);	
	}
	
	/**
	 * Execute all child Operations of this Operation
	 * @param function the function to execute
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to execute into
	 */
	void loopChildOperation(String function, Output output) {
		for (Operation op: getOperationList())
			if (app.hasFilter(op.getProperty("filter")))
				op.executeOperation(function, output);	
	}
	
	/**
	 * Add to the list of child Operations
	 * @param operation the Operation to add
	 */
	public void addOperation(Operation operation) {
		operationList.add(operation);
	}
	
	/**
	 * Get the list of child Operations
	 * @return the list of child operations
	 */
	public ArrayList<Operation> getOperationList() {
		return operationList;
	}
	
	/**
	 * Get the next child operation to process
	 * @return the next child Operation
	 */
	public Operation getNextOperation() {
		Operation op;
		
		if (null==it)
			it = operationList.iterator();
		
		op = (Operation)(it.next());
		
		if (!it.hasNext()) 
			it = null;
		
		return op;
	}
	
	/**
	 * Find an operation of a given type
	 * @param operationType the Operation type to find
	 * @param name the name of the Operation to find
	 * @return the Operation if found; <code>NULL</code> otherwise
	 */
	public Operation findOperation(String operationType, String name) {
		Operation operation = null;

		if (getOperationType().equalsIgnoreCase(operationType) && name.equalsIgnoreCase(getProperty("name")))
			operation = this;
		else
			for (Operation op : operationList) {
				if (null==operation) 
					operation = op.findOperation(operationType, name);
			}

		return operation;
	}
	
	/**
	 * Get the type of this Operation.  This must be overridden in subclasses.
	 * @return the type
	 */
	abstract public String getOperationType();
	
	/**
	 * Output this Operation as an XML element with children
	 * @return the XML of this Operation
	 */
	public String dumpXML() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<").append(getOperationType());

		Iterator<Map.Entry<String, String>> it = parameters.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> me = (Map.Entry<String, String>)it.next();
	        sb.append(" " + me.getKey() + "=\"" + me.getValue() + "\"");
	    }
	    
		if (0==operationList.size()) {
			sb.append("/>\n");
		} else {
			sb.append(">\n");
			
			for (Operation op : operationList) {
				sb.append(op.dumpXML());
			}
			sb.append("</" + getOperationType() + ">\n");
		}
		return sb.toString();
	}
	
	/**
	 * Parse a string
	 * @param data the string to parse
	 * @return the parsed string
	 */
	public String parse(String data) {
		String value = data.replace("$", "");
		
		return value;
	}
	
	/**
	 * Propagate values from this Operation to an active {@link com.biogen.generator.io.output.Output Output} 
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to set parameters in
	 * @param distinct the distinct rows value to push
	 * @param sorted the sorted rows value to push
	 */
	public void pushOutputCodes(Output output, String distinct, String sorted) {
		this.priorDistinct = output.getDistinctRows();
		this.priorSorted = output.getSortedRows();
		
		output.setDistinctRows(distinct);
		output.setSortedRows(sorted);
	}
	
	/**
	 * Reset prior values to the {@link com.biogen.generator.io.output.Output Output}
	 * @param output the {@link com.biogen.generator.io.output.Output Output} to set old parameters in
	 */
	public void popOutputCodes(Output output) {
		output.setDistinctRows(this.priorDistinct);
		output.setSortedRows(this.priorSorted);
	}
	
	/**
	 * Can this Operation have children?
	 * @return <code>TRUE</code>/<code>FALSE</code>
	 */
	public boolean canHaveChildren() {
		return true;
	}
			
}
