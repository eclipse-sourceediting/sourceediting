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
package org.eclipse.wst.xml.core.internal.parser.regions;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class XMLHeadParserFactory {
	public ITextRegion createToken(String context, int start, int textLength, int length, String text) {
		ITextRegion newRegion = null;
		//		if (context == XMLRegionContext.XML_CDATA_TEXT) {
		newRegion = new XMLHeadParserRegion(context, start, textLength, length, text);
		//		}
		return newRegion;
	}
}
