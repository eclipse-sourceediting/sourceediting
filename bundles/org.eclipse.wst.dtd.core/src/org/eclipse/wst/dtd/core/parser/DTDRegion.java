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
package org.eclipse.wst.dtd.core.parser;

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.parser.ContextRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;

public class DTDRegion extends ContextRegion {
	//TODO private String text;
	//TODO protected IStructuredDocumentRegion parent;

	public DTDRegion(String newContext, int newStart, int newLength) {
		super(newContext, newStart, newLength, newLength);
	}

	/* TODO
	 public int getEndOffset() 
	 {
	 return parent.getStartOffset() + getEnd();
	 }

	 public int getStartOffset() 
	 {
	 return parent.getStartOffset() + getStart();
	 }

	 public int getTextEndOffset() 
	 {
	 return parent.getStartOffset() + getTextEnd();
	 }
	 
	 public void setParent(IStructuredDocumentRegion parent)
	 {
	 this.parent = parent;
	 }
	 
	 public IStructuredDocumentRegion getParent()
	 {
	 return parent;
	 }
	 */

	public StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion flatnode, String changes, int requestStart, int requestLength) {
		return null;
	}
}
