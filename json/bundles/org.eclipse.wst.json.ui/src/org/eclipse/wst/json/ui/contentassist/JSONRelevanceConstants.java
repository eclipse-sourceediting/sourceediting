/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants
 *                                           modified in order to process JSON Objects.                    
 *******************************************************************************/
package org.eclipse.wst.json.ui.contentassist;

import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;

/**
 * some relevance constants for content assist higher relevance means it shows
 * up higher on the list
 */
public interface JSONRelevanceConstants extends IRelevanceConstants {

	int R_OBJECT_KEY = 400;

	// int R_CDATA = 400;
	// int R_CLOSE_TAG = 1500;
	//
	// int R_COMMENT = 100;
	//
	// // moved this above macros
	// int R_DOCTYPE = 600;
	// int R_END_TAG = 1400;
	// int R_END_TAG_NAME = 1100;
	// int R_ENTITY = 1000;
	// int R_JSP = 500;
	//
	// int R_JSP_ATTRIBUTE_VALUE = 700;
	//
	// // (pa) make these the same relevance so proposals are same order for
	// V501
	// int R_MACRO = 500;
	//
	// // add this onto "required" attrs, elements, etc to bubble them up on
	// // sorting...
	// // CMVC 246618
	// int R_REQUIRED = 10;
	// int R_TAG_INSERTION = 500;
	// int R_STRICTLY_VALID_TAG_INSERTION = 600;
	// int R_TAG_NAME = 1200;
	// int R_STRICTLY_VALID_TAG_NAME = 1250;
	// int R_XML_ATTRIBUTE_NAME = 900;
	// int R_XML_ATTRIBUTE_VALUE = 800;
	// int R_XML_DECLARATION = 1300;
}
