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
package org.eclipse.wst.html.core.internal.validate;



import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.w3c.dom.Node;

public class HTMLValidationAdapterFactory extends AbstractAdapterFactory {

	private static HTMLValidationAdapterFactory instance = null;

	/**
	 * HTMLValidationAdapterFactory constructor comment.
	 */
	public HTMLValidationAdapterFactory() {
		super(ValidationAdapter.class, true);
	}

	/**
	 * HTMLValidationAdapterFactory constructor comment.
	 * @param adapterKey java.lang.Object
	 * @param registerAdapters boolean
	 */
	public HTMLValidationAdapterFactory(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	/**
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {
		Node node = (Node) target;
		switch (node.getNodeType()) {
			case Node.DOCUMENT_NODE :
				return new DocumentPropagatingValidator();
			case Node.ELEMENT_NODE :
				return new ElementPropagatingValidator();
			default :
				return new NullValidator();
		}
	}

	/**
	 */
	public synchronized static HTMLValidationAdapterFactory getInstance() {
		if (instance != null)
			return instance;
		instance = new HTMLValidationAdapterFactory();
		return instance;
	}

	/**
	 * Overriding Object's clone() method
	 * This is used in IModelManager's IStructuredModel copying.
	 */
	public INodeAdapterFactory copy() {
		return getInstance();
	}
}
