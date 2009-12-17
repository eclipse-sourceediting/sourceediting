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

package org.eclipse.jst.jsp.core.internal.java.jspel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.jspel.ELProblem;
import org.eclipse.jst.jsp.core.jspel.IJSPELTranslator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

	
public class JSPELTranslator implements IJSPELTranslator {
	/**
	 * JSP Expression Language Parser.
	 */
	private JSPELParser elParser = null;
	
	public List translateEL(String elText, String delim,
			IStructuredDocumentRegion currentNode, int contentStart,
			int contentLength, StringBuffer fUserELExpressions,
			HashMap fUserELRanges, IStructuredDocument document) {
		
		ArrayList elProblems = new ArrayList();
		
		try {
			synchronized(this) {
				if(null == elParser) {
					elParser = JSPELParser.createParser(elText);
				} else {
					elParser.ReInit(elText);
				}
			
				ASTExpression expression = elParser.Expression();
				ELGenerator gen = new ELGenerator();
				List generatorELProblems = gen.generate(expression, currentNode, fUserELExpressions, fUserELRanges, document, currentNode, contentStart, contentLength);
				elProblems.addAll(generatorELProblems);
			}
		} catch (ParseException e) {
			Token curTok = e.currentToken;
			int problemStartOffset;
			int problemEndOffset;
			Position pos = null;
			problemStartOffset =  contentStart + curTok.beginColumn;
			problemEndOffset = contentStart + curTok.endColumn;
			
			pos = new Position(problemStartOffset, problemEndOffset - problemStartOffset + 1);
			elProblems.add(new ELProblem(pos, e.getLocalizedMessage()));
		} catch (TokenMgrError te) {
			Position pos = new Position(contentStart, contentLength);
			elProblems.add(new ELProblem(pos, JSPCoreMessages.JSPEL_Token));
		}
		return elProblems;
	}
}
