/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.views.contentoutline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.wst.sse.ui.IContentSelectionProvider;
import org.eclipse.wst.sse.ui.quickoutline.AbstractQuickOutlineConfiguration;

public class CSSQuickOutlineConfiguration extends AbstractQuickOutlineConfiguration {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.quickoutline.AbstractQuickOutlineConfiguration#getContentProvider()
	 */
	public ITreeContentProvider getContentProvider() {
		return new JFaceNodeContentProviderCSS();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.quickoutline.AbstractQuickOutlineConfiguration#getContentSelectionProvider()
	 */
	public IContentSelectionProvider getContentSelectionProvider() {
		return new CSSContentSelectionProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.quickoutline.AbstractQuickOutlineConfiguration#getLabelProvider()
	 */
	public ILabelProvider getLabelProvider() {
		return new JFaceNodeLabelProviderCSS();
	}

}
