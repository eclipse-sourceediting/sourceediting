/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;


public abstract class BaseNode {
	protected String name = null;
	protected ErrorMessage errorMessage = null;
	// protected String errorMessage = null;
	protected String comment = null;
	protected String ownerDTD = null;

	/** Default constructor. */
	public BaseNode(String name, String ownerDTD) {
		this(name, ownerDTD, null, null);
	}

	public BaseNode(String name, String ownerDTD, String comment, ErrorMessage errorMessage) {
		this.name = name;
		this.ownerDTD = ownerDTD;
		this.comment = comment;
		this.errorMessage = errorMessage;
	}

	public String getNodeName() {
		return name;
	}

	public void setNodeName(String name) {
		this.name = name;
	}

	public String getOwnerDTD() {
		return ownerDTD;
	}

	public void setOwnerDTD(String owner) {
		ownerDTD = owner;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(ErrorMessage message) {
		errorMessage = message;
	}

}
