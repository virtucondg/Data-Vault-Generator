package com.biogen.generator.ertools.erstudio;

import java.util.ArrayList;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.collection.CollectionLoader;
import com.biogen.generator.ertools.erstudio.raw.PseudoActiveXComponent;
import com.biogen.generator.ertools.erstudio.raw.RawDataObject;
import com.biogen.utils.Utility;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Variant;

/**
 * The abstract class that all other ER/Studio objects are descended from.
 * @author drothste
 *
 */
abstract public class ModelingObject {
	/** empty object used for testing and initialization */
	static public ModelingObject nullObj = null;
	private String selfType = "MODELINGOBJECT";
	
	static private String NULL = "$$NULL\n\n\t\t$$";
	static private HashMap<String, ModelingObject> currentObjects = new HashMap<String, ModelingObject>();
	static private int sequenceGenerator = 0;
	
	private ActiveXComponent object;
	private HashMap<String, String> properties = new HashMap<String, String>();
	private HashMap<String, String> headerColumns = new HashMap<String, String>();
	private String sortKey;
	private ModelingObject parent;
	private String name;
	private String id;
	private String guid;
	private int sequence;
	private boolean realActiveXObject = true;
	
	private HashMap<String, CollectionLoader<? extends ModelingObject>> children = new HashMap<String, CollectionLoader<? extends ModelingObject>>();
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param object the ER/Studio object underlying this instance
	 */
	public ModelingObject (ActiveXComponent object) {
		this(object, true);
	}
	
	/**
	 * Constructor with an ER/Studio component as a base
	 * @param object the ER/Studio object underlying this instance
	 */
	public ModelingObject (ActiveXComponent object, boolean doBoundAttachment) {
		this.object = object;
		
		if ((null!=object)&&doBoundAttachment) {
			addChild("BOUNDATTACHMENT", new CollectionLoader<BoundAttachment>(this, "BoundAttachments", BoundAttachment.nullObj, true));
		}
	}

	/**
	 * Instantiate the class with an underlying ER/Studio object
	 * @param object the ER/Studio object underlying this instance
	 * @return an initialized subclass instance of the ActiveX component
	 */
	abstract public ModelingObject create(ActiveXComponent object);
	
	/**
	 * Find out if the ER/Studio object has already been instantiated; if so return it; otherwise create it and return the new object.
	 * @param object the ER/Studio object underlying this instance
	 * @return the initialized subclass instance of the ActiveX component
	 */
	abstract public ModelingObject find(ActiveXComponent object);

	/**
	 * Create a list of objects based on internal CSV structures
	 * @param parent the parent object to these newly created objects
	 * @param objects map of raw CSV data to instantiate the child objects
	 * @param parentKey the parent key to push to the new objects
	 * @return a list of initialized subclass instances of the raw CSV data
	 */
	static public ArrayList<? extends ModelingObject> create(ModelingObject parent, HashMap<String, RawDataObject> objects, String parentKey) {
		return null;
	}

	/**
	 * If manually creating, set the pseudo underlying ActiveX object
	 * @param object the underlying ActiveX object
	 */
	public void setObject(PseudoActiveXComponent object) {
		this.object = object;
	}

	/**
	 * Get the underlying ActiveX object
	 * @return the underlying ActiveX object
	 */
	public ActiveXComponent getObject() {
		return this.object;
	}
	
	/**
	 * Set the subclass type of this object
	 * @param type the subclass type of this object
	 */
	public void setSelfType(String type) {
		this.selfType = type;
	}
	
	/**
	 * Get the subclass type of this object
	 * @return the subclass type of this object
	 */
	public String getSelfType() {
		return this.selfType;
	}
	
	/**
	 * Set the parent object of this object
	 * @param obj the parent object
	 */
	public void setParent(ModelingObject obj) {
		this.parent = obj;
	}
	
	/**
	 * Get the parent object of this object
	 * @return the parent object
	 */
	public ModelingObject getParent() {
		return this.parent;
	}
	
	/**
	 * Add a child collection to this object
	 * @param name the name of this child collection
	 * @param object the {@link CollectionLoader} of the children
	 */
	public void addChild(String name, CollectionLoader<? extends ModelingObject> object) {
		children.put(name.toUpperCase(), object);
	}
	
	/** Force the initialization of a child collection
	 * @param name the name of the child collection
	 */
	public void initChild(String name) {
		CollectionLoader cl = children.get(name.toUpperCase());
		
		if (0==cl.getHashMap().size())
			cl.initObjects(true);
	}
	
	/**
	 * Set this object as the current object of this type
	 */
	public void setCurrentObject() {
		currentObjects.put(this.selfType.toUpperCase(), this);
	}
	
