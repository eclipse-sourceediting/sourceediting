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
package org.eclipse.wst.sse.core.cleanup;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.Assert;

public class StructuredContentCleanupHandlerImpl implements StructuredContentCleanupHandler {
	protected Map fCleanupProcessors;

	/**
	 * @see com.ibm.sed.partitionCleanup.StructuredContentCleanupHandler#getCleanupProcessor(java.lang.String)
	 */
	public IStructuredCleanupProcessor getCleanupProcessor(String contentType) {
		Assert.isNotNull(contentType);

		if (fCleanupProcessors == null)
			return null;

		return (IStructuredCleanupProcessor) fCleanupProcessors.get(contentType);
	}

	/**
	 * @see com.ibm.sed.partitionCleanup.StructuredContentCleanupHandler#setCleanupProcessor(com.ibm.sed.partitionCleanup.CleanupProcessor,
	 *      java.lang.String)
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
}
