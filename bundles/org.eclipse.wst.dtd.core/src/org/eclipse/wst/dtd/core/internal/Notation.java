/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


public class Notation extends ExternalNode {

	public Notation(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode, DTDRegionTypes.NOTATION_TAG);
	}

	public String getImagePath() {
		return DTDResource.NOTATIONICON;
	}
}
