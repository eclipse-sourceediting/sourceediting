/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.projection;

import org.eclipse.jface.text.Position;
import org.eclipse.wst.dtd.core.internal.Comment;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Unrecognized;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.projection.AbstractStructuredFoldingStrategy;
import org.eclipse.wst.xml.ui.internal.projection.XMLCommentFoldingPosition;
import org.eclipse.wst.xml.ui.internal.projection.XMLElementFoldingPosition;


/**
 * A folding strategy for DTD structured documents.
 * See AbstractStructuredFoldingStrategy for more details.
 * 
 * This strategy is rather lame because the DTD parser does not
 * create regions for syntax such as <[ ]> so folding on it
 * can not currently occur.
 */
public class DTDFoldingStrategy extends AbstractStructuredFoldingStrategy {

	/**
	 * Create an instance of the folding strategy.
	 * Be sure to set the viewer and document after creation.
	 */
	public DTDFoldingStrategy() {
		super();
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.internal.projection.AbstractFoldingStrategy#calcNewFoldPosition(org.eclipse.wst.sse.core.internal.provisional.IndexedRegion)
	 */
	protected Position calcNewFoldPosition(IndexedRegion indexedRegion) {
		Position newPos = null;
		
		//only want to fold regions with a valid range
		if(indexedRegion.getStartOffset() >= 0 && indexedRegion.getLength() >= 0) {
			IStructuredDocumentRegion structRegion = ((DTDNode) indexedRegion).getStructuredDTDDocumentRegion();
			//if Comment then use comment folding position
			//else use element folding position
			if(indexedRegion instanceof Comment) {
				newPos = new XMLCommentFoldingPosition(structRegion);
			} else {
				newPos = new XMLElementFoldingPosition(structRegion);
			}
		}
		return newPos;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.projection.AbstractFoldingStrategy#indexedRegionValidType(org.eclipse.wst.sse.core.internal.provisional.IndexedRegion)
	 */
	protected boolean indexedRegionValidType(IndexedRegion indexedRegion) {
		//can only fold DTDNodes
		return (indexedRegion instanceof DTDNode && !(indexedRegion instanceof Unrecognized));
	}
}
