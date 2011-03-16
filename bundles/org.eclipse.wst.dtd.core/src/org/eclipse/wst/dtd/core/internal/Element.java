/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.w3c.dom.Node;


public class Element extends NamedTopLevelNode {

	List attListList = new ArrayList();

	List attributes = new ArrayList();

	protected CMNode fContentModel;

	public Element(DTDFile dtdFile, IStructuredDocumentRegion flatNode) {
		super(dtdFile, flatNode, DTDRegionTypes.ELEMENT_TAG);
	}

	public void addAttribute(String name) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ELEMENT_ADD_ATTR); //$NON-NLS-1$
		List attLists = getAttributeLists();
		if (attLists.size() == 0) {
			getDTDFile().createAttributeList(this, getName(), true);
			attLists = getAttributeLists();
		}
		if (attLists.size() > 0) {
			AttributeList attList = (AttributeList) attLists.get(attLists.size() - 1);
			attList.addAttribute(name);
		}
		endRecording(this);
	}

	public void addChild() {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ELEMENT_ADD_CHILD); //$NON-NLS-1$
		addContent(this, " EMPTY"); //$NON-NLS-1$
		endRecording(this);
	}

	protected void addContent(Object requestor, String content) {
		ITextRegion whitespace = getWhitespaceAfterName();
		int startOffset = 0;
		int length = 0;
		if (whitespace != null) {
			startOffset = getStructuredDTDDocumentRegion().getStartOffset(whitespace);
			length = whitespace.getLength() >= 2 ? 1 : 0;
		}
		else {
			ITextRegion nameRegion = getNameRegion();
			if (nameRegion != null) {
				startOffset = getStructuredDTDDocumentRegion().getEndOffset(nameRegion);
			}
			else {
				ITextRegion elementTag = getNextRegion(iterator(), DTDRegionTypes.ELEMENT_TAG);
				startOffset = getStructuredDTDDocumentRegion().getEndOffset(elementTag);
			}
		}
		replaceText(requestor, startOffset, length, content);
	}

	public void addGroup() {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ELEMENT_ADD_GRP); //$NON-NLS-1$
		addContent(this, " ()"); //$NON-NLS-1$
		endRecording(this);
	}

	public Node cloneNode(boolean deep) {
		return new Element(dtdFile, flatNode);
	}

	public List getAttributeLists() {
		attListList.clear();
		String elementName = getName();
		Iterator iter = dtdFile.getNodes().iterator();
		while (iter.hasNext()) {
			DTDNode node = (DTDNode) iter.next();
			if (node instanceof AttributeList && node.getName().equals(elementName)) {
				attListList.add(node);
			}
		}
		return attListList;
	}



	public CMNode getContentModel() {
		// Object[] children = getChildren()
		return (CMNode) getFirstChild();// contentModel;
	}

	public List getElementAttributes() {
		attributes.clear();
		Iterator attLists = getAttributeLists().iterator();
		while (attLists.hasNext()) {
			AttributeList attList = (AttributeList) attLists.next();

			Object[] children = attList.getChildren();
			for (int i = 0; i < children.length; i++) {
				attributes.add(children[i]);
			}
		}
		return attributes;
	}

	public String getImagePath() {
		return DTDResource.ELEMENTICON;
	}

	public short getNodeType() {
		return Node.ELEMENT_NODE;
	}

	public void replaceContentModel(Object requestor, CMNode node) {
		replaceContentModel(requestor, node.getNodeText());
	}

	public void replaceContentModel(Object requestor, String nodeText) {
		int offset = 0;
		int length = 0;
		CMNode contentModel = getContentModel();
		if (contentModel != null) {
			offset = contentModel.getStartOffset();
			length = contentModel.getWhitespaceEndOffset() - offset;
			replaceText(requestor, offset, length, nodeText);
		}
		else {
			addContent(requestor, nodeText);
		}
	}

	public void resolveRegions() {
		// System.out.println("element node stream = " +
		// tokenStream.getString());
		fContentModel = null;
		removeChildNodes();
		RegionIterator iter = iterator();

		if (getNameRegion() != null) {
			// we skip past the name token is our name
			skipPastName(iter);
		}

		while (iter.hasNext()) {
			ITextRegion currentRegion = iter.next();

			if (fContentModel == null) {
				if (currentRegion.getType().equals(DTDRegionTypes.NAME)) {
					fContentModel = new CMBasicNode(getDTDFile(), getStructuredDTDDocumentRegion());
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.CONTENT_PCDATA)) {
					fContentModel = new CMBasicNode(getDTDFile(), getStructuredDTDDocumentRegion());
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.LEFT_PAREN)) {
					fContentModel = new CMGroupNode(getDTDFile(), getStructuredDTDDocumentRegion());
				}
			}

			if (fContentModel != null) {
				if (!currentRegion.getType().equals(DTDRegionTypes.END_TAG)) {
					// content model gets all regions except for the '>'
					fContentModel.addRegion(currentRegion);
				}
				else {
					// if it is equal to the end tag, then don't add anymore
					// regions
					// for the content model
					break;
				}

			}

		}
		if (fContentModel != null) {
			appendChild(fContentModel);
			// this is the root element content so set it true
			fContentModel.setRootElementContent(true);
			// now tell the content model to resolve it's regions
			fContentModel.resolveRegions();

		}
	}

	public void setContentModel(CMNode contentModel) {
		this.fContentModel = contentModel;
	}
}
