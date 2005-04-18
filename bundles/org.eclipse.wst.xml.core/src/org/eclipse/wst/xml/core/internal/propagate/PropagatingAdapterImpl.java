/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
import java.util.Iterator;
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

	public static final Class PropagatingAdapterClass = PropagatingAdapter.class;
	// because so many of these are created in huge file,
	// Jeffrey Liu suggested these be done lazily, since not all
	// models and not all nodes actually have a list of factories.
	private List adaptOnCreateFactories = null;

	/**
	 * AbstractPropagatingAdapterImpl constructor comment.
	 */
	public PropagatingAdapterImpl() {
		super();
	}

	protected void adaptOnCreate(IDOMNode node) {
		// give each of the factories a chance to adapt the node, if it
		// chooses to
		if (adaptOnCreateFactories != null) {
			Iterator iterator = adaptOnCreateFactories.iterator();
			while (iterator.hasNext()) {
				INodeAdapterFactory factory = (INodeAdapterFactory) iterator.next();
				factory.adapt(node);
			}
		}

	}

	/**
	 * This mechanism can be made "easier to use" later.
	 */
	public void addAdaptOnCreateFactory(INodeAdapterFactory factory) {
		//adaptOnCreateFactories.add(factory);
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

	//	protected void unadaptOnRemove(INodeNotifier node) {
	//		// give each of the factories a chance to process remove event
	//		// This is a bit out of the normal adapter pattern, but I couldn't
	//		// think of a better way to "remove" pageDirectiveWatchers, if and
	//		// when the page directive was 'removed' (edited).
	//		//
	//		Iterator iterator = adaptOnCreateFactories.iterator();
	//		while (iterator.hasNext()) {
	//			IAdapterFactory factory = (IAdapterFactory) iterator.next();
	//			if (factory instanceof PropagatingAdapterFactory) {
	//				((PropagatingAdapterFactory)factory).unadapt(node);
	//			}
	//		}
	//
	//	}

	/**
	 * @see PropagatingAdapter#initializeForFactory(INodeAdapterFactory,
	 *      INodeNotifier)
	 */
	public void initializeForFactory(INodeAdapterFactory factory, INodeNotifier node) {
		// we're DOM specific implimentation
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
		return type.equals(PropagatingAdapterClass);
	}

	protected boolean isInteresting(Object newValue) {
		return (newValue != null && (newValue instanceof Element || newValue instanceof Document || newValue instanceof DocumentType));
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// DMW, 2002.8.10. I changed this so we only propagate to Elements ...
		// not attributes too!
		// I'm assuming this will help performance and memory, but don't know
		// if anyone was depending on
		// this being proagate to attributes.
		if (eventType == INodeNotifier.ADD && isInteresting(newValue)) {
			propagateTo((IDOMNode) newValue);
		}
		//	else if (eventType == INodeNotifier.CONTENT_CHANGED) {
		//		notifier.getAdapterFor(PropagatingAdapterClass);
		//	}
		//	else if (eventType == INodeNotifier.CHANGE) {
		//	}
		//		else if (eventType == INodeNotifier.REMOVE &&
		// isInteresting(oldValue)) {
		//			unadaptOnRemove((XMLNode)oldValue);
		//		}
		//	else if (eventType == INodeNotifier.STRUCTURE_CHANGED) {
		//	}
	}

	protected void propagateTo(IDOMNode node) {
		// get adapter to ensure its created
		node.getAdapterFor(PropagatingAdapterClass);
		adaptOnCreate(node);
		propagateToChildren(node);
	}

	protected void propagateToChildren(IDOMNode parent) {
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			propagateTo((IDOMNode) child);
		}
	}

	/**
	 * @see PropagatingAdapter#release()
	 */
	public void release() {
		if (adaptOnCreateFactories != null) {
			Iterator iterator = adaptOnCreateFactories.iterator();
			while (iterator.hasNext()) {
				INodeAdapterFactory factory = (INodeAdapterFactory) iterator.next();
				factory.release();
			}
		}
	}

}
