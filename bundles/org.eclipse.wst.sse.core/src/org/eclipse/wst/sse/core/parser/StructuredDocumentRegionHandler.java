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
package org.eclipse.wst.sse.core.parser;



import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;

public interface StructuredDocumentRegionHandler {

	// Sent when a IStructuredDocumentRegion is first parsed
	public void nodeParsed(IStructuredDocumentRegion aCoreStructuredDocumentRegion);

	// Sent when the calling parser's model undergoes a full reset
	//   and any information based upon the old model should be
	//   cleared
	public void resetNodes();
}
