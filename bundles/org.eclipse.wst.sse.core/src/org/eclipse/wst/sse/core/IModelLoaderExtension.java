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
package org.eclipse.wst.sse.core;

import org.eclipse.wst.sse.core.text.IStructuredDocument;

/**
 * @author nsd
 */
public interface IModelLoaderExtension {
	/**
	 * Create a Structured Model with the given StructuredDocument instance as
	 * its document (instead of a new document instance as well)
	 */
	IStructuredModel createModel(IStructuredDocument document);
}
