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
package org.eclipse.wst.sse.core.modelhandler;

import org.eclipse.core.resources.IFile;

public interface IModelHandlerForJSP extends IModelHandler {

	/**
	 * This method leaves it completely up to the JSP Model Handler Handler 
	 * which EmbeddedTypeHandler to return.
	 * 
	 * NOTE: may not stay, since leaves lots up to the implementer
	 * of this content type
	 * 
	 * @deprecated - see head comment
	 */
	EmbeddedTypeHandler getEmbeddedContentHandlerFor(IFile iFile);
}