	/** 
	 * Get the current object of a given type
	 * @param name the type of object to get (ex: ENTITY)
	 * @return the current object of that object type
	 */
	static public ModelingObject getCurrentObject(String name) {
		return currentObjects.get(name.toUpperCase());
	}
	
	/**
	 * Clear the current object of this type
	 */
	public void clearCurrentObject() {
		currentObjects.remove(this.selfType.toUpperCase());
	}
	
	/**
	 * Get the map of all current objects
	 * @return the HashMap of all current objects
	 */
	public HashMap<String, ModelingObject> getCurrentObjects() {
		return currentObjects;
	}
	
	public void setRealActiveXObject(boolean value) {
		realActiveXObject = value;
	}
	
	public boolean isRealActiveXObject() {
		return realActiveXObject;
	}
	
	/**
	 * Set the header column list for this object; used when loading raw CSV data
	 * @param columns the list of column names represented in the CSV data
	 */
	public void setHeaderColumns(HashMap<String, String> columns) {
		this.headerColumns = columns;
	}

	/**
	 * Get the header column list for this object
	 * @return the header column list
	 */
	public HashMap<String, String> getHeaderColumns() {
		return this.headerColumns;
	}
	
	/**
	 * Update the internal sequence generator to the next value
	 */
	public void nextSequence() {
		this.sequence++;
	}
	
	/**
	 * Reset the internal sequence generator
	 */
	public void resetSequence() {
		sequence = 0;
	}
	
	/**
	 * Get the current sequence value
	 * @return the sequence value prepended with "_"
	 */
	public String getSequence() {
		if (0==this.sequence)
			return "";
		else
			return "_" + String.valueOf(sequence);
	}
// routines to handle the ActiveX component properties
	/**
	 * Get the value of a property, assuming it has been initialized
	 * @param name the name of the property
	 * @return the property value
	 */
	String getProp(String name) {
		String result = properties.get(name.toUpperCase());
		
		return result;
	}
	
	/** 
	 * Put a property to the internal list
	 * @param name the name of the property
	 * @param value the value to set
	 */
	void putProp(String name, String value) {
		properties.put(name.toUpperCase(), value);
	}
	
	/**
	 * Get a property as a ModelingObject subclass; typically used when there are indirect references to objects
	 * @param name the name of the property
	 * @return the ModelingObject subclass instantiated object if applicable
	 */
	public ModelingObject getModelingObject(String name) {
		ModelingObject ob = null;
		
		return ob;
	}
	
	/**
	 * Get the value for an ActiveX component property
	 * @param name the name of the property
	 * @return the {@link com.jacob.com.Variant} property value
	 */
	public Variant getProperty(String name) {
		String data = null;
		
		data = getProp(name);
		
		if (null==data) {
			if (null!=object)
				try {
					Variant v = object.getProperty(name);
					if (null != v) 
						putProp(name, v.toString());
					
					return v;
				} catch (ComFailException ignored ) {
				} catch (Exception ex) {
					reportError("", name);
				}
			else 
				return null;
		} else
			return getVariant(data);
		
		return null;
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
		
		if (name.toUpperCase().equals("COUNT")) {
			ArrayList<? extends ModelingObject> al = getChildren(object);
			
			if (null!=al)
				value = getVariant(String.valueOf(al.size()));
		}
		
		return value;
	}
	
	/**
	 * Get a property value as a string.
	 * @param name the name of the property
	 * @param defaultValue default value to return if no real value found
	 * @return the value of the property, or defaultValue if value was <code>NULL</code>
	 */
	public String getPropertyAsString(String name, String defaultValue) {
		String value = getPropertyAsString(name);
		
		if (null==value)
			value = defaultValue;
		
		return value;
	}
	
