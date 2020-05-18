/*******************************************************************************
 * Copyright (c) 2009, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

public interface JSP21Namespace extends JSP20Namespace {
	String JSP21_URI = "";//$NON-NLS-1$
	String ATTR_NAME_TRIM_DIRECTIVE_WHITESPACES = "trimDirectiveWhitespaces"; //$NON-NLS-1$
	String ATTR_NAME_DEFERRED_SYNTAX_ALLOWED_AS_LITERAL = "deferredSyntaxAllowedAsLiteral"; //$NON-NLS-1$
	String ATTR_NAME_OMIT = "omit";  //$NON-NLS-1$
}
