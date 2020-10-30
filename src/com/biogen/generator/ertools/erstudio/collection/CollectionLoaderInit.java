package com.biogen.generator.ertools.erstudio.collection;

/**
 * This class provides a framework for custom initializations of {@link CollectionLoader}s.
 * @author drothste
 *
 */
abstract public interface CollectionLoaderInit {

	/**
	 * Initialize the objects in a {@link CollectionLoader}
	 */
	abstract public void init();
}
