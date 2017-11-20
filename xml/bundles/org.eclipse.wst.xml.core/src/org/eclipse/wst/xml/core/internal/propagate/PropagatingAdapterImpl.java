/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.propagate;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class PropagatingAdapterImpl implements PropagatingAdapter {

	private List adaptOnCreateFactories = null;

	/**
	 * AbstractPropagatingAdapterImpl constructor comment.
	 */
	public PropagatingAdapterImpl() {
		super();
	}

	private void adaptOnCreate(IDOMNode node) {
		// give each of the factories a chance to adapt the node, if it
		// chooses to
		if (adaptOnCreateFactories != null) {


			synchronized (adaptOnCreateFactories) {
				int length = adaptOnCreateFactories.size();
				for (int i = 0; i < length; i++) {
					INodeAdapterFactory factory = (INodeAdapterFactory) adaptOnCreateFactories.get(i);
					factory.adapt(node);
				}
			}

		}

	}

	/**
	 * This mechanism can be made "easier to use" later.
	 */
	public void addAdaptOnCreateFactory(INodeAdapterFactory factory) {
		getAdaptOnCreateFactories().add(factory);
	}

	/**
	 * Gets the adaptOnCreateFactories.
	 * 
	 * @return Returns a List
	 */
	public List getAdaptOnCreateFactories() {
		if (adaptOnCreateFactories == null)
			adaptOnCreateFactories = new ArrayList();
		return adaptOnCreateFactories;
	}


	/**
	 * @see PropagatingAdapter#initializeForFactory(INodeAdapterFactory,
	 *      INodeNotifier)
	 */
	public void initializeForFactory(INodeAdapterFactory factory, INodeNotifier node) {
		// ISSUE: we are a DOM specific implimentation, 
		// we should not be.
		if (node instanceof IDOMNode) {
			IDOMNode xmlNode = (IDOMNode) node;
			propagateTo(xmlNode);
		}
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return type.equals(PropagatingAdapter.class);
	}

	private boolean isInteresting(Object newValue) {
		return (newValue != null && (newValue instanceof Element || newValue instanceof Document || newValue instanceof DocumentType));
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// Issue: We only propagate to Elements ...
		// not attributes too! We should careful consider doning all when
		// when we improve "adapt on create" design.
		if (eventType == INodeNotifier.ADD && isInteresting(newValue)) {
			propagateTo((IDOMNode) newValue);
		}
	}

	private void propagateTo(IDOMNode node) {
		// get adapter to ensure its created
		node.getAdapterFor(PropagatingAdapter.class);
		adaptOnCreate(node);
		propagateToChildren(node);
	}

	private void propagateToChildren(IDOMNode parent) {
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			propagateTo((IDOMNode) child);
		}
	}

	/**
	 * @see PropagatingAdapter#release()
	 */
	public void release() {
		if (adaptOnCreateFactories != null) {

			synchronized (adaptOnCreateFactories) {
				int length = adaptOnCreateFactories.size();
				for (int i = 0; i < length; i++) {
					INodeAdapterFactory factory = (INodeAdapterFactory) adaptOnCreateFactories.get(i);
					factory.release();
				}
			}

		}
	}

}
