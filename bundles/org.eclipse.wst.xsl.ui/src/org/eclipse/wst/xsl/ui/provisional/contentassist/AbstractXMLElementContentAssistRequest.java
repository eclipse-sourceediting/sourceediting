/*******************************************************************************
 *Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR)  - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.provisional.contentassist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryAction;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.internal.editor.CMImageUtil;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.eclipse.wst.xml.ui.internal.taginfo.MarkupTagInfoProvider;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xsl.ui.internal.contentassist.contentmodel.XSLContentModelGenerator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Adopters can extend this class to implement their own content assistance for Element
 * proposals using the XML Content Model.
 * 
 * @author David Carver
 * @since 1.1
 */
public abstract class AbstractXMLElementContentAssistRequest extends AbstractXSLContentAssistRequest {

	protected static final String XPATH_FIRST_XSLANCESTOR_NODE = "ancestor::xsl:*[1]"; //$NON-NLS-1$
	protected MarkupTagInfoProvider infoProvider = null;
	protected XSLContentModelGenerator contentModel;
	
	/**
	 *
	 * @param node
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public AbstractXMLElementContentAssistRequest(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length, filter, textViewer);
	}

	protected Iterator<CMNode> getAvailableContentNodes(IDOMDocument domDocument, Node ancestorNode, int includeOptions) {
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(domDocument);
		CMElementDeclaration cmElementDec = modelQuery.getCMElementDeclaration((Element)ancestorNode);
		List <CMNode> cmNodeList = modelQuery.getAvailableContent((Element)ancestorNode, cmElementDec, includeOptions);
		Iterator <CMNode> cmNodeIt = cmNodeList.iterator();
		return cmNodeIt;
	}

	protected CustomCompletionProposal createProposal(String proposedText, String additionalInfo, int offset,
			Image image, int startLength) {
				CustomCompletionProposal proposal = new CustomCompletionProposal(
						proposedText, offset, 0, startLength + proposedText.length() - getMatchString().length(), 
						image, proposedText, null, additionalInfo, 0, true);
				return proposal;
			}

	protected Image getCMNodeImage(CMNode cmNode) {
		Image image = CMImageUtil.getImage(cmNode);
		if (image == null) {
			image = XMLEditorPluginImageHelper
					.getInstance()
					.getImage(
							XMLEditorPluginImages.IMG_OBJ_TAG_GENERIC);
		}
		return image;
	}

	protected String getRequiredName(Node ownerNode, CMNode cmnode) {
		if (ownerNode != null) {
			return DOMNamespaceHelper.computeName(cmnode, ownerNode, null);
		}
		return cmnode.getNodeName();
	}

	/**
	 * Retrieves cmnode's documentation to display in the completion proposal's
	 * additional info. If no documentation exists for cmnode, try displaying
	 * parentOrOwner's documentation
	 * 
	 * String any documentation information to display for cmnode.
	 * <code>null</code> if there is nothing to display.
	 */
	protected String getAdditionalInfo(CMNode parentOrOwner, CMNode cmnode) {
		String addlInfo = null;
	
		if (cmnode == null) {
			if (Debug.displayWarnings) {
				new IllegalArgumentException("Null declaration!").printStackTrace(); //$NON-NLS-1$
			}
			return null;
		}
	
		addlInfo = getInfoProvider().getInfo(cmnode);
		if ((addlInfo == null) && (parentOrOwner != null)) {
			addlInfo = getInfoProvider().getInfo(parentOrOwner);
		}
		return addlInfo;
	}

	/**
	 * Gets the infoProvider.
	 * 
	 * fInfoProvider and if fInfoProvider was <code>null</code> create a new
	 * instance
	 */
	protected MarkupTagInfoProvider getInfoProvider() {
		if (infoProvider == null) {
			infoProvider = new MarkupTagInfoProvider();
		}
		return infoProvider;
	}

