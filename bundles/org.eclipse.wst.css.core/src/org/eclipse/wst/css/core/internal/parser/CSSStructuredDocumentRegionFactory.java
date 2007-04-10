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
package org.eclipse.wst.css.core.internal.parser;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;

public class CSSStructuredDocumentRegionFactory {
	public static IStructuredDocumentRegion createRegion(String type) {
		IStructuredDocumentRegion instance = null;
		if (type != null) {
			instance = new BasicStructuredDocumentRegion();
		}
		else {

		}
		return instance;
	}
}
