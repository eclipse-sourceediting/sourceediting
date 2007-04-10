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
package org.eclipse.wst.xml.core.internal.commentelement.impl;



import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.commentelement.CommentElementHandler;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;


/**
 */
public class CommentElementRegistry {

	private static CommentElementRegistry fInstance = null;

	public synchronized static CommentElementRegistry getInstance() {
		if (fInstance == null) {
			fInstance = new CommentElementRegistry();
		}
		return fInstance;
	}

	private String EXTENSION_POINT_ID = "commentElementHandler"; //$NON-NLS-1$
	private CommentElementConfiguration[] fConfigurations = null;

	private String PLUGIN_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$

	/**
	 * Constructor for CommentElementRegistry.
	 */
	private CommentElementRegistry() {
		super();
	}

	public CommentElementConfiguration[] getConfigurations() {
		if (fConfigurations == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
			if (point != null) {
				IConfigurationElement[] elements = point.getConfigurationElements();
				fConfigurations = new CommentElementConfiguration[elements.length];
				for (int i = 0; i < elements.length; i++) {
					fConfigurations[i] = new CommentElementConfiguration(elements[i]);
				}
			}
			if (fConfigurations == null) {
				fConfigurations = new CommentElementConfiguration[0];
			}
		}
		return fConfigurations;
	}

	public boolean setupCommentElement(IDOMElement element) {
		CommentElementConfiguration configurations[] = getConfigurations();
		int length = configurations.length;
		for (int i = 0; i < length; i++) {
			CommentElementConfiguration conf = configurations[i];
			boolean isJSP = element.isJSPTag();
			if (isJSP && conf.acceptJSPComment() || !isJSP && conf.acceptXMLComment()) {
				CommentElementHandler handler = conf.getHandler();
				if (handler.isCommentElement(element)) {
					conf.setupCommentElement(element);
					return true;
				}
			}
		}
		return false;
	}
}
