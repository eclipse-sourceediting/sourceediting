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

import java.util.Enumeration;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 */
class CMAttributeDeclarationBuddySystem extends CMNodeBuddySystem implements CMAttributeDeclaration {


	public CMAttributeDeclarationBuddySystem(CMAttributeDeclaration self, CMAttributeDeclaration buddy, boolean isXHTML) {
		super(self, buddy, isXHTML);
	}

	/*
	 * @see CMAttributeDeclaration#getAttrName()
	 */
	public String getAttrName() {
		return getSelf().getAttrName();
	}

	/*
	 * @see CMAttributeDeclaration#getAttrType()
	 */
	public CMDataType getAttrType() {
		return getSelf().getAttrType();
	}

	/*
	 * @see CMAttributeDeclaration#getDefaultValue()
	 * @deprecated in superclass
	 */
	public String getDefaultValue() {
		return getSelf().getDefaultValue();
	}

	/*
	 * @see CMAttributeDeclaration#getEnumAttr()
	 * @deprecated in superclass
	 */
	public Enumeration getEnumAttr() {
		return getSelf().getEnumAttr();
	}

	/*
	 * @see CMAttributeDeclaration#getUsage()
	 */
	public int getUsage() {
		return getSelf().getUsage();
	}

	private CMAttributeDeclaration getSelf() {
		return (CMAttributeDeclaration) self;
	}
}
