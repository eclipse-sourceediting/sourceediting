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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.Assert;

public class StructuredContentCleanupHandlerImpl implements StructuredContentCleanupHandler {
	protected Map fCleanupProcessors;

	/**
	 */
	public void setCleanupProcessor(IStructuredCleanupProcessor cleanupProcessor, String contentType) {
		Assert.isNotNull(contentType);

		if (fCleanupProcessors == null)
			fCleanupProcessors = new HashMap();

		if (fCleanupProcessors == null)
			fCleanupProcessors.remove(contentType);
		else
			fCleanupProcessors.put(contentType, cleanupProcessor);
	}

	/**
	 */
	public IStructuredCleanupProcessor getCleanupProcessor(String contentType) {
		Assert.isNotNull(contentType);

		if (fCleanupProcessors == null)
			return null;

		return (IStructuredCleanupProcessor) fCleanupProcessors.get(contentType);
	}
}
