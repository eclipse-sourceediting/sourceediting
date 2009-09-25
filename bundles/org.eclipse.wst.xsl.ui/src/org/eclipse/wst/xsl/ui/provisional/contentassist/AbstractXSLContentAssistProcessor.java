/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.provisional.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Node;

/**
 * This is an Abstract class that implementors should extend for implementing
 * XSL Content Assist Processors.   It provides common convience methods for
 * information that is needed to construct a content assist processor.
 * 
 * @author dcarver
 * @since 1.1
 */
public abstract class AbstractXSLContentAssistProcessor implements IXSLContentAssistProcessor {

	protected String errorMessage = ""; //$NON-NLS-1$
	protected ITextViewer textViewer = null;
	private IndexedRegion treeNode;
	private Node node;
	protected IDOMNode xmlNode;
	protected IStructuredDocumentRegion sdRegion;
	protected ITextRegion completionRegion;
	protected String matchString;
	protected int cursorPosition;

	/**
	 * @param textViewer
	 * @param documentPosition
	 */
	protected void initializeProposalVariables(ITextViewer textViewer, int documentPosition) {
		this.textViewer = textViewer;
		cursorPosition = documentPosition;
		treeNode = ContentAssistUtils.getNodeAt(textViewer, cursorPosition);
		node = getActualDOMNode((Node) treeNode);
		xmlNode = (IDOMNode) node;
		sdRegion = getStructuredDocumentRegion();
		completionRegion = getCompletionRegion(cursorPosition, node);
		matchString = getMatchString(sdRegion, completionRegion, cursorPosition);
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

	protected ITextRegion getCompletionRegion(int offset, IStructuredDocumentRegion sdRegion) {
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

	private String getMatchString(IStructuredDocumentRegion parent, ITextRegion aRegion, int offset) {
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

	private boolean hasMatchString(IStructuredDocumentRegion parent, ITextRegion aRegion, int offset) {
		return (parent.getText(aRegion).length() > 0)
				&& (parent.getStartOffset(aRegion) < offset);
	}

	private boolean hasNoMatchString(int offset, String regionType, int totalRegionOffset) {
		return regionType.equals(DOMRegionContext.XML_CONTENT)
				|| regionType.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)
				|| regionType.equals(DOMRegionContext.XML_TAG_OPEN)
				|| offset > totalRegionOffset;
	}

	private String extractMatchString(IStructuredDocumentRegion parent, ITextRegion aRegion, int offset) {
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

}
