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
package org.eclipse.wst.sse.core.format;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.w3c.dom.Node;


public interface IStructuredFormatProcessor {
	/**
	 * This form of the FormatProcessor takes an input string as input,
	 * creates an InputStream from the input string, create a temporary model
	 * of the content type specified, formats the whole model, then returns
	 * the formatted input string.
	 */
	String formatContent(String content) throws IOException, CoreException;

	/**
	 * This form of the FormatProcessor takes an input string as input,
	 * creates an InputStream from the input string, create a temporary model
	 * of the content type specified, formats the model within start and
	 * length, then returns the formatted input string.
	 */
	String formatContent(String content, int start, int length) throws IOException, CoreException;

	/**
	 * This form of the FormatProcessor takes an IDocument as input, creates a
	 * temporary model of content type calculated using the IDocument's file
	 * extension, formats the whole model, then releases the model.
	 */
	void formatDocument(IDocument document) throws IOException, CoreException;

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
	 * This form of the FormatProcessor takes an IFile as input, creates a
	 * temporary model of content type calculated using the IFile's file
	 * extension, formats the model within start and length, then releases the
	 * model.
	 */
	void formatFile(IFile file, int start, int length) throws IOException, CoreException;

	/**
	 * This form of the FormatProcessor takes a file name as input,creates an
	 * InputStream from the file, create a temporary model of content type
	 * calculated using the file name's file extension, formats the whole
	 * model, then releases the model.
	 */
	void formatFileName(String fileName) throws IOException, CoreException;

	/**
	 * This form of the FormatProcessor takes a file name as input,creates an
	 * InputStream from the file, create a temporary model of content type
	 * calculated using the file name's file extension, formats the model
	 * within start and length, then releases the model.
	 */
	void formatFileName(String fileName, int start, int length) throws IOException, CoreException;

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

	void setProgressMonitor(IProgressMonitor monitor);
}
