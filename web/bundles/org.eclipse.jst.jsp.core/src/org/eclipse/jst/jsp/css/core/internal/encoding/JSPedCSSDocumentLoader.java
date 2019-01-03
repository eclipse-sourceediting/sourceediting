/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.css.core.internal.encoding;

import org.eclipse.jst.jsp.css.core.internal.parser.JSPedCSSSourceParser;
import org.eclipse.wst.css.core.internal.encoding.CSSDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;

public class JSPedCSSDocumentLoader extends CSSDocumentLoader {
public RegionParser getParser() {
	return new JSPedCSSSourceParser();
}
public IDocumentLoader newInstance() {
	return new JSPedCSSDocumentLoader();
}
}
