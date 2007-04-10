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
package org.eclipse.wst.html.core.internal.contentmodel;



import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * CMNamespace implementation.
 */
class CMNamespaceImpl extends CMNodeImpl implements CMNamespace {

	private java.lang.String prefix = null;

	/**
	 * CMNamespaceImpl constructor comment.
	 */
	public CMNamespaceImpl(String uri, String pfx) {
		super(uri);
		prefix = pfx;
	}

	/**
	 * getNodeType method
	 * @return int
	 *
	 * Returns one of :
	 *
	 */
	public int getNodeType() {
		return CMNode.NAME_SPACE;
	}

	/**
	 * getPrefix method
	 * @return java.lang.String
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * getURI method
	 * @return java.lang.String
	 */
	public String getURI() {
		return getNodeName();
	}
}
