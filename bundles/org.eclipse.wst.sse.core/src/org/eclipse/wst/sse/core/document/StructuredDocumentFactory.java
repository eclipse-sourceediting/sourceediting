/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.document;

import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


/**
 * At the moment, this is primarily intended as a convenience to help switch
 * between various types of threading models in the document, all in a central
 * piece of code.
 */
public class StructuredDocumentFactory {
	public static final int WRITE_SYNCHRONIZED = 3;
	public static final int DEFAULT = WRITE_SYNCHRONIZED;
	public static final int UNSYNCHRONIZED = 1;

	public static IStructuredDocument getNewStructuredDocumentInstance() {
		// be default, use thread safe one
		return getNewStructuredDocumentInstance(DEFAULT);
	}

	public static IStructuredDocument getNewStructuredDocumentInstance(int type) {
		IStructuredDocument result = null;
		switch (type) {
			case UNSYNCHRONIZED :
				result = new BasicStructuredDocument();
				break;

			case WRITE_SYNCHRONIZED :
				result = new JobSafeStructuredDocument();
				break;

			default :
				throw new IllegalArgumentException("request document type was not known");

		}
		return result;
	}

	public static IStructuredDocument getNewStructuredDocumentInstance(int type, RegionParser parser) {
		IStructuredDocument result = null;
		switch (type) {
			case UNSYNCHRONIZED :
				result = new BasicStructuredDocument(parser);
				break;
			case WRITE_SYNCHRONIZED :
				result = new JobSafeStructuredDocument(parser);
				break;

			default :
				throw new IllegalArgumentException("request document type was not known");

		}
		return result;
	}

	public static IStructuredDocument getNewStructuredDocumentInstance(RegionParser parser) {
		// be default, use thread safe one
		return getNewStructuredDocumentInstance(DEFAULT, parser);
	}

	/**
	 * Not intended to be instantiated
	 *  
	 */
	protected StructuredDocumentFactory() {
		super();
	}
}
