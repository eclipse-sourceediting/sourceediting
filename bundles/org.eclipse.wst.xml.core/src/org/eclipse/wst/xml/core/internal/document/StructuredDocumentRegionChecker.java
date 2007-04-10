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
package org.eclipse.wst.xml.core.internal.document;



import java.io.IOException;
import java.io.Writer;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;


/**
 * This class is only for debug purpose.
 */
public class StructuredDocumentRegionChecker {
	String EOL = System.getProperty("line.separator"); //$NON-NLS-1$

	private int offset = 0;
	Writer testWriter = null;

	/**
	 */
	public StructuredDocumentRegionChecker() {
		super();
	}

	public StructuredDocumentRegionChecker(Writer writer) {
		super();
		testWriter = writer;
	}

	/**
	 */
	private void checkChildNodes(Node node) {
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			checkNode(child);
		}
	}

	/**
	 */
	public void checkModel(IDOMModel model) {
		checkChildNodes(model.getDocument());
	}

	/**
	 */
	private void checkNode(Node node) {
		checkStructuredDocumentRegion(((NodeImpl) node).getStructuredDocumentRegion());
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			checkChildNodes(node);
			checkStructuredDocumentRegion(((ElementImpl) node).getEndStructuredDocumentRegion());
		}
	}

	/**
	 */
	private void checkStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return;

		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			int n = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < n; i++) {
				IStructuredDocumentRegion c = container.getStructuredDocumentRegion(i);
				if (c == null) {
					reportError("null"); //$NON-NLS-1$
					continue;
				}
				checkStructuredDocumentRegion(c);
			}
			return;
		}

		int start = flatNode.getStart();
		if (start < this.offset)
			reportError("overwrap"); //$NON-NLS-1$
		if (start > this.offset)
			reportError("gap"); //$NON-NLS-1$
		int end = flatNode.getEnd();
		this.offset = end;

		if (flatNode instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
			IStructuredDocumentRegion p = proxy.getStructuredDocumentRegion();
			if (p == null) {
				reportError("null"); //$NON-NLS-1$
				return;
			}
			int s = p.getStart();
			int e = p.getEnd();
			if (s > start || e < end)
				reportError("out"); //$NON-NLS-1$
			if (s == start && e == end)
				reportWarning("vain"); //$NON-NLS-1$
		}
	}

	/**
	 */
	private void reportError(String message) {
		String msg = "StructuredDocumentRegionChecker : error : " + message; //$NON-NLS-1$
		if (testWriter != null) {
			try {
				testWriter.write(msg + EOL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(msg);
		}
		throw new StructuredDocumentRegionManagementException();
	}

	/**
	 */
	private void reportWarning(String message) {
		String msg = "StructuredDocumentRegionChecker : warning : " + message; //$NON-NLS-1$
		if (testWriter != null) {
			try {
				testWriter.write(msg + EOL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(msg);
		}
	}
}
