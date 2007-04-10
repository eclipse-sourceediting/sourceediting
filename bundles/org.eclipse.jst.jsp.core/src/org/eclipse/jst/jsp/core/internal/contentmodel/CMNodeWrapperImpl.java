/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel;



import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;

public class CMNodeWrapperImpl implements CMNode, CMNodeWrapper {
	private CMNode fNode = null;
	private String fNodeName = null;

	protected String fPrefix = null;

	/**
	 * CMNodeWrapper constructor comment.
	 */
	public CMNodeWrapperImpl(String prefix, CMNode node) {
		super();
		fPrefix = prefix;
		fNode = node;

		fNodeName = fPrefix + ":" + fNode.getNodeName(); //$NON-NLS-1$
	}

	/**
	 * getNodeName method
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return fNodeName;
	}

	/**
	 * getNodeType method
	 * @return int
	 *
	 * Returns one of :
	 *
	 */
	public int getNodeType() {
		return fNode.getNodeType();
	}

	public CMNode getOriginNode() {
		return fNode;
	}

	/**
	 * getProperty method
	 * @return java.lang.Object
	 *
	 * Returns the object property desciped by the propertyName
	 *
	 */
	public Object getProperty(String propertyName) {
		return fNode.getProperty(propertyName);
	}

	/**
	 * supports method
	 * @return boolean
	 *
	 * Returns true if the CMNode supports a specified property
	 *
	 */
	public boolean supports(String propertyName) {
		return fNode.supports(propertyName);
	}
}
