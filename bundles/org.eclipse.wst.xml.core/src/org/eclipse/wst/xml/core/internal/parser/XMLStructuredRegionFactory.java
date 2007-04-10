/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.parser;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.text.XMLStructuredDocumentRegion;


/**
 * A simple class to generate instances of StructuredRegions.
 */
public class XMLStructuredRegionFactory {
	public final static int JSP_DIRECTIVE = 1003;
	public final static int XML = 1001;
	public final static int XML_BLOCK = 1002;

	public static IStructuredDocumentRegion createRegion(int type) {
		IStructuredDocumentRegion instance = null;
		switch (type) {
			case XML :
				instance = new XMLStructuredDocumentRegion();
				break;
			case XML_BLOCK :
				instance = new BlockStructuredDocumentRegion();
				break;
			default :
				throw new IllegalArgumentException("AbstractRegion::createRegion. Invalid type."); //$NON-NLS-1$
		}
		return instance;
	}

}
