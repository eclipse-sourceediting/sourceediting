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
package org.eclipse.wst.sse.core.internal.util;

/**
 * Contains list of script languages and mime types
 */
public interface ScriptLanguageKeys {

	public static final String JAVA = "java"; //$NON-NLS-1$

	public static final String[] JAVA_LANGUAGE_KEYS = new String[]{"java"}; //$NON-NLS-1$

	public static final String JAVASCRIPT = "javascript"; //$NON-NLS-1$
	public static final String[] JAVASCRIPT_LANGUAGE_KEYS = {"javascript", //$NON-NLS-1$
				"javascript1.0", //$NON-NLS-1$
				"javascript1.1", //$NON-NLS-1$
				"javascript1.2", //$NON-NLS-1$
				"javascript1.3", //$NON-NLS-1$
				"javascript1.4", //$NON-NLS-1$
				"javascript1.5", //$NON-NLS-1$
				"javascript1.6", //$NON-NLS-1$
				"jscript", //$NON-NLS-1$
				"sashscript"}; //$NON-NLS-1$

	public static final String[] JAVASCRIPT_MIME_TYPE_KEYS = {"text/javascript", //$NON-NLS-1$
				"text/jscript", //$NON-NLS-1$
				"text/sashscript"}; //$NON-NLS-1$
}
