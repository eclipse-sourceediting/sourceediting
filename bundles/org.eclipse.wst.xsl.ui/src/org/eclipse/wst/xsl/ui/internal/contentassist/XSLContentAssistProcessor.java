/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *     David Carver - STAR - bug 230958 - refactored to fix bug with getting
 *                                        the DOM Document for the current editor
 *     David Carver - STAR - bug 240170 - refactored code to help with narrowing of
 *                                        results and easier maintenance.
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.Messages;
import org.w3c.dom.Node;

/**
 * The XSL Content Assist Processor provides content assistance for various
 * attributes values within the XSL Editor. This includes support for xpaths on
 * select statements as well as on test and match attributes.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XSLContentAssistProcessor implements IContentAssistProcessor {

	private String errorMessage = ""; //$NON-NLS-1$
	private ITextViewer textViewer = null;
	private ArrayList<ICompletionProposal> xslProposals;
	private ArrayList<ICompletionProposal> additionalProposals;
	private IndexedRegion treeNode;
	private Node node;
	private IDOMNode xmlNode;
	private IStructuredDocumentRegion sdRegion;
	private ITextRegion completionRegion;
	private String matchString;
	private int cursorPosition;

	/**
	 * Provides an XSL Content Assist Processor class that is XSL aware and XML
	 * aware.
	 */
	public XSLContentAssistProcessor() {
		super();
		xslProposals = new ArrayList<ICompletionProposal>();
		additionalProposals = new ArrayList<ICompletionProposal>();
	}

	/**
	 * CONTENT ASSIST STARTS HERE
	 * 
	 * Return a list of proposed code completions based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text-editor control.
	 * 
	 * @param textViewer
	 * @param documentPosition
	 *            - the cursor location within the document
	 * 
	 * @return an array of ICompletionProposal
	 */
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentPosition) {
		initializeProposalVariables(textViewer, documentPosition);

		ICompletionProposal[] xmlProposals = getXMLProposals();

		additionalProposals = getAdditionalXSLElementProposals();

		xslProposals = getXSLNamespaceProposals();

		ArrayList<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
		addProposals(xmlProposals, proposalList);
		proposalList.addAll(additionalProposals);
		proposalList.addAll(xslProposals);

		ICompletionProposal[] combinedProposals = combineProposals(proposalList);

		if (combinedProposals == null || combinedProposals.length == 0) {
			setErrorMessage(Messages.NoContentAssistance);
		}

		return combinedProposals;
	}

	/**
	 * @param textViewer
	 * @param documentPosition
	 */
	private void initializeProposalVariables(ITextViewer textViewer,
			int documentPosition) {
		this.textViewer = textViewer;
		cursorPosition = documentPosition;
		treeNode = ContentAssistUtils.getNodeAt(textViewer, cursorPosition);
		node = getActualDOMNode((Node) treeNode);
		xmlNode = (IDOMNode) node;
		sdRegion = getStructuredDocumentRegion();
		completionRegion = getCompletionRegion(cursorPosition, node);
		matchString = getMatchString(sdRegion, completionRegion, cursorPosition);
	}

	private ArrayList<ICompletionProposal> getXSLNamespaceProposals() {
		if (XSLCore.isXSLNamespace(xmlNode)) {
			XSLContentAssistRequestFactory requestFactory = new XSLContentAssistRequestFactory(
					textViewer, cursorPosition, xmlNode, sdRegion,
					completionRegion, matchString);

			IContentAssistProposalRequest contentAssistRequest = requestFactory
					.getContentAssistRequest();
			xslProposals = contentAssistRequest.getCompletionProposals();
		}
		return xslProposals;
	}

	private ArrayList<ICompletionProposal> getAdditionalXSLElementProposals() {
		if (!XSLCore.isXSLNamespace(xmlNode)) {
			additionalProposals = new XSLElementContentAssistRequest(xmlNode,
					sdRegion, completionRegion, cursorPosition, 0, matchString,
					textViewer).getCompletionProposals();
		}
		return additionalProposals;
	}

	private ICompletionProposal[] getXMLProposals() {
		AbstractContentAssistProcessor processor = new XMLContentAssistProcessor();

		ICompletionProposal proposals[] = processor.computeCompletionProposals(
				textViewer, cursorPosition);
		return proposals;
	}

	private void addProposals(ICompletionProposal[] proposals,
			ArrayList<ICompletionProposal> proposalList) {
		if (proposals != null) {
			for (int cnt = 0; cnt < proposals.length; cnt++) {
				proposalList.add(proposals[cnt]);
			}
		}
	}

	private ICompletionProposal[] combineProposals(
			ArrayList<ICompletionProposal> proposalList) {
		ICompletionProposal[] combinedProposals = new ICompletionProposal[proposalList
				.size()];
		proposalList.toArray(combinedProposals);
		return combinedProposals;
	}

	/**
	 * @param node
	 * @return
	 */
	private Node getActualDOMNode(Node node) {
		while ((node != null) && (node.getNodeType() == Node.TEXT_NODE)
				&& (node.getParentNode() != null)) {
			node = node.getParentNode();
		}
		return node;
	}

	/**
	 * StructuredTextViewer must be set before using this.
	 * 
	 * @param pos
	 * @return
	 */
	private IStructuredDocumentRegion getStructuredDocumentRegion() {
		return ContentAssistUtils.getStructuredDocumentRegion(textViewer,
				cursorPosition);
	}

	/**
	 * Return the region whose content's require completion. This is something
	 * of a misnomer as sometimes the user wants to be prompted for contents of
	 * a non-existent ITextRegion, such as for enumerated attribute values
	 * following an '=' sign.
	 * 
	 * Copied from AbstractContentAssist Processor.
	 */
	protected ITextRegion getCompletionRegion(int documentPosition, Node domnode) {
		if (domnode == null) {
			return null;
		}

		ITextRegion region = null;
		int offset = documentPosition;
		IStructuredDocumentRegion flatNode = null;
		IDOMNode node = (IDOMNode) domnode;

		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			if (node.getStructuredDocument().getLength() == 0) {
				return null;
			}
			ITextRegion result = node.getStructuredDocument()
					.getRegionAtCharacterOffset(offset)
					.getRegionAtCharacterOffset(offset);
			while (result == null) {
				offset--;
				result = node.getStructuredDocument()
						.getRegionAtCharacterOffset(offset)
						.getRegionAtCharacterOffset(offset);
			}
			return result;
		}

		IStructuredDocumentRegion startTag = node
				.getStartStructuredDocumentRegion();
		IStructuredDocumentRegion endTag = node
				.getEndStructuredDocumentRegion();

		if ((startTag != null) && (startTag.getStartOffset() <= offset)
				&& (offset < startTag.getStartOffset() + startTag.getLength())) {
			flatNode = startTag;
		} else if ((endTag != null) && (endTag.getStartOffset() <= offset)
				&& (offset < endTag.getStartOffset() + endTag.getLength())) {
			flatNode = endTag;
		}

		if (flatNode != null) {
			region = getCompletionRegion(offset, flatNode);
		} else {
			flatNode = node.getStructuredDocument().getRegionAtCharacterOffset(
					offset);
			if ((flatNode.getStartOffset() <= documentPosition)
					&& (flatNode.getEndOffset() >= documentPosition)) {
				if ((offset == flatNode.getStartOffset())
						&& (flatNode.getPrevious() != null)
						&& (((flatNode
								.getRegionAtCharacterOffset(documentPosition) != null) && (flatNode
								.getRegionAtCharacterOffset(documentPosition)
								.getType() != DOMRegionContext.XML_CONTENT))
								|| (flatNode.getPrevious().getLastRegion()
										.getType() == DOMRegionContext.XML_TAG_OPEN) || (flatNode
								.getPrevious().getLastRegion().getType() == DOMRegionContext.XML_END_TAG_OPEN))) {
					region = flatNode.getPrevious().getLastRegion();
				} else if (flatNode.getEndOffset() == documentPosition) {
					region = flatNode.getLastRegion();
				} else {
					region = flatNode.getFirstRegion();
				}
			} else {
				region = flatNode.getLastRegion();
			}
		}

		return region;
	}

	protected ITextRegion getCompletionRegion(int offset,
			IStructuredDocumentRegion sdRegion) {
		ITextRegion region = sdRegion.getRegionAtCharacterOffset(offset);
		if (region == null) {
			return null;
		}

		if (sdRegion.getStartOffset(region) == offset) {
			// The offset is at the beginning of the region
			if ((sdRegion.getStartOffset(region) == sdRegion.getStartOffset())
					&& (sdRegion.getPrevious() != null)
					&& (!sdRegion.getPrevious().isEnded())) {
				region = sdRegion.getPrevious().getRegionAtCharacterOffset(
						offset - 1);
			} else {
				// Is there no separating whitespace from the previous region?
				// If not,
				// then that region is the important one
				ITextRegion previousRegion = sdRegion
						.getRegionAtCharacterOffset(offset - 1);
				if ((previousRegion != null)
						&& (previousRegion != region)
						&& (previousRegion.getTextLength() == previousRegion
								.getLength())) {
					region = previousRegion;
				}
			}
		} else {
			// The offset is NOT at the beginning of the region
			if (offset > sdRegion.getStartOffset(region)
					+ region.getTextLength()) {
				// Is the offset within the whitespace after the text in this
				// region?
				// If so, use the next region
				ITextRegion nextRegion = sdRegion
						.getRegionAtCharacterOffset(sdRegion
								.getStartOffset(region)
								+ region.getLength());
				if (nextRegion != null) {
					region = nextRegion;
				}
			} else {
				// Is the offset within the important text for this region?
				// If so, then we've already got the right one.
			}
		}

		// valid WHITE_SPACE region handler (#179924)
		if ((region != null)
				&& (region.getType() == DOMRegionContext.WHITE_SPACE)) {
			ITextRegion previousRegion = sdRegion
					.getRegionAtCharacterOffset(sdRegion.getStartOffset(region) - 1);
			if (previousRegion != null) {
				region = previousRegion;
			}
		}

		return region;
	}

	private String getMatchString(IStructuredDocumentRegion parent,
			ITextRegion aRegion, int offset) {
		String matchString = ""; //$NON-NLS-1$
		
		if (isNotMatchStringRegion(parent, aRegion, offset)) {
			return matchString; 
		}

		if (hasMatchString(parent, aRegion, offset)) {
			matchString = extractMatchString(parent, aRegion, offset);
		}
		return matchString;
	}

	private boolean isNotMatchStringRegion(IStructuredDocumentRegion parent, ITextRegion aRegion, int offset) {
		if (aRegion == null || parent == null)
			return true;
		
		String regionType = aRegion.getType();
		int totalRegionOffset = parent.getStartOffset(aRegion)
				+ aRegion.getTextLength();
		return (isCloseRegion(aRegion)
				|| hasNoMatchString(offset, regionType, totalRegionOffset));
	}

	private boolean isCloseRegion(ITextRegion region) {
		String type = region.getType();
		return ((type == DOMRegionContext.XML_PI_CLOSE)
				|| (type == DOMRegionContext.XML_TAG_CLOSE)
				|| (type == DOMRegionContext.XML_EMPTY_TAG_CLOSE)
				|| (type == DOMRegionContext.XML_CDATA_CLOSE)
				|| (type == DOMRegionContext.XML_COMMENT_CLOSE)
				|| (type == DOMRegionContext.XML_ATTLIST_DECL_CLOSE)
				|| (type == DOMRegionContext.XML_ELEMENT_DECL_CLOSE)
				|| (type == DOMRegionContext.XML_DOCTYPE_DECLARATION_CLOSE) || (type == DOMRegionContext.XML_DECLARATION_CLOSE));
	}

	private boolean hasMatchString(IStructuredDocumentRegion parent,
			ITextRegion aRegion, int offset) {
		return (parent.getText(aRegion).length() > 0)
				&& (parent.getStartOffset(aRegion) < offset);
	}

	private boolean hasNoMatchString(int offset, String regionType,
			int totalRegionOffset) {
		return regionType.equals(DOMRegionContext.XML_CONTENT)
				|| regionType.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)
				|| regionType.equals(DOMRegionContext.XML_TAG_OPEN)
				|| offset > totalRegionOffset;
	}

	private String extractMatchString(IStructuredDocumentRegion parent,
			ITextRegion aRegion, int offset) {
		String matchString;
		matchString = parent.getText(aRegion).substring(0,
				offset - parent.getStartOffset(aRegion));
		if (matchString.startsWith("\"")) { //$NON-NLS-1$
			matchString = matchString.substring(1);
		}
		return matchString;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	/**
	 * Returns the characters which when entered by the user should
	 * automatically trigger the presentation of possible completions.
	 * 
	 * the auto activation characters for completion proposal or
	 * <code>null</code> if no auto activation is desired
	 * 
	 * @return an array of activation characters
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		char[] completionProposals = { '"', '\'', ':', '[', '{', '<' };

		return completionProposals;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message for why content assistance didn't complete.
	 * 
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
