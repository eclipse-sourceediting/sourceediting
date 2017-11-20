/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.parser.regions.CSSTextRegionFactory
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.parser.regions;

import org.eclipse.wst.sse.core.internal.parser.ContextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class JSONTextRegionFactory {

	public synchronized static JSONTextRegionFactory getInstance() {
		if (fInstance == null) {
			fInstance = new JSONTextRegionFactory();
		}
		return fInstance;
	}

	public ITextRegion createRegion(String context, int start, int textLength,
			int length) {
		ITextRegion region = null;
		region = new ContextRegion(context, start, textLength, length);
		return region;
	}

	private JSONTextRegionFactory() {
		super();
	}

	private static JSONTextRegionFactory fInstance = null;

}
