/*******************************************************************************
 * Copyright (c) 2015-2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLCompletionProposalComputer
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.ui.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.ui.internal.JSONUIMessages;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.w3c.dom.Node;

public abstract class AbstractJSONCompletionProposalComputer implements
		ICompletionProposalComputer {

	private static final String BLANK = " "; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String COLON = ":"; //$NON-NLS-1$
	protected static final String QUOTE = "\""; //$NON-NLS-1$
	protected static final String TRUE = "true"; //$NON-NLS-1$
	protected static final String FALSE = "false"; //$NON-NLS-1$
	private String fErrorMessage;
	private ITextViewer fTextViewer;

	public AbstractJSONCompletionProposalComputer() {
		fErrorMessage = null;
		fTextViewer = null;
	}

	@Override
	public List computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {

		ITextViewer textViewer = context.getViewer();
		int documentPosition = context.getInvocationOffset();

		setErrorMessage(null);

		fTextViewer = textViewer;

		IndexedRegion treeNode = ContentAssistUtils.getNodeAt(textViewer,
				documentPosition <= 0 ? 0 : documentPosition - 1);

		IJSONNode node = (IJSONNode) treeNode;
		while ((node != null) && (node.getNodeType() == Node.TEXT_NODE)
				&& (node.getParentNode() != null)) {
			node = node.getParentNode();
		}

		ContentAssistRequest contentAssistRequest = null;

		IStructuredDocumentRegion sdRegion = getStructuredDocumentRegion(documentPosition);
		ITextRegion completionRegion = getCompletionRegion(documentPosition,
				node);

		// Fix completion region in case of JSON_OBJECT_CLOSE
		if (completionRegion != null && completionRegion.getType() == JSONRegionContexts.JSON_OBJECT_CLOSE && documentPosition > 0) {
			completionRegion = getCompletionRegion(documentPosition, node);
		}
		String matchString = EMPTY;
		if (completionRegion != null) {
			if (isPairValue(context, node)) {
				try {
					String nodeText = getNodeText(node);
					int colonIndex  = nodeText.indexOf(COLON);
					int offset = documentPosition - node.getStartOffset();
					if (colonIndex >= 0 && offset >= 0) {
						String str = nodeText.substring(colonIndex+1, offset);
						str = str.replaceAll(",", BLANK); //$NON-NLS-1$
						matchString = str;
					}
				} catch (BadLocationException e) {
					// ignore
				}
			} else {
				matchString = getMatchString(sdRegion, completionRegion, documentPosition);
			}
		}

		// compute normal proposals
		contentAssistRequest = computeCompletionProposals(matchString,
				completionRegion, (IJSONNode) treeNode, node != null ? node.getParentNode() : null, context);
		if (contentAssistRequest == null) {
			contentAssistRequest = new ContentAssistRequest(
					(IJSONNode) treeNode, node != null ? node.getParentNode() : null, sdRegion,
					completionRegion, documentPosition, 0, EMPTY);
			setErrorMessage(JSONUIMessages.Content_Assist_not_availab_UI_);
		}

		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=123892 Only set this
		 * error message if nothing else was already set
		 */
		if (contentAssistRequest.getProposals().size() == 0
				&& getErrorMessage() == null) {
			setErrorMessage(JSONUIMessages.Content_Assist_not_availab_UI_);
		}

		ICompletionProposal[] props = contentAssistRequest
				.getCompletionProposals();
		return (props != null) ? Arrays.asList(props) : new ArrayList(0);
	}

	/**
	 * @param errorMessage
	 *            the reason why computeProposals was not able to find any
	 *            completions.
	 */
	protected void setErrorMessage(String errorMessage) {
		fErrorMessage = errorMessage;
	}

	/**
	 * <p>
	 * This does all the magic of figuring out where in the JSON type document
	 * the content assist was invoked and then calling the corresponding method
	 * to add the correct proposals
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE: </b>if overriding be sure to make super call back to this method
	 * otherwise you will loose all of the proposals generated by this method
	 * </p>
	 * 
	 * @param matchString
	 * @param completionRegion
	 * @param treeNode
	 * @param xmlnode
	 * @param context
	 * 
	 * @return {@link ContentAssistRequest} that now has all the proposals in it
	 */
	protected ContentAssistRequest computeCompletionProposals(
			String matchString, ITextRegion completionRegion,
			IJSONNode treeNode, IJSONNode xmlnode,
			CompletionProposalInvocationContext context) {

		int documentPosition = context.getInvocationOffset();

		ContentAssistRequest contentAssistRequest = null;
		String regionType = completionRegion!= null ? completionRegion.getType() : EMPTY;
		IStructuredDocumentRegion sdRegion = getStructuredDocumentRegion(documentPosition);

		// Handle the most common and best supported cases
		if(xmlnode != null) {
			if (xmlnode.getNodeType() == IJSONNode.DOCUMENT_NODE || xmlnode.getNodeType() == IJSONNode.OBJECT_NODE) {
				if (treeNode.getNodeType() == IJSONNode.OBJECT_NODE) {
					if (regionType == JSONRegionContexts.JSON_OBJECT_OPEN
							|| regionType == JSONRegionContexts.JSON_OBJECT_CLOSE
							|| regionType == JSONRegionContexts.JSON_OBJECT_KEY
							|| regionType == JSONRegionContexts.JSON_COMMA
							|| regionType == JSONRegionContexts.JSON_UNKNOWN) {
						contentAssistRequest = computeObjectKeyProposals(matchString,
								completionRegion, treeNode, xmlnode, context);
					}
				} else if ((treeNode.getNodeType() == IJSONNode.PAIR_NODE)) {
					if (regionType == JSONRegionContexts.JSON_OBJECT_KEY
							|| regionType == JSONRegionContexts.JSON_OBJECT_OPEN
							|| regionType == JSONRegionContexts.JSON_OBJECT_CLOSE
							|| regionType == JSONRegionContexts.JSON_ARRAY_OPEN
							|| regionType == JSONRegionContexts.JSON_ARRAY_CLOSE
							|| regionType == JSONRegionContexts.JSON_COMMA
							|| regionType == JSONRegionContexts.JSON_VALUE_BOOLEAN
							|| regionType == JSONRegionContexts.JSON_UNKNOWN
							|| regionType == JSONRegionContexts.JSON_COLON
							|| regionType == JSONRegionContexts.JSON_VALUE_STRING) {
						contentAssistRequest = computeObjectKeyProposals(matchString,
								completionRegion, treeNode, xmlnode, context);
					}
				}
			}
		}
		return contentAssistRequest;
	}

	private ContentAssistRequest computeObjectKeyProposals(String matchString,
			ITextRegion completionRegion, IJSONNode nodeAtOffset,
			IJSONNode node, CompletionProposalInvocationContext context) {
		int documentPosition = context.getInvocationOffset();
		ContentAssistRequest contentAssistRequest = null;
		IStructuredDocumentRegion sdRegion = getStructuredDocumentRegion(documentPosition);

		int replaceLength = 0;
		int begin = documentPosition;
		if (completionRegion.getType() == JSONRegionContexts.JSON_OBJECT_KEY || completionRegion.getType() == JSONRegionContexts.JSON_UNKNOWN) {
			replaceLength = completionRegion.getTextLength();
			// if container region, be sure replace length is only the attribute
			// value region not the entire container
			if (completionRegion instanceof ITextRegionContainer) {
				ITextRegion openRegion = ((ITextRegionContainer) completionRegion)
						.getFirstRegion();
				ITextRegion closeRegion = ((ITextRegionContainer) completionRegion)
						.getLastRegion();
				if (openRegion.getType() != closeRegion.getType()) {
					replaceLength = openRegion.getTextLength();
				}
			}
			begin = sdRegion.getStartOffset(completionRegion);
		}

		if (isPairValue(context, nodeAtOffset)) {
			IJSONPair pair = (IJSONPair) nodeAtOffset;
			IJSONValue value = pair.getValue();
			if (value != null) {
				try {
					begin = value.getStartOffset();
					String valueText = getNodeText(value);
					valueText = valueText.trim();
					replaceLength = valueText.length();
					if (valueText.startsWith(QUOTE)) {
						begin = begin + 1;
						replaceLength = replaceLength - 1;
					}
					if (valueText.endsWith(QUOTE)) {
						replaceLength = replaceLength - 1;
					}
				} catch (BadLocationException e) {
					// ignore
				}
			}
		} else if (nodeAtOffset instanceof IJSONPair) {
			IJSONPair pair = (IJSONPair) nodeAtOffset;
				try {
					begin = pair.getStartOffset();
					String text = getNodeText(pair);
					text = text.trim();
					replaceLength = pair.getName().length();
					if (text.startsWith(QUOTE)) {
						begin = begin + 1;
					}
				} catch (BadLocationException e) {
					// ignore
				}
		}
		contentAssistRequest = new ContentAssistRequest(nodeAtOffset,
				node.getParentNode(), sdRegion, completionRegion, begin,
				replaceLength, matchString);
		addObjectKeyProposals(contentAssistRequest, context);
		return contentAssistRequest;
	}

	protected abstract void addObjectKeyProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context);

	private ITextRegion getCompletionRegion(int offset,
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
				// Is the region also the start of the node? If so, the
				// previous IStructuredDocumentRegion is
				// where to look for a useful region.
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
			if ((region.getType() != JSONRegionContexts.JSON_COLON)
					&& (offset > sdRegion.getStartOffset(region)
							+ region.getTextLength())) { // attached
															// JSON_TAG_ATTRIBUTE_EQUALS
															// filter due to
															// #bug219992
				// Is the offset within the whitespace after the text in this
				// region?
				// If so, use the next region
				ITextRegion nextRegion = sdRegion
						.getRegionAtCharacterOffset(sdRegion
								.getStartOffset(region) + region.getLength());
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
				&& (region.getType() == JSONRegionContexts.WHITE_SPACE)) {
			ITextRegion previousRegion = sdRegion
					.getRegionAtCharacterOffset(sdRegion.getStartOffset(region) - 1);
			if (previousRegion != null) {
				region = previousRegion;
			}
		}

		return region;
	}

	/**
	 * Return the region whose content's require completion. This is something
	 * of a misnomer as sometimes the user wants to be prompted for contents of
	 * a non-existant ITextRegion, such as for enumerated attribute values
	 * following an '=' sign.
	 */
	private ITextRegion getCompletionRegion(int documentPosition, IJSONNode node) {
		if (node == null) {
			return null;
		}

		ITextRegion region = null;
		int offset = documentPosition;
		IStructuredDocumentRegion flatNode = null;

		if (node.getNodeType() == IJSONNode.DOCUMENT_NODE) {
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

		// Determine if the offset is within the start
		// IStructuredDocumentRegion, end IStructuredDocumentRegion, or
		// somewhere within the Node's JSON content.
		if ((startTag != null) && (startTag.getStartOffset() <= offset)
				&& (offset < startTag.getStartOffset() + startTag.getLength())) {
			flatNode = startTag;
		} else if ((endTag != null) && (endTag.getStartOffset() <= offset)
				&& (offset < endTag.getStartOffset() + endTag.getLength())) {
			flatNode = endTag;
		}

		if (flatNode != null) {
			// the offset is definitely within the start or end tag, continue
			// on and find the region
			region = getCompletionRegion(offset, flatNode);
		} else {
			// the docPosition is neither within the start nor the end, so it
			// must be content
			flatNode = node.getStructuredDocument().getRegionAtCharacterOffset(
					offset);
			// (pa) ITextRegion refactor
			// if (flatNode.contains(documentPosition)) {
			if ((flatNode.getStartOffset() <= documentPosition)
					&& (flatNode.getEndOffset() >= documentPosition)) {
				// we're interesting in completing/extending the previous
				// IStructuredDocumentRegion if the current
				// IStructuredDocumentRegion isn't plain content or if it's
				// preceded by an orphan '<'
				/*
				 * if ((offset == flatNode.getStartOffset()) &&
				 * (flatNode.getPrevious() != null) && (((flatNode
				 * .getRegionAtCharacterOffset(documentPosition) != null)) ||
				 * (flatNode.getPrevious().getLastRegion() .getType() ==
				 * JSONRegionContext.JSON_TAG_OPEN) || (flatNode
				 * .getPrevious().getLastRegion().getType() ==
				 * JSONRegionContext.JSON_END_TAG_OPEN))) {
				 * 
				 * // Is the region also the start of the node? If so, the //
				 * previous IStructuredDocumentRegion is // where to look for a
				 * useful region. region =
				 * flatNode.getPrevious().getLastRegion(); } else if
				 * (flatNode.getEndOffset() == documentPosition) { region =
				 * flatNode.getLastRegion(); } else { region =
				 * flatNode.getFirstRegion(); }
				 */
				region = flatNode.getFirstRegion();
			} else {
				// catch end of document positions where the docPosition isn't
				// in a IStructuredDocumentRegion
				region = flatNode.getLastRegion();
			}
		}

		return region;
	}

	private String getMatchString(IStructuredDocumentRegion parent,
			ITextRegion aRegion, int offset) {
		if (aRegion == null) {
			return EMPTY;
		}
		String regionType = aRegion.getType();
		if (regionType != JSONRegionContexts.JSON_OBJECT_KEY) {
			return EMPTY;
		}
		if ((parent.getText(aRegion).length() > 0)
				&& (parent.getStartOffset(aRegion) < offset)) {
			return parent.getText(aRegion).substring(0,
					offset - parent.getStartOffset(aRegion));
		}
		return EMPTY;
	}

	/**
	 * StructuredTextViewer must be set before using this.
	 */
	private IStructuredDocumentRegion getStructuredDocumentRegion(int pos) {
		return ContentAssistUtils.getStructuredDocumentRegion(fTextViewer, pos);
	}

	/**
	 * <p>
	 * Returns information about possible contexts based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text viewer.
	 * </p>
	 * 
	 * @see org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer#computeContextInformation(org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public List computeContextInformation(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {

		// no default context info
		return Collections.EMPTY_LIST;
	}

	protected boolean isPairValue(CompletionProposalInvocationContext context, IJSONNode node) {
		if ( !(node instanceof IJSONPair) ) {
			return false;
		}
		int documentPosition = context.getInvocationOffset();
		try {
			String nodeText = getNodeText(node);
			int colonIndex  = nodeText.indexOf(COLON); //$NON-NLS-1$
			if (colonIndex >= 0) {
				return documentPosition > node.getStartOffset() + colonIndex;
			}
		} catch (BadLocationException e) {
			// ignore
		}
		return false;
	}

	private String getNodeText(IJSONNode node) throws BadLocationException {
		return node.getStructuredDocument().get(node.getStartOffset(), node.getEndOffset() - node.getStartOffset());
	}

	/**
	 * <p>
	 * helpful utility method for determining if one string starts with another
	 * one. This is case insensitive. If either are null then result is
	 * <code>true</code>
	 * </p>
	 * 
	 * @param aString
	 *            the string to check to see if it starts with the given prefix
	 * @param prefix
	 *            check that the given string starts with this prefix
	 * 
	 * @return <code>true</code> if the given string starts with the given
	 *         prefix, <code>false</code> otherwise
	 */
	protected static boolean beginsWith(String aString, String prefix) {
		if ((aString == null) || (prefix == null)) {
			return true;
		}
		return aString.toLowerCase().startsWith(prefix.toLowerCase());
	}
}
