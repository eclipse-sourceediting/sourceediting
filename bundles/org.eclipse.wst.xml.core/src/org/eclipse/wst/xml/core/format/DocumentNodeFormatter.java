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
package org.eclipse.wst.xml.core.format;

import org.eclipse.wst.sse.core.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.format.IStructuredFormatter;
import org.eclipse.wst.xml.core.document.XMLNode;


public class DocumentNodeFormatter extends NodeFormatter {
	protected void formatChildren(XMLNode node, IStructuredFormatContraints formatContraints) {
		String singleIndent = getFormatPreferences().getIndent();
		String lineIndent = formatContraints.getCurrentIndent();

		if (node != null && (fProgressMonitor == null || !fProgressMonitor.isCanceled())) {
			// normalize node first to combine adjacent text nodes
			node.normalize();

			XMLNode nextChild = (XMLNode) node.getFirstChild();
			while (nextChild != null) {
				XMLNode eachChildNode = nextChild;
				nextChild = (XMLNode) eachChildNode.getNextSibling();
				IStructuredFormatter formatter = getFormatter(eachChildNode);
				IStructuredFormatContraints childFormatContraints = formatter.getFormatContraints();
				String childIndent = lineIndent + singleIndent;
				childFormatContraints.setCurrentIndent(childIndent);
				childFormatContraints.setClearAllBlankLines(formatContraints.getClearAllBlankLines());

				// format each child
				formatter.format(eachChildNode, childFormatContraints);

				if (nextChild != null && nextChild.getParentNode() == null)
					// nextNode is deleted during format
					nextChild = (XMLNode) eachChildNode.getNextSibling();
			}
		}
	}

	protected void formatNode(XMLNode node, IStructuredFormatContraints formatContraints) {
		if (node != null)
			formatChildren(node, formatContraints);
	}
}
