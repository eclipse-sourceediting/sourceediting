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
package org.eclipse.wst.sse.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


/**
 * Model provider for IBuilderParticipants - provides Structured Documents and
 * Models to participants. Clients should not implement. Models for any
 * resource may be requested, not just for the current delta. The same
 * document and model instances are NOT guaranteed to be returned with each
 * call.
 */

public interface IBuilderModelProvider {
	/**
	 * @param file
	 * @return an IStructuredDocument with the file's contents if the file's
	 *         Content Type is supported, null if not
	 */
	IStructuredDocument getDocument(IFile file);

	/**
	 * @param file
	 * @return an IStructuredModel with the file's contents if the file's
	 *         Content Type is supported, null if not
	 */
	IStructuredModel getModel(IFile file);
}
