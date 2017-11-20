/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.javascript.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.core.search.IJavaScriptSearchScope;
import org.eclipse.wst.jsdt.core.search.SearchDocument;
import org.eclipse.wst.jsdt.core.search.SearchEngine;
import org.eclipse.wst.jsdt.core.search.SearchParticipant;
import org.eclipse.wst.jsdt.core.search.SearchPattern;
import org.eclipse.wst.jsdt.core.search.SearchRequestor;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*(copied from JSP)
 * Integration of JSP w/ java search.
 * 
 * @author pavery
 */
public class JsSearchParticipant extends SearchParticipant {

	// for debugging
	private static final boolean DEBUG = calculateValue();

	private static boolean calculateValue() {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspsearch"); //$NON-NLS-1$
		boolean debug = (value != null) && value.equalsIgnoreCase("true"); //$NON-NLS-1$
		return debug;
	}

	/**
	 * Important to never return null here or else Java search participation
	 * will break.
	 */
	public SearchDocument getDocument(String documentPath) {
		SearchDocument sDoc = JsSearchSupport.getInstance().getSearchDocument(documentPath);

		if (sDoc == null) {
			// return a dummy doc here so search participation doesn't break
			return new NullSearchDocument(documentPath);
		}
		return sDoc;
	}

	public String getDescription() {
		return "Embeded JavaScript"; //$NON-NLS-1$
	}

	public IPath[] selectIndexes(SearchPattern pattern, IJavaScriptSearchScope scope) {
		JsPathIndexer indexer = new JsPathIndexer();
		return indexer.getVisibleJspPaths(pattern, scope);
	}

	public void indexDocument(SearchDocument document, IPath indexPath) {
		if (!(document instanceof JSDTSearchDocumentDelegate)) {
			return;
		}

		// use Java search indexing
		SearchEngine.getDefaultSearchParticipant().indexDocument(document, indexPath);
	}

	public void locateMatches(SearchDocument[] indexMatches, SearchPattern pattern, IJavaScriptSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {

		if ((monitor != null) && monitor.isCanceled()) {
			return;
		}

		// filter out null matches
		List filtered = new ArrayList();
		SearchDocument match = null;
		for (int i = 0; i < indexMatches.length; i++) {
			if (DEBUG) {
				System.out.println("found possible matching JavaSearchDocumentDelegate: " + indexMatches[i]); //$NON-NLS-1$
			}
			match = indexMatches[i];
			if (match != null) {
				// some matches may be null, or if the index is out of date,
				// the file may not even exist
				if ((match instanceof JSDTSearchDocumentDelegate) && ((JSDTSearchDocumentDelegate) match).getFile().exists()) {
					filtered.add(match);
				}
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
