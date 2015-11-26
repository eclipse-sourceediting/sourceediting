/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.translation;

import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.java.jspel.ASTExpression;
import org.eclipse.jst.jsp.core.internal.java.jspel.ELGenerator;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParser;
import org.eclipse.jst.jsp.core.internal.java.jspel.ParseException;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

public class JSPELTranslationTest extends TestCase {

	public void testBooleanMethods() throws ParseException {
		final String content = "pageContext.request.secure";
		final IStructuredModel model = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForJSP.ContentTypeID_JSP);
		final IStructuredDocument document = model.getStructuredDocument();
		document.set("${" + content + "}");
		final JSPELParser parser = JSPELParser.createParser(content);
		ASTExpression expression = parser.Expression();
		IStructuredDocumentRegion currentNode = document.getFirstStructuredDocumentRegion();
		StringBuffer result = new StringBuffer();
		new ELGenerator().generate(expression, currentNode, result, new HashMap(), document, currentNode, 0, content.length());
		int index = result.indexOf("((HttpServletRequest)");
		assertTrue("return of HTTPServletRequest was not found", index >= 0);
		assertTrue("pageContext.request.secure is not using the boolean accessor", result.substring(index).startsWith("((HttpServletRequest)pageContext.getRequest()).isSecure()"));
	}

}
