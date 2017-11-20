/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.sse.core.tests.events;

import java.io.Reader;
import java.util.List;

import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class NullParser implements RegionParser {

	public IStructuredDocumentRegion getDocumentRegions() {

		return null;
	}

	public List getRegions() {

		return null;
	}

	public RegionParser newInstance() {

		return this;
	}

	public void reset(Reader reader) {
		// do nothing

	}

	public void reset(Reader reader, int offset) {
		// do nothing

	}

	public void reset(String input) {
		// do nothing

	}

	public void reset(String input, int offset) {
		// do nothing
	}

}
