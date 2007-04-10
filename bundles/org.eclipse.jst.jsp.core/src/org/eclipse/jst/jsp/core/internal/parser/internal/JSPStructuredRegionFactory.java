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
package org.eclipse.jst.jsp.core.internal.parser.internal;

import org.eclipse.jst.jsp.core.internal.parser.JSPDirectiveStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredRegionFactory;

/**
 * A simple class to generate instances of StructuredRegions. 
 */
public class JSPStructuredRegionFactory extends XMLStructuredRegionFactory {

	public static IStructuredDocumentRegion createRegion(int type) {
		IStructuredDocumentRegion instance = null;
		switch (type) {
			case JSP_DIRECTIVE :
				instance = new JSPDirectiveStructuredDocumentRegion();
				break;
			default :
				instance = XMLStructuredRegionFactory.createRegion(type);
		}
		return instance;
	}

}
