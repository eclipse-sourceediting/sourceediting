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
package org.eclipse.wst.dtd.core;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;

public class Unrecognized extends TopLevelNode {

	public Unrecognized(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode);
	}

	public Image getImage() {
		return DTDPlugin.getInstance().getImage(DTDResource.UNRECOGNIZEDICON);
	}

	public String getName() {
		String text = getStructuredDocumentRegion().getText();
		if (text.length() <= 30) {
			return text;
		}
		else {
			return text.substring(0, 29) + "..."; //$NON-NLS-1$
		}
	}

}
