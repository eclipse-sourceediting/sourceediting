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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;


/**
 * @author pavery
 */
public class OccurrencesSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter {

	private ISearchQuery query = null;
	private final Match[] NO_MATCHES = new Match[0];
	
	public OccurrencesSearchResult(ISearchQuery query) {
		this.query = query;
	}
	
	public IEditorMatchAdapter getEditorMatchAdapter() {	
		return this;
	}

	public IFileMatchAdapter getFileMatchAdapter() {
		return this;
	}

	/**
	 * This label shows up in the search history
	 */
	public String getLabel() {
		return getQuery().getLabel();
	}

	public String getTooltip() {
		return getLabel();
	}

	public ImageDescriptor getImageDescriptor() {
		return EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_OBJ_OCC_MATCH);
	}

	public ISearchQuery getQuery() {
		return this.query;
	}

	public Match[] getMatches() {
		return ((OccurrencesSearchQuery)getQuery()).getMatches();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.IEditorMatchAdapter#isShownInEditor(org.eclipse.search.ui.text.Match, org.eclipse.ui.IEditorPart)
	 */
	public boolean isShownInEditor(Match match, IEditorPart editor) {
		return true;
	}

	/**
	 * @see org.eclipse.search.ui.text.IEditorMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult, org.eclipse.ui.IEditorPart)
	 */
	public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor) {

		IEditorInput editorInput= editor.getEditorInput();
		if (editorInput instanceof IFileEditorInput)  {
			IFileEditorInput fileEditorInput= (IFileEditorInput) editorInput;
			return computeContainedMatches(result, fileEditorInput.getFile());
		}
		return this.NO_MATCHES;
	}

	/**
	 * @see org.eclipse.search.ui.text.IFileMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult, org.eclipse.core.resources.IFile)
	 */
	public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file) {
		return getMatches();	
	}

	/**
	 * @see org.eclipse.search.ui.text.IFileMatchAdapter#getFile(java.lang.Object)
	 */
	public IFile getFile(Object element) {
		// return the file for the match
		IFile file = null;
		//System.out.println("get file for:"+element);
		if(element instanceof IMarker) {
			IResource r = ((IMarker)element).getResource();
			if(r instanceof IFile) {
				file = (IFile)r;
			}
		}
		return file;
	}
}
