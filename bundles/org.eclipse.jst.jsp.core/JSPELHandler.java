package org.eclipse.jst.jsp.core.internal.java;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.java.jspel.ASTExpression;
import org.eclipse.jst.jsp.core.internal.java.jspel.ELGenerator;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParser;
import org.eclipse.jst.jsp.core.internal.java.jspel.ParseException;
import org.eclipse.jst.jsp.core.internal.java.jspel.Token;
import org.eclipse.jst.jsp.core.internal.java.jspel.TokenMgrError;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class JSPELHandler implements IELHandler {
	/**
	 * JSP Expression Language Parser.
	 */
	private JSPELParser elParser = null;
	
	private static JSPELHandler handlerInstance = null;
	
	private JSPELHandler() {

	}
	
	synchronized public static JSPELHandler getJSPELHandler() {
		if(handlerInstance == null)
			handlerInstance = new JSPELHandler();
		
		return handlerInstance;
	}

	public ArrayList translateEL(String elText, String delim,
			IStructuredDocumentRegion currentNode, int contentStart,
			int contentLength, StringBuffer fUserELExpressions,
			HashMap fUserELRanges, JSPTranslator translator) {
		
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
				gen.generate(expression, fUserELExpressions, fUserELRanges, translator, currentNode, contentStart, contentLength);
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
