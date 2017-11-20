/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *     Balazs Banfai: Bug 154737 getUserData/setUserData support for Node
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154737
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.UserDataHandler;


/**
 * ProcessingInstructionImpl class
 */
public class ProcessingInstructionImpl extends NodeImpl implements ProcessingInstruction {
	private String data = null;

	private String target = null;

	/**
	 * ProcessingInstructionImpl constructor
	 */
	protected ProcessingInstructionImpl() {
		super();
	}

	/**
	 * ProcessingInstructionImpl constructor
	 * 
	 * @param that
	 *            ProcessingInstructionImpl
	 */
	protected ProcessingInstructionImpl(ProcessingInstructionImpl that) {
		super(that);

		if (that != null) {
			this.target = that.target;
			this.data = that.getData();
		}
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		ProcessingInstructionImpl cloned = new ProcessingInstructionImpl(this);
		notifyUserDataHandlers(UserDataHandler.NODE_CLONED, cloned);
		return cloned;
	}

	/**
	 * getData method
	 * 
	 * @return java.lang.String
	 */
	public String getData() {
		if (this.data != null)
			return this.data;

		IStructuredDocumentRegion flatNode = getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return NodeImpl.EMPTY_STRING;
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return NodeImpl.EMPTY_STRING;

		ITextRegion targetRegion = null;
		ITextRegion dataRegion = null;
		ITextRegion closeRegion = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_PI_OPEN)
				continue;
			if (regionType == DOMRegionContext.XML_PI_CLOSE) {
				closeRegion = region;
			}
			else {
				if (targetRegion == null)
					targetRegion = region;
				else if (dataRegion == null)
					dataRegion = region;
			}
		}
		if (dataRegion == null)
			return NodeImpl.EMPTY_STRING;
		int offset = dataRegion.getStart();
		int end = flatNode.getLength();
		if (closeRegion != null)
			end = closeRegion.getStart();
		String source = flatNode.getText();
		return source.substring(offset, end);
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return getTarget();
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return PROCESSING_INSTRUCTION_NODE;
	}

	/**
	 * getNodeValue method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeValue() {
		return getData();
	}

	/**
	 * getTarget method
	 * 
	 * @return java.lang.String
	 */
	public String getTarget() {
		if (this.target == null)
			return NodeImpl.EMPTY_STRING;
		return this.target;
	}

	/**
	 */
	public boolean isClosed() {
		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return true; // will be generated
		String regionType = StructuredDocumentRegionUtil.getLastRegionType(flatNode);
		return (regionType == DOMRegionContext.XML_PI_CLOSE);
	}

	/**
	 */
	void resetStructuredDocumentRegions() {
		this.data = getData();
		setStructuredDocumentRegion(null);
	}

	/**
	 * setData method
	 * 
	 * @param data
	 *            java.lang.String
	 */
	public void setData(String data) throws DOMException {
		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		this.data = data;

		notifyValueChanged();
	}

	/**
	 * setNodeValue method
	 * 
	 * @param nodeValue
	 *            java.lang.String
	 */
	public void setNodeValue(String nodeValue) throws DOMException {
		setData(nodeValue);
	}

	/**
	 */
	void setStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		super.setStructuredDocumentRegion(flatNode);
		if (flatNode != null)
			this.data = null;
	}

	/**
	 * setTarget method
	 * 
	 * @param target
	 *            java.lang.String
	 */
	protected void setTarget(String target) {
		this.target = target;
	}

	/**
	 * toString method
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getTarget());
		buffer.append('(');
		buffer.append(getData());
		buffer.append(')');
		return buffer.toString();
	}
}
