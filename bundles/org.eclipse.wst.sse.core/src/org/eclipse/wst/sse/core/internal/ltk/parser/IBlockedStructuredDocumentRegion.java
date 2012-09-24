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
package org.eclipse.wst.sse.core.internal.ltk.parser;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

/**
 * IBlockedStructuredDocumentRegion is just like an IStructuredDocumentRegion
 * except results from parsing a "block tag" (such as SCRIPT or STYLE).
 * Because these are "variable" partition types, its often handy (efficient)
 * to keep track of the partition type.
 * 
 * @plannedfor 1.0
 */
public interface IBlockedStructuredDocumentRegion extends IStructuredDocumentRegion {
	/**
	 * Return the partion type for this region.
	 * 
	 * @return the partion type.
	 */
	String getPartitionType();

	/**
	 * Sets the partion type.
	 * 
	 * For use by parsers and re-parsers only.
	 * 
	 * @param partitionType
	 */
	void setPartitionType(String partitionType);
}
