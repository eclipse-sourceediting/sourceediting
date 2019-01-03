/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;

public class ElementDecl extends BaseNode {
	CMNode contentModelNode = null;

	public ElementDecl(String name, String ownerDTD) {
		this(name, ownerDTD, null, null);
	}

	public ElementDecl(String name, String ownerDTD, String comment, ErrorMessage errorMessage) {
		super(name, ownerDTD, comment, errorMessage);
	}

	public CMNode getContentModelNode() {
		return contentModelNode;
	}

	public void setContentModelNode(CMNode cmnode) {
		contentModelNode = cmnode;
	}

}
