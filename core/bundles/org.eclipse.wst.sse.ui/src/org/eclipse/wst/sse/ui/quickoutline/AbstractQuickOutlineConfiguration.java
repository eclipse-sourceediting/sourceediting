/*******************************************************************************
 * Copyright (c) 2010, 2019 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.quickoutline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.wst.sse.ui.IContentSelectionProvider;

/**
 * Base class that provides configuration for a quick outline.
 *
 */
public abstract class AbstractQuickOutlineConfiguration {

	/**
	 * Provides the label provider to be used by the quick outline
	 * 
	 * @return an <code>ILabelProvider</code> to be used by the quick outline
	 */
	public abstract ILabelProvider getLabelProvider();

	/**
	 * Provides the content provider to be used by the quick outline
	 * 
	 * @return a <code>ITreeContentProvider</code> to be used when providing the content of the quick outline
	 */
	public abstract ITreeContentProvider getContentProvider();

	/**
	 * Provides the content selection provider to be used by the quick outline. This allows
	 * for adjusting the selection to be displayed in the outline based on the editor's selection
	 * 
	 * @return an <code>IContentSelectionProvider</code> used by the quick outline. By default, returns
	 * null indicating that the selection in the editor translates to the selection in the outline.
	 */
	public IContentSelectionProvider getContentSelectionProvider() {
		return null;
	}

	/**
	 * @return The filter that will apply the contents of the filter text box
	 * to what is shown by this configuration.
	 */
	public StringPatternFilter getFilter() {
		return new StringPatternFilter();
	}

	/**
	 * @return the next configuration for the quick outline to cycle through,
	 *         or <code>null</code> when the original contributed
	 *         configuration should be shown next, or there are no others.
	 */
	public AbstractQuickOutlineConfiguration getNextConfiguration() {
		return null;
	}

	/**
	 * @return The string to show the user indicating what this configuration
	 *         will do when active with an open placeholder for the trigger
	 *         key binding, e.g. "Press ''{0}'' to filter on these things",
	 *         or <code>null<.code> if it will not be shown.
	 */
	public String getShowMessage() {
		return null;
	}
}
