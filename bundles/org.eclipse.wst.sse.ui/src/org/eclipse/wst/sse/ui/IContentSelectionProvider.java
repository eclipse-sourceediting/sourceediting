package org.eclipse.wst.sse.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Allows contributors to redefine a selection when provided with the current
 * selection.
 * 
 */
public interface IContentSelectionProvider {
	ISelection getSelection(TreeViewer viewer, ISelection selection);
}
