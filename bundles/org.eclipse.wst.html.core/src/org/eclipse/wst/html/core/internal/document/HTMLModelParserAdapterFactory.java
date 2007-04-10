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
package org.eclipse.wst.html.core.internal.document;



import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.document.ModelParserAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;

/**
 */
public class HTMLModelParserAdapterFactory implements INodeAdapterFactory {

	private static HTMLModelParserAdapterFactory instance = null;

	/**
	 */
	private HTMLModelParserAdapterFactory() {
		super();
	}

	/**
	 * Method that returns the adapter associated with the given object. It
	 * may be a singleton or not ... depending on the needs of the
	 * INodeAdapter ... but in general it is recommended for an adapter to be
	 * stateless, so the efficiencies of a singleton can be gained.
	 * 
	 * The implementation of this method should call addAdapter on the adapted
	 * object with the correct instance of the adapter.
	 */
	public INodeAdapter adapt(INodeNotifier notifier) {
		INodeAdapter adapter = null;
		if (notifier != null) {
			if (notifier instanceof IDOMDocument) {
				adapter = notifier.getExistingAdapter(ModelParserAdapter.class);
				if (adapter == null) {
					adapter = new HTMLModelParserAdapter();
					notifier.addAdapter(adapter);
				}
			}
		}
		return adapter;

	}

	/**
	 */
	public synchronized static HTMLModelParserAdapterFactory getInstance() {
		if (instance == null)
			instance = new HTMLModelParserAdapterFactory();
		return instance;
	}

	/**
	 */
	public boolean isFactoryForType(Object type) {
		return (type == ModelParserAdapter.class);
	}

	/**
	 */
	public void release() {
		// no state to cleanup
	}

	/**
	 * Overriding copy method
	 */
	public INodeAdapterFactory copy() {
		return getInstance();
	}
}
