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
/*
 * Created on 28-Aug-03
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.dtd.core.internal.encoding;

import java.io.IOException;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.contenttype.XMLResourceEncodingDetector;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentCharsetDetector;


/**
 * @author kboo
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class DTDDocumentCharsetDetector extends AbstractResourceEncodingDetector implements IDocumentCharsetDetector {

	public String getSpecDefaultEncoding() {
		// by default, UTF-8 as per XML spec
		final String enc = "UTF-8"; //$NON-NLS-1$
		return enc;
	}

	/**
	 * 
	 */

	protected void parseInput() throws IOException {
		IDocumentCharsetDetector documentEncodingDetector = new XMLDocumentCharsetDetector();
		documentEncodingDetector.set(fReader);
		fEncodingMemento = ((XMLResourceEncodingDetector)documentEncodingDetector).getEncodingMemento();

	}

	public void set(IDocument document) {
		set(new DocumentReader(document, 0));


	}

}
