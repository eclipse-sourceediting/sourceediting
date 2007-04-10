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
package org.eclipse.wst.sse.core.internal.ltk.parser;



import java.io.Reader;
import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


public interface RegionParser {

	IStructuredDocumentRegion getDocumentRegions();

	List getRegions();

	/**
	 * The 'newInstance' method is similar to 'clone', but does not include
	 * the copying of any content. For a pure RegionParser itself, there would
	 * be little state to "clone", but for some subtypes, such as
	 * StructuredDocumentRegionParser and JSPCapableParser, there could the
	 * more internal data to "clone", such as the internal tokenizer should be
	 * cloned (including block tags, etc).
	 */
	RegionParser newInstance();

	void reset(Reader reader);

	/**
	 * An additional offset for use with any position-dependant parsing rules
	 */
	void reset(Reader reader, int offset);

	void reset(String input);

	/**
	 * An additional offset for use with any position-dependant parsing rules
	 */
	void reset(String input, int offset);
}
