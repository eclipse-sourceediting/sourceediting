/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.wst.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.contentmodel.CMContent;
import org.eclipse.wst.contentmodel.CMElementDeclaration;
import org.eclipse.wst.contentmodel.CMGroup;
import org.eclipse.wst.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.contentmodel.CMNode;
import org.eclipse.wst.contentmodel.CMNodeList;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Calculates attribute context information based on a StructuedDocument and document position.
 * 
 * @author pavery
 */
class AttributeContextInformationProvider {

	private IStructuredDocument fDocument = null;
	private final IContextInformation[] EMPTY_CONTEXT_INFO = new IContextInformation[0];
	private ContextInfoModelUtil fModelUtil = null;
	private Comparator fComparator;

	public AttributeContextInformationProvider(IStructuredDocument doc, AttributeContextInformationPresenter presenter) {
		fDocument = doc;
		fModelUtil = new ContextInfoModelUtil(fDocument);
	}

	public IContextInformation[] getAttributeInformation(int offset) {
		/*
		 * need to take care of special cases w/ ambiguous regions <tag>|</tag>
		 * also end tags..
		 */
		IContextInformation[] results = EMPTY_CONTEXT_INFO;

		IStructuredDocumentRegion sdRegion = fModelUtil.getDocument().getRegionAtCharacterOffset(offset);
		if (!canProposeInfo(sdRegion))
			return EMPTY_CONTEXT_INFO;

		XMLNode node = fModelUtil.getXMLNode(offset);
		if (node != null) {
			switch (node.getNodeType()) {
				case Node.ELEMENT_NODE :
					results = getInfoForElement(node);
					break;
			// future...
			//			case Node.TEXT_NODE :
			//				results = getInfoForText(node);
			//				break;
			}
		}
		return results;
	}

	/**
	 * @param sdRegion
	 * @return
	 */
	private boolean canProposeInfo(IStructuredDocumentRegion sdRegion) {
		if (sdRegion != null && isEndTag(sdRegion))
			return false;
		else
			return true;
	}

	/**
	 * @param sdRegion
	 * @return
	 */
	private boolean isEndTag(IStructuredDocumentRegion sdRegion) {
		ITextRegionList regions = sdRegion.getRegions();
		return regions.get(0).getType() == XMLRegionContext.XML_END_TAG_OPEN;
	}

	/**
	 * @param node
	 * @return
	 */
	private IContextInformation[] getInfoForText(XMLNode node) {
		Node parent = node.getParentNode();
		String contextString = node.getNodeName();
		StringBuffer info = new StringBuffer(" "); //$NON-NLS-1$
		if (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			CMElementDeclaration decl = fModelUtil.getModelQuery().getCMElementDeclaration((Element) parent);
			CMContent content = decl.getContent();
			if (content instanceof CMGroup) {
				CMGroup cmGroup = (CMGroup) content;
				CMNodeList children = cmGroup.getChildNodes();
				CMNode cmNode = null;
				for (int i = 0; i < children.getLength(); i++) {
					cmNode = children.item(i);
					contextString = cmNode.getNodeName();
					if (contextString != null) {
						info.append("<" + cmNode.getNodeName() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
						if (i < children.getLength() - 1)
							info.append(" "); //$NON-NLS-1$
					}
				}
			}
		}
		if (!info.toString().trim().equals("")) //$NON-NLS-1$
			return new IContextInformation[]{new ContextInformation(contextString, info.toString())};
		else
			return EMPTY_CONTEXT_INFO;
	}

	/**
	 * @param node
	 * @return
	 */
	private IContextInformation[] getInfoForElement(XMLNode node) {
		IContextInformation[] results = EMPTY_CONTEXT_INFO;
		CMElementDeclaration decl = fModelUtil.getModelQuery().getCMElementDeclaration((Element) node);
		if (decl != null) {
			CMNamedNodeMap attributes = decl.getAttributes();

			String attrContextString = node.getNodeName();
			StringBuffer attrInfo = new StringBuffer(" "); //$NON-NLS-1$
			String name = ""; //$NON-NLS-1$
			HashMap attrPosMap = new HashMap();
			int pos = 0;
			int length = 0;
			int numPerLine = 8;

			CMAttributeDeclaration[] sortedAttrs = getSortedAttributes(attributes);

			for (int i = 0; i < sortedAttrs.length; i++) {
				name = sortedAttrs[i].getAttrName();
				length = name.length();
				pos = attrInfo.length();

				attrInfo.append(name);

				if (sortedAttrs[i].getUsage() == CMAttributeDeclaration.REQUIRED) {
					attrInfo.append("*"); //$NON-NLS-1$
					length++;
				}
				if (i < attributes.getLength() - 1) {
					attrInfo.append(" "); //$NON-NLS-1$
					if (i != 0 && i % numPerLine == 0)
						attrInfo.append("\n "); //$NON-NLS-1$
				}
				attrPosMap.put(name, new Position(pos, length));
			}
			if (!attrInfo.toString().trim().equals("")) //$NON-NLS-1$
				return new IContextInformation[]{new AttributeContextInformation(attrContextString, attrInfo.toString(), attrPosMap)};
		}
		return results;
	}

	/**
	 * Returns sorted array of CMAttributeDeclarations.
	 * @param attributes
	 * @return
	 */
	private CMAttributeDeclaration[] getSortedAttributes(CMNamedNodeMap attributes) {
		List sorted = new ArrayList();
		for (int i = 0; i < attributes.getLength(); i++)
			sorted.add(attributes.item(i));
		Collections.sort(sorted, getCMAttributeComparator());
		return (CMAttributeDeclaration[]) sorted.toArray(new CMAttributeDeclaration[sorted.size()]);
	}

	/**
	 * Returns a comparator that compares CMAttributeDeclaration names.
	 * @return the comparator
	 */
	private Comparator getCMAttributeComparator() {
		if (fComparator == null)
			fComparator = new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((CMAttributeDeclaration) o1).getAttrName().compareTo(((CMAttributeDeclaration) o2).getAttrName());
				}
			};
		return fComparator;
	}
}
