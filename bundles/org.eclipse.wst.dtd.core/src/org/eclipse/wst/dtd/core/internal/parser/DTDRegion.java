/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal.parser;

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.parser.ContextRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;



public class DTDRegion extends ContextRegion {


	public DTDRegion(String newContext, int newStart, int newLength) {
		super(newContext, newStart, newLength, newLength);
	}


	public StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion flatnode, String changes, int requestStart, int requestLength) {
		return null;
	}
}
