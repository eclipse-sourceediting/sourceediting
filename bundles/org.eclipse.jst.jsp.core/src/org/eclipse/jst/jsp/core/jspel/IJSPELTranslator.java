/*******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.jspel;

import java.util.List;
import java.util.HashMap;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public interface IJSPELTranslator {

	/**
	 * To override the EL translation, please see the extension point 
	 * org.eclipse.jst.jsp.core.eltranslator.
	 * 
	 * @param elText  The text to be translated.
	 * @param delim	The starting delimiter
	 * @param currentNode The current IStructuredDocumentRegion
	 * @param contentStart The starting offset of the EL to be translated
	 * @param contentLength The length of the EL content to be translated
	 * @param fUserELExpressions A string buffer to which generated code can be appended.
	 *		The text appended here will be inserted into the generated class at the top level 
	 *		not at the point in the JSP translation where the EL was found. 
	 * 
	 * @param fUserELRanges  Map of location ranges from JSP EL offsets to generated Java code.
	 * @param document The structured document.
	 * @return A list of ELProblems that describes any syntactic issues found.
	 */
	public List translateEL(String elText,
			String delim,
			IStructuredDocumentRegion currentNode, 
			int contentStart,
			int contentLength,
			StringBuffer userELExpressions,
			HashMap userELRanges,
			IStructuredDocument document);
}
