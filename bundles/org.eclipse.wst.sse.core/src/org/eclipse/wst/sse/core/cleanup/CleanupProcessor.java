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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.IStructuredModel;


/**
 * @deprecated renamed to IStructuredCleanupProcessor
 *
 * TODO will delete in C5
 */
public interface CleanupProcessor {
	/**
	 * This form of the CleanupProcessor takes an input string as input, creates
	 * an InputStream from the input string, create a temporary model of the
	 * content type specified, cleanups the whole model, then returns the
	 * cleaned up input string.
	 */
	String cleanupContent(String content);

	/**
	 * This form of the CleanupProcessor takes an input string as input, creates
	 * an InputStream from the input string, create a temporary model of the
	 * content type specified, cleanups the model within start and length, then
	 * returns the cleaned up input string.
	 */
	String cleanupContent(String content, int start, int length);

	/**
	 * This form of the CleanupProcessor takes an IFile as input, creates a
	 * temporary model of content type calculated using the IFile's file
	 * extension, cleanups the whole model, then releases the model. The IFile is
	 * updated when the last reference of the model is released  in the model
	 * manager.
	 */
	void cleanupFile(IFile file);

	/**
	 * This form of the CleanupProcessor takes an IFile as input, creates a
	 * temporary model of content type calculated using the IFile's file
	 * extension, cleanups the model within start and length, then releases the
	 * model. The IFile is updated when the last reference of the model is
	 * released in the model manager.
	 */
	void cleanupFile(IFile file, int start, int length);

	/**
	 * This form of the CleanupProcessor takes a file name as input,creates an
	 * InputStream from the file, create a temporary model of content type
	 * calculated using the file name's file extension, cleanups the whole model,
	 * then releases the model. The file is updated when the last reference of
	 * the model is released in the model manager.
	 */
	void cleanupFileName(String fileName);

	/**
	 * This form of the CleanupProcessor takes a file name as input,creates an
	 * InputStream from the file, create a temporary model of content type
	 * calculated using the file name's file extension, cleanups the model within
	 * start and length, then releases the model. The file is updated when the
	 * last reference of the model is released in the model manager.
	 */
	void cleanupFileName(String fileName, int start, int length);

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
}