	protected boolean beginsWith(String aString, String prefix) {
		if ((aString == null) || (prefix == null)) {
			return true;
		}
		return aString.toLowerCase().startsWith(prefix.toLowerCase());
	}

	/**
	 * Check to see if the current position is in an Attribute Region if so,
	 * return true otherwise false
	 * @return True if in attribute region, false otherwise.
	 */
	protected boolean inAttributeRegion() {
		return replacementBeginPosition > documentRegion.getStartOffset(region) + region.getLength();
	}

	/**
	 * Adds proposals for the XML_TAG_NAME region.
	 * @param position
	 */
	protected void addTagNameProposals(int position) {
	
		Node ancestorNode = null;
		try {
			ancestorNode = XSLTXPathHelper.selectSingleNode(getNode(),
					XPATH_FIRST_XSLANCESTOR_NODE);
		} catch (TransformerException ex) {
			
		}
		if (ancestorNode == null) {
			return;
		}
	
		List<CMNode> cmnodes = null;
	
		if (ancestorNode.getNodeType() == Node.ELEMENT_NODE) {
			cmnodes = getAvailableChildElementDeclarations(
					(Element) ancestorNode, 0);
			Iterator<CMNode> nodeIterator = cmnodes.iterator();
			// chop off any leading <'s and whitespace from the matchstring
			while ((matchString.length() > 0)
					&& (Character.isWhitespace(matchString.charAt(0)) || beginsWith(
							matchString, "<"))) { //$NON-NLS-1$
				//$NON-NLS-1$
				matchString = matchString.substring(1);
			}
			if (!nodeIterator.hasNext()) {
				return;
			}
			while (nodeIterator.hasNext()) {
				CMNode elementDecl = nodeIterator.next();
				if (elementDecl != null) {
					// only add proposals for the child element's that begin
					// with the matchstring
					String proposedText = null;
	
					proposedText = contentModel.getRequiredName(ancestorNode,
							elementDecl);
					int cursorAdjustment = proposedText.length();
	
					if (elementDecl instanceof CMElementDeclaration) {
						CMElementDeclaration ed = (CMElementDeclaration) elementDecl;
						if (ed.getContentType() == CMElementDeclaration.EMPTY) {
							proposedText += contentModel.getStartTagClose(
									ancestorNode, ed);
							cursorAdjustment = proposedText.length();
						} else {
							StringBuffer sb = new StringBuffer();
							contentModel.generateTag(ancestorNode, ed, sb);
							// since it's a name proposal, assume '<' is
							// already there
							// only return the rest of the tag
							proposedText = sb.toString().substring(1);
							cursorAdjustment = getCursorPositionForProposedText(proposedText);
	
						}
					}
					if (beginsWith(proposedText, matchString)) {
						Image image = CMImageUtil.getImage(elementDecl);
						if (image == null) {
							image = XMLEditorPluginImageHelper
									.getInstance()
									.getImage(
											XMLEditorPluginImages.IMG_OBJ_TAG_GENERIC);
						}
						String proposedInfo = getAdditionalInfo(
								getCMElementDeclaration(getParent()), elementDecl);
						CustomCompletionProposal proposal = new CustomCompletionProposal(
								proposedText, getReplacementBeginPosition(),
								getReplacementLength(), cursorAdjustment,
								image, contentModel.getRequiredName(getParent(),
										elementDecl), null, proposedInfo,
								XMLRelevanceConstants.R_TAG_NAME);
						addProposal(proposal);
					}
				}
			}
		}
	
	}

