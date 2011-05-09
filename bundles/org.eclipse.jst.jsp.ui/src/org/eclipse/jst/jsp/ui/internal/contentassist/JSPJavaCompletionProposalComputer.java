/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistUtilities;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.internal.util.SharedXMLEditorPluginImageHelper;

/**
 * <p>Generates Java proposals for JSP documents</p>
 * 
 * @base org.eclipse.jst.jsp.ui.internal.contentassist.JSPJavaContentAssistProcessor
 */
public class JSPJavaCompletionProposalComputer extends DefaultXMLCompletionProposalComputer {

	/** The translation adapter used to create the Java proposals */
	private JSPTranslationAdapter fTranslationAdapter = null;
	
	/** translation adapter may be stale, check the model id */
	private String fModelId = null;

	/**
	 * Create the computer
	 */
	public JSPJavaCompletionProposalComputer() {
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#sessionEnded()
	 */
	public void sessionEnded() {
		fTranslationAdapter = null;
	}

	/**
	 * <p>Return a list of proposed code completions based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text-editor control.</p>
	 * 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLCompletionProposalComputer#computeCompletionProposals(org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		
		List results = new ArrayList(0);
		if(isValidContext(context)) {
			ITextViewer viewer = context.getViewer();
			int documentPosition = context.getInvocationOffset();
			IndexedRegion treeNode = ContentAssistUtils.getNodeAt(viewer, documentPosition);
			
			// get results from JSP completion processor
			results = computeJavaCompletionProposals(viewer, documentPosition, 0);

			IDOMNode xNode = null;
			IStructuredDocumentRegion flat = null;
			if (treeNode instanceof IDOMNode) {
				xNode = (IDOMNode) treeNode;
				flat = xNode.getFirstStructuredDocumentRegion();
				if (flat != null && flat.getType() == DOMJSPRegionContexts.JSP_CONTENT) {
					flat = flat.getPrevious();
				}
			}

			// this is in case it's a <%@, it will be a region container...
			ITextRegion openRegion = null;
			if (flat != null && flat instanceof ITextRegionContainer) {
				ITextRegionList v = ((ITextRegionContainer) flat).getRegions();
				if (v.size() > 0)
					openRegion = v.get(0);
			}

			// ADD CDATA PROPOSAL IF IT'S AN XML-JSP TAG
			if (flat != null && flat.getType() != DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN &&
					flat.getType() != DOMJSPRegionContexts.JSP_DECLARATION_OPEN &&
					flat.getType() != DOMJSPRegionContexts.JSP_EXPRESSION_OPEN &&
					flat.getType() != DOMRegionContext.BLOCK_TEXT &&
					(openRegion != null &&
							openRegion.getType() != DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN) &&
							!inAttributeRegion(flat, documentPosition)) {
				
				// determine if cursor is before or after selected range
				int adjustedDocPosition = documentPosition;
				int realCaretPosition = viewer.getTextWidget().getCaretOffset();
				int selectionLength = viewer.getSelectedRange().y;
				if (documentPosition > realCaretPosition) {
					adjustedDocPosition -= selectionLength;
				}

				CustomCompletionProposal cdataProposal = createCDATAProposal(adjustedDocPosition, selectionLength);
				results.add(cdataProposal);
			}
		}

		return results;
	}

	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLCompletionProposalComputer#computeContextInformation(org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
	
		ITextViewer viewer = context.getViewer();
		int documentOffset = context.getInvocationOffset();
		
		List results = new ArrayList();
		// need to compute context info here, if it's JSP, call java computer
		IDocument doc = viewer.getDocument();
		IDocumentPartitioner dp = null;
		if (doc instanceof IDocumentExtension3) {
			dp = ((IDocumentExtension3) doc).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
		}
		if (dp != null) {
			//IDocumentPartitioner dp = viewer.getDocument().getDocumentPartitioner();
			String type = dp.getPartition(documentOffset).getType();
			if (type == IJSPPartitions.JSP_DEFAULT || type == IJSPPartitions.JSP_CONTENT_JAVA) {
				// get context info from completion results...
				List proposals = computeCompletionProposals(context,monitor);
				for (int i = 0; i < proposals.size(); i++) {
					IContextInformation ci = ((ICompletionProposal)proposals.get(i)).getContextInformation();
					if (ci != null)
						results.add(ci);
				}
			}
		}
		return results;
	}
	
	/**
	 * The same as the normal <code>computeCompeltaionProposals</code> except the calculated
	 * java position is offset by the given extra offset.
	 * 
	 * @param viewer
	 *            the viewer whose document is used to compute the proposals
	 * @param documentPosition
	 *            an offset within the document for which completions should
	 *            be computed
	 * @param javaPositionExtraOffset
	 * 				the extra offset for the java position
	 * @return an array of completion proposals or <code>null</code> if no
	 *         proposals are possible
	 */
	protected List computeJavaCompletionProposals(ITextViewer viewer,
			int pos, int javaPositionExtraOffset) {
		
		JSPProposalCollector collector = null;
		
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());

			IDOMDocument xmlDoc = xmlModel.getDocument();
			if (fTranslationAdapter == null || xmlModel.getId() != fModelId) {
				fTranslationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				fModelId = xmlModel.getId();
			}
			if (fTranslationAdapter != null) {

				JSPTranslation translation = fTranslationAdapter.getJSPTranslation();
				int javaPosition = translation.getJavaOffset(pos) + javaPositionExtraOffset;

				try {

					ICompilationUnit cu = translation.getCompilationUnit();

					// can't get java proposals w/out a compilation unit
					// or without a valid position
					if (cu == null || -1 == javaPosition)
						return new ArrayList(0);
					
					collector = getProposalCollector(cu, translation);
					synchronized (cu) {
						cu.codeComplete(javaPosition, collector, (WorkingCopyOwner) null);
					}
				}
				catch (CoreException coreEx) {
					// a possible Java Model Exception due to not being a Web
					// (Java) Project
					coreEx.printStackTrace();
				}
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
			// throw out exceptions on code assist.
		}
		finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		ICompletionProposal[] results = new ICompletionProposal[0];
		if(collector != null) {
			results = collector.getJSPCompletionProposals();
			if (results == null || results.length < 1)
				this.setErrorMessage(JSPUIMessages.Java_Content_Assist_is_not_UI_);
		}
		
		return Arrays.asList(results);
	}
	
