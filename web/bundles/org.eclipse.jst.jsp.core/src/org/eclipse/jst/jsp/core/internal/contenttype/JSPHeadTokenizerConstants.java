/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.jst.jsp.core.internal.contenttype;

import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizerConstants;

public interface JSPHeadTokenizerConstants extends XMLHeadTokenizerConstants {
	String PageDirectiveStart = "PageDirectiveStart"; //$NON-NLS-1$
	String PageDirectiveEnd = "PageDirectiveEnd"; //$NON-NLS-1$
	String PageLanguage = "PageLanguage"; //$NON-NLS-1$
	String PageEncoding = "PageEncoding"; //$NON-NLS-1$
	String PageContentType = "PageContentType"; //$NON-NLS-1$
}
