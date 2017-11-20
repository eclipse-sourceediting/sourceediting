/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.CMDocumentImpl;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDFunction;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParserConstants;
import org.eclipse.jst.jsp.core.internal.java.jspel.JSPELParserTokenManager;
import org.eclipse.jst.jsp.core.internal.java.jspel.SimpleCharStream;
import org.eclipse.jst.jsp.core.internal.java.jspel.Token;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

/**
 * <p>Compute JSP EL completion proposals</p>
 */
public class JSPELCompletionProposalComputer extends
		JSPJavaCompletionProposalComputer {
	
	/**
	 * @see org.eclipse.jst.jsp.ui.internal.contentassist.JSPJavaCompletionProposalComputer#computeCompletionProposals(org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		
		ITextViewer viewer = context.getViewer();
		int documentPosition = context.getInvocationOffset();

		// get results from JSP completion processor
		//3 for the "get" at the beginning of the java proposal
		List results = new ArrayList(computeJavaCompletionProposals(viewer, documentPosition, 3));

		//get the function proposals for syntax like: ${ fn:| }
		IStructuredDocumentRegion flat = ContentAssistUtils.getStructuredDocumentRegion(viewer, documentPosition);
		if (flat != null) {
			ITextRegion cursorRegion = flat.getRegionAtCharacterOffset(documentPosition);
			String elText;
			int startOffset;
			//if container then need to get inner region
			//else can use flat region
			if (cursorRegion instanceof ITextRegionContainer) {
				ITextRegionContainer container = (ITextRegionContainer) cursorRegion;
				cursorRegion = container.getRegionAtCharacterOffset(documentPosition);
				elText = container.getText(cursorRegion);
				startOffset = container.getStartOffset(cursorRegion);
			} else {
				elText = flat.getText(cursorRegion);
				startOffset = flat.getStartOffset(cursorRegion);
			}
			
			//sanity check that we are actually in EL region
			if (cursorRegion.getType() == DOMJSPRegionContexts.JSP_EL_CONTENT) {
				String prefix = getPrefix(documentPosition - startOffset, elText);
				if (null != prefix) {
					List proposals = getFunctionProposals(prefix, viewer, documentPosition);
					results.addAll(proposals);
				}
			}
		}

		return results;
	}
	
	/**
	 * @see org.eclipse.jst.jsp.ui.internal.contentassist.JSPJavaCompletionProposalComputer#getProposalCollector(
	 * 		org.eclipse.jdt.core.ICompilationUnit, org.eclipse.jst.jsp.core.internal.java.JSPTranslation)
	 */
	protected JSPProposalCollector getProposalCollector(ICompilationUnit cu,
			JSPTranslation translation) {
		
		return new JSPELProposalCollector(cu, translation);
	}

	/**
	 * <p>Gets the EL prefix from the relative position and the given EL text</p>
	 * 
	 * @param relativePosition
	 * @param elText
	 * @return
	 */
	private String getPrefix(int relativePosition, String elText) {
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

	/**
	 * <p>Get the EL function proposals, ex: ${fn:| }</p>
	 * @param prefix
	 * @param viewer
	 * @param offset
	 * @return
	 */
	private List getFunctionProposals(String prefix, ITextViewer viewer, int offset) {
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
