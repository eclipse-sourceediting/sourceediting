/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.css.core.internal.encoding;

import org.eclipse.wst.jsdt.web.css.core.internal.parser.JSPedCSSSourceParser;
import org.eclipse.wst.css.core.internal.encoding.CSSDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;

public class JSPedCSSDocumentLoader extends CSSDocumentLoader {
	@Override
	public RegionParser getParser() {
		return new JSPedCSSSourceParser();
	}

	@Override
	public IDocumentLoader newInstance() {
		return new JSPedCSSDocumentLoader();
	}
}
