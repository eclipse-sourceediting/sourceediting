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
package org.eclipse.wst.sse.core.text;



import java.util.Enumeration;

/**
 * This is a class used to provide a list of StructuredDocumentRegions, so the
 * implementation of how the list is formed can be hidden (encapsulated by
 * this class).
 */
public interface IStructuredDocumentRegionList {

	Enumeration elements();

	int getLength();

	IStructuredDocumentRegion item(int i);
}
