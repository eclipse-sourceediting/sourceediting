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
package org.eclipse.wst.sse.core.cleanup;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.w3c.dom.Node;


public interface IStructuredCleanupProcessor {
	/**
	 * This form of the CleanupProcessor takes an input string as input, creates
	 * an InputStream from the input string, create a temporary model of the
	 * content type specified, cleanups the whole model, then returns the
	 * cleaned up input string.
	 */
	String cleanupContent(String content) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes an input string as input, creates
	 * an InputStream from the input string, create a temporary model of the
	 * content type specified, cleanups the model within start and length, then
	 * returns the cleaned up input string.
	 */
	String cleanupContent(String content, int start, int length) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes an IDocument as input, creates a
	 * temporary model of content type calculated using the IDocument's file
	 * extension, cleanups the whole model, then releases the model.
	 */
	void cleanupDocument(IDocument document) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes an IDocument as input, creates a
	 * temporary model of content type calculated using the IDocument's file
	 * extension, cleanups the model within start and length, then releases the
	 * model.
	 */
	void cleanupDocument(IDocument document, int start, int length) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes an IFile as input, creates a
	 * temporary model of content type calculated using the IFile's file
	 * extension, cleanups the whole model, then releases the model. The IFile is
	 * updated when the last reference of the model is released  in the model
	 * manager.
	 */
	void cleanupFile(IFile file) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes an IFile as input, creates a
	 * temporary model of content type calculated using the IFile's file
	 * extension, cleanups the model within start and length, then releases the
	 * model. The IFile is updated when the last reference of the model is
	 * released in the model manager.
	 */
	void cleanupFile(IFile file, int start, int length) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes a file name as input,creates an
	 * InputStream from the file, create a temporary model of content type
	 * calculated using the file name's file extension, cleanups the whole model,
	 * then releases the model. The file is updated when the last reference of
	 * the model is released in the model manager.
	 */
	void cleanupFileName(String fileName) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes a file name as input,creates an
	 * InputStream from the file, create a temporary model of content type
	 * calculated using the file name's file extension, cleanups the model within
	 * start and length, then releases the model. The file is updated when the
	 * last reference of the model is released in the model manager.
	 */
	void cleanupFileName(String fileName, int start, int length) throws IOException, CoreException;

	/**
	 * This form of the CleanupProcessor takes a model as input, and cleanups the
	 * whole model.
	 */
	void cleanupModel(IStructuredModel structuredModel);

	/**
	 * This form of the CleanupProcessor takes a model as input, and cleanups the
	 * model within start and length.
	 */
	void cleanupModel(IStructuredModel structuredModel, int start, int length);

	/**
	 * This form of the CleanupProcessor takes a node as input, and formats the
	 * node and all its children.
	 */
	void cleanupNode(Node node);
}
