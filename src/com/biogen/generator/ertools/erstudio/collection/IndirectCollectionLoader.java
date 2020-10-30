package com.biogen.generator.ertools.erstudio.collection;

import com.biogen.generator.ertools.erstudio.ModelingObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

/**
 * This implements an indirect {@link CollectionLoader}, where the child list actually are pointers to the actual objects (Ex. DisplayObject-&gt;Entity).
 * @author drothste
 *
 * @param <T> the subclass of {@link ModelingObject} for the children
 */
public class IndirectCollectionLoader<T extends ModelingObject> extends CollectionLoader<T>{
	private CollectionLoader<? extends ModelingObject> map;
	private String property;
	
	/**
	 * Constructor
	 * @param parent the parent {@link ModelingObject}
	 * @param propertyName the parent property name that contains the list of children
	 * @param baseObj the subclass of the children; needed to instantiate properly
	 * @param property the property of the main object whose value is used to look up the indirect
	 * @param lookup the map to lookup the child objects
	 * @param lazyLoad if <code>TRUE</code> then do not immediately load the child objects
	 */
	public IndirectCollectionLoader (ModelingObject parent, String propertyName, T baseObj, String property, CollectionLoader<? extends ModelingObject> lookup, boolean lazyLoad) {	
		super(parent, propertyName, baseObj, lazyLoad);
		this.property = property;
		this.map = lookup;
	}

	/**
	 * Constructor
	 * @param parent the parent {@link ModelingObject}
	 * @param propertyName the parent property name that contains the list of children
	 * @param baseObj the subclass of the children; needed to instantiate properly
	 * @param property the property of the main object whose value is used to look up the indirect
	 * @param lookup the map to lookup the child objects
	 */
	public IndirectCollectionLoader (ModelingObject parent, String propertyName, T baseObj, String property, CollectionLoader<ModelingObject> lookup) {	
		this(parent, propertyName, baseObj, property, lookup, true);
	}

	/**
	 * Get the indirect object from an ActiveX component
	 * @param ax the ActiveX component
	 * @return the indirect object
	 */
	public ActiveXComponent parseComponent(ActiveXComponent ax) {
		Variant value = ax.getProperty(property);
		
		switch (value.getvt()) {
		case Variant.VariantDispatch :
			ActiveXComponent ax2 = new ActiveXComponent(value.getDispatch());
			return map.get(ax2.getPropertyAsString("GUID")).getObject();
		default :
			return map.get(value.toString()).getObject();
		}
	}
	
	@Override
	public boolean isSetParent() {
		return false;
	}
	
}
