package org.eclipse.jst.jsp.core.internal.java;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public interface IELHandler {

	public ArrayList translateEL(String elText,
			String delim,
			IStructuredDocumentRegion currentNode, 
			int contentStart,
			int contentLength,
			StringBuffer fUserELExpressions,
			HashMap fUserELRanges,
			JSPTranslator translator);
}
