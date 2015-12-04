/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.internal.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;

public class JSONDocumentCharsetDetector implements IDocumentCharsetDetector {

	@Override
	public void set(IStorage paramIStorage) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getEncoding() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSpecDefaultEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(InputStream paramInputStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(Reader paramReader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(IDocument paramIDocument) {
		// TODO Auto-generated method stub

	}

}
