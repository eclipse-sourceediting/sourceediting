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



import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;

public interface IStructuredTextReParser {

	void initialize(Object requester, int start, int lengthToReplace, String changes);

	/**
	 * This method is provided in anticipation of eventual multithreading.
	 * This is needed since the intialize method sets state variables that
	 * must be "in sync" with the structuredDocument.
	 */
	public boolean isParsing();

	public IStructuredTextReParser newInstance();

	/**
	 * An entry point for reparsing. It calculates the dirty start and dirty
	 * end flatnodes based on the start point and length of the changes, which
	 * are provided by the initialize method.
	 *  
	 */
	public StructuredDocumentEvent reparse();

	public void setStructuredDocument(IStructuredDocument newStructuredDocument);
}
