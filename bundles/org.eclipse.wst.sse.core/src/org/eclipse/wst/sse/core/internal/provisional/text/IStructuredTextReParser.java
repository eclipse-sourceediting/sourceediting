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
package org.eclipse.wst.sse.core.internal.provisional.text;


import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;

/**
 * IStructuredTextReParser describes the characteristics and responsibilities
 * for reparsing a structured document.
 * 
 * @plannedfor 1.0
 */
public interface IStructuredTextReParser {

	/**
	 * Begins the process of reparsing, by providing the information needed
	 * for the reparse. The reparse is actually performed when the reparse
	 * method is called. Will through an IllegalStateException if document as
	 * not be set.
	 * 
	 * @param requester
	 * @param start
	 * @param lengthToReplace
	 * @param changes
	 */
	void initialize(Object requester, int start, int lengthToReplace, String changes);

	/**
	 * This method is provided to enable multiple threads to reparse a
	 * document. This is needed since the intialize method sets state
	 * variables that must be "in sync" with the structuredDocument.
	 */
	public boolean isParsing();

	/**
	 * Returns a new instance of this reparser, used when cloning documents.
	 * 
	 * @return a new instance of this reparser.
	 */
	public IStructuredTextReParser newInstance();

	/**
	 * An entry point for reparsing. It needs to calculates the dirty start
	 * and dirty end in terms of structured document regions based on the
	 * start point and length of the changes, which are provided by the
	 * initialize method. Will through an IllegalStateException if document as
	 * not be set.
	 * 
	 */
	public StructuredDocumentEvent reparse();



	/**
	 * The reparser is initialized with its document, either in or shortly
	 * after its constructed is called.
	 * 
	 * @param structuredDocument
	 */
	public void setStructuredDocument(IStructuredDocument newStructuredDocument);
}
