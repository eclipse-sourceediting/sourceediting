/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
