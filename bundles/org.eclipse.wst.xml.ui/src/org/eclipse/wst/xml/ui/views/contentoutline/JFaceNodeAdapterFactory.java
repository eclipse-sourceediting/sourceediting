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
package org.eclipse.wst.xml.ui.views.contentoutline;



import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapterFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;


/**
 * An adapter factory to create JFaceNodeAdapters. Use this adapter factory
 * with a JFaceAdapterContentProvider to display DOM nodes in a tree.
 */
public class JFaceNodeAdapterFactory extends AbstractAdapterFactory implements IJFaceNodeAdapterFactory {
	protected CMDocumentManager cmDocumentManager;
	/**
	 * This keeps track of all the listeners.
	 */
	protected ArrayList fListeners = new ArrayList();

	protected INodeAdapter singletonAdapter;

	public JFaceNodeAdapterFactory() {
		this(IJFaceNodeAdapter.class, true);
	}

	public JFaceNodeAdapterFactory(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	public synchronized void addListener(Object listener) {
		fListeners.add(listener);
	}

	public IAdapterFactory copy() {

		return new JFaceNodeAdapterFactory(this.adapterKey, this.shouldRegisterAdapter);
	}

	/**
	 * Create a new JFace adapter for the DOM node passed in
	 */
	protected INodeAdapter createAdapter(INodeNotifier node) {
		if (singletonAdapter == null) {
			// create the JFaceNodeAdapter
			singletonAdapter = new JFaceNodeAdapter(this);
			initAdapter(singletonAdapter, node);
		}
		return singletonAdapter;
	}

	/**
	 * returns "copy" so no one can modify our list. Its is a shallow copy.
	 */
	public synchronized Collection getListeners() {
		return (Collection) fListeners.clone();
	}

	protected void initAdapter(INodeAdapter adapter, INodeNotifier node) {
		// register for CMDocumentManager events
		if (((JFaceNodeAdapter) adapter).getCMDocumentManagerListener() != null) {
			ModelQueryAdapter mqadapter = (ModelQueryAdapter) node.getAdapterFor(ModelQueryAdapter.class);
			if (mqadapter != null) {
				ModelQuery mquery = mqadapter.getModelQuery();
				if (mquery != null && mquery.getCMDocumentManager() != null) {
					cmDocumentManager = mquery.getCMDocumentManager();
					cmDocumentManager.addListener(((JFaceNodeAdapter) adapter).getCMDocumentManagerListener());
				}
			}
		}
	}

	public void release() {
		// deregister from CMDocumentManager events
		if (cmDocumentManager != null && singletonAdapter != null && ((JFaceNodeAdapter) singletonAdapter).getCMDocumentManagerListener() != null) {
			cmDocumentManager.removeListener(((JFaceNodeAdapter) singletonAdapter).getCMDocumentManagerListener());
		}
	}

	public synchronized void removeListener(Object listener) {
		fListeners.remove(listener);
	}
}
