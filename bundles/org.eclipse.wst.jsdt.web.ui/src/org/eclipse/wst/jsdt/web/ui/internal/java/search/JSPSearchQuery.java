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
import org.eclipse.jsdt.core.IJavaElement;
import org.eclipse.jsdt.core.search.SearchDocument;
import org.eclipse.wst.jsdt.web.core.internal.java.search.JSPSearchScope;
import org.eclipse.wst.jsdt.web.core.internal.java.search.JSPSearchSupport;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.wst.sse.ui.internal.search.BasicSearchQuery;

/**
 * Implementation of <code>ISearchQuery</code> for <code>IJavaElement</code>s
 * in JSP files.
 * 
 * @author pavery
 */
public class JSPSearchQuery extends BasicSearchQuery {

	/** the IJavaElement we are searching for in the file * */
	private IJavaElement fElement = null;

	public JSPSearchQuery(IFile file, IJavaElement element) {
		super(file);
		this.fElement = element;
	}

	public IJavaElement getJavaElement() {
		return this.fElement;
	}

	// for access by inner class
	public JSPSearchQuery getInstance() {
		return this;
	}

	@Override
	protected IStatus doQuery() {

		clearMatches();

		IStatus status = Status.OK_STATUS;
		try {
			JSPSearchSupport support = JSPSearchSupport.getInstance();
			// index the file
			SearchDocument delegate = support.addJspFile(getFile());

			String scopePath = delegate.getPath();
			JSPSearchScope singleFileScope = new JSPSearchScope(new String[] {
					getFile().getFullPath().toString(), scopePath });

			// perform a searchs
			// by passing in this jsp search query, requstor can add matches
			// support.searchRunnable(getJavaElement(), singleFileScope, new
			// JSPSingleFileSearchRequestor(getInstance()));
			support.searchRunnable(getJavaElement(), singleFileScope,
					new JSPSingleFileSearchRequestor(getInstance()));
		} catch (Exception e) {
			status = new Status(IStatus.ERROR,
					"org.eclipse.wst.sse.ui", IStatus.OK, "", null); //$NON-NLS-1$	//$NON-NLS-2$
		}
		return status;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	@Override
	public String getLabel() {
		String[] args = { getSearchText(), getOccurrencesCountText(),
				getFilename() };
		return NLS.bind(JSPUIMessages.OccurrencesSearchQuery_0, args);
	}

	private String getFilename() {
		String filename = JSPUIMessages.OccurrencesSearchQuery_2;
		if (getFile() != null) {
			filename = getFile().getName();
		}
		return filename;
	}

	private String getOccurrencesCountText() {
		String count = ""; //$NON-NLS-1$
		// pa_TODO make dynamic
		return count;
	}

	@Override
	protected String getSearchText() {
		return fElement.getElementName();
	}

	@Override
	public boolean canRerun() {

		return false;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
	 */
	@Override
	public boolean canRunInBackground() {
		return true;
	}

	/**
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	@Override
	public ISearchResult getSearchResult() {

		return new JSPOccurrencesSearchResult(this);
	}

}
