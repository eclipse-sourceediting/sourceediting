/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.CMDocumentImpl;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDFunction;
import org.eclipse.jst.jsp.core.internal.java.jspel.ASTExpression;
import org.eclipse.jst.jsp.core.internal.java.jspel.ASTFunctionInvocation;
import org.eclipse.jst.jsp.core.internal.java.jspel.FindFunctionInvocationVisitor;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParser;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParserConstants;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParserTokenManager;
import org.eclipse.jst.jsp.core.internal.java.jspel.ParseException;
import org.eclipse.jst.jsp.core.internal.java.jspel.SimpleCharStream;
import org.eclipse.jst.jsp.core.internal.java.jspel.Token;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * @deprecated This class is no longer used locally and will be removed in the future
 */
public class JSPELContentAssistProcessor extends JSPJavaContentAssistProcessor {
	protected char elCompletionProposalAutoActivationCharacters[] = new char[]{'.', ':'};

	protected JSPCompletionProcessor getJspCompletionProcessor() {
		if (fJspCompletionProcessor == null) {
			fJspCompletionProcessor = new JSPELCompletionProcessor();
		}
		return fJspCompletionProcessor;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentPosition) {


		// get results from JSP completion processor
		fJspCompletionProcessor = getJspCompletionProcessor();
		ICompletionProposal[] results = fJspCompletionProcessor.computeCompletionProposals(viewer, documentPosition);
		fErrorMessage = fJspCompletionProcessor.getErrorMessage();
		if (results.length == 0 && (fErrorMessage == null || fErrorMessage.length() == 0)) {
			fErrorMessage = UNKNOWN_CONTEXT;
		}

		IStructuredDocumentRegion flat = ContentAssistUtils.getStructuredDocumentRegion(viewer, documentPosition);
		
		if (flat != null) {
			ITextRegion cursorRegion = flat.getRegionAtCharacterOffset(documentPosition);
			if (DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE == cursorRegion.getType()) {
				ITextRegionContainer container = (ITextRegionContainer) cursorRegion;
				cursorRegion = container.getRegionAtCharacterOffset(documentPosition);
				if (cursorRegion.getType() == DOMJSPRegionContexts.JSP_EL_CONTENT) {
					String elText = container.getText(cursorRegion).trim();
					String prefix = getPrefix(documentPosition - container.getStartOffset(cursorRegion) - 1, elText);
					if (null != prefix) {
						List proposals = getFunctionProposals(prefix, (StructuredTextViewer) viewer, documentPosition);
						results = new ICompletionProposal[proposals.size()];
						proposals.toArray(results);
					}
				}
			}
		}


		return results;
	}

	protected String getPrefix(int relativePosition, String elText) {
		java.io.StringReader reader = new java.io.StringReader(elText);
		JSPELParserTokenManager scanner = new JSPELParserTokenManager(new SimpleCharStream(reader, 1, 1));
		Token curToken = null, lastIdentifier = null;
		while (JSPELParserConstants.EOF != (curToken = scanner.getNextToken()).kind) {
			if (JSPELParserConstants.COLON == curToken.kind && curToken.endColumn == relativePosition && null != lastIdentifier) {
				return (lastIdentifier.image);
			}

			if (JSPELParserConstants.IDENTIFIER == curToken.kind) {
				lastIdentifier = curToken;
			}
			else {
				lastIdentifier = null;
			}
		}
		return null;
	}

	protected ASTFunctionInvocation getInvocation(int relativePosition, String elText) {
		FindFunctionInvocationVisitor visitor = new FindFunctionInvocationVisitor(relativePosition);
		JSPELParser parser = JSPELParser.createParser(elText);
		try {
			ASTExpression expression = parser.Expression();
			return (ASTFunctionInvocation) expression.jjtAccept(visitor, null);
		}
		catch (ParseException e) { /* parse exception = no completion */
		}
		return (null);
	}


	public char[] getCompletionProposalAutoActivationCharacters() {
		return elCompletionProposalAutoActivationCharacters;
	}

	protected List getFunctionProposals(String prefix, StructuredTextViewer viewer, int offset) {
		TLDCMDocumentManager docMgr = TaglibController.getTLDCMDocumentManager(viewer.getDocument());
		ArrayList completionList = new ArrayList();
		if (docMgr == null)
			return null;

		Iterator taglibs = docMgr.getCMDocumentTrackers(offset).iterator();
		while (taglibs.hasNext()) {
			TaglibTracker tracker = (TaglibTracker) taglibs.next();
			if (tracker.getPrefix().equals(prefix)) {
				CMDocumentImpl doc = (CMDocumentImpl) tracker.getDocument();

				List functions = doc.getFunctions();
				for (Iterator it = functions.iterator(); it.hasNext();) {
					TLDFunction function = (TLDFunction) it.next();
					CustomCompletionProposal proposal = new CustomCompletionProposal(function.getName() + "()", //$NON-NLS-1$
								offset, 0, function.getName().length() + 1, null, function.getName() + " - " + function.getSignature(), null, null, 1); //$NON-NLS-1$

					completionList.add(proposal);
				}
			}
		}
		return completionList;
	}



}
