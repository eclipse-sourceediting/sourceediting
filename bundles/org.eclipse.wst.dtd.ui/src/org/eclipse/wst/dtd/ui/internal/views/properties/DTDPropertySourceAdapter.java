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
package org.eclipse.wst.dtd.ui.internal.views.properties;

import java.util.Stack;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.properties.CustomPropertyDescriptor;


/**
 * An IPropertySource implementation for a JFace viewer used to display
 * properties of DOM nodes. Requires an adapter factory to create JFace
 * adapters for the nodes in the tree.
 */
public class DTDPropertySourceAdapter implements INodeAdapter, IPropertySource {
	protected final static String CATEGORY_ATTRIBUTES = "Attributes"; //$NON-NLS-1$

	private static final String ID_NAME = DTDUIMessages.DTDPropertySourceAdapter_0; //$NON-NLS-1$
	private static final String ID_TEXT = DTDUIMessages.DTDPropertySourceAdapter_1; //$NON-NLS-1$

	protected IPropertyDescriptor[] fDescriptors = null;
	protected INodeNotifier fNode = null;

	protected Stack fValuesBeingSet = new Stack();

	public DTDPropertySourceAdapter(INodeNotifier target) {
		super();
		fNode = target;
	}

	/**
	 * @return
	 */
	private IPropertyDescriptor[] createPropertyDescriptors() {
		CustomPropertyDescriptor nameDescriptor = new CustomPropertyDescriptor(ID_NAME, ID_NAME, null);
		nameDescriptor.setCategory(DTDUIMessages.DTDPropertySourceAdapter_2); //$NON-NLS-1$
		// CustomPropertyDescriptor contentDescriptor = new
		// CustomPropertyDescriptor(ID_TEXT, ID_TEXT, null);
		// contentDescriptor.setCategory("Attributes");
		return new IPropertyDescriptor[]{nameDescriptor};
	}

	/**
	 * Returns a value for this Node that can be editted in a property sheet.
	 * 
	 * @return a value that can be editted
	 */
	public Object getEditableValue() {
		return null;
	}

	/**
	 * Returns the current collection of property descriptors.
	 * 
	 * @return all valid descriptors.
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (fDescriptors == null || fDescriptors.length == 0) {
			fDescriptors = createPropertyDescriptors();
		}
		else {
			updatePropertyDescriptors();
		}
		return fDescriptors;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object value = null;
		if (id.equals(ID_NAME) && fNode instanceof DTDNode) {
			value = ((DTDNode) fNode).getName();
		}
		if (id.equals(ID_TEXT) && fNode instanceof DTDNode) {
			value = ((DTDNode) fNode).getFullNodeText();
		}
		return value;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return type == IPropertySource.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object nameObject, Object value) {
	}

	protected void updatePropertyDescriptors() {
	}
}
