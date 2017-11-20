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

public class DeclNode {
	static public final int INVALID = -1;
	static public final int ELEMENT = 1;
	static public final int ATTLIST = 2;
	static public final int ENTITY = 3;
	static public final int INTERNAL_ENTITY = 4;
	static public final int EXTERNAL_ENTITY = 5;
	static public final int INTERNAL_GENERAL_ENTITY = 6;
	static public final int INTERNAL_PARAMETER_ENTITY = 7;
	static public final int EXTERNAL_GENERAL_ENTITY = 8;
	static public final int EXTERNAL_PARAMETER_ENTITY = 9;
	static public final int PARAMETER_ENTITY_REFERENCE = 10;
	static public final int NOTATION = 11;
	static public final int COMMENT = 12;

	static public final int START_DTD = 21;
	static public final int END_DTD = 22;
	static public final int START_ENTITY_DTD = 23;
	static public final int END_ENTITY_DTD = 24;
	static public final int ERROR = 25;

	public int type = INVALID;
	private String declName = null;
	private String contentString = null;
	private String errorMessage = null;
	private String comment = null;

	//
	// Constants
	//

	/** Default constructor. */
	public DeclNode(int type) {
		this(null, type, null);
	}

	public DeclNode(String name, int type, String contentString) {
		this.declName = name;
		this.type = type;
		this.contentString = contentString;
	}

	public String getDeclName() {
		return declName;
	}

	public void setDeclName(String name) {
		declName = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String message) {
		errorMessage = message;
	}

	public String getContentString() {
		return contentString;
	}

	public void setContentString(String content) {
		this.contentString = content;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String toString()

	{
		return "Node Name: " + declName + " Type: " + type + " ContentString: " + contentString; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}


}
