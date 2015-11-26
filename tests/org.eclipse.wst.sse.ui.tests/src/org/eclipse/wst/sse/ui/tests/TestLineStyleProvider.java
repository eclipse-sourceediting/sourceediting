/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.tests;

import java.util.Collection;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;

public class TestLineStyleProvider implements LineStyleProvider {

	Color foreground = null;

	/**
	 * 
	 */
	public TestLineStyleProvider() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.style.LineStyleProvider#init(org.eclipse.wst.sse.core.text.IStructuredDocument,
	 *      org.eclipse.wst.sse.ui.style.Highlighter)
	 */
	public void init(IStructuredDocument document, Highlighter highlighter) {
		// nothing to init
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.style.LineStyleProvider#prepareRegions(org.eclipse.jface.text.ITypedRegion,
	 *      int, int, java.util.Collection)
	 */
	public boolean prepareRegions(ITypedRegion currentRegion, int start, int length, Collection styleRanges) {
		// make everything bold grey
		if (foreground == null)
			foreground = EditorUtility.getColor(new RGB(127, 127, 127));
		styleRanges.add(new StyleRange(start, length, foreground, null, SWT.BOLD));
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.style.LineStyleProvider#release()
	 */
	public void release() {
		// nothing to release
	}

}
