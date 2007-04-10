/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

/**
 * Integration of JSP w/ java search.
 * 
 * @author pavery
 */
public class JSPSearchParticipant extends SearchParticipant {

	// for debugging
	private static final boolean DEBUG = calculateValue();

	private static boolean calculateValue() {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspsearch"); //$NON-NLS-1$
		boolean debug = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
		return debug;
	}

	/**
	 * Important to never return null here or else Java search participation
	 * will break.
	 */
	public SearchDocument getDocument(String documentPath) {
		SearchDocument sDoc = JSPSearchSupport.getInstance().getSearchDocument(documentPath);

		if (sDoc == null) {
			// return a dummy doc here so search participation doesn't break
			return new NullSearchDocument(documentPath);
		}
		return sDoc;
	}

	public String getDescription() {
		return "JSP"; //$NON-NLS-1$
	}

	public IPath[] selectIndexes(SearchPattern pattern, IJavaSearchScope scope) {
		JSPPathIndexer indexer = new JSPPathIndexer();
		return indexer.getVisibleJspPaths(pattern, scope);
	}

	public void indexDocument(SearchDocument document, IPath indexPath) {
		if (!(document instanceof JavaSearchDocumentDelegate))
			return;

		// use Java search indexing
		SearchEngine.getDefaultSearchParticipant().indexDocument(document, indexPath);
	}

	public void locateMatches(SearchDocument[] indexMatches, SearchPattern pattern, IJavaSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {

		if (monitor != null && monitor.isCanceled())
			return;

		// filter out null matches
		List filtered = new ArrayList();
		SearchDocument match = null;
		for (int i = 0; i < indexMatches.length; i++) {
			if (DEBUG)
				System.out.println("found possible matching JavaSearchDocumentDelegate: " + indexMatches[i]); //$NON-NLS-1$
			match = indexMatches[i];
			if (match != null) {
				// some matches may be null, or if the index is out of date,
				// the file may not even exist
				if (match instanceof JavaSearchDocumentDelegate && ((JavaSearchDocumentDelegate) match).getFile().exists())
					filtered.add(match);
			}
		}

		indexMatches = (SearchDocument[]) filtered.toArray(new SearchDocument[filtered.size()]);
		SearchEngine.getDefaultSearchParticipant().locateMatches(indexMatches, pattern, scope, requestor, monitor);
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchParticipant#getDocument(org.eclipse.core.resources.IFile)
	 */
	public SearchDocument getDocument(IFile file) {
		// never gets called?
		return null;
	}
}
