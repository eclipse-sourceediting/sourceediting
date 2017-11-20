/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.Logger;


/**
 * This class reads the plugin manifests and adds each specified gramamr
 * annotation file with the AnnotationProvider
 */
public class AnnotationFileRegistryReader {
	protected static final String EXTENSION_POINT_ID = "annotationFiles"; //$NON-NLS-1$
	protected static final String TAG_NAME = "annotationFile"; //$NON-NLS-1$
	protected static final String ATT_PUBLIC_ID = "publicId"; //$NON-NLS-1$
	protected static final String ATT_LOCATION = "location"; //$NON-NLS-1$

	private AnnotationFileRegistry annotationFileRegistry;

	public AnnotationFileRegistryReader(AnnotationFileRegistry annotationFileRegistry) {
		this.annotationFileRegistry = annotationFileRegistry;
	}

	/**
	 * read from plugin registry and parse it.
	 */
	public void readRegistry() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		String pluginId = "org.eclipse.wst.xml.core"; //$NON-NLS-1$
		IExtensionPoint point = extensionRegistry.getExtensionPoint(pluginId, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				readElement(elements[i]);
			}
		}
	}

	/**
	 * readElement() - parse and deal with an extension like:
	 * 
	 * <extension
	 * point="org.eclipse.wst.xml.core.internal.contentmodel.util_implementation">
	 * <util_implementation class =
	 * corg.eclipse.wst.baseutil.CMUtilImplementationImpl /> </extension>
	 */
	protected void readElement(IConfigurationElement element) {
		if (element.getName().equals(TAG_NAME)) {
			String publicId = element.getAttribute(ATT_PUBLIC_ID);
			String location = element.getAttribute(ATT_LOCATION);
			if (publicId != null && location != null) {
				try {
					String bundleId = element.getNamespace();
					AnnotationFileInfo fileInfo = new AnnotationFileInfo(location, bundleId);
					annotationFileRegistry.addAnnotationFile(publicId, fileInfo);
				}
				catch (Exception e) {
					Logger.logException("problem adding annotation file " + location, e); //$NON-NLS-1$
				}
			}
		}
	}
}
