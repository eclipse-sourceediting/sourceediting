/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.adaptdom;

import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This class exists only to satisfy some of the unit tests.
 * 
 * To test, use following line in client, such as when it intitialized.
 * FactoryRegistry.getFactoryRegistry().addFactory(new
 * AdapterFactoryTestOnly());
 * 
 * Creation date: (11/14/00 7:08:18 PM)
 * 
 * @author: David Williams
 */
public class AdapterFactoryTestOnly implements INodeAdapterFactory {
	// This factory deals with three adapters
	java.util.List adapters = new java.util.ArrayList();

	/**
	 * CAAdapterFactory constructor.
	 */
	public AdapterFactoryTestOnly() {
		super();
		adapters.add(new AdapterForDocumentTestOnly());
		adapters.add(new AdapterForElementTestOnly());
		adapters.add(new AdapterForAttrTestOnly());
	}

	/**
	 * Method that returns the adapter associated with the this factory and
	 * the given object, and "sets up" the adaptable object to use the
	 * adapter.
	 * 
	 * The adapter may be a singleton or not ... depending on the needs of the
	 * INodeAdapter ... but in general it is recommended for an adapter to be
	 * stateless, so the efficiencies of a singleton can be gained.
	 * 
	 * The implementation of this method should call addAdapter on the adapted
	 * object with the correct instance of the adapter.
	 */
	public INodeAdapter adapt(INodeNotifier target) {
		// object.addAdapter(adapterInstance);
		// return adapterInstance;
		INodeAdapter result = null;

		Iterator adaptersList = adapters.iterator();
		while (adaptersList.hasNext()) {
			INodeAdapter adapter = (INodeAdapter) adaptersList.next();
			if (adapter.isAdapterForType(target)) {
				INodeAdapter existingAdapter = target.getExistingAdapter(adapter);
				if (existingAdapter == null) {
					target.addAdapter(adapter);
					result = adapter;
				}
			}
		}

		return result;

	}

	/**
	 * isFactoryForType method comment.
	 */
	public boolean isFactoryForType(java.lang.Object type) {
		boolean result = false;

		// for now, one adapter for documents, elements, and attr.
		//
		if (type instanceof Document) {
			result = true;
		}
		else {
			if (type instanceof Element) {
				result = true;
			}
			else {
				if (type instanceof Attr) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * 
	 */
	public void release() {
		// TODO: create unit test

	}

	public INodeAdapterFactory copy() {
		return new AdapterFactoryTestOnly();
	}

}
