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
package org.eclipse.wst.xml.ui.internal.correction;

public interface ProblemIDsXML {
	int Unclassified = 0;
	int EmptyTag = 1;
	int MissingEndTag = 2;
	int AttrsInEndTag = 3;
	int MissingAttrValue = 4;
	int NoAttrValue = 5;
	int SpacesBeforeTagName = 6;
	int SpacesBeforePI = 7;
	int NamespaceInPI = 8;
	int UnknownElement = 9;
	int UnknownAttr = 10;
	int InvalidAttrValue = 11;
	int MissingRequiredAttr = 12;
	int AttrValueNotQuoted = 13;
	int MissingClosingBracket = 14;
}