	/** Returns a list of CMNodes that are available within this parent context
	 * Given the grammar shown below and a snippet of XML code (where the '|'
	 * indicated the cursor position)
	 * the list would return all of the element declarations that are
	 * potential child elements of Foo.
	 *
	 * grammar : Foo -> (A, B, C)
	 * snippet : <Foo><A>|
	 * result : {A, B, C}
	 * 
	 * @param parent
	 * @param childPosition
	 * @return
	 */
	protected List<CMNode> getAvailableChildElementDeclarations(Element parent,
			int childPosition) {
				List modelQueryActions = getAvailableChildrenAtIndex(parent,
						childPosition, ModelQuery.VALIDITY_NONE);
				Iterator iterator = modelQueryActions.iterator();
				List<CMNode> cmnodes = new Vector();
				while (iterator.hasNext()) {
					ModelQueryAction action = (ModelQueryAction) iterator.next();
					if ((childPosition < 0)
							|| (((action.getStartIndex() <= childPosition) && (childPosition <= action
									.getEndIndex())))) {
						CMNode actionCMNode = action.getCMNode();
						if ((actionCMNode != null) && !cmnodes.contains(actionCMNode)) {
							cmnodes.add(actionCMNode);
						}
					}
				}
				return cmnodes;
			}

	protected List getAvailableChildrenAtIndex(Element parent, int index,
			int validityChecking) {
				List list = new ArrayList();
				CMElementDeclaration parentDecl = getCMElementDeclaration(parent);
				if (parentDecl != null) {
					ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent
							.getOwnerDocument());
					// taken from ActionManagers
					// int editMode = modelQuery.getEditMode();
					int editMode = ModelQuery.EDIT_MODE_UNCONSTRAINED;
					int ic = (editMode == ModelQuery.EDIT_MODE_CONSTRAINED_STRICT) ? ModelQuery.INCLUDE_CHILD_NODES
							| ModelQuery.INCLUDE_SEQUENCE_GROUPS
							: ModelQuery.INCLUDE_CHILD_NODES;
					modelQuery.getInsertActions(parent, parentDecl, index, ic,
							validityChecking, list);
				}
				return list;
			}

	protected CMElementDeclaration getCMElementDeclaration(Node node) {
		CMElementDeclaration result = null;
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(node
					.getOwnerDocument());
			if (modelQuery != null) {
				result = modelQuery.getCMElementDeclaration((Element) node);
			}
		}
		return result;
	}

	protected int getElementPosition(Node child) {
		Node parent = child.getParentNode();
		if (parent == null) {
			return 0;
		}
	
		NodeList children = parent.getChildNodes();
		if (children == null) {
			return 0;
		}
		int count = 0;
	
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) == child) {
				return count;
			} else {
				// if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
				count++;
			}
		}
		return 0;
	}

	/**
	 * This is the position the cursor should be in after the proposal is
	 * applied
	 * 
	 * @param proposedText
	 * @return the position the cursor should be in after the proposal is
	 *         applied
	 */
	protected int getCursorPositionForProposedText(String proposedText) {
		int cursorAdjustment;
		cursorAdjustment = proposedText.indexOf("\"\"") + 1; //$NON-NLS-1$
		// otherwise, after the first tag
		if (cursorAdjustment == 0) {
			cursorAdjustment = proposedText.indexOf('>') + 1;
		}
		if (cursorAdjustment == 0) {
			cursorAdjustment = proposedText.length() + 1;
		}
	
		return cursorAdjustment;
	}

	protected ITextRegion getNameRegion(IStructuredDocumentRegion flatNode) {
		if (flatNode == null) {
			return null;
		}
		Iterator regionList = flatNode.getRegions().iterator();
		while (regionList.hasNext()) {
			ITextRegion region = (ITextRegion) regionList.next();
			if (isNameRegion(region)) {
				return region;
			}
		}
		return null;
	}

	/**
	 * Checks to the see if the element is in the correct region.
	 * @param region
	 * @return
	 */
	protected boolean isNameRegion(ITextRegion region) {
		String type = region.getType();
		return ((type == DOMRegionContext.XML_TAG_NAME)
				|| (type == DOMRegionContext.XML_ELEMENT_DECL_NAME)
				|| (type == DOMRegionContext.XML_DOCTYPE_NAME) || (type == DOMRegionContext.XML_ATTLIST_DECL_NAME));
	}

}
