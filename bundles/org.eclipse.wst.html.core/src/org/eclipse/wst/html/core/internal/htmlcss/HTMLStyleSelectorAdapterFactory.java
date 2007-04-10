/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.htmlcss;



import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSelectorAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;

/**
 * Insert the type's description here.
 */
public class HTMLStyleSelectorAdapterFactory implements INodeAdapterFactory {

	private static HTMLStyleSelectorAdapterFactory instance;
	private Object toMatch = IStyleSelectorAdapter.class;

	/**
	 * CSSModelProvideAdapterFactory constructor comment.
	 */
	public HTMLStyleSelectorAdapterFactory() {
		super();
	}

	/**
	 * Method that returns the adapter associated with the given object.
	 * It may be a singleton or not ... depending on the needs of the INodeAdapter  ...
	 * but in general it is recommended for an adapter to be stateless, 
	 * so the efficiencies of a singleton can be gained.
	 *
	 * The implementation of this method should call addAdapter on the adapted
	 * object with the correct instance of the adapter.
	 */
	public INodeAdapter adapt(INodeNotifier notifier) {
		INodeAdapter adapter = notifier.getExistingAdapter(IStyleSelectorAdapter.class);
		if (adapter != null)
			return adapter;
		adapter = HTMLStyleSelectorAdapter.getInstance();
		notifier.addAdapter(adapter);
		return adapter;
	}

	public synchronized static HTMLStyleSelectorAdapterFactory getInstance() {
		if (instance == null)
			instance = new HTMLStyleSelectorAdapterFactory();
		return instance;
	}

	/**
	 * isFactoryForType method comment.
	 */
	public boolean isFactoryForType(Object type) {
		return type == toMatch;
	}

	public void release() {
		// default is to do nothing
	}

	/**
	 * Overriding copy method
	 */
	public INodeAdapterFactory copy() {
		return getInstance();
	}

}
