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
package org.eclipse.wst.sse.core.contentmodel;



import org.eclipse.wst.sse.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;


/**
 * Position dependent CMDocument tracker. A tracker matches a
 * IStructuredDocumentRegion with a CMDocument that it enables allowing for
 * position-dependent content models
 */
public interface CMDocumentTracker {

	CMDocument getDocument();

	IStructuredDocumentRegion getStructuredDocumentRegion();
}

