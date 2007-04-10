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
package org.eclipse.jst.jsp.ui.internal.java.search;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;


/**
 * @author pavery
 */
public class JSPSearchRequestor extends BasicJSPSearchRequestor {
	
	private ISearchRequestor fJavaRequestor = null;
	
	public JSPSearchRequestor() {
		super();
	}
	
	public JSPSearchRequestor(ISearchRequestor javaRequestor) {
		// need to report matches to javaRequestor
		this.fJavaRequestor = javaRequestor;
	}
	

	protected void addSearchMatch(IDocument jspDocument, IFile jspFile, int jspStart, int jspEnd, String jspText) {
		
		if(!jspFile.exists())
			return;

		int lineNumber = -1;
		try {
			lineNumber = jspDocument.getLineOfOffset(jspStart);
		} catch (BadLocationException e) {
			Logger.logException("offset: " + Integer.toString(jspStart), e); //$NON-NLS-1$
		}
		createSearchMarker(jspFile, jspStart, jspEnd, lineNumber);
		
		if(this.fJavaRequestor != null) {
			Match match = new Match(jspFile, jspStart, jspEnd - jspStart);
			this.fJavaRequestor.reportMatch(match);
		}
	}

	/**
	 * @param jspFile
	 * @param jspStart
	 * @param jspEnd
	 */
	private void createSearchMarker(IFile jspFile, int jspStart, int jspEnd, int lineNumber) {
		
		try {
			IMarker marker = jspFile.createMarker(NewSearchUI.SEARCH_MARKER);
			HashMap attributes = new HashMap(4);
			attributes.put(IMarker.CHAR_START, new Integer(jspStart));
			attributes.put(IMarker.CHAR_END, new Integer(jspEnd));
			attributes.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
			marker.setAttributes(attributes);
			
		} catch (CoreException e) {
			Logger.logException(e);
		}
	}
}
