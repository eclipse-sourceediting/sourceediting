/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.core.internal.java.search.JavaSearchDocumentDelegate;

/**
 * Accepts matches from JSPSearchSupport.search(...) request.
 * Adapts the results from java to JSP and displays in the SearchResultView.
 * 
 * @author pavery
 */
public class BasicJSPSearchRequestor extends SearchRequestor {

	// for debugging
	private static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspsearch"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	/**
	 * Maps java search coordinates to corresponding JSP coordinates.
	 * Adds the matches to the Search Results view.
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 */
	public void acceptSearchMatch(SearchMatch match) throws CoreException {

		if(JSPSearchSupport.getInstance().isCanceled())
			return;
				
		String matchDocumentPath = match.getResource().getFullPath().toString();
		SearchDocument searchDoc = JSPSearchSupport.getInstance().getSearchDocument(matchDocumentPath);
		
		if (searchDoc != null && searchDoc instanceof JavaSearchDocumentDelegate) {
			JavaSearchDocumentDelegate javaSearchDoc = (JavaSearchDocumentDelegate)searchDoc;
			int jspStart = javaSearchDoc.getJspOffset(match.getOffset());
			int jspEnd = javaSearchDoc.getJspOffset(match.getOffset() + match.getLength());

			JSPTranslation trans = javaSearchDoc.getJspTranslation();
			String jspText = trans.getJspText();
			String javaText = javaSearchDoc.getJavaText();

			if (DEBUG) 
				displayDebugInfo(match, jspStart, jspEnd, jspText, javaText);
		
			if (jspStart > -1 && jspEnd > -1)
				addSearchMatch(new Document(trans.getJspText()), javaSearchDoc.getFile(), jspStart, jspEnd, jspText);
		}
	}

	/**
	 * @param searchDoc
	 * @param jspStart
	 * @param jspEnd
	 * @param jspTranslation
	 * @param jspText
	 * @throws CoreException
	 */
	protected void addSearchMatch(IDocument jspDocument, IFile jspFile, int jspStart, int jspEnd, String jspText) {
		// implement in subclass
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.search.SearchRequestor#beginReporting()
	 */
	public void beginReporting() {

		if (DEBUG)
			System.out.println("JSP Search requestor: beginReporting()"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#endReporting()
	 */
	public void endReporting() {

		if (DEBUG)
			System.out.println("JSP Search requestor: endReporting()"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#enterParticipant(org.eclipse.jdt.core.search.SearchParticipant)
	 */
	public void enterParticipant(SearchParticipant participant) {

		if (DEBUG)
			System.out.println("JSP Search requestor: enterParticipant()"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#exitParticipant(org.eclipse.jdt.core.search.SearchParticipant)
	 */
	public void exitParticipant(SearchParticipant participant) {

		if (DEBUG)
			System.out.println("JSP Search requestor: exitParticipant()"); //$NON-NLS-1$
	}

	/**
	 * For debug.
	 * @param origMatch
	 * @param jspStart
	 * @param jspEnd
	 * @param jspText
	 * @param javaText
	 */
	private void displayDebugInfo(SearchMatch origMatch, int jspStart, int jspEnd, String jspText, String javaText) {

		if (origMatch == null || jspStart == -1 || jspEnd == -1 || jspEnd < jspStart || jspText == null || javaText == null)
			return;

		System.out.println("+-----------------------------------------+"); //$NON-NLS-1$
		System.out.println("accept possible match [jspDoc: " + origMatch.getResource().getFullPath().toOSString() + " " + origMatch.getOffset() + ":" + origMatch.getOffset() + origMatch.getLength() + "]?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		System.out.println("match info:"); //$NON-NLS-1$
		System.out.println("the java text is:" + javaText.substring(origMatch.getOffset(), origMatch.getOffset() + origMatch.getLength())); //$NON-NLS-1$
		System.out.println("java search match translates to jsp coords [start: " + jspStart + " end:" + jspEnd + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		System.out.println(" the jsp text is:" + jspText.substring(jspStart, jspEnd)); //$NON-NLS-1$
	}
}
