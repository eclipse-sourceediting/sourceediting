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
package org.eclipse.wst.xml.ui.reconcile;

import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileStepAdapter;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @deprecated moving toward reconcileValidator extension point
 */
public class ReconcilerAdapterFactoryForXML extends AbstractAdapterFactory {

	/**
	 * This flag governs whether or not this ReconcilerFactory is responsible
	 * for marking nodes "dirty" when an adapter is created for them. This is
	 * true on startup, then set to false thereafter.
	 */
	protected boolean fShouldMarkForReconciling = false;

	protected AbstractReconcileStepAdapter singleton = null;

	public ReconcilerAdapterFactoryForXML() {
		this(IReconcileStepAdapter.class, true);
	}

	/**
	 * @param adapterKey
	 * @param registerAdapters
	 */
	public ReconcilerAdapterFactoryForXML(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	protected void adaptAll(Node top) {
		int length = top.getChildNodes().getLength();
		NodeList children = top.getChildNodes();
		for (int i = 0; i < length; i++) {
			adaptAll(children.item(i));
		}
		((INodeNotifier) top).getAdapterFor(adapterKey);
	}

	public void adaptAll(XMLModel model) {
		if (adapterKey != null)
			adaptAll(model.getDocument());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.model.AbstractAdapterFactory#createAdapter(com.ibm.sed.model.INodeNotifier)
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {
		if (target instanceof Node) {
			Node nodeTarget = (Node) target;
			if (nodeTarget.getNodeType() == Node.ELEMENT_NODE || nodeTarget.getNodeType() == Node.DOCUMENT_NODE || nodeTarget.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
				if (singleton == null) {
					this.singleton = new ReconcileStepAdapterForXML();
				}
				// (pa) perf: don't do this on initial startup
				if (shouldMarkForReconciling())
					singleton.markForReconciling(target);
				return singleton;
			}
		}
		return null;
	}

	public void release() {
		if (this.singleton != null)
			this.singleton.release();
	}

	public void setShouldMarkForReconciling(boolean should) {
		fShouldMarkForReconciling = should;
	}

	public boolean shouldMarkForReconciling() {
		return fShouldMarkForReconciling;
	}
}
