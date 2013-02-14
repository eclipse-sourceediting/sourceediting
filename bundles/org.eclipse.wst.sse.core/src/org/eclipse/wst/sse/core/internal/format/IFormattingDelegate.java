/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.format;

/**
 * The formatting delegate will pass off formatting of a document
 * based on the text viewer context.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IFormattingDelegate {
	/**
	 * Formats a document from the given context.
	 * 
	 * @param context the <code>org.eclipse.wst.sse.ui.internal.StructuredTextViewer</code> that
	 * is used as context for performing the format operation. The type is <code>Object</code> to
	 * avoid dependencies on UI code.
	 */
	void format(Object context);
}
