package com.biogen.generator.ertools.erstudio.collection;

import com.biogen.generator.ertools.erstudio.ModelingObject;
import com.jacob.activeX.ActiveXComponent;
/**
 * This class implements a manual {@link CollectionLoader}, where the object initialization is done via a custom method.
 * @author drothste
 *
 * @param <T> the ModelingObject subclass this instance is of
 */
public class ManualCollectionLoader<T extends ModelingObject> extends CollectionLoader<T> {
	private boolean fullyInitialized = false;
	private CollectionLoaderInit init = null;
	private boolean force = false;
	
	/**
	 * Constructor
	 * @param parent the parent {@link ModelingObject}
	 * @param baseObj the subclass of the children; needed to instantiate properly
	 * @param init the custom method for initialization
	 */
	public ManualCollectionLoader (ModelingObject parent, T baseObj, CollectionLoaderInit init) {	
		this(parent, baseObj, init, false);		
	}

	/**
	 * Constructor
	 * @param parent the parent {@link ModelingObject}
	 * @param baseObj the subclass of the children; needed to instantiate properly
	 * @param init the custom method for initialization
	 * @param force if <code>TRUE</code> then immediately load the child objects
	 */
	@SuppressWarnings({ })
	public ManualCollectionLoader (ModelingObject parent, T baseObj, CollectionLoaderInit init, boolean force) {	
		super(parent, null, baseObj, false);
		this.init = init;
		this.force = force;
	}

	@Override
	T activeXAdd(ActiveXComponent item) {
		return null;
	}

	@Override
	public void initObjects(boolean energize) {
		if (!fullyInitialized || force) {
			fullyInitialized = true;
			if (!force) clear(); // if forcing initObjects then initObjects needs to handle clearing.
			if (null!=init) init.init();
		}
	}	
}
