/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.internal.contenttype;

public class HeadParserToken {

	private String fText;
	private String fType;
	private int fStart;

	public String getText() {
		return fText;
	}

	public String getType() {
		return fType;
	}

	protected HeadParserToken() {
		super();
	}

	public HeadParserToken(String type, int start, String text) {
		this();
		fType = type;
		fStart = start;
		fText = text;

	}

	public String toString() {
		return ("text: " + fText + " offset: " + fStart + " type: " + fType); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
