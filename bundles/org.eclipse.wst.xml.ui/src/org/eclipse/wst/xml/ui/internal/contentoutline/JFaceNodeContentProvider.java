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
package org.eclipse.wst.xml.ui.internal.contentoutline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.document.DOMModel;


/**
 * An ITreeContentProvider for a TreeViewers used to display DOM nodes. This
 * content provider takes an adapter factory to create JFace adapters for the
 * nodes in the tree.
 */
public class JFaceNodeContentProvider implements ITreeContentProvider {
	protected IAdapterFactory adapterFactory;

	public JFaceNodeContentProvider(IAdapterFactory jfaceAdapterFactory) {
		super();
		this.adapterFactory = jfaceAdapterFactory;
	}

	/**
	 * The visual part that is using this content provider is about to be
	 * disposed. Deallocate all allocated SWT resources.
	 */
	public void dispose() {
	}

	/**
	 * Returns the JFace adapter for the specified object.
	 * 
	 * @return com.ibm.sed.view.tree.DOMJFaceAdapter The JFace adapter
	 * @param adaptable
	 *            java.lang.Object The object to get the adapter for
	 */
	protected IJFaceNodeAdapter getAdapter(Object adaptable) {
		if (adaptable instanceof INodeNotifier) {
			INodeAdapter adapter = adapterFactory.adapt((INodeNotifier) adaptable);
			if (adapter instanceof IJFaceNodeAdapter)
				return (IJFaceNodeAdapter) adapter;
		}
		return null;
	}

	public Object[] getChildren(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);

		if (adapter != null)
			return adapter.getChildren(object);

		return new Object[0];
	}

	public Object[] getElements(Object object) {
		// The root is usually an instance of an XMLStructuredModel in
		// which case we want to extract the document.
		Object topNode = object;
		if (object instanceof DOMModel)
			topNode = ((DOMModel) object).getDocument();

		IJFaceNodeAdapter adapter = getAdapter(topNode);

		if (adapter != null)
			return adapter.getElements(topNode);

		return new Object[0];
	}

	public Object getParent(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);

		if (adapter != null)
			return adapter.getParent(object);

		return null;
	}

	public boolean hasChildren(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);

		if (adapter != null)
			return adapter.hasChildren(object);

		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
