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
package org.eclipse.wst.sse.core.internal.format;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.w3c.dom.Node;

/**
 * The main formatting engine.  
 * Loops through all the nodes in an IStructuredModel.
 */
public interface IStructuredFormatProcessor {

	/**
	 * This form of the FormatProcessor takes an IDocument as input, creates a
	 * temporary model of content type calculated using the IDocument's file
	 * extension, formats the model within start and length, then releases the
	 * model.
	 */
	void formatDocument(IDocument document, int start, int length) throws IOException, CoreException;

	/**
	 * This form of the FormatProcessor takes an IFile as input, creates a
	 * temporary model of content type calculated using the IFile's file
	 * extension, formats the whole model, then releases the model.
	 */
	void formatFile(IFile file) throws IOException, CoreException;

	/**
	 * This form of the FormatProcessor takes a model as input, and formats
	 * the whole model.
	 */
	void formatModel(IStructuredModel structuredModel);

	/**
	 * This form of the FormatProcessor takes a model as input, and formats
	 * the model within start and length.
	 */
	void formatModel(IStructuredModel structuredModel, int start, int length);

	/**
	 * This form of the FormatProcessor takes a node as input, and formats the
	 * node and all its children.
	 */
	void formatNode(Node node);

	/**
	 * Sets the progress monitor for this <code>IStructuredFormatProcessor</code>.
	 * The monitor is used to display progress or cancel if the formatter is run 
	 * in a background job.
	 * @param monitor
	 */
	void setProgressMonitor(IProgressMonitor monitor);
}
