/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.parser;



import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.eclipse.wst.sse.core.text.ITextRegion;


public interface BlockTokenizer {

	void addBlockMarker(BlockMarker marker);

	void beginBlockMarkerScan(String newTagName, String context);

	void beginBlockTagScan(String newTagName);

	List getBlockMarkers();

	ITextRegion getNextToken() throws IOException;

	int getOffset();

	boolean isEOF();

	void removeBlockMarker(BlockMarker marker);

	void removeBlockMarker(String tagname);

	void reset(char[] charArray, int newOffset);

	void reset(char[] charArray);

	void reset(InputStream in, int newOffset);

	void reset(InputStream in);

	void reset(Reader in, int newOffset);

	void reset(Reader in);

	BlockTokenizer newInstance();
}
