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
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.text;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;


public class DTDStructuredDocumentRegionFactory {
	public static final int DTD_GENERIC = 5;

	public static IStructuredDocumentRegion createStructuredDocumentRegion(int type) {
		IStructuredDocumentRegion instance = null;
		switch (type) {
			case DTD_GENERIC :
				instance = new BasicStructuredDocumentRegion();
				break;
			default :
				break;
		}
		return instance;
	}
}