	protected JSPProposalCollector getProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		return new JSPProposalCollector(cu, translation);
	}
	
	private CustomCompletionProposal createCDATAProposal(int adjustedDocPosition, int selectionLength) {
		return new CustomCompletionProposal("<![CDATA[]]>", //$NON-NLS-1$
					adjustedDocPosition, selectionLength, // should be the selection length
					9, SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_CDATASECTION), 
					"CDATA Section", //$NON-NLS-1$
					null, null, XMLRelevanceConstants.R_CDATA);
	}

	private boolean inAttributeRegion(IStructuredDocumentRegion flat, int documentPosition) {
		ITextRegion attrContainer = flat.getRegionAtCharacterOffset(documentPosition);
		if (attrContainer != null && attrContainer instanceof ITextRegionContainer) {
			if (attrContainer.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param viewer
	 * @param documentPosition
	 * @return String
	 */
	private String getPartitionType(ITextViewer viewer, int documentPosition) {
		String partitionType = null;
		try {
			if (viewer instanceof ITextViewerExtension5)
				partitionType = TextUtilities.getContentType(viewer.getDocument(), IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, ((ITextViewerExtension5) viewer).modelOffset2WidgetOffset(documentPosition), false);
			else
				partitionType = TextUtilities.getContentType(viewer.getDocument(), IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, documentPosition, false);
		}
		catch (BadLocationException e) {
			partitionType = IDocument.DEFAULT_CONTENT_TYPE;
		}
		return partitionType;
	}
	
	/**
	 * <p>Determines if the context is a valid one for JSP Java proposals.
	 * The default result is <code>true</code></p>
	 * 
	 * @param context check this context to see if it is valid for JSP
	 * Java proposals
	 * @return <code>true</code> if the given context is a valid one for
	 * JSP Java proposals, <code>false</code> otherwise.  <code>true</code>
	 * is the default response if a specific case for <code>false</code> is
	 * not found.
	 */
	private boolean isValidContext(CompletionProposalInvocationContext context) {
		ITextViewer viewer = context.getViewer();
		int documentPosition = context.getInvocationOffset();
		
		String partitionType = getPartitionType(viewer, documentPosition);
		if (partitionType == IJSPPartitions.JSP_CONTENT_JAVA)
			return true;
		IStructuredDocument structuredDocument = (IStructuredDocument) viewer.getDocument();
		IStructuredDocumentRegion fn = structuredDocument.getRegionAtCharacterOffset(documentPosition);
		IStructuredDocumentRegion sdRegion = ContentAssistUtils.getStructuredDocumentRegion(viewer, documentPosition);
		// ////////////////////////////////////////////////////////////////////////////
		// ANOTHER WORKAROUND UNTIL PARTITIONING TAKES CARE OF THIS
		// check for xml-jsp tags...
		if (partitionType == IJSPPartitions.JSP_DIRECTIVE && fn != null) {
			IStructuredDocumentRegion possibleXMLJSP = ((fn.getType() == DOMRegionContext.XML_CONTENT) && fn.getPrevious() != null) ? fn.getPrevious() : fn;
			ITextRegionList regions = possibleXMLJSP.getRegions();
			if (regions.size() > 1) {
				// check bounds cases
				ITextRegion xmlOpenOrClose = regions.get(0);
				if (xmlOpenOrClose.getType() != DOMRegionContext.XML_TAG_OPEN &&
						documentPosition != possibleXMLJSP.getStartOffset() &&
						xmlOpenOrClose.getType() != DOMRegionContext.XML_END_TAG_OPEN &&
						documentPosition <= possibleXMLJSP.getStartOffset()) {
					
					// possible xml-jsp
					ITextRegion nameRegion = regions.get(1);
					String name = possibleXMLJSP.getText(nameRegion);
					if (name.equals("jsp:scriptlet") || name.equals("jsp:expression") || name.equals("jsp:declaration")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						return true;
					}
				}
			}
		}

		// ////////////////////////////////////////////////////////////////////////////
		// ** THIS IS A TEMP FIX UNTIL PARTITIONING TAKES CARE OF THIS...
		// check for XML-JSP in a <script> region
		if (partitionType == IJSPPartitions.JSP_CONTENT_JAVASCRIPT || partitionType == IHTMLPartitions.SCRIPT) {
			// fn should be block text
			IStructuredDocumentRegion decodedSDRegion = decodeScriptBlock(fn.getFullText());
			// System.out.println("decoded > " +
			// blockOfText.substring(decodedSDRegion.getStartOffset(),
			// decodedSDRegion.getEndOffset()));
			if (decodedSDRegion != null) {
				IStructuredDocumentRegion sdr = decodedSDRegion;
				while (sdr != null) {
					// System.out.println("sdr " + sdr.getType());
					// System.out.println("sdr > " +
					// blockOfText.substring(sdr.getStartOffset(),
					// sdr.getEndOffset()));
					if (sdr.getType() == DOMJSPRegionContexts.JSP_CONTENT) {
						if (documentPosition >= fn.getStartOffset() + sdr.getStartOffset() && documentPosition <= fn.getStartOffset() + sdr.getEndOffset()) {
							return true;
						}
					}
					else if (sdr.getType() == DOMRegionContext.XML_TAG_NAME) {
						if (documentPosition > fn.getStartOffset() + sdr.getStartOffset() && documentPosition < fn.getStartOffset() + sdr.getEndOffset()) {
							return false;
						}
						else if (documentPosition == fn.getStartOffset() + sdr.getEndOffset() && sdr.getNext() != null && sdr.getNext().getType() == DOMJSPRegionContexts.JSP_CONTENT) {
							// the end of an open tag <script>
							// <jsp:scriptlet>| blah </jsp:scriptlet>
							return true;
						}
						else if (documentPosition == fn.getStartOffset() + sdr.getStartOffset() && sdr.getPrevious() != null && sdr.getPrevious().getType() == DOMRegionContext.XML_TAG_NAME) {
							return true;
						}
					}
					sdr = sdr.getNext();
				}
			}
		}
		// /////////////////////////////////////////////////////////////////////////
		// check special JSP delimiter cases
		if (fn != null && partitionType == IJSPPartitions.JSP_CONTENT_DELIMITER) {
			IStructuredDocumentRegion fnDelim = fn;

			// if it's a nested JSP region, need to get the correct
			// StructuredDocumentRegion
			// not sure why this check was there...
			// if (fnDelim.getType() == XMLRegionContext.BLOCK_TEXT) {
			Iterator blockRegions = fnDelim.getRegions().iterator();
			ITextRegion temp = null;
			ITextRegionContainer trc;
			while (blockRegions.hasNext()) {
				temp = (ITextRegion) blockRegions.next();
				// we hit a nested
				if (temp instanceof ITextRegionContainer) {
					trc = (ITextRegionContainer) temp;
					// it's in this region
					if (documentPosition >= trc.getStartOffset() && documentPosition < trc.getEndOffset()) {
						Iterator nestedJSPRegions = trc.getRegions().iterator();
						while (nestedJSPRegions.hasNext()) {
							temp = (ITextRegion) nestedJSPRegions.next();
							if (XMLContentAssistUtilities.isJSPOpenDelimiter(temp.getType()) && documentPosition == trc.getStartOffset(temp)) {
								// HTML content assist
								// we actually want content assist for the
								// previous type of region,
								// well get those proposals from the embedded
								// adapter
								if (documentPosition > 0) {
									partitionType = getPartitionType(viewer, documentPosition - 1);
									break;
								}
							}
							else if (XMLContentAssistUtilities.isJSPCloseDelimiter(temp.getType()) && documentPosition == trc.getStartOffset(temp)) {
								// JSP content assist
								return true;
							}
						}
					}
				}
				// }
			}

			// take care of XML-JSP delimter cases
			if (XMLContentAssistUtilities.isXMLJSPDelimiter(fnDelim)) {
				// since it's a delimiter, we know it's a ITextRegionContainer
				ITextRegion firstRegion = fnDelim.getRegions().get(0);
				if (fnDelim.getStartOffset() == documentPosition && (firstRegion.getType() == DOMRegionContext.XML_TAG_OPEN)) {
					// |<jsp:scriptlet> </jsp:scriptlet>
					// (pa) commented out so that we get regular behavior JSP
					// macros etc...
					// return getHTMLCompletionProposals(viewer,
					// documentPosition);
				}
				else if (fnDelim.getStartOffset() == documentPosition && (firstRegion.getType() == DOMRegionContext.XML_END_TAG_OPEN)) {
					// <jsp:scriptlet> |</jsp:scriptlet>
					// check previous partition type to see if it's JAVASCRIPT
					// if it is, we're just gonna let the embedded JAVASCRIPT
					// adapter get the proposals
					if (documentPosition > 0) {
						String checkType = getPartitionType(viewer, documentPosition - 1);
						if (checkType != IJSPPartitions.JSP_CONTENT_JAVASCRIPT) { // this
							// check is failing for XML-JSP (region is not javascript...)
							return true;
						}
						partitionType = IJSPPartitions.JSP_CONTENT_JAVASCRIPT;
					}
				}
				else if ((firstRegion.getType() == DOMRegionContext.XML_TAG_OPEN) && documentPosition >= fnDelim.getEndOffset()) {
					// anything else inbetween
					return true;
				}
			}
			else if (XMLContentAssistUtilities.isJSPDelimiter(fnDelim)) {
				// the delimiter <%, <%=, <%!, ...
				if (XMLContentAssistUtilities.isJSPCloseDelimiter(fnDelim)) {
					if (documentPosition == fnDelim.getStartOffset()) {
						// check previous partition type to see if it's
						// JAVASCRIPT
						// if it is, we're just gonna let the embedded
						// JAVASCRIPT adapter get the proposals
						if (documentPosition > 0) {
							String checkType = getPartitionType(viewer, documentPosition - 1);
							if (checkType != IJSPPartitions.JSP_CONTENT_JAVASCRIPT) {
								return true;
							}
							partitionType = IJSPPartitions.JSP_CONTENT_JAVASCRIPT;
						}
					}
				}
				else if (XMLContentAssistUtilities.isJSPOpenDelimiter(fnDelim)) {
					// if it's the first position of open delimiter
					// use embedded HTML results
					if (documentPosition == fnDelim.getEndOffset()) {
						// it's at the EOF <%|
						return true;
					}
				}
			}
		}

		// need to check if it's JSP region inside of CDATA w/ no region
		// <![CDATA[ <%|%> ]]>
		// or a comment region
		// <!-- <% |%> -->
		if (fn != null && (fn.getType() == DOMRegionContext.XML_CDATA_TEXT || fn.getType() == DOMRegionContext.XML_COMMENT_TEXT)) {
			if (fn instanceof ITextRegionContainer) {
				Object[] cdataRegions = fn.getRegions().toArray();
				ITextRegion r = null;
				ITextRegion jspRegion = null;
				for (int i = 0; i < cdataRegions.length; i++) {
					r = (ITextRegion) cdataRegions[i];
					if (r instanceof ITextRegionContainer) {
						// CDATA embedded container, or comment container
						Object[] jspRegions = ((ITextRegionContainer) r).getRegions().toArray();
						for (int j = 0; j < jspRegions.length; j++) {
							jspRegion = (ITextRegion) jspRegions[j];
							if (jspRegion.getType() == DOMJSPRegionContexts.JSP_CLOSE) {
								if (sdRegion.getStartOffset(jspRegion) == documentPosition) {
									return true;
								}
							}
						}
					}
				}
			}
		}

		// check if it's in an attribute value, if so, don't add CDATA
		// proposal
		ITextRegion attrContainer = (fn != null) ? fn.getRegionAtCharacterOffset(documentPosition) : null;
		if (attrContainer != null && attrContainer instanceof ITextRegionContainer) {
			if (attrContainer.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				// test location of the cursor
				// return null if it's in the middle of an open/close delimiter
				Iterator attrRegions = ((ITextRegionContainer) attrContainer).getRegions().iterator();
				ITextRegion testRegion = null;
				while (attrRegions.hasNext()) {
					testRegion = (ITextRegion) attrRegions.next();
					// need to check for other valid attribute regions
					if (XMLContentAssistUtilities.isJSPOpenDelimiter(testRegion.getType())) {
						if (!(((ITextRegionContainer) attrContainer).getEndOffset(testRegion) <= documentPosition))
							return false;
					}
					else if (XMLContentAssistUtilities.isJSPCloseDelimiter(testRegion.getType())) {
						if (!(((ITextRegionContainer) attrContainer).getStartOffset(testRegion) >= documentPosition))
							return false;
					}
				}
				// TODO: handle non-Java code such as nested tags
				if (testRegion.getType().equals(DOMJSPRegionContexts.JSP_CONTENT)) {
					return true;
				}
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * ** TEMP WORKAROUND FOR CMVC 241882 Takes a String and blocks out
	 * jsp:scriptlet, jsp:expression, and jsp:declaration @param blockText
	 * @return
	 */
	private IStructuredDocumentRegion decodeScriptBlock(String blockText) {
		XMLSourceParser parser = new XMLSourceParser();
		// use JSP_CONTENT for region type
		parser.addBlockMarker(new BlockMarker("jsp:scriptlet", null, DOMJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.addBlockMarker(new BlockMarker("jsp:expression", null, DOMJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.addBlockMarker(new BlockMarker("jsp:declaration", null, DOMJSPRegionContexts.JSP_CONTENT, false, false)); //$NON-NLS-1$
		parser.reset(blockText);
		return parser.getDocumentRegions();
	}
}
