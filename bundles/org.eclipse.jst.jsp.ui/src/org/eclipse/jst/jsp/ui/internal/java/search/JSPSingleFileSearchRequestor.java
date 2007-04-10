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
import org.eclipse.jface.text.IDocument;


/**
 * <p>
 * Special requestor that adds search results for single file search
 * (Used for JSPFindOccurrences action).
 * It groups each match as it's own result.
 * </p>
 * 
 * @author pavery
 */
public class JSPSingleFileSearchRequestor extends BasicJSPSearchRequestor {

	private JSPSearchQuery fQuery = null;
	
	public JSPSingleFileSearchRequestor(JSPSearchQuery query) {
		this.fQuery = query;
	}
	
	protected void addSearchMatch(IDocument jspDocument, IFile jspFile, int jspStart, int jspEnd, String jspText) {
		// add match to JSP query...
		this.fQuery.addMatch(jspDocument, jspStart, jspEnd);
	}
}
