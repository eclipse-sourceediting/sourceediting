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
package org.eclipse.wst.xml.core.internal.parser;



import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;
import org.eclipse.wst.sse.core.parser.IBlockedStructuredDocumentRegion;

public class BlockStructuredDocumentRegion extends BasicStructuredDocumentRegion implements IBlockedStructuredDocumentRegion {

	private String partitionType;

	/**
	 * A BlockStructuredDocumentRegion is like a IStructuredDocumentRegion, but is the result of a "block scan". 
	 */
	public BlockStructuredDocumentRegion() {
		super();
	}

	public String getPartitionType() {
		if (partitionType == null) {
			// eventually can look up surroundingTag name
			// but this field is primarily entended for future
			// extensibility. This may change.
		}
		return partitionType;
	}

	public void setPartitionType(String partitionType) {
		this.partitionType = partitionType;
	}
}
