/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.jst.jsp.ui.tests.other;

import java.io.PrintStream;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DebugDocument {

	public static void dump(Document document) {
		if (document == null)
			return;
		System.out.println("Dump of DOM"); //$NON-NLS-1$

		dump(document, System.out);

		//
		System.out.println();
		System.out.println("= = = = = ="); //$NON-NLS-1$
		System.out.println();

	}

	public static void dump(Document document, PrintStream out) {
		Node node = document.getFirstChild();
		while (node != null) {
			dump(node, out);
			node = node.getNextSibling();
		}

	}

	public static void dump(Node topNode) {
		dump(topNode, System.out);
	}

	public static void dump(Node topNode, PrintStream out) {
		if (topNode == null)
			return;
		// print out this node
		//
		printNode(topNode, out);

		// now print out its children
		//NodeList nodes = topNode.getChildNodes();
		//int len = nodes.getLength();
		//for (int i = 0; i < len; i++) {

		//Node node = nodes.item(i);
		//dump(node, out);
		//}
	}

	public static void printNode(Node topNode) {
		printNode(topNode, System.out);

	}

	public static void printNode(Node topNode, PrintStream out) {
		// print out this node
		//
		IStructuredDocumentRegion firstStructuredDocumentRegion = ((IDOMNode) topNode).getFirstStructuredDocumentRegion();
		IStructuredDocumentRegion lastStructuredDocumentRegion = ((IDOMNode) topNode).getLastStructuredDocumentRegion();
		if ((firstStructuredDocumentRegion == null) || (lastStructuredDocumentRegion == null)) {
			// no text to output
		} else {
			int start = firstStructuredDocumentRegion.getStart();
			int end = lastStructuredDocumentRegion.getEnd();

			String outString = topNode.toString();
			outString = org.eclipse.wst.sse.core.utils.StringUtils.escape(outString);
			out.println("[" + start + ", " + end + "]" + outString); //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		}
		// now do its children
		NodeList nodes = topNode.getChildNodes();
		int len = nodes.getLength();
		for (int i = 0; i < len; i++) {
			Node childNode = nodes.item(i);
			printNode(childNode, out);
		}

	}

	public DebugDocument() {
		super();
	}

}
