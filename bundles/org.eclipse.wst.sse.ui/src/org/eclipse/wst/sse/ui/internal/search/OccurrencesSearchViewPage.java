/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.search;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.Logger;


/**
 * Base page for Occurrences in file search results.
 * 
 * @author pavery
 */
public class OccurrencesSearchViewPage extends AbstractTextSearchViewPage {

	private OccurrencesContentProvider fContentProvider = null;

	public OccurrencesSearchViewPage() {
		super(AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT);
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
	 */
	protected void clear() {
		if (this.fContentProvider != null)
			this.fContentProvider.clear();
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
	 */
	protected void configureTableViewer(TableViewer viewer) {

		// pa_TODO need sorter?
		viewer.setLabelProvider(new BasicSearchLabelProvider());
		this.fContentProvider = new OccurrencesContentProvider();
		viewer.setContentProvider(this.fContentProvider);
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
	 */
	protected void configureTreeViewer(TreeViewer viewer) {
		// not supported at the moment
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
	 */
	protected void elementsChanged(Object[] objects) {
		if (this.fContentProvider != null) {
			this.fContentProvider.elementsChanged(objects);
		}
	}

	public void forceRefresh() {
		this.fContentProvider.refresh();
	}

	private IWorkbenchPage getActivePage() {

		IWorkbench workbench = ((AbstractUIPlugin) Platform.getPlugin(EditorPlugin.ID)).getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return workbench.getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getDisplayedMatches(java.lang.Object)
	 */
	public Match[] getDisplayedMatches(Object element) {
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=2640
		// we only ever show one at a time, the element passed in is the match
		// super was returning null
		return new Match[]{(Match) element};
	}

	private void show(IMarker marker) {

		IResource resource = marker.getResource();
		if (resource == null || !resource.exists())
			return;

		IWorkbenchPage activePage = getActivePage();
		try {
			if (activePage != null) {

				// open editor if needed
				IDE.openEditor(getActivePage(), marker);

				IEditorPart editor = activePage.getActiveEditor();
				if (editor != null) {
					IGotoMarker gotoMarker = (IGotoMarker) editor.getAdapter(IGotoMarker.class);
					if (gotoMarker != null)
						gotoMarker.gotoMarker(marker);
				}

			}
		} catch (PartInitException e) {
			// possible exception trying to open editor
			Logger.logException(e);
		}
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match,
	 *      int, int)
	 */
	protected void showMatch(Match match, int currentOffset, int currentLength) throws PartInitException {
		Object o = match.getElement();
		if (o instanceof IMarker) {
			show((IMarker) o);
		}
	}
}
