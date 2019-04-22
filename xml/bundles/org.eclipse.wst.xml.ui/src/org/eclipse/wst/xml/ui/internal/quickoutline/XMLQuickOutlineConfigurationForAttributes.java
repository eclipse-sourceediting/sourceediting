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
package org.eclipse.wst.xml.ui.internal.quickoutline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.sse.ui.IContentSelectionProvider;
import org.eclipse.wst.sse.ui.quickoutline.AbstractQuickOutlineConfiguration;
import org.eclipse.wst.sse.ui.quickoutline.StringMatcher;
import org.eclipse.wst.sse.ui.quickoutline.StringPatternFilter;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;

public final class XMLQuickOutlineConfigurationForAttributes extends AbstractQuickOutlineConfiguration {
	static class IdMatcher extends StringMatcher {
		public IdMatcher(String pattern, boolean ignoreCase, boolean ignoreWildCards) {
			super(pattern, ignoreCase, ignoreWildCards);
		}

		@Override
		public boolean match(String text) {
			int valueStart;
			int length = text.length();
			/*
			 * This is very imprecise, but there's not enough shared context
			 * to be smarter
			 */
			if ((valueStart = text.indexOf('=', 1)) > 1) {
				if (valueStart + 1 < length)
					return match(text, valueStart + 1, length);
			}
			if ((valueStart = text.indexOf(" : ", 1)) > 1) {
				if (valueStart + 3 < length) {
					return match(text, valueStart + 3, length);
				}
			}
			return super.match(text);
		}
	}

	AttributeShowingLabelProvider fLastLabelProvider = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.IOutlineContentManager#getContentProvider()
	 */
	public ITreeContentProvider getContentProvider() {
		return new JFaceNodeContentProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.IOutlineContentManager#
	 * getContentSelectionProvider()
	 */
	public IContentSelectionProvider getContentSelectionProvider() {
		return new XMLContentSelectionProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.IOutlineContentManager#getLabelProvider()
	 */
	public ILabelProvider getLabelProvider() {
		AttributeShowingLabelProvider labelProvider = fLastLabelProvider = new AttributeShowingLabelProvider();
		labelProvider.showAttributes(true);
		return labelProvider;
	}

	@Override
	public String getShowMessage() {
		return XMLUIMessages.QuickOutlineShowAttributes;
	}

	@Override
	public StringPatternFilter getFilter() {
		return new StringPatternFilter() {
			public void updatePattern(String pattern) {
				if (pattern.length() == 0) {
					fStringMatcher = null;
				}
				else {
					fStringMatcher = new IdMatcher(pattern, pattern.toLowerCase().equals(pattern), false);
				}

			}

			@Override
			protected String getMatchLabel(Object element, TreeViewer treeViewer) {
				if (fLastLabelProvider != null) {
					String matchLabel = fLastLabelProvider.getIdMatchValue(element);
					if (matchLabel != null) {
						return matchLabel;
					}
				}
				return super.getMatchLabel(element, treeViewer);
			}
		};
	}
}
