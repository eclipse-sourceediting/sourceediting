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
package org.eclipse.wst.sse.core.internal.encoding;

import org.eclipse.core.runtime.QualifiedName;


public interface IContentDescriptionExtended {
	/**
	 * The APPROPRIATE_DEFAULT field is used only when the
	 * IContentType.getDefaultCharset returns null. Its typically set from
	 * user preferences. Known uses cases are HTML and CSS, where there is no
	 * "spec default" for those content types.
	 */
	public static final QualifiedName APPROPRIATE_DEFAULT = new QualifiedName(ICodedResourcePlugin.ID, "appropriateDefault"); //$NON-NLS-1$
	/**
	 * The DETECTED_CHARSET property should be set when the "detected" charset
	 * is different from the java charset, even though functionally
	 * equivelent. This can occur, for example, when the cases are different,
	 * or when an alias name is used instead of the conanical name.
	 */
	public final static QualifiedName DETECTED_CHARSET = new QualifiedName(ICodedResourcePlugin.ID, "detectedCharset"); //$NON-NLS-1$
	/**
	 * The UNSUPPORTED_CHARSET property holds the charset value, if its been
	 * found to be an unsuppoted charset. This is helpful in error messages,
	 * or in cases when even though the charset is invalid, the java charset
	 * is assumed to be the default.
	 */
	public final static QualifiedName UNSUPPORTED_CHARSET = new QualifiedName(ICodedResourcePlugin.ID, "unsupportedCharset"); //$NON-NLS-1$


}
