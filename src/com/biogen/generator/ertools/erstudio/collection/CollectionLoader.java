package com.biogen.generator.ertools.erstudio.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.biogen.generator.ertools.erstudio.ModelingObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;

/**
 * This class instantiates collections of child objects (Ex ENTITY-&gt;ATTRIBUTES).  It can either immediately load all child objects or 
 * work in a lazy fashion; loading an object only on first reference.  This is due to the inherent slowness of receiving objects from 
 * ER/Studio through the API calls.  It provides both {@link java.util.ArrayList} and {@link java.util.HashMap} collections to accommodate multiple access requirements.
 * @author drothste
 *
 * @param <T> the {@link ModelingObject} subclass this CollectionLoader is collecting 
 */
public class CollectionLoader<T extends ModelingObject> {
	@SuppressWarnings("rawtypes")
	private static HashMap<String, ArrayList<CollectionLoader>> references = new HashMap<String, ArrayList<CollectionLoader>>();
	private ArrayList<T> list;
	private HashMap<String, T> map;
	
	private ActiveXComponent cObject;
	private ModelingObject parentT;
	private String propertyName; 
	private T clz;
	private boolean manualLoader;
	private boolean lazyLoader;
	private boolean initializing = false;
	private boolean fullyInitialized = false;
	
	/**
	 * Constructor
	 * @param parent the parent {@link ModelingObject}
	 * @param propertyName the parent property name that contains the list of children
	 * @param baseObj the subclass of the children; needed to instantiate properly
	 */
	public CollectionLoader (ModelingObject parent, String propertyName, T baseObj) {	
		this(parent, propertyName, baseObj, false);		
	}

	/**
	 * Constructor
	 * @param parent the parent {@link ModelingObject}
	 * @param propertyName the parent property name that contains the list of children
	 * @param baseObj the subclass of the children; needed to instantiate properly
	 * @param lazyLoad if <code>TRUE</code> then do not immediately load the child objects
	 */
	@SuppressWarnings("rawtypes")
	public CollectionLoader (ModelingObject parent, String propertyName, T baseObj, boolean lazyLoad) {	
		this.parentT = parent;
		this.propertyName = propertyName;
		manualLoader = (null==propertyName);
		this.clz = baseObj;
		lazyLoader = lazyLoad;

		if(null!=parent) {
			ArrayList<CollectionLoader> al = references.get(baseObj.getSelfType().toUpperCase());
			if (null==al) {
				al = new ArrayList<CollectionLoader>();
				references.put(baseObj.getSelfType().toUpperCase(), al);
			}
			
			al.add(this);

			if (null!=propertyName)
				cObject = parentT.getPropertyAsComponent(propertyName);
		}
	}

	/**
	 * Get the {@link java.util.HashMap} of child objects.  This map will contain entries for all fields in getCollectionKeys()
	 * of the child {@link ModelingObject} subclass
	 * @return the {@link java.util.HashMap} of child objects
	 */
	public HashMap<String, T> getHashMap() {
		initObjects(true);
		return map;
	}
	
	/**
	 * Get the {@link java.util.HashMap} of child objects based on a filter of objects
	 * @param filter the {@link java.util.ArrayList} of objects to filter with
	 * @param filterName the property name from the filter list to test the map records with
	 * @return the filtered {@link java.util.HashMap}
	 */
	public HashMap<String, T> getFilteredHashMap(ArrayList<? extends ModelingObject> filter, String filterName) {
		HashMap<String, T> result = new HashMap<String, T>();
		T obj;
		
		initObjects(true);

		for (ModelingObject item : filter)
			if (null != (obj = map.get(item.getPropertyAsString(filterName))))
				putToMap(result, obj);
		
		return result;
	}
	
	/**
	 * Get the {@link java.util.ArrayList} of child objects
	 * @return the {@link java.util.ArrayList} of child objects
	 */
	public ArrayList<T> getList() {
		initObjects(true);
		return list;
	}

	/** 
	 * Is an object in the list of children?
	 * @param name the object name to find
	 * @return <code>TRUE</code> if the object was found; <code>FALSE</code> otherwise
	 */
	public boolean contains(String name) {
		return (null!=getHashMap().get(name.toUpperCase()));
	}

	/**
	 * Get the {@link java.util.ArrayList} of child objects based on a filter of objects
	 * @param filter the {@link java.util.ArrayList} of objects to filter with
	 * @param name the child object property name to test
	 * @param filterName the property name from the filter list to test the map records with
	 * @return the filtered {@link java.util.ArrayList}
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<T> getFilteredList(ArrayList<? extends ModelingObject> filter, String name, String filterName) {
		ArrayList<T> result = new ArrayList<T>();
		initObjects(true);

		for (ModelingObject sourceItem : filter) {
			String sourceKey = sourceItem.getPropertyAsString(filterName);

			for (ModelingObject item : list)
				if (item.getPropertyAsString(name).equalsIgnoreCase(sourceKey)) {
//					if (!result.contains((T) item)) 
							result.add((T) item);
							break;
				}
		}
		
		return result;
	}
	
	/**
	 * Find a child object in the collection.  If it is not found and is supposed to be in the list instantiate and return it.
	 * @param type the type of object the child is (Ex. ENTITY)
	 * @param id one or more keys in order to find the object
	 * @return the object if found, <code>NULL</code> otherwise
	 */
	@SuppressWarnings("rawtypes")
	static public ModelingObject findGet(String type, Variant... id) {
		ModelingObject result = null;
		ArrayList<CollectionLoader> acl = references.get(type.toUpperCase());
			
		for (int i = acl.size(); (i-- > 0) && (null==result); ) 
			result = acl.get(i).get(id);
		
		return result;
	}
	
