/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.breakpointproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;

/**
 * This registry provides all additional class patterns to be associated with a specific content type
 *
 */
public class ClassPatternRegistry {

	private static ClassPatternRegistry fInstance = null;

	private Map fPatterns = null;

	private ClassPatternRegistry() {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry != null) {
			IConfigurationElement[] elements = RegistryFactory.getRegistry().getConfigurationElementsFor(JSPUIPlugin.ID, "classPatternProvider"); //$NON-NLS-1$
			fPatterns = new HashMap(elements.length);
			for (int i = 0; i < elements.length; i++) {
				final IConfigurationElement element = elements[i];
				final String contentType = element.getAttribute("contentType"); //$NON-NLS-1$
				String pattern = element.getAttribute("pattern"); //$NON-NLS-1$
				if (pattern != null && contentType != null) {
					String old = (String) fPatterns.get(contentType);
					final StringBuffer buffer = old == null ? new StringBuffer() : new StringBuffer(old);
					pattern = pattern.trim();
					if (pattern.length() > 0) {
						if (pattern.charAt(0) != ',' && buffer.length() > 0 && buffer.charAt(buffer.length() - 1) != ',')
							buffer.append(',');
						buffer.append(pattern);
						fPatterns.put(contentType, buffer.toString());
					}
				}
				
			}
		}
	}

	/**
	 * Returns an iterator for the additional class patterns to be associated with the provided content type id.
	 * @param contentType the content type id to find patterns for
	 * @return an iterator for the additional class patterns
	 */
	public String getClassPattern(String contentType) {
		return fPatterns != null ? (String) fPatterns.get(contentType) : null;
	}

	public static synchronized ClassPatternRegistry getInstance() {
		if (fInstance == null) {
			fInstance = new ClassPatternRegistry();
		}
		return fInstance;
	}
}
