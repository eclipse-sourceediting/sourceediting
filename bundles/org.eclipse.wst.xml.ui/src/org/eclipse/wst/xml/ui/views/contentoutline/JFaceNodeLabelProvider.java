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



import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapter;


/**
 * A class that uses a JFaceNodeAdapterFactory to provide adapters to provide
 * the labels and images for DOM nodes.
 */
public class JFaceNodeLabelProvider implements ILabelProvider {

	protected AdapterFactory adapterFactory;

	/**
	 * JFaceNodeLabelProvider constructor comment.
	 */
	public JFaceNodeLabelProvider(AdapterFactory adapterFactory) {
		super();
		this.adapterFactory = adapterFactory;
	}

	/**
	 * Adds a listener to the label provider. A label provider should inform
	 * its listener about state changes that enforces rendering of the visual
	 * part that uses this label provider.
	 */
	public void addListener(ILabelProviderListener listener) {
		// The label provider state never changes so we do not have
		// to implement this method.
	}

	/**
	 * The visual part that is using this label provider is about to be
	 * disposed. Deallocate all allocated SWT resources.
	 */
	public void dispose() {
		// Nothing to dispose
	}

	/**
	 * Returns the JFace adapter for the specified object.
	 * 
	 * @return com.ibm.sed.view.tree.DOMJFaceAdapter The JFace adapter
	 * @param adaptable
	 *            java.lang.Object The object to get the adapter for
	 */
	protected IJFaceNodeAdapter getAdapter(Object adaptable) {
		return (IJFaceNodeAdapter) adapterFactory.adapt((INodeNotifier) adaptable);
	}

	/**
	 * Returns the image for the label of the given element, for use in the
	 * given viewer.
	 * 
	 * @param element
	 *            The element for which to provide the label image. Element
	 *            can be <code>null</code> indicating no input object is set
	 *            to the viewer.
	 */
	public Image getImage(Object element) {
		return getAdapter(element).getLabelImage(element);
	}

	/**
	 * Returns the text for the label of the given element, for use in the
	 * given viewer.
	 * 
	 * @param element
	 *            The element for which to provide the label text. Element can
	 *            be <code>null</code> indicating no input object is set to
	 *            the viewer.
	 */
	public java.lang.String getText(Object element) {
		// This was returning null, on occasion ... probably should not be,
		// but
		// took the quick and easy way out for now. (dmw 3/8/01)
		String result = getAdapter(element).getLabelText(element);
		if (result == null)
			result = "";//$NON-NLS-1$
		return result;
	}

	/**
	 * Checks whether this label provider is affected by the given domain
	 * event.
	 */
	public boolean isAffected(Object dummy) {//DomainEvent event) {
		//return event.isModifier(DomainEvent.NON_STRUCTURE_CHANGE);
		return true;

	}

	/**
	 * Returns whether the label would be affected by a change to the given
	 * property of the given element. This can be used to optimize a
	 * non-structural viewer update. If the property mentioned in the update
	 * does not affect the label, then the viewer need not update the label.
	 * 
	 * @param element
	 *            the element
	 * @param property
	 *            the property
	 * @return <code>true</code> if the label would be affected, and
	 *         <code>false</code> if it would be unaffected
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * Removes a listener from the label provider.
	 */
	public void removeListener(ILabelProviderListener listener) {
		// The label provider state never changes so we do not have
		// to implement this method.
	}
}
