/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.wst.sse.ui.internal.Logger;


/**
 * @author pavery
 */
public class BasicSearchQuery implements ISearchQuery {

	/** attribute to identify markers added by find occurrences */
	public static final String ATTR_OCCURRENCES_MARKER = "occurrences_marker"; //$NON-NLS-1$

	private static int LINE_LENGTH_LIMIT = 200;

	/** the file we're searching * */
	private IFile fFile = null;
	/** occurrence search matches * */
	private List fMatches = null;

	public BasicSearchQuery(IFile file) {
		this.fFile = file;
		this.fMatches = new ArrayList();
	}

	public void addMatch(IDocument document, int matchStart, int matchEnd) {

		try {
			int lineNumber = document.getLineOfOffset(matchStart);
			int lineStart = document.getLineOffset(lineNumber);
			int lineLength = document.getLineLength(lineNumber);

			String searchResultString = document.get().substring(lineStart, lineStart + lineLength).trim();

			// create search marker (so annotations show up in editor)
			IMarker marker = createSearchMarker(matchStart, matchEnd, lineNumber, searchResultString);

			addMatch(new Match(marker, Match.UNIT_CHARACTER, matchStart, matchStart + matchEnd));

		} catch (BadLocationException e) {
			Logger.logException(e);
		}
	}

	private void addMatch(Match match) {
		if (match != null)
			this.fMatches.add(match);
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#canRerun()
	 */
	public boolean canRerun() {
		return false;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
	 */
	public boolean canRunInBackground() {
		return true;
	}

	public void clearMatches() {
		this.fMatches.clear();
	}

	protected IMarker createSearchMarker(int matchStart, int matchEnd, int lineNumber, String searchResultString) {

		IMarker marker = null;
		try {
			if (getFile() != null) {

				marker = getFile().createMarker(NewSearchUI.SEARCH_MARKER);
				HashMap attributes = new HashMap(6);

				MarkerUtilities.setCharStart(attributes, matchStart);
				MarkerUtilities.setCharEnd(attributes, matchEnd);
				MarkerUtilities.setLineNumber(attributes, lineNumber);

				// this might be bad if line of text is VERY long?
				if (searchResultString.length() > LINE_LENGTH_LIMIT)
					searchResultString = searchResultString.substring(0, LINE_LENGTH_LIMIT) + "..."; //$NON-NLS-1$
				MarkerUtilities.setMessage(attributes, searchResultString);

				// so we can remove them later
				attributes.put(ATTR_OCCURRENCES_MARKER, new Boolean(true));

				marker.setAttributes(attributes);
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}
		return marker;
	}

	private void deleteOccurrencesMarkers() {

		final List removals = new ArrayList();
		try {
			// clear all old find occurrences markers
			IMarker[] searchMarkers = fFile.findMarkers(NewSearchUI.SEARCH_MARKER, false, IResource.DEPTH_ZERO);
			for (int i = 0; i < searchMarkers.length; i++) {
				Object o = searchMarkers[i].getAttribute(BasicSearchQuery.ATTR_OCCURRENCES_MARKER);
				if (o != null && ((Boolean) o).booleanValue() == true)
					removals.add(searchMarkers[i]);

			}

			if (removals.size() > 0) {
				IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						for (int i = 0; i < removals.size(); i++)
							((IMarker) removals.get(i)).delete();
					}
				};
				// BUG158846 - deadlock if lock up entire workspace, so only lock
				// up the file we are searching on
				ISchedulingRule markerRule = ResourcesPlugin.getWorkspace().getRuleFactory().markerRule(fFile);
				ResourcesPlugin.getWorkspace().run(runnable, markerRule, IWorkspace.AVOID_UPDATE, null); 
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}
	}

	/**
	 * The acutal work of the query. Will be run in a background Job
	 * automatically if canRunInBackground(..) returns true.
	 * 
	 * @return
	 */
	protected IStatus doQuery() {
		return Status.OK_STATUS;
	}

	/*
	 * public to avoid synthetic method access from inner class
	 */
	public IFile getFile() {
		return this.fFile;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	public String getLabel() {
		return ""; //$NON-NLS-1$
	}

	public Match[] getMatches() {
		// get rid of the old markers
		if(fFile.exists()) {
			deleteOccurrencesMarkers();
			doQuery();
		}
		return (Match[]) this.fMatches.toArray(new Match[this.fMatches.size()]);
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public ISearchResult getSearchResult() {
		return null;
	}

	/**
	 * used in search result display labels
	 * 
	 * @return
	 */
	protected String getSearchText() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor) {
		// defer to "get(...)"
		return Status.OK_STATUS;
	}
}
