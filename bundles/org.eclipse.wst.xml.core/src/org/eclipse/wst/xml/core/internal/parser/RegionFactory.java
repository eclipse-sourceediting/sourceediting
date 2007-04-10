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
package org.eclipse.wst.xml.core.internal.parser;



import org.eclipse.wst.sse.core.internal.parser.ContextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;


public class RegionFactory {

	public RegionFactory() {
		super();
	}

	public ITextRegion createToken(ITextRegionContainer parent, String context, int start, int textLength, int length) {
		return this.createToken(parent, context, start, textLength, length, null, null);
	}

	public ITextRegion createToken(ITextRegionContainer parent, String context, int start, int textLength, int length, String lang, String surroundingTag) {
		ITextRegion newRegion = createToken(context, start, textLength, length);
		// DW, 4/16/2003 token regions no longer have parents
		//newRegion.setParent(parent);
		return newRegion;
	}

	public ITextRegion createToken(String context, int start, int textLength, int length) {
		return this.createToken(context, start, textLength, length, null, null);
	}

	public ITextRegion createToken(String context, int start, int textLength, int length, String lang, String surroundingTag) {
		ITextRegion newRegion = new ContextRegion(context, start, textLength, length);
		return newRegion;


	}
}
