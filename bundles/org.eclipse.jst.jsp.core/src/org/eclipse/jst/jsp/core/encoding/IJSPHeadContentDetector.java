/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.encoding;

import java.io.IOException;

import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;

public interface IJSPHeadContentDetector extends IDocumentCharsetDetector {
	String getContentType() throws IOException;

	String getLanguage() throws IOException;

}