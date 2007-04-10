/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.text;



import java.util.Vector;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.util.Assert;


public class StructuredDocumentRegionIterator {

	public final static IStructuredDocumentRegion adjustStart(IStructuredDocumentRegion headNode, int adjustment) {
		IStructuredDocumentRegion aNode = headNode;
		while (aNode != null) {
			aNode.adjustStart(adjustment);
			aNode = aNode.getNext();
		}
		return headNode;
	}

	public final static int countRegions(IStructuredDocumentRegionList flatNodes) {
		int result = 0;
		if (flatNodes != null) {
			int length = flatNodes.getLength();
			for (int i = 0; i < length; i++) {
				IStructuredDocumentRegion node = flatNodes.item(i);
				// don't know why, but we're getting null pointer exceptions
				// in this method
				if (node != null) {
					result = result + node.getNumberOfRegions();
				}
			}
		}
		return result;
	}

	public final static String getText(CoreNodeList flatNodes) {
		String result = null;
		if (flatNodes == null) {
			result = ""; //$NON-NLS-1$
		} else {
			StringBuffer buff = new StringBuffer();
			//IStructuredDocumentRegion aNode = null;
			int length = flatNodes.getLength();
			for (int i = 0; i < length; i++) {
				buff.append(flatNodes.item(i).getText());
			}
			result = buff.toString();
		}
		return result;
	}

	public final static CoreNodeList setParentDocument(CoreNodeList nodelist, IStructuredDocument textStore) {
		Assert.isNotNull(nodelist, "nodelist was null in CoreNodeList::setTextStore(CoreNodeList, StructuredTextStore)"); //$NON-NLS-1$
		int len = nodelist.getLength();
		for (int i = 0; i < len; i++) {
			IStructuredDocumentRegion node = nodelist.item(i);
			//Assert.isNotNull(node, "who's putting null in the node list? in
			// CoreNodeList::setTextStore(CoreNodeList,
			// StructuredTextStore)"); //$NON-NLS-1$
			node.setParentDocument(textStore);
		}
		return nodelist;
	}

	//	public final static IStructuredDocumentRegion
	// setStructuredDocument(IStructuredDocumentRegion headNode,
	// BasicStructuredDocument structuredDocument) {
	//		IStructuredDocumentRegion aNode = headNode;
	//		while (aNode != null) {
	//			aNode.setParentDocument(structuredDocument);
	//			aNode = (IStructuredDocumentRegion) aNode.getNext();
	//		}
	//		return headNode;
	//	}
	public final static IStructuredDocumentRegion setParentDocument(IStructuredDocumentRegion headNode, IStructuredDocument document) {
		IStructuredDocumentRegion aNode = headNode;
		while (aNode != null) {
			aNode.setParentDocument(document);
			aNode = aNode.getNext();
		}
		return headNode;
	}

	public final static Vector toVector(IStructuredDocumentRegion headNode) {
		IStructuredDocumentRegion aNode = headNode;
		Vector v = new Vector();
		while (aNode != null) {
			v.addElement(aNode);
			aNode = aNode.getNext();
		}
		return v;
	}

	/**
	 *  
	 */
	private StructuredDocumentRegionIterator() {
	}
}
