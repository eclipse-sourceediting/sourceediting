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
package org.eclipse.wst.sse.ui.internal.format;

import org.eclipse.wst.sse.core.internal.format.IFormattingDelegate;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

public class FormattingDelegate implements IFormattingDelegate {

	/**
	 * 
	 */
	public FormattingDelegate() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.core.internal.format.IFormattingDelegate#format(java.lang.Object)
	 */
	public void format(Object context) {
		if (context instanceof StructuredTextViewer) {
			StructuredTextViewer viewer = (StructuredTextViewer) context;
			if (viewer.canDoOperation(StructuredTextViewer.FORMAT_DOCUMENT)) {
				viewer.doOperation(StructuredTextViewer.FORMAT_DOCUMENT);
			}
		}
	}

}