	/**
	 * Get a property value as a string.
	 * @param name the name of the property
	 * @return the value of the property
	 */
	public String getPropertyAsString(String name) {
		String value = null;
		Variant v;
		String object = null;
		String object2 = "";
		String param = "";
		String[] replacements = null;
		
		if (-1 < name.indexOf("]]")) {
			String[] p = name.split("\\[\\[|\\]\\]");
			name = p[0];
			replacements = new String[] {p[1].substring(0, p[1].indexOf(",")), p[1].substring(p[1].indexOf(",") + 1)};
		}
		
		String[] tokens = Utility.splitProperty(name);
		param = tokens[tokens.length - 1];

		if (tokens.length>1) object = tokens[0].toUpperCase();
		if (tokens.length>2) object2 = tokens[1];
			
		ModelingObject mo = null;
		if (null!=object) {
			if("APP".equalsIgnoreCase(object)) {
				mo = Application.getInstance();
			} else {
				ModelingObject mo2 = this;
				while ((null!=mo2) && (null==mo)) { // see if this object type is in my hierarchy
					if (object.equals(mo2.getSelfType()))
						mo = mo2;
					mo2 = mo2.getParent();
				}
				if (null==mo) mo = currentObjects.get(object);
			}
		}
		if (null==mo) mo = this;
		
		switch((object2 + param).substring(0,  1)) {
		case "!": // take as is regardless of possible tokens 
			value = param.substring(2);
			break;
		case "#": // bound attachment
			BoundAttachment result = (BoundAttachment)mo.getChild("BOUNDATTACHMENT", param.replace("#", ""));
			value = (null==result ? getPropertyAsString(param.replace("#", "")) : result.getPropertyAsString("ValueCurrent"));
			break;
		case "$": //  derived property
			if (0==object2.length())
				param = param.replaceAll("^\\$|\\$$", "");
			else
				param = param.replaceAll("\\$$", "");
			
			v = mo.getDerivedProperty(object2.replace("$", ""), param);
			if (null!=v) value = v.toString();
			break;
		default:
			if (null==(value = mo.getProp(param))) {
				try {
					v = mo.getProperty(param);
					if (null != v)
						value = v.toString();
					
					if (null != value) 
						mo.putProp(param, value); 
					else 
						properties.put(param, NULL);
					
				} catch (ComFailException ex) {
					value = null;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		if (NULL.equals(value)) 
			value = null;
		else if (null != replacements)
			value = value.replaceAll(replacements[0], replacements[1]);
		
		return value;
	}

	/**
	 * Get the value of a property as a boolean value
	 * @param name the name of the property
	 * @return the value of the property
	 */
	public boolean getPropertyAsBoolean(String name) {
		if (null != object)
			try {
				return object.getPropertyAsBoolean(name);
			} catch (Exception ignored) {
				reportError("Boolean ", name);
			}
		
		return false;
	}
	
	/**
	 * Get the value of a property as a numeric value
	 * @param name the name of the property
	 * @return the value of the property
	 */
	public int getPropertyAsInt(String name) {
		try {
			return object.getPropertyAsInt(name);
		} catch (Exception ignored) {
			reportError("Int ", name);
		}
		
		return -1;
	}
	
	/**
	 * get the value of a property as an ActiveX component
	 * @param name the name of the property
	 * @return the value of the property
	 */
	public ActiveXComponent getPropertyAsComponent(String name) {
		if (null!=object)	
			try {
				return object.getPropertyAsComponent(name);
			} catch (Exception ignored) {
				reportError("Component ", name);
			}
		
		return null;
	}

	/**
	 * Report an error to the console
	 * @param type the data type of the value being retrieved
	 * @param name the name of the property
	 */
	private void reportError(String type, String name) {
		System.out.print("Error retrieving " + type + "property " + name + " from " + this.getSelfType() + " \"" + this.getName() + "\"!");
	}
	
	/**
	 *  Get the name of this object.  
	 *  This will be overridden as needed in derived objects.
	 * @return the object name
	 */
	public String getName() {
		if (null==this.name) 
			this.name = getPropertyAsString("Name");
		
		return this.name;
	}
	
	/**
	 * Set the name of this object.
	 * @param name the name of this object
	 */
	public void setName(String name) {
		this.name = (null==name) ? "" : name.trim();
	}
	
	/**
	 * Get the ID for this object,
	 * This may be overridden for differing behavior
	 * @return the ID for this object
	 */
	public String getID() {
		if (null==id) id = getPropertyAsString("ID");
		
		return id;
	}
	
	/**
	 * Set the ID for this object
	 * @param id the ID value
	 */
	public void setID(String id) {
		this.id = id;
	}
	
	/**
	 * Get the GUID for this object.
	 * This may be overridden for differing behavior
	 * @return the GUID for this object
	 */
	public String getGUID() {
		if (null==guid) guid = getPropertyAsString("GUID");

		return guid;
	}
	
	/**
	 * Set the GUID for this object
	 * @param guid the GUID value
	 */
	public void setGUID(String guid) {
		this.guid = guid;
	}
	
	/**
	 * Get the list of additional properties that can be searched.
	 * This can be overridden to set additional searchable properties
	 * @return the sort key for this object
	 */
	public String[] getCollectionKeys() {
		return new String[] {};
	}
		
	/**
	 * Calculate and get the sort key for this instance.
	 * This may be overridden to enrich the sort behavior.
	 * @return the sort key for this object
	 */
	public String getSortKeyValue() {
		return getName();
	};
	
	/**
	 * Get the sort key for this instance.  It will be cached so the sort key will only be calculated once.
	 * @return the sort key for this object
	 */
	public String getSortKey() {
		if (null==sortKey) sortKey = getSortKeyValue();
		
		return sortKey;
	}

	/**
	 * Set the value for a property 
	 * @param name the name of the property
	 * @param value the value for the property
	 */
	public void setProperty(String name, Variant value) {
		object.setProperty(name, value);
		properties.put(name.toUpperCase(), value.toString());
	}
	
	/**
	 * Set the value for a property as a boolean
	 * @param name the name of the property
	 * @param value the value for the property
	 */
	public void setPropertyAsBoolean(String name, boolean value) {
		object.setProperty(name, value);
	}
	
	/**
	 * Set the value for a property as a boolean.  The string value will be converted to <code>TRUE</code> if "Y" or "TRUE" is the string; <code>FALSE</code> otherwise
	 * @param name the name of the property
	 * @param value the value for the property
	 */
	public void setPropertyAsBoolean(String name, String value) {
		object.setProperty(name, "Y".equalsIgnoreCase(value) || "TRUE".equalsIgnoreCase(value));
	}
	
	/**
	 * Set the value for a property as a String
	 * @param name the name of the property
	 * @param value the value for the property
	 */
	public void setPropertyAsString(String name, String value) {
		object.setProperty(name, value);
		properties.put(name.toUpperCase(), value);
	}
	
	/**
	 * Set the value for a property as an int
	 * @param name the name of the property
	 * @param value the value for the property
	 */
	public void setPropertyAsInt(String name, int value) {
		object.setProperty(name, value);
	}
	
	/**
	 * Get a child object of this object
	 * @param objectType the type of object (ex. ENTITY)
	 * @param name list of one or mire values to search for
	 * @return the object found or <code>NULL</code> for no object found
	 */
	public ModelingObject getChild(String objectType, String... name) {
		Variant[] arr = new Variant[name.length];
		for (int i = name.length; i-- > 0; )
			arr[i] = getVariant(name[i]);
		
		return getChild(objectType, arr);
	}
	
	/**
	 * Get a child object of this object
	 * @param objectType the type of object (ex. ENTITY)
	 * @param name list of one or mire values to search for
	 * @return the object found or <code>NULL</code> for no object found
	 */
	public ModelingObject getChild(String objectType, Variant... name) {
		ModelingObject mo = null;

		CollectionLoader<? extends ModelingObject> cmo = children.get(objectType.toUpperCase());

		if (null!=cmo)
			mo = cmo.get(name);
		
		return mo;
	}
	
	/**
	 * Get the collection of child objects of a given type
	 * @param objectType the type of object (ex. ENTITY)
	 * @return the collection of child objects of that type
	 */
	public CollectionLoader<? extends ModelingObject> getChildCollection(String objectType) {
		return children.get(objectType.toUpperCase());
	}
	/**
	 * Get the list of child objects of a given type
	 * @param objectType the type of object (ex. ENTITY)
	 * @return the list of child objects of that type
	 */
	public ArrayList<? extends ModelingObject> getChildren(String objectType) {
		ArrayList<? extends ModelingObject> mo = new ArrayList<ModelingObject>();

		CollectionLoader<? extends ModelingObject> cmo = children.get(objectType.toUpperCase());

		if (null!=cmo)
			mo = cmo.getList();
		
		return mo;
	}
	
	/**
	 * Get the map of child objects of a given type
	 * @param objectType the type of object (ex. ENTITY)
	 * @return the map of objects of that type
	 */
	public HashMap<String, ? extends ModelingObject> getChildMap(String objectType) {
		HashMap<String, ? extends ModelingObject> mo = new HashMap<String, ModelingObject>();

		CollectionLoader<? extends ModelingObject> cmo = children.get(objectType.toUpperCase());
		
		if (null!=cmo)
			mo = cmo.getHashMap();

		return mo;
	}
	
	public void setChildManual(boolean value) {
		for (CollectionLoader<? extends ModelingObject> cl : children.values())
			cl.setManualLoader(value);
	}
	/**
	 * Convert a value to {@link com.jacob.com.Variant}
	 * @param value the value to convert
	 * @return the converted {@link com.jacob.com.Variant}
	 */
	public Variant getVariant(String value) {
		if (null!=value) 
			return new Variant(value);
		
		return null;
	}

	static public String getNextIDSequence() {
		return String.valueOf(sequenceGenerator++);
	}
	
	/**
	 * Output a property to the console
	 * @param name the property to output
	 */
	public void dump(String name) {
		dump(name, "");
	}
	
	/**
	 * Output a property to the console
	 * @param name the name of the property to output
	 * @param prefix text to output before the property name/value (usually an indent such as "\t")
	 */
	public void dump(String name, String prefix) {
		System.out.println(prefix + name + "=" + getPropertyAsString(name));

	}
}
