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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLEntityDeclaration;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;



/**
 */
class HTMLEntityDeclImpl extends CMNodeImpl implements HTMLEntityDeclaration {

	private java.lang.String value = null;

	/**
	 * CMEntityDeclImpl constructor comment.
	 * @param entityName java.lang.String; Entity name.
	 * @param entityValue java.lang.String; Value string.
	 */
	public HTMLEntityDeclImpl(String entityName, String entityValue) {
		super(entityName);
		value = entityValue;
	}

	/**
	 * getName method
	 * @return java.lang.String
	 */
	public String getName() {
		return getNodeName();
	}

	/**
	 * Get CMNode type.<br>
	 * @return int; Always return ENTITY_DECLARATION.
	 */
	public int getNodeType() {
		return CMNode.ENTITY_DECLARATION;
	}

	/**
	 * getValue method
	 * @return java.lang.String
	 */
	public String getValue() {
		return value;
	}

	/**
	 */
	public boolean supports(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE))
			return true;
		return super.supports(propertyName);
	}

	/**
	 * Entities in HTML documents are always treated with ignoring cases.
	 * Because no special entities are defined in JSP 1.0, this method
	 * can always return <code>true</code>.<br>
	 */
	public Object getProperty(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE))
			return new Boolean(true);
		return super.getProperty(propertyName);
	}
}
