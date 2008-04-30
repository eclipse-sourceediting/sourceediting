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
package org.eclipse.wst.jsdt.web.ui.internal.java.search.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.search.SearchPattern;
import org.eclipse.wst.jsdt.core.search.SearchRequestor;
import org.eclipse.wst.jsdt.ui.search.ElementQuerySpecification;
import org.eclipse.wst.jsdt.ui.search.IMatchPresentation;
import org.eclipse.wst.jsdt.ui.search.IQueryParticipant;
import org.eclipse.wst.jsdt.ui.search.ISearchRequestor;
import org.eclipse.wst.jsdt.ui.search.PatternQuerySpecification;
import org.eclipse.wst.jsdt.ui.search.QuerySpecification;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchScope;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchSupport;
import org.eclipse.wst.jsdt.web.ui.internal.java.search.JsSearchRequestor;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author pavery
 */
public class JsQueryParticipant implements IQueryParticipant {
	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspsearch"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.wst.jsdt.ui.search.IQueryParticipant#estimateTicks(org.eclipse.wst.jsdt.ui.search.QuerySpecification)
	 */
	public int estimateTicks(QuerySpecification data) {
		// pa_TODO use project file counter from JSPSearchSupport...
		return 0;
	}
	
	/**
	 * @see org.eclipse.wst.jsdt.ui.search.IQueryParticipant#getUIParticipant()
	 */
	public IMatchPresentation getUIParticipant() {
		return new JsMatchPresentation();
	}
	
	/**
	 * @see org.eclipse.wst.jsdt.ui.search.IQueryParticipant#search(org.eclipse.wst.jsdt.ui.search.ISearchRequestor,
	 *      org.eclipse.wst.jsdt.ui.search.QuerySpecification,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void search(ISearchRequestor requestor, QuerySpecification querySpecification, IProgressMonitor monitor) throws CoreException {
		// indexIfNeeded();
		// do search based on the particular Java query
		if (querySpecification instanceof ElementQuerySpecification) {
			// element search (eg. from global find references in Java file)
			ElementQuerySpecification elementQuery = (ElementQuerySpecification) querySpecification;
			IJavaScriptElement element = elementQuery.getElement();
			if (JsQueryParticipant.DEBUG) {
				System.out.println("JSP Query Participant searching on ELEMENT: " + element); //$NON-NLS-1$
			}
			SearchRequestor jspRequestor = new JsSearchRequestor(requestor);
			// pa_TODO need to adapt JavaSearchScope to a JSPSearchScope
			JsSearchSupport.getInstance().search(element, new JsSearchScope(), jspRequestor);
		} else if (querySpecification instanceof PatternQuerySpecification) {
			// pattern search (eg. from Java search page)
			PatternQuerySpecification patternQuery = (PatternQuerySpecification) querySpecification;
			String pattern = patternQuery.getPattern();
			if (JsQueryParticipant.DEBUG) {
				System.out.println("JSP Query Participant searching on PATTERN: " + pattern); //$NON-NLS-1$
			}
			SearchRequestor jspRequestor = new JsSearchRequestor(requestor);
			JsSearchSupport.getInstance().search(pattern, new JsSearchScope(), patternQuery.getSearchFor(), patternQuery.getLimitTo(), SearchPattern.R_PATTERN_MATCH, false, jspRequestor);
		}
	}
}
