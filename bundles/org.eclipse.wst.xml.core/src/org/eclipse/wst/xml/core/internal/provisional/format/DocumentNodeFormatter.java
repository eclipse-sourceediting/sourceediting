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
 *     Jesper Steen Møller - xml:space='preserve' support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.provisional.format;

import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;


public class DocumentNodeFormatter extends NodeFormatter {
	protected void formatChildren(IDOMNode node, IStructuredFormatContraints formatContraints) {
		String singleIndent = getFormatPreferences().getIndent();
		String lineIndent = formatContraints.getCurrentIndent();

		if (node != null && (fProgressMonitor == null || !fProgressMonitor.isCanceled())) {
			// normalize node first to combine adjacent text nodes
			node.normalize();

			IDOMNode nextChild = (IDOMNode) node.getFirstChild();
			while (nextChild != null) {
				IDOMNode eachChildNode = nextChild;
				nextChild = (IDOMNode) eachChildNode.getNextSibling();
				IStructuredFormatter formatter = getFormatter(eachChildNode);
				IStructuredFormatContraints childFormatContraints = formatter.getFormatContraints();
				String childIndent = lineIndent + singleIndent;
				childFormatContraints.setCurrentIndent(childIndent);
				childFormatContraints.setClearAllBlankLines(formatContraints.getClearAllBlankLines());
				childFormatContraints.setInPreserveSpaceElement(formatContraints.getInPreserveSpaceElement());

				// format each child
				formatter.format(eachChildNode, childFormatContraints);

				if (nextChild != null && nextChild.getParentNode() == null)
					// nextNode is deleted during format
					nextChild = (IDOMNode) eachChildNode.getNextSibling();
			}
		}
	}

	protected void formatNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		if (node != null)
			formatChildren(node, formatContraints);
	}
}
