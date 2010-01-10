/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.provisional.format;

import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;


public class CommentNodeFormatter extends NodeFormatter {
	static private final String CR = "\r"; //$NON-NLS-1$
	static private final String LF = "\n"; //$NON-NLS-1$

	protected String adjustIndentations(String aString, String lineIndent, String singleIndent) {
		String result = NodeFormatter.EMPTY_STRING;

		int indexOfLineDelimiter = StringUtils.indexOfLineDelimiter(aString);
		result = aString.substring(0, indexOfLineDelimiter);
		while (indexOfLineDelimiter != -1) {
			// Before find the next LineDelimiter, we have to figure out the
			// size of the current LineDelimiter
			// so we can figure out how many bytes to skip before finding the
			// next LineDelimiter.
			// Otherwise, we may treat the LF in CRLF as the next
			// LineDelimiter.
			int lineDelimiterSize = 1;
			if (aString.length() >= indexOfLineDelimiter + 2 && aString.substring(indexOfLineDelimiter, indexOfLineDelimiter + 1).compareTo(CR) == 0 && aString.substring(indexOfLineDelimiter + 1, indexOfLineDelimiter + 2).compareTo(LF) == 0)
				lineDelimiterSize = 2;

			int indexOfNextLineDelimiter = StringUtils.indexOfLineDelimiter(aString, indexOfLineDelimiter + lineDelimiterSize);
			int indexOfNonblank = StringUtils.indexOfNonblank(aString, indexOfLineDelimiter);

			if (indexOfNonblank != -1) {
				if (indexOfNextLineDelimiter == -1) {
					// last line; copy till the end
					result += lineIndent + singleIndent + aString.substring(indexOfNonblank);
				} else if (indexOfNextLineDelimiter != -1 && indexOfNextLineDelimiter < indexOfNonblank) {
					// blank line; just add a indent
					result += lineIndent + singleIndent;
				} else {
					// copy all text between indexOfNonblank and
					// indexOfNextLineDelimiter
					result += lineIndent + singleIndent + aString.substring(indexOfNonblank, indexOfNextLineDelimiter);
				}

				indexOfLineDelimiter = indexOfNextLineDelimiter;
			} else {
				if (indexOfNextLineDelimiter == -1) {
					result += lineIndent;
				} else {
					// blank line; just add a indent
					result += lineIndent + singleIndent;
				}

				indexOfLineDelimiter = indexOfNextLineDelimiter;
			}
		}

		return result;
	}

	protected void formatNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		if (node != null) {
			// lineDelimiterFound means multi line comment
			String nodeValue = node.getNodeValue();
			boolean lineDelimiterFoundInComment = StringUtils.containsLineDelimiter(nodeValue);

			if (lineDelimiterFoundInComment) {
				// format indentation before node
				formatIndentationBeforeNode(node, formatContraints);

				// adjust indentations in multi line comment
				String lineDelimiter = node.getModel().getStructuredDocument().getLineDelimiter();
				String lineIndent = formatContraints.getCurrentIndent();
				String singleIndent = getFormatPreferences().getIndent();
				String newNodevalue = adjustIndentations(nodeValue, lineDelimiter + lineIndent, singleIndent);
				if (nodeValue.compareTo(newNodevalue) != 0)
					node.setNodeValue(newNodevalue);
			}

			if (!nodeHasSiblings(node) || (node.getPreviousSibling() != null && node.getPreviousSibling().getNodeType() == Node.TEXT_NODE && !StringUtils.containsLineDelimiter(node.getPreviousSibling().getNodeValue()) && node.getNextSibling() == null)) {
				// single child
				// or inline comment after text
				// do nothing
			} else
				// format indentation after node
				formatIndentationAfterNode(node, formatContraints);
		}
	}
}
