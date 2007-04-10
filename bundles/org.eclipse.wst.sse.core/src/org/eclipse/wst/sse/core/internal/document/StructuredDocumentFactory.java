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
package org.eclipse.wst.sse.core.internal.document;

import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;


/**
 * At the moment, this is primarily intended as a convenience to help switch
 * between various types of threading models in the document, all in a central
 * piece of code.
 */
public class StructuredDocumentFactory {
	private static final int WRITE_SYNCHRONIZED = 3;
	private static final int DEFAULT = WRITE_SYNCHRONIZED;
	private static final int UNSYNCHRONIZED = 1;

	private static IStructuredDocument getNewStructuredDocumentInstance(int type, RegionParser parser) {
		IStructuredDocument result = null;
		switch (type) {
			case UNSYNCHRONIZED :
				result = new BasicStructuredDocument(parser);
				break;
			case WRITE_SYNCHRONIZED :
				result = new JobSafeStructuredDocument(parser);
				break;

			default :
				throw new IllegalArgumentException("request document type was not known"); //$NON-NLS-1$

		}
		return result;
	}

	/**
	 * Provides the (system default) structured document initialized with the
	 * parser.
	 * 
	 * @param parser
	 * @return
	 */
	public static IStructuredDocument getNewStructuredDocumentInstance(RegionParser parser) {
		return getNewStructuredDocumentInstance(DEFAULT, parser);
	}

	/**
	 * Not intended to be instantiated
	 * 
	 */
	private StructuredDocumentFactory() {
		super();
	}
}
