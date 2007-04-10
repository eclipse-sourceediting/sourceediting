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
package org.eclipse.wst.html.core.internal.modelquery;

import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 */
class CMNodeBuddySystem implements CMNode {


	protected boolean isXHTML = false;
	protected CMNode self = null;
	protected CMNode buddy = null;

	public CMNodeBuddySystem(CMNode self, CMNode buddy, boolean isXHTML) {
		super();
		this.self = self;
		this.buddy = buddy;
		this.isXHTML = isXHTML;
	}

	/*
	 * @see CMNode#getNodeName()
	 */
	public String getNodeName() {
		return self.getNodeName();
	}

	/*
	 * @see CMNode#getNodeType()
	 */
	public int getNodeType() {
		return self.getNodeType();
	}

	/*
	 * @see CMNode#supports(String)
	 */
	public boolean supports(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE))
			return true;
		if (propertyName.equals(HTMLCMProperties.IS_XHTML))
			return true;
		if (buddy == null)
			return false;
		return buddy.supports(propertyName);
	}

	/*
	 * @see CMNode#getProperty(String)
	 */
	public Object getProperty(String propertyName) {
		if (propertyName.equals(HTMLCMProperties.SHOULD_IGNORE_CASE)) {
			return new Boolean(!isXHTML);
		}
		if (propertyName.equals(HTMLCMProperties.IS_XHTML)) {
			return new Boolean(isXHTML);
		}

		if (buddy == null || (!buddy.supports(propertyName)))
			return null;
		return buddy.getProperty(propertyName);
	}
}
