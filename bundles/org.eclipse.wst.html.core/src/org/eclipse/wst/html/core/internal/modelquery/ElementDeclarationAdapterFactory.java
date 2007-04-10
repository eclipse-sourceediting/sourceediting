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
package org.eclipse.wst.html.core.internal.modelquery;



import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;

/**
 */
public class ElementDeclarationAdapterFactory implements INodeAdapterFactory {

	private static ElementDeclarationAdapterFactory instance = null;

	/**
	 */
	private ElementDeclarationAdapterFactory() {
		super();
	}

	/**
	 */
	public INodeAdapter adapt(INodeNotifier notifier) {
		if (notifier == null)
			return null;
		INodeAdapter adapter = notifier.getExistingAdapter(ElementDeclarationAdapter.class);
		if (adapter != null)
			return adapter;
		adapter = new HTMLElementDeclarationAdapter();
		notifier.addAdapter(adapter);
		return adapter;
	}

	/**
	 */
	public synchronized static ElementDeclarationAdapterFactory getInstance() {
		if (instance == null)
			instance = new ElementDeclarationAdapterFactory();
		return instance;
	}

	/**
	 */
	public boolean isFactoryForType(Object type) {
		return (type == ElementDeclarationAdapter.class);
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
