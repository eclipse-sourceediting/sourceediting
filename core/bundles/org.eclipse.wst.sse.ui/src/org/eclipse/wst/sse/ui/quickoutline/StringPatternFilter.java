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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.wst.sse.ui.internal.quickoutline.QuickOutlinePopupDialog;

/**
 * Default Viewer Filter to be used by the {@link QuickOutlinePopupDialog}
 * <p>
 * Based on {@link org.eclipse.jdt.internal.ui.text.AbstractInformationControl.NamePatternFilter}
 * </p>
 */
public class StringPatternFilter extends ViewerFilter {

	protected StringMatcher fStringMatcher;

	/*
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		StringMatcher matcher = fStringMatcher;
		if (matcher == null || !(viewer instanceof TreeViewer))
			return true;
		TreeViewer treeViewer = (TreeViewer) viewer;

		String matchName = getMatchLabel(element, treeViewer);
		matchName = TextProcessor.deprocess(matchName);
		if (matchName != null && matcher.match(matchName))
			return true;

		return hasUnfilteredChild(treeViewer, element);
	}

	protected String getMatchLabel(Object element, TreeViewer treeViewer) {
		return ((ILabelProvider) treeViewer.getLabelProvider()).getText(element);
	}

	private boolean hasUnfilteredChild(TreeViewer viewer, Object element) {
		Object[] children=  ((ITreeContentProvider) viewer.getContentProvider()).getChildren(element);
		for (int i= 0; i < children.length; i++)
			if (select(viewer, element, children[i]))
				return true;
		return false;
	}

	public void updatePattern(String pattern) {
		if (pattern.length() == 0) {
			fStringMatcher = null;
		}
		else {
			fStringMatcher = new StringMatcher(pattern, pattern.toLowerCase().equals(pattern), false);
		}
		
	}

	public StringMatcher getStringMatcher() {
		return fStringMatcher;
	}

}
