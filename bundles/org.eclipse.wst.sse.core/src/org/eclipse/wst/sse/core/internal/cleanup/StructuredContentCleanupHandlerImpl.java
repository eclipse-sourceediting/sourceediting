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
package org.eclipse.wst.sse.core.internal.cleanup;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.Assert;

public class StructuredContentCleanupHandlerImpl implements StructuredContentCleanupHandler {
	protected Map fCleanupProcessors;

	public IStructuredCleanupProcessor getCleanupProcessor(String contentType) {
		Assert.isNotNull(contentType);

		if (fCleanupProcessors == null)
			return null;

		return (IStructuredCleanupProcessor) fCleanupProcessors.get(contentType);
	}

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
