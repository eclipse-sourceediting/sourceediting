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

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


/**
 * Basic ISearchQuery that finds matches of region type and region text.
 * 
 * @author pavery
 */
public class OccurrencesSearchQuery extends BasicSearchQuery {

	/**
	 * We need a runnable so that the search markers show up in the live document.
	 */
	private class FindRegions implements IWorkspaceRunnable {

		private IStructuredDocument findRegionsDocument = null;
		private String matchRegionType = null;
		private String matchName = null;

		public FindRegions(IStructuredDocument document, String matchText, String matchRegionType) {
			this.findRegionsDocument = document;
			this.matchName = matchText;
			this.matchRegionType = matchRegionType;
		}

		public void run(IProgressMonitor monitor) throws CoreException {
			findOccurrences(monitor);
		}

		private void findOccurrences(IProgressMonitor monitor) {
			
			if (!isCanceled(monitor)) {

				int matchStart = -1;
				int matchEnd = -1;
				String findRegionText = ""; //$NON-NLS-1$
				String findResultText = ""; //$NON-NLS-1$
				String lineText = ""; //$NON-NLS-1$
				
				ITextRegion r = null;
				ITextRegionList regions = null;
				IStructuredDocumentRegion current = this.findRegionsDocument.getFirstStructuredDocumentRegion();

				// this is the main loop that iterates the document
				while (current != null && !isCanceled(monitor)) {
					regions = current.getRegions();
					for (int i = 0; i < regions.size() && !isCanceled(monitor); i++) {
						
						r = regions.get(i);
						
						// maybe this is the equals check where some valid matches are failing (like searching on end tag)
						if (r.getType().equals(this.matchRegionType) && current.getText(r).equals(this.matchName)) {
							
							findRegionText = current.getText(r);
							
							// region found
							matchStart = current.getStartOffset(r);
							matchEnd = matchStart + findRegionText.trim().length();
							
							addMatch(this.findRegionsDocument, matchStart, matchEnd);
						}
					}
					current = current.getNext();
				}
			}
		}

		private boolean isCanceled(IProgressMonitor monitor) {
			return monitor != null && monitor.isCanceled();
		}
	}
	// end inner class FindRegions
	

	private IStructuredDocument document = null;
	private String regionText = null;
	private String regionType = null;
	
	public OccurrencesSearchQuery(IFile file, IStructuredDocument document, String regionText, String regionType) {
		super(file);
		this.document = document;
		this.regionText = regionText;
		this.regionType = regionType;
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.internal.search.BasicSearchQuery#getRunnable()
	 */
	protected IWorkspaceRunnable getRunnable() {
		return new FindRegions(this.document, this.regionText, this.regionType);
	}
	
	public String getLabel() {
		String label = ResourceHandler.getString("OccurrencesSearchQuery.0"); //$NON-NLS-1$
		String[] args = {getSearchText(), getOccurrencesCountText(), getFilename()};
		return MessageFormat.format(label, args);
	}

	/**
	 * @return
	 */
	private String getOccurrencesCountText() {
		String count = "";
		// pa_TODO dynamically change count
		return count;
	}
	
	private String getFilename() {
		String filename = ResourceHandler.getString("OccurrencesSearchQuery.2"); //$NON-NLS-1$ "file"
		if(getFile() != null)
			filename = getFile().getName().toString();
		return filename;
	}

	protected String getSearchText() {
		return this.regionText;
	}
	
	public boolean canRerun() {
		return false;
	}
	
	public boolean canRunInBackground() {
		// pa_TODO investigate what is required to do this safely
		return false;
	}

	public ISearchResult getSearchResult() {
		return new OccurrencesSearchResult(this);
	}
}
