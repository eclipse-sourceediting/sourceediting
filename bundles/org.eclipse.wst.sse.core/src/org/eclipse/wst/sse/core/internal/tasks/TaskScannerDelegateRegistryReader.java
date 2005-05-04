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
package org.eclipse.wst.sse.core.internal.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.util.StringUtils;

class TaskScannerDelegateRegistryReader {
	private static final boolean _debugReader = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/registry")); //$NON-NLS-1$ //$NON-NLS-2$

	private String ATT_CLASS = "class"; //$NON-NLS-1$
	private String ATT_CONTENT_TYPES = "contentTypeIds"; //$NON-NLS-1$
	private String DELEGATE_EXTENSION_POINT_ID = SSECorePlugin.ID + ".taskscanner"; //$NON-NLS-1$

	private IConfigurationElement[] fDelegateElements;

	// a mapping from content types to ITaskScannerDelegate instances
	private Map fDelegates = null;

	private String NAME_SCANNER = "scanner"; //$NON-NLS-1$

	TaskScannerDelegateRegistryReader() {
		super();
	}

	public ITaskScannerDelegate[] getTaskScannerDelegates(String contentTypeID) {
		if (fDelegateElements == null) {
			readRegistry();
		}
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		ITaskScannerDelegate[] delegates = (ITaskScannerDelegate[]) fDelegates.get(contentTypeID);
		if (delegates == null) {
			// fine the relevant extensions
			List elements = new ArrayList(0);
			IConfigurationElement[] delegateElements = fDelegateElements;
			IContentType contentType = contentTypeManager.getContentType(contentTypeID);
			if (contentType != null) {
				for (int j = 0; j < delegateElements.length; j++) {
					if (!delegateElements[j].getName().equals(NAME_SCANNER))
						continue;
					String[] contentTypeIds = StringUtils.unpack(delegateElements[j].getAttribute(ATT_CONTENT_TYPES));
					IContentType[] contentTypes = new IContentType[contentTypeIds.length];
					for (int i = 0; i < contentTypeIds.length; i++) {
						contentTypes[i] = contentTypeManager.getContentType(contentTypeIds[i].trim());
					}
					for (int k = 0; k < contentTypeIds.length; k++) {
						// allow wildcards to be returned as well
						if (contentTypes[k] != null && contentType.isKindOf(contentTypes[k])) {
							elements.add(delegateElements[j]);
						}
					}
				}
				// instantiate and save them
				List delegateList = new ArrayList(elements.size());
				for (int i = 0; i < elements.size(); i++) {
					try {
						ITaskScannerDelegate delegate = (ITaskScannerDelegate) ((IConfigurationElement) elements.get(i)).createExecutableExtension(ATT_CLASS);
						if (delegate != null) {
							delegateList.add(delegate);
						}
					}
					catch (CoreException e) {
						Logger.logException("Non-fatal exception creating task scanner delegate for " + contentTypeID, e); //$NON-NLS-1$
					}
				}
				delegates = (ITaskScannerDelegate[]) delegateList.toArray(new ITaskScannerDelegate[delegateList.size()]);
				fDelegates.put(contentTypeID, delegates);
				if (_debugReader) {
					System.out.println("Created " + delegates.length + " task scanner delegate for " + contentTypeID); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return delegates;
	}

	private void readRegistry() {
		fDelegates = new HashMap();
		// Just remember the elements, so plugins don't have to be activated,
		// unless extension attributes match those of interest
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(DELEGATE_EXTENSION_POINT_ID);
		if (point != null) {
			fDelegateElements = point.getConfigurationElements();
		}
	}
}
