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
package org.eclipse.wst.dtd.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.dtd.core.parser.DTDRegionTypes;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.w3c.dom.Node;


public class Element extends NamedTopLevelNode {

	public Element(DTDFile dtdFile, IStructuredDocumentRegion flatNode) {
		super(dtdFile, flatNode, DTDRegionTypes.ELEMENT_TAG);
	}

	public short getNodeType() {
		return Node.ELEMENT_NODE;
	}

	public Node cloneNode(boolean deep) {
		return new Element(dtdFile, flatNode);
	}

	protected CMNode contentModel;

	public void setContentModel(CMNode contentModel) {
		this.contentModel = contentModel;
	}

	public void addAttribute(String name) {
		beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_ELEMENT_ADD_ATTR")); //$NON-NLS-1$
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

	List attListList = new ArrayList();

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

	List attributes = new ArrayList();

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



	public CMNode getContentModel() {
		//    Object[] children = getChildren()
		return (CMNode) getFirstChild();//contentModel;
	}

	public Image getImage() {
		return DTDPlugin.getInstance().getImage(DTDResource.ELEMENTICON);
	}

	public void resolveRegions() {
		//    System.out.println("element node stream = " + tokenStream.getString());
		contentModel = null;
		removeChildNodes();
		RegionIterator iter = iterator();

		if (getNameRegion() != null) {
			// we skip past the name token is our name
			skipPastName(iter);
		}

		CMBasicNode basicNode = null;
		while (iter.hasNext()) {
			ITextRegion currentRegion = iter.next();

			if (contentModel == null) {
				if (currentRegion.getType().equals(DTDRegionTypes.NAME)) {
					contentModel = basicNode = new CMBasicNode(getDTDFile(), getStructuredDocumentRegion());
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.CONTENT_PCDATA)) {
					contentModel = basicNode = new CMBasicNode(getDTDFile(), getStructuredDocumentRegion());
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.LEFT_PAREN)) {
					contentModel = new CMGroupNode(getDTDFile(), getStructuredDocumentRegion());
				}
			}

			if (contentModel != null) {
				if (!currentRegion.getType().equals(DTDRegionTypes.END_TAG)) {
					// content model gets all regions except for the '>'
					contentModel.addRegion(currentRegion);
				}
				else {
					// if it is equal to the end tag, then don't add anymore regions
					// for the content model
					break;
				}

			}

		}
		if (contentModel != null) {
			appendChild(contentModel);
			// this is the root element content so set it true
			contentModel.setRootElementContent(true);
			// now tell the content model to resolve it's regions
			contentModel.resolveRegions();

		}
	}

	protected void addContent(Object requestor, String content) {
		ITextRegion whitespace = getWhitespaceAfterName();
		int startOffset = 0;
		int length = 0;
		if (whitespace != null) {
			startOffset = getStructuredDocumentRegion().getStartOffset(whitespace);
			length = whitespace.getLength() >= 2 ? 1 : 0;
		}
		else {
			ITextRegion nameRegion = getNameRegion();
			if (nameRegion != null) {
				startOffset = getStructuredDocumentRegion().getEndOffset(nameRegion);
			}
			else {
				ITextRegion elementTag = getNextRegion(iterator(), DTDRegionTypes.ELEMENT_TAG);
				startOffset = getStructuredDocumentRegion().getEndOffset(elementTag);
			}
		}
		replaceText(requestor, startOffset, length, content);
	}

	public void addGroup() {
		beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_ELEMENT_ADD_GRP")); //$NON-NLS-1$
		addContent(this, " ()"); //$NON-NLS-1$
		endRecording(this);
	}

	public void addChild() {
		beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_ELEMENT_ADD_CHILD")); //$NON-NLS-1$
		addContent(this, " EMPTY"); //$NON-NLS-1$
		endRecording(this);
	}

	public void replaceContentModel(Object requestor, CMNode node) {
		int offset = 0;
		int length = 0;
		String nodeText = node.getNodeText();
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
}
