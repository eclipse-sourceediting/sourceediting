/*******************************************************************************
 * Copyright (c) 2001, 20100 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation;

public interface ProblemIDsXML {
	int AttrsInEndTag = 3;
	int AttrValueNotQuoted = 13;
	int EmptyTag = 1;
	int InvalidAttrValue = 11;
	int MissingAttrValue = 4;
	int MissingClosingBracket = 14;
	int MissingEndTag = 2;
	int MissingRequiredAttr = 12;
	int MissingStartTag = 15;
	int NamespaceInPI = 8;
	int NoAttrValue = 5;
	int SpacesBeforePI = 7;
	int SpacesBeforeTagName = 6;
	int Unclassified = 0;
	int UnknownAttr = 10;
	int UnknownElement = 9;
}
