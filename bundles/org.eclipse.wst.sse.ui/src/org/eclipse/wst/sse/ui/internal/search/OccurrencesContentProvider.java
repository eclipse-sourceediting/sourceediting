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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author pavery
 */
public class OccurrencesContentProvider implements IStructuredContentProvider {

	protected final Object[] EMPTY_ARRAY= new Object[0];
	private TableViewer tableViewer = null;
	private OccurrencesSearchResult result = null;
	
	
	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		
		this.result = (OccurrencesSearchResult)inputElement;
		return this.result.getMatches();
	}
	
	public void elementsChanged(Object[] objects) {
		
		// only add what's not already added...
		if(this.tableViewer != null) {
			if(this.result != null && this.result.getMatches().length > 0) {
				this.tableViewer.add(this.result.getMatches());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// do nothing
	}
	
	public void clear() {
		
		if(this.tableViewer != null)
			this.tableViewer.refresh();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		if(viewer instanceof TableViewer)
			this.tableViewer= (TableViewer)viewer;
	}
}