	/**
	 * Get an object from the collection.  If it does not currently exist but should then instantiate it.
	 * @param name one or more keys to identify the object
	 * @return the object if part of the collection; <code>NULL</code> otherwise
	 */
	public T get(String... name) {
		Variant[] arr = new Variant[name.length];
		
		for (int i = name.length; i-- > 0; )
			arr[i] = new Variant(name[i]);
		
		return get(arr);
	}
	
	/**
	 * Get an object from the collection.  If it does not currently exist it should then instantiate it.
	 * @param id one or more keys to identify the object
	 * @return the object if part of the collection; <code>NULL</code> otherwise
	 */
	public T get(Variant... id) {
		T result = null;
		
		if (initializing)
			return null;
		
		initObjects(false);
		for (Variant v : id) {
			result = map.get(v.toString());
			if (null!=result) break;
		}
		
		if (lazyLoader && (null==result)) {
			initializing = true;

			if (null!=cObject)
				for (int i = id.length; (i-- > 0) && (null==result); ) {
					ActiveXComponent ax = cObject.invokeGetComponent("Item", id[i]);
					result = activeXAdd(ax);
				}
						
			sort();

			initializing = false;
		}
		
		return result;
	}
	
	private void putToMap(HashMap<String, T> map, T item) {
		map.put(item.getName().trim(), item);
		map.put(item.getID(), item);
		map.put(item.getGUID(), item);
		
		String[] keys = item.getCollectionKeys();
		String value;
		
		for (int i = keys.length; i-- > 0; )
			if (-1==keys[i].indexOf("||")) {
				if (null != (value = item.getPropertyAsString(keys[i]))) 
					map.put(value.trim(), item);
			} else {
				StringBuilder sb = new StringBuilder();
				for (String s : keys[i].split("\\|\\|")) 
					sb.append(".").append(item.getPropertyAsString(s));
				
				map.put(sb.toString().substring(1), item);
			}
					
	}
	
	public void setManualLoader(boolean value) {
		manualLoader = value;
	}
	
	/**
	 * Add an item to the collection
	 * @param item the item to add
	 */
	public void add(T item) {
		if (manualLoader) {
			if (null==list)
				initObjects(false);
			
			list.add(item);
			putToMap(map, item);
		}
	}
	
	/**
	 * Add a list of objects to the collection
	 * @param objects {@link java.util.ArrayList} of objects to add
	 */
	public void addAll(ArrayList<? extends ModelingObject> objects) {
		if (manualLoader) {
			if (null==list)
				initObjects(false);
			
			for (ModelingObject item : objects)
				add((T) item);
		}
	}
	
	/**
	 * Sort the collection based on the {@link ModelingObject} subclass sorting profile
	 */
	public void sort() {
		if (null!=list)
			Collections.sort(list, new comparitor<T>());
	}

	/**
	 * Should child objects be set to their parent if instantiated?  This can be overridden in subclasses.
	 * @return <code>TRUE</code> if the parent should be set; <code>FALSE</code> otherwise
	 */
	public boolean isSetParent() {
		return true;
	}
	
	/**
	 * Perform any processing on the ActiveX component before use.  This can be overridden in subclasses.
	 * @param ax the ActiveX component
	 * @return the processed ActiveX component
	 */
	public ActiveXComponent parseComponent(ActiveXComponent ax) {
		return ax;
	}

	/**
	 * Add an item from the ActiveX component data
	 * @param item the ActiveX component
	 * @return the {@link ModelingObject} subclass reference.
	 */
	@SuppressWarnings("unchecked")
	T activeXAdd(ActiveXComponent item) {
		T obj = null;
		if (null!=item)
			if (0 < item.m_pDispatch) {
				obj = (T)clz.find(parseComponent(item));
				if (isSetParent()) obj.setParent(parentT);
				list.add(obj);
				putToMap(map, obj);
			}
		
		return obj;
	}

	/**
	 * Clear the collection
	 */
	public void clear() {
		fullyInitialized = false;
		list = new ArrayList<T>();
		map = new HashMap<String, T>();
	}
	
	/**
	 * Initialize the collection.  If lazy loading then may do nothing.
	 * @param energize if <code>TRUE</code> load all objects regardless of lazy loading status
	 */
	public void initObjects(boolean energize) {
		if (((null==list)||energize) && !fullyInitialized) {
			if (null==list) 
				list = new ArrayList<T>();

			if (null==map)
				map = new HashMap<String, T>();
		
			if (!manualLoader) {
				if (!lazyLoader || energize) {
					list.clear();
					fullyInitialized = true;
					
					if (null != cObject) {
						EnumVariant ev = new EnumVariant(cObject);
					
						initializing = true;
						
						while (ev.hasMoreElements()) {
							activeXAdd(new ActiveXComponent(ev.nextElement().getDispatch()));
						}
					
						sort(); 
					}
					
					initializing = false;
				}
			}
		}
	}

	/**
	 * The comparator for sorting
	 * @author drothste
	 *
	 * @param <T> the subclass of {@link ModelinObject} for the CollectionLoaader
	 */
	@SuppressWarnings("hiding")
	private class comparitor<T extends ModelingObject> implements Comparator<T> {

		public int compare(T arg0, T arg1) {
			return arg0.getSortKey().compareTo(arg1.getSortKey());
		}
	}
	
}
