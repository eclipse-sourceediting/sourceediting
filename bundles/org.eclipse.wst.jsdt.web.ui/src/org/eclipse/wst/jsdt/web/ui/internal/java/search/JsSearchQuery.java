/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.search.SearchDocument;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchScope;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchSupport;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIMessages;
import org.eclipse.wst.sse.ui.internal.search.BasicSearchQuery;

/**
 * Implementation of <code>ISearchQuery</code> for <code>IJavaElement</code>s
 * in JSP files.
 * 
 * @author pavery
 */
public class JsSearchQuery extends BasicSearchQuery {
	/** the IJavaElement we are searching for in the file * */
	private IJavaElement fElement = null;
	
	public JsSearchQuery(IFile file, IJavaElement element) {
		super(file);
		this.fElement = element;
	}
	
	
	public boolean canRerun() {
		return false;
	}
	
	/**
	 * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
	 */
	
	public boolean canRunInBackground() {
		return true;
	}
	
	
	protected IStatus doQuery() {
		clearMatches();
		IStatus status = Status.OK_STATUS;
		try {
			JsSearchSupport support = JsSearchSupport.getInstance();
			// index the file
			SearchDocument delegate = support.addJspFile(getFile());
			String scopePath = delegate.getPath();
			JsSearchScope singleFileScope = new JsSearchScope(new String[] { getFile().getFullPath().toString(), scopePath });
			// perform a searchs
			// by passing in this jsp search query, requstor can add matches
			// support.searchRunnable(getJavaElement(), singleFileScope, new
			// JSPSingleFileSearchRequestor(getInstance()));
			support.searchRunnable(getJavaElement(), singleFileScope, new JsSingleFileSearchRequestor(getInstance()));
		} catch (Exception e) {
			status = new Status(IStatus.ERROR, "org.eclipse.wst.sse.ui", IStatus.OK, "", null); //$NON-NLS-1$	//$NON-NLS-2$
		}
		return status;
	}
	
	private String getFilename() {
		String filename = JsUIMessages.OccurrencesSearchQuery_2;
		if (getFile() != null) {
			filename = getFile().getName();
		}
		return filename;
	}
	
	// for access by inner class
	public JsSearchQuery getInstance() {
		return this;
	}
	
	public IJavaElement getJavaElement() {
		return this.fElement;
	}
	
	/**
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	
	public String getLabel() {
		String[] args = { getSearchText(), getOccurrencesCountText(), getFilename() };
		return NLS.bind(JsUIMessages.OccurrencesSearchQuery_0, args);
	}
	
	private String getOccurrencesCountText() {
		String count = ""; //$NON-NLS-1$
		// pa_TODO make dynamic
		return count;
	}
	
	/**
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	
	public ISearchResult getSearchResult() {
		return new JsOccurrencesSearchResult(this);
	}
	
	
	protected String getSearchText() {
		return fElement.getElementName();
	}
}
