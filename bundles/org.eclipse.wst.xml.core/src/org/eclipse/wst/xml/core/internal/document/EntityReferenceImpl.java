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
package org.eclipse.wst.xml.core.internal.document;



import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

/**
 * EntityReference class
 */
public class EntityReferenceImpl extends NodeImpl implements EntityReference {

	private String name = null;

	/**
	 * EntityReferenceImpl constructor
	 */
	protected EntityReferenceImpl() {
		super();
	}

	/**
	 * EntityReferenceImpl constructor
	 * 
	 * @param that
	 *            EntityReferenceImpl
	 */
	protected EntityReferenceImpl(EntityReferenceImpl that) {
		super(that);

		if (that != null) {
			this.name = that.name;
		}
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		EntityReferenceImpl cloned = new EntityReferenceImpl(this);
		return cloned;
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		if (this.name == null)
			return new String();
		return this.name;
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return ENTITY_REFERENCE_NODE;
	}

	/**
	 * setName method
	 * 
	 * @param name
	 *            java.lang.String
	 */
	protected void setName(String name) {
		this.name = name;
	}
}
