/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
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
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.ui.EditorPlugin;


/**
 * Base page for Occurrences in file search results.
 * 
 * @author pavery
 */
public class OccurrencesSearchViewPage extends AbstractTextSearchViewPage {

	private OccurrencesContentProvider contentProvider = null;
	
	public OccurrencesSearchViewPage() {
		super(AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT);
	}
	
	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
	 */
	protected void elementsChanged(Object[] objects) {
		if(this.contentProvider != null)
			this.contentProvider.elementsChanged(objects);
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
	 */
	protected void clear() {
		if(this.contentProvider != null)
			this.contentProvider.clear();
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
	 */
	protected void configureTreeViewer(TreeViewer viewer) {
		// not supported at the moment
	}

	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
	 */
	protected void configureTableViewer(TableViewer viewer) {
		
		// pa_TODO need sorter?
		viewer.setLabelProvider(new BasicSearchLabelProvider());
		this.contentProvider = new OccurrencesContentProvider();
		viewer.setContentProvider(this.contentProvider);
	}
	
	/**
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match, int, int)
	 */
	protected void showMatch(Match match, int currentOffset, int currentLength) throws PartInitException {
		Object o = match.getElement();
		if(o instanceof IMarker) {
			show((IMarker)o);
		}
	}
	
	private void show(IMarker marker) {
		IResource resource = marker.getResource();
		if (resource == null || !resource.exists())
			return;

		IEditorPart editor = getActivePage().getActiveEditor();
		if(editor != null) {
			IGotoMarker gotoMarker = (IGotoMarker) editor.getAdapter(IGotoMarker.class);
			if (gotoMarker != null)
				gotoMarker.gotoMarker(marker);
		}
	}
	
	private IWorkbenchPage getActivePage() {
		IWorkbench workbench = ((AbstractUIPlugin) Platform.getPlugin(EditorPlugin.ID)).getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return workbench.getActiveWorkbenchWindow().getActivePage();
	}
}
