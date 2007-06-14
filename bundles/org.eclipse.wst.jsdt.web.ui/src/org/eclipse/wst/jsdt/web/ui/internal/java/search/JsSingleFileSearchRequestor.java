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
import org.eclipse.jface.text.IDocument;

/**
 * <p>
 * Special requestor that adds search results for single file search (Used for
 * JSPFindOccurrences action). It groups each match as it's own result.
 * </p>
 * 
 * @author pavery
 */
public class JsSingleFileSearchRequestor extends BasicJsSearchRequestor {
	private JsSearchQuery fQuery = null;
	
	public JsSingleFileSearchRequestor(JsSearchQuery query) {
		this.fQuery = query;
	}
	
	@Override
	protected void addSearchMatch(IDocument jspDocument, IFile jspFile, int jspStart, int jspEnd, String jspText) {
		// add match to JSP query...
		this.fQuery.addMatch(jspDocument, jspStart, jspEnd);
	}
}