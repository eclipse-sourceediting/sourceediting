/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.views.contentoutline;



import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.document.XMLModel;

/**
 * A Content provider for a JFace viewer used to display DOM nodes.
 * This content provider takes an adapter factory to create JFace
 * adapters for the nodes in the tree.
 */
public class JFaceNodeContentProvider implements ITreeContentProvider {
	protected AdapterFactory adapterFactory;

	//protected DomainNotifier domainNotifier;
	/**
	 * JFaceAdapterContentProvider constructor comment.
	 */
	public JFaceNodeContentProvider(AdapterFactory adapterFactory) {
		super();
		this.adapterFactory = adapterFactory;
	}

	/**
	 * The visual part that is using this content provider is about
	 * to be disposed. Deallocate all allocated SWT resources.
	 */
	public void dispose() {
	}

	/**
	 * Returns the JFace adapter for the specified object.
	 * @param adaptable java.lang.Object The object to get the adapter for
	 */
	protected IJFaceNodeAdapter getAdapter(Object adaptable) {
		if (adaptable instanceof INodeNotifier) {
			INodeAdapter adapter = adapterFactory.adapt((INodeNotifier) adaptable);
			if (adapter instanceof IJFaceNodeAdapter)
				return (IJFaceNodeAdapter) adapter;
		}

		return null;
	}

	/**
	 * Returns an enumeration containing all child nodes of the given
	 * element, which represents a node in a tree. The difference to
	 * <code>IStructuredContentProvider.getElements(Object)</code> is
	 * as follows: <code>getElements</code> is called to obtain the 
	 * tree viewer's root elements. Method <code>getChildren</code> is used
	 * to obtain the children of a given node in the tree, which can
	 * can be a root node, too.
	 */
	public Object[] getChildren(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);

		if (adapter != null)
			return adapter.getChildren(object);

		return new Object[0];
	}

	/**
	 * Returns an enumeration with the elements belonging to the
	 * passed element. These elements can be presented as rows in a table,
	 * items in a list etc.
	 */
	public Object[] getElements(Object object) {
		// The root is usually an instance of an XMLStructuredModel in
		// which case we want to extract the document.
		Object topNode = object;
		if (object instanceof XMLModel)
			topNode = ((XMLModel) object).getDocument();

		IJFaceNodeAdapter adapter = getAdapter(topNode);

		if (adapter != null)
			return adapter.getElements(topNode);

		return new Object[0];
	}

	/**
	 * Returns the parent for the given element. This method can 
	 * return <code>null</code> indicating that the parent can't 
	 * be computed. In this case the tree viewer can't expand
	 * a given node correctly if requested.
	 */
	public Object getParent(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);

		if (adapter != null)
			return adapter.getParent(object);

		return null;
	}

	/**
	 * Returns <code>true</code> if the given element has children.
	 * Otherwise <code>false</code> is returned.
	 */
	public boolean hasChildren(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);

		if (adapter != null)
			return adapter.hasChildren(object);

		return false;
	}

	/**
	 * Called when the viewer's input is changing from <code>oldInput</code> to <code>newInput</code>.
	 * Both <code>newInput</code> and <code>oldInput</code> can be <code>null</code>. 
	 * If <code>oldInput</code> is <code>null</code> it is the viewer's first connection to the content provider. 
	 * If <code>newInput</code> is <code>null</code> the visual part is disconnected from any input. 
	 * A typical implementation  of this methods registers the content provider as a listener
	 * to changes on the new input, and deregisters the viewer from the old input.
	 * The content provider then updates the viewer in response to change notifications from the input.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//// If there was no old input, then we must be providing content for this part for the first time...
		//if (oldInput == null) {
		//// If the part is an IDomainListener then make the part start listening to us.
		//if (viewer instanceof IDomainListener)
		//domainNotifier.addDomainListener((IDomainListener) viewer);
		//}
		//// If there is no new input, we must clean ourselves up as if we'd never seen the viewer.
		//else
		//if (newInput == null) {
		//// If the part is an IDomainListener, then we make it stop listening to us.
		//if (viewer instanceof IDomainListener)
		//domainNotifier.removeDomainListener((IDomainListener) viewer);

		//}
	}

	/**
	 * Checks whether the given element is deleted or not.
	 */
	public boolean isDeleted(Object element) {
		return false;
	}
}
