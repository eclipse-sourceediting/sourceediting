/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.ltk.modelhandler;

import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;

/**
 * Responsible for providing the mechanisms used in the correct loading of an
 * IStructuredDocument's contents and determine its self-described encoding.
 */
public interface IDocumentTypeHandler {

	/**
	 * The Loader is reponsible for decoding the Resource,
	 */
	IDocumentLoader getDocumentLoader();

	/**
	 * @deprecated - likely to go away, so I marked as deprecated to
	 *             discoursage use
	 */
	IDocumentCharsetDetector getEncodingDetector();

	/**
	 * Must return unique ID that is the same as identified in plugin registry
	 */
	String getId();



}
