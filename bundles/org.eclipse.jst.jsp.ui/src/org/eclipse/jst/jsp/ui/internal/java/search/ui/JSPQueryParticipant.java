/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.search.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchScope;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.java.search.JSPSearchRequestor;
import org.eclipse.jst.jsp.ui.internal.preferences.JSPUIPreferenceNames;

/**
 * @author pavery 
 */
public class JSPQueryParticipant implements IQueryParticipant {

	// for debugging
	private static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspsearch"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	/**
	 * @see org.eclipse.jdt.ui.search.IQueryParticipant#search(org.eclipse.jdt.ui.search.ISearchRequestor, org.eclipse.jdt.ui.search.QuerySpecification, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void search(ISearchRequestor requestor, QuerySpecification querySpecification, IProgressMonitor monitor) throws CoreException {
		
		if(shouldSupplyJSPSearchResultsToJavaSearch()) {
			//indexIfNeeded();
			
			// do search based on the particular Java query
			if(querySpecification instanceof ElementQuerySpecification) {
				// element search (eg. from global find references in Java file)
				ElementQuerySpecification elementQuery = (ElementQuerySpecification)querySpecification;
				IJavaElement element = elementQuery.getElement();
				
				if(DEBUG)
					System.out.println("JSP Query Participant searching on ELEMENT: " + element); //$NON-NLS-1$
				
				SearchRequestor jspRequestor = new JSPSearchRequestor(requestor);
				
				// pa_TODO need to adapt JavaSearchScope to a JSPSearchScope
				JSPSearchSupport.getInstance().search(element, new JSPSearchScope(), jspRequestor, monitor);
				
			}
			else if(querySpecification instanceof PatternQuerySpecification) {
				
				// pattern search (eg. from Java search page)
				PatternQuerySpecification patternQuery = (PatternQuerySpecification)querySpecification;
				String pattern = patternQuery.getPattern();
				
				if(DEBUG)
					System.out.println("JSP Query Participant searching on PATTERN: " + pattern); //$NON-NLS-1$
				
				SearchRequestor jspRequestor = new JSPSearchRequestor(requestor);
				
				JSPSearchSupport.getInstance().search(pattern, 
														new JSPSearchScope(), 
														patternQuery.getSearchFor(), 
														patternQuery.getLimitTo(), 
														SearchPattern.R_PATTERN_MATCH, 
														false, 
														jspRequestor,
														monitor);
			}
		}
	}

	/**
	 * @see org.eclipse.jdt.ui.search.IQueryParticipant#estimateTicks(org.eclipse.jdt.ui.search.QuerySpecification)
	 */
	public int estimateTicks(QuerySpecification data) {
		// pa_TODO use project file counter from JSPSearchSupport...
		return 0;
	}

	/**
	 * @see org.eclipse.jdt.ui.search.IQueryParticipant#getUIParticipant()
	 */
	public IMatchPresentation getUIParticipant() {
		return new JSPMatchPresentation();
	}
	
	private boolean shouldSupplyJSPSearchResultsToJavaSearch() {
		return JSPUIPlugin.getDefault().getPreferenceStore().getBoolean(JSPUIPreferenceNames.SUPPLY_JSP_SEARCH_RESULTS_TO_JAVA_SEARCH);
	}

}
