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
package org.eclipse.wst.jsdt.web.ui.internal.java.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.jsdt.core.search.SearchDocument;
import org.eclipse.wst.jsdt.core.search.SearchMatch;
import org.eclipse.wst.jsdt.core.search.SearchParticipant;
import org.eclipse.wst.jsdt.core.search.SearchRequestor;

import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.search.JSDTSearchDocumentDelegate;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchSupport;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author pavery
 */
public class BasicJsSearchRequestor extends SearchRequestor {
	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspsearch"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	/**
	 * Maps java search coordinates to corresponding JSP coordinates. Adds the
	 * matches to the Search Results view.
	 * 
	 * @see org.eclipse.wst.jsdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.wst.jsdt.core.search.SearchMatch)
	 */
	
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (JsSearchSupport.getInstance().isCanceled()) {
			return;
		}
		String matchDocumentPath = match.getResource().getFullPath().toString();
		SearchDocument searchDoc = JsSearchSupport.getInstance().getSearchDocument(matchDocumentPath);
		if (searchDoc != null && searchDoc instanceof JSDTSearchDocumentDelegate) {
			JSDTSearchDocumentDelegate javaSearchDoc = (JSDTSearchDocumentDelegate) searchDoc;
			int jspStart = match.getOffset();
			int jspEnd = match.getOffset() + match.getLength();
			IJsTranslation trans = javaSearchDoc.getJspTranslation();
			String jspText = trans.getHtmlText();
			String javaText = javaSearchDoc.getJavaText();
			if (BasicJsSearchRequestor.DEBUG) {
				displayDebugInfo(match, jspStart, jspEnd, jspText, javaText);
			}
			if (jspStart > -1 && jspEnd > -1) {
				addSearchMatch(new Document(trans.getHtmlText()), javaSearchDoc.getFile(), jspStart, jspEnd, jspText);
			}
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
	 * @see org.eclipse.wst.jsdt.core.search.SearchRequestor#beginReporting()
	 */
	
	public void beginReporting() {
		if (BasicJsSearchRequestor.DEBUG) {
			System.out.println("JSP Search requestor: beginReporting()"); //$NON-NLS-1$
		}
	}
	
	/**
	 * For debug.
	 * 
	 * @param origMatch
	 * @param jspStart
	 * @param jspEnd
	 * @param jspText
	 * @param javaText
	 */
	private void displayDebugInfo(SearchMatch origMatch, int jspStart, int jspEnd, String jspText, String javaText) {
		if (origMatch == null || jspStart == -1 || jspEnd == -1 || jspEnd < jspStart || jspText == null || javaText == null) {
			return;
		}
		System.out.println("+-----------------------------------------+"); //$NON-NLS-1$
		System.out.println("accept possible match [jspDoc: " + origMatch.getResource().getFullPath().toOSString() + " " + origMatch.getOffset() + ":" + origMatch.getOffset() + origMatch.getLength() + "]?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		System.out.println("match info:"); //$NON-NLS-1$
		System.out.println("the java text is:" + javaText.substring(origMatch.getOffset(), origMatch.getOffset() + origMatch.getLength())); //$NON-NLS-1$
		System.out.println("java search match translates to jsp coords [start: " + jspStart + " end:" + jspEnd + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		System.out.println(" the jsp text is:" + jspText.substring(jspStart, jspEnd)); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.wst.jsdt.core.search.SearchRequestor#endReporting()
	 */
	
	public void endReporting() {
		if (BasicJsSearchRequestor.DEBUG) {
			System.out.println("JSP Search requestor: endReporting()"); //$NON-NLS-1$
		}
	}
	
	/**
	 * @see org.eclipse.wst.jsdt.core.search.SearchRequestor#enterParticipant(org.eclipse.wst.jsdt.core.search.SearchParticipant)
	 */
	
	public void enterParticipant(SearchParticipant participant) {
		if (BasicJsSearchRequestor.DEBUG) {
			System.out.println("JSP Search requestor: enterParticipant()"); //$NON-NLS-1$
		}
	}
	
	/**
	 * @see org.eclipse.wst.jsdt.core.search.SearchRequestor#exitParticipant(org.eclipse.wst.jsdt.core.search.SearchParticipant)
	 */
	
	public void exitParticipant(SearchParticipant participant) {
		if (BasicJsSearchRequestor.DEBUG) {
			System.out.println("JSP Search requestor: exitParticipant()"); //$NON-NLS-1$
		}
	}
}
