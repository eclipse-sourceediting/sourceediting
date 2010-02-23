/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (Intalio) - bug 300434 - Make inner classes static where possibl *     
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
import org.eclipse.wst.sse.core.internal.provisional.tasks.IFileTaskScanner;
import org.eclipse.wst.sse.core.utils.StringUtils;

public class FileTaskScannerRegistryReader {
	private static class ScannerInfo {
		String fId;
		IFileTaskScanner fScanner;

		ScannerInfo(String id, IFileTaskScanner scanner) {
			super();
			fId = id;
			fScanner = scanner;
		}

		public boolean equals(Object obj) {
			return obj instanceof ScannerInfo && fId.equals(((ScannerInfo) obj).fId);
		}

		public IFileTaskScanner getScanner() {
			return fScanner;
		}
	}

	private static FileTaskScannerRegistryReader _instance = null;

	public static FileTaskScannerRegistryReader getInstance() {
		if (_instance == null) {
			_instance = new FileTaskScannerRegistryReader();
		}
		return _instance;
	}

	private String ATT_CLASS = "class"; //$NON-NLS-1$

	private String ATT_CONTENT_TYPES = "contentTypeIds"; //$NON-NLS-1$

	private String ATT_ID = "id"; //$NON-NLS-1$

	private IConfigurationElement[] fScannerElements;

	// a mapping from content types to ScannerInfo instances
	private Map fScannerInfos = null;

	private String NAME_SCANNER = "scanner"; //$NON-NLS-1$

	private String SCANNER_EXTENSION_POINT_ID = SSECorePlugin.ID + ".taskscanner"; //$NON-NLS-1$

	private FileTaskScannerRegistryReader() {
		super();
	}

	IFileTaskScanner[] getFileTaskScanners(IContentType[] contentTypes) {
		if (fScannerElements == null) {
			readRegistry();
		}

		List scannerInfos = new ArrayList(1);

		for (int i = 0; i < contentTypes.length; i++) {
			ScannerInfo[] scannerInfosForContentType = (ScannerInfo[]) fScannerInfos.get(contentTypes[i].getId());
			if (scannerInfosForContentType == null) {
				scannerInfosForContentType = loadScanners(contentTypes[i]);
			}
			// only add non-duplicate scanners
			for (int j = 0; j < scannerInfosForContentType.length; j++) {
				if (!scannerInfos.contains(scannerInfosForContentType[j])) {
					scannerInfos.add(scannerInfosForContentType[j]);
				}
			}
		}
		IFileTaskScanner[] scanners = new IFileTaskScanner[scannerInfos.size()];
		for (int i = 0; i < scanners.length; i++) {
			scanners[i] = ((ScannerInfo) scannerInfos.get(i)).getScanner();
		}
		return scanners;
	}

	public String[] getSupportedContentTypeIds() {
		if (fScannerElements == null) {
			readRegistry();
		}

		// find the relevant extensions
		List types = new ArrayList(0);
		IConfigurationElement[] scannerElements = fScannerElements;
		for (int j = 0; j < scannerElements.length; j++) {
			if (!scannerElements[j].getName().equals(NAME_SCANNER))
				continue;
			String[] contentTypeIds = StringUtils.unpack(scannerElements[j].getAttribute(ATT_CONTENT_TYPES));
			for (int i = 0; i < contentTypeIds.length; i++) {
				if (!types.contains(contentTypeIds[i])) {
					types.add(contentTypeIds[i]);
				}
			}
		}

		return (String[]) types.toArray(new String[types.size()]);
	}

	private ScannerInfo[] loadScanners(IContentType contentType) {
		List elements = new ArrayList(0);
		ScannerInfo[] scannerInfos = null;
		IConfigurationElement[] delegateElements = fScannerElements;
		if (contentType != null) {
			IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
			for (int j = 0; j < delegateElements.length; j++) {
				if (!delegateElements[j].getName().equals(NAME_SCANNER))
					continue;
				String[] supportedContentTypeIds = StringUtils.unpack(delegateElements[j].getAttribute(ATT_CONTENT_TYPES));
				IContentType[] supportedContentTypes = new IContentType[supportedContentTypeIds.length];
				for (int k = 0; k < supportedContentTypeIds.length; k++) {
					supportedContentTypes[k] = contentTypeManager.getContentType(supportedContentTypeIds[k].trim());
				}
				for (int k = 0; k < supportedContentTypeIds.length; k++) {
					// allow subtypes to be returned as well
					if (supportedContentTypes[k] != null && contentType.isKindOf(supportedContentTypes[k])) {
						elements.add(delegateElements[j]);
					}
				}
			}
			// instantiate and save the scanners
			List scannerInfoList = new ArrayList(elements.size());
			for (int i = 0; i < elements.size(); i++) {
				try {
					IFileTaskScanner scanner = (IFileTaskScanner) ((IConfigurationElement) elements.get(i)).createExecutableExtension(ATT_CLASS);
					if (scanner != null) {
						scannerInfoList.add(new ScannerInfo(((IConfigurationElement) elements.get(i)).getAttribute(ATT_ID), scanner));
					}
				}
				catch (CoreException e) {
					Logger.logException("Non-fatal exception creating task scanner for " + contentType.getId(), e); //$NON-NLS-1$
				}
			}
			scannerInfos = (ScannerInfo[]) scannerInfoList.toArray(new ScannerInfo[scannerInfoList.size()]);
			fScannerInfos.put(contentType.getId(), scannerInfos);
			if (Logger.DEBUG_TASKSREGISTRY) {
				System.out.println("Created " + scannerInfos.length + " task scanner for " + contentType.getId()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return scannerInfos;
	}

	private void readRegistry() {
		fScannerInfos = new HashMap();
		// Just remember the elements, so plugins don't have to be activated,
		// unless extension attributes match those of interest
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(SCANNER_EXTENSION_POINT_ID);
		if (point != null) {
			fScannerElements = point.getConfigurationElements();
		}
	}
}
