/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.format;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.core.format.IStructuredFormattingStrategy;

public class HTMLStructuredFormattingStrategyImpl implements IStructuredFormattingStrategy {

	private String fInitialIndentation;
	private ISourceViewer fViewer;
	protected IProgressMonitor fProgressMonitor = null;

	/**
	 * Constructor for HTMLStructuredFormattingStrategyImpl.
	 */
	public HTMLStructuredFormattingStrategyImpl(ISourceViewer viewer) {
		fViewer = viewer;
	}

	/**
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStarts(java.lang.String)
	 */
	public void formatterStarts(String initialIndentation) {
		fInitialIndentation = initialIndentation;
	}

	/**
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#format(java.lang.String, boolean, java.lang.String, int)
	 */
	public String format(String content, boolean isLineStart, String indentation, int[] positions) {
		return content;
	}

	public void format(IStructuredModel model, int start, int length, boolean isLineStart, String indentation, int[] positions) {
		IStructuredFormatProcessor formatProcessor = new HTMLFormatProcessorImpl();
		formatProcessor.formatModel(model, start, length);
	}

	/**
	 * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStops()
	 */
	public void formatterStops() {
	}

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		fProgressMonitor = progressMonitor;
	}
}