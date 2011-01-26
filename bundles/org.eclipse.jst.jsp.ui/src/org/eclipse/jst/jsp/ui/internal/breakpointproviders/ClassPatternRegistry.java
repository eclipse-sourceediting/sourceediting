/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;

import com.ibm.icu.util.StringTokenizer;

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
				final String pattern = element.getAttribute("pattern"); //$NON-NLS-1$
				if (pattern != null && contentType != null) {
					final StringTokenizer tokenizer = new StringTokenizer(pattern, ","); //$NON-NLS-1$
					Set patterns = (Set) fPatterns.get(contentType);
					if (patterns == null) {
						patterns = new HashSet(0);
						fPatterns.put(contentType, patterns);
					}

					while (tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken();
						token = token.trim();
						if (token.length() > 0) {
							patterns.add(token);
						}
					}
				}
				
			}
		}
	}

	/**
	 * Returns the additional class patterns to be associated with the provided content type id.
	 * @param contentType the content type id to find patterns for
	 * @return an String for the additional class patterns
	 */
	public String getClassPattern(String contentType) {
		if (fPatterns == null)
			return null;
		final Set patterns = (Set) fPatterns.get(contentType);
		if (patterns != null) {
			final Iterator it = patterns.iterator();
			final StringBuffer buffer = new StringBuffer();
			while (it.hasNext()) {
				if (buffer.length() > 0)
					buffer.append(',');
				buffer.append(it.next());
			}
			return buffer.toString();
		}
		return null;
	}

	public Iterator getClassPatternSegments(String contentType) {
		Iterator result = EMPTY;
		if (fPatterns != null) {
			Set patterns = (Set) fPatterns.get(contentType);
			if (patterns != null)
				result = patterns.iterator();
		}
		return result;
	}

	private static final Iterator EMPTY = new Iterator() {

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return false;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Object next() {
			return null;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {}
		
	};

	public static synchronized ClassPatternRegistry getInstance() {
		if (fInstance == null) {
			fInstance = new ClassPatternRegistry();
		}
		return fInstance;
	}
}
