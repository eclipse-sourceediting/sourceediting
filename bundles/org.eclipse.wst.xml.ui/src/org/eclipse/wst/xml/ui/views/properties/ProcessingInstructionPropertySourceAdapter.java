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
package org.eclipse.wst.xml.ui.views.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.util.Assert;
import org.eclipse.wst.sse.ui.views.properties.CustomPropertyDescriptor;
import org.eclipse.wst.sse.ui.views.properties.IPropertySourceExtension;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;


/**
 * An IPropertySource implementation for a JFace viewer used to display
 * properties of DOM nodes. Requires an adapter factory to create JFace
 * adapters for the nodes in the tree.
 */
public class ProcessingInstructionPropertySourceAdapter implements INodeAdapter, IPropertySource, IPropertySourceExtension {
	protected final static String CATEGORY_ATTRIBUTES = "Instructions"; //$NON-NLS-1$

	protected IPropertyDescriptor[] fDescriptors = null;
	protected Node fNode = null;

	public ProcessingInstructionPropertySourceAdapter(INodeNotifier target) {
		super();
		fNode = (Node) target;
		Assert.isTrue(target instanceof XMLNode);
		Assert.isTrue(fNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE);
	}

	/**
	 * Returns the current collection of property descriptors.
	 * 
	 * @return all valid descriptors.
	 */
	protected IPropertyDescriptor[] createPropertyDescriptors() {
		boolean isXML = ((ProcessingInstruction) fNode).getTarget().equalsIgnoreCase("xml"); //$NON-NLS-1$
		if (!isXML)
			return new IPropertyDescriptor[0];
		CustomPropertyDescriptor[] descriptors = new CustomPropertyDescriptor[2];
		descriptors[0] = new CustomPropertyDescriptor("version", "version", null); //$NON-NLS-1$ //$NON-NLS-2$
		descriptors[0].setCategory(CATEGORY_ATTRIBUTES);
		descriptors[1] = new CustomPropertyDescriptor("encoding", "encoding", null); //$NON-NLS-1$ //$NON-NLS-2$
		descriptors[1].setCategory(CATEGORY_ATTRIBUTES);
		return descriptors;
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
		if (fDescriptors == null) {
			fDescriptors = createPropertyDescriptors();
		}
		return fDescriptors;
	}

	/**
	 * Returns the current value for the named property.
	 * 
	 * @param name
	 *            the name of the property as named by its property descriptor
	 * @return the current value of the property
	 */
	public Object getPropertyValue(Object nameObject) {
		XMLNode node = (XMLNode) fNode;
		String value = null;
		String name = null;
		IStructuredDocumentRegion docRegion = node.getFirstStructuredDocumentRegion();
		if (docRegion == null)
			return null;
		ITextRegionList regions = docRegion.getRegions();
		ITextRegion region = null;
		int i = 2;
		while (value == null && i < regions.size()) {
			region = regions.get(i);
			if (region.getType().equals(XMLRegionContext.XML_TAG_ATTRIBUTE_NAME))
				name = docRegion.getText(region);
			else if (region.getType().equals(XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) && name != null && name.equals(nameObject))
				value = docRegion.getText(region);
			i++;
		}
		if (value != null)
			value = StringUtils.strip(value);
		return value;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return type == IPropertySource.class;
	}

	/**
	 * Returns whether the property value has changed from the default.
	 * 
	 * @return <code>true</code> if the value of the specified property has
	 *         changed from its original default value; <code>false</code>
	 *         otherwise.
	 */
	public boolean isPropertySet(Object propertyObject) {
		return false;
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, java.lang.Object changedFeature, java.lang.Object oldValue, java.lang.Object newValue, int pos) {
	}

	/**
	 * Remove the given attribute from the Node
	 * 
	 * @param propertyObject
	 */
	public void removeProperty(Object propertyObject) {
	}

	/**
	 * Resets the specified property's value to its default value.
	 * 
	 * @param property
	 *            the property to reset
	 */
	public void resetPropertyValue(Object propertyObject) {
	}

	/**
	 * Sets the named property to the given value.
	 * 
	 * @param name
	 *            the name of the property being set
	 * @param value
	 *            the new value for the property
	 */
	public void setPropertyValue(Object nameObject, Object value) {
	}
}
