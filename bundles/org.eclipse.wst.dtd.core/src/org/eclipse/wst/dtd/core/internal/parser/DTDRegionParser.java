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
package org.eclipse.wst.dtd.core.internal.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.wst.dtd.core.internal.text.DTDStructuredDocumentRegionFactory;
import org.eclipse.wst.dtd.core.internal.tokenizer.DTDTokenizer;
import org.eclipse.wst.dtd.core.internal.tokenizer.Token;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


public class DTDRegionParser implements RegionParser {
	private Vector cachedRegions = null;
	private DTDTokenizer tokenizer = null;
	private IStructuredDocumentRegion cachedNode = null; // top of node
															// chain

	public RegionParser newInstance() {
		return new DTDRegionParser();
	}

	private IStructuredDocumentRegion addNewNodes(IStructuredDocumentRegion lastNode, Vector regions) {
		IStructuredDocumentRegion leadingSpaceNode = null;
		IStructuredDocumentRegion contentNode = null;
		IStructuredDocumentRegion trailingSpaceNode = null;
		LinkedList nodeSeeds = new LinkedList();
		int nRegions = regions.size();
		int leadingSpaceEnd = -1;
		int trailingSpaceBegin = nRegions;

		// find leading space
		nodeSeeds.clear();
		for (int i = 0; i < nRegions; i++) {
			ITextRegion region = (ITextRegion) regions.get(i);
			String type = region.getType();
			if (isBlankRegion(type)) {
				leadingSpaceEnd = i;
				nodeSeeds.addLast(region);
			}
			else {
				break;
			}
		}
		if (!nodeSeeds.isEmpty()) {
			leadingSpaceNode = createNode(nodeSeeds);
			if (lastNode != null) {
				lastNode.setNext(leadingSpaceNode);
				leadingSpaceNode.setPrevious(lastNode);
			}
			lastNode = leadingSpaceNode;
		}

		// find trailing space
		if (leadingSpaceEnd < nRegions - 1) {
			nodeSeeds.clear();
			for (int i = nRegions - 1; 0 <= i; i--) {
				ITextRegion region = (ITextRegion) regions.get(i);
				String type = ((ITextRegion) regions.get(i)).getType();
				if (isBlankRegion(type)) {
					trailingSpaceBegin = i;
					nodeSeeds.addFirst(region);
				}
				else {
					break;
				}
			}
			if (!nodeSeeds.isEmpty()) {
				trailingSpaceNode = createNode(nodeSeeds);
			}

			nodeSeeds.clear();
			for (int i = leadingSpaceEnd + 1; i < trailingSpaceBegin; i++) {
				nodeSeeds.addLast(regions.get(i));
			}
			if (!nodeSeeds.isEmpty()) {
				contentNode = createNode(nodeSeeds);
				if (lastNode != null) {
					lastNode.setNext(contentNode);
					contentNode.setPrevious(lastNode);
				}
				lastNode = contentNode;
			}
			if (trailingSpaceNode != null) {
				lastNode.setNext(trailingSpaceNode);
				trailingSpaceNode.setPrevious(lastNode);
				lastNode = trailingSpaceNode;
			}
		}

		return lastNode;
	}

	private IStructuredDocumentRegion createNode(LinkedList regions) {
		if (regions.size() == 0) {
			return null;
		}

		IStructuredDocumentRegion node = DTDStructuredDocumentRegionFactory.createStructuredDocumentRegion(DTDStructuredDocumentRegionFactory.DTD_GENERIC);
		int start = ((ITextRegion) regions.getFirst()).getStart();
		int length = ((ITextRegion) regions.getLast()).getEnd() - start;
		node.setStart(start);
		node.setLength(length);
		for (ListIterator i = regions.listIterator(0); i.hasNext();) {
			ITextRegion region = (ITextRegion) i.next();
			node.addRegion(region);
			region.adjustStart(-start);
		}
		node.setEnded(true);

		return node;
	}

	private IStructuredDocumentRegion createNodeChain(List regions) {
		IStructuredDocumentRegion headNode = null;
		IStructuredDocumentRegion lastNode = null;
		Vector nodeSeeds = new Vector();

		for (Iterator e = regions.iterator(); e.hasNext();) {
			ITextRegion region = (ITextRegion) e.next();
			String type = region.getType();
			// If the following regions appear,
			// a previous node is closed in front of it.
			if (!nodeSeeds.isEmpty() && isBeginningRegion(type)) {
				lastNode = addNewNodes(lastNode, nodeSeeds);
				nodeSeeds.clear();
			}
			nodeSeeds.addElement(region);

			// The following regions close the current node.
			if (!nodeSeeds.isEmpty() && isEndingRegion(type)) {
				lastNode = addNewNodes(lastNode, nodeSeeds);
				nodeSeeds.clear();
			}

			if (headNode == null && lastNode != null) {
				headNode = findFirstNode(lastNode);
			}
		}

		// close current node forcibly.
		if (!nodeSeeds.isEmpty()) {
			lastNode = addNewNodes(lastNode, nodeSeeds);
			if (headNode == null && lastNode != null) {
				headNode = findFirstNode(lastNode);
			}
		}
		return headNode;
	}

	private IStructuredDocumentRegion findFirstNode(IStructuredDocumentRegion node) {
		IStructuredDocumentRegion firstNode = node;
		IStructuredDocumentRegion prevNode = null;
		while ((prevNode = firstNode.getPrevious()) != null) {
			firstNode = prevNode;
		}
		return firstNode;
	}

	public IStructuredDocumentRegion getDocumentRegions() {
		if (cachedNode != null) {
			return cachedNode;
		}

		List regions = getRegions();
		IStructuredDocumentRegion headNode = createNodeChain(regions);
		cachedNode = headNode;

		return headNode;
	}

	public List getRegions() {
		if (cachedRegions != null) {
			return cachedRegions;
		}

		Vector regions = new Vector();
		Token currentToken = null;
		do {
			try {
				currentToken = (Token) tokenizer.yylex();
				if (currentToken != null) {
					ITextRegion region = DTDRegionFactory.createRegion(currentToken.getType(), currentToken.getStartOffset(), currentToken.getLength());
					regions.add(region);
				}
			}
			catch (java.io.FileNotFoundException e) {
				System.out.println("File not found"); //$NON-NLS-1$
			}
			catch (java.io.IOException e) {
				System.out.println("Error opening file"); //$NON-NLS-1$
			}
		}
		while (currentToken != null);

		cachedRegions = regions;
		return regions;
	}

	public void reset(Reader reader) {
		if (tokenizer == null) {
			try {
				tokenizer = new DTDTokenizer(reader);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Usage : java DTDTokenizer <inputfile>"); //$NON-NLS-1$
			}
		}
		else {
			try {
				tokenizer.yyreset(reader);
			}
			catch (java.io.IOException e) {
				System.out.println("Error opening file"); //$NON-NLS-1$
			}
		}

		cachedNode = null;
		cachedRegions = null;
	}

	/**
	 * An additional offset for use with any position-dependant parsing rules
	 */
	public void reset(Reader reader, int offset) {
		reset(reader);
	}

	public void reset(String input) {
		reset(new StringReader(input));
	}

	public void reset(String input, int offset) {
		reset(input);
	}

	// never used
	DTDTokenizer getTokenizer() {
		return tokenizer;
	}

	private boolean isBeginningRegion(String type) {
		return (type == DTDRegionTypes.START_TAG) || (type == DTDRegionTypes.ENTITY_PARM);
	}

	private boolean isBlankRegion(String type) {
		return (type == DTDRegionTypes.WHITESPACE);
	}

	private boolean isEndingRegion(String type) {
		return (type == DTDRegionTypes.END_TAG) || (type == DTDRegionTypes.ENTITY_PARM) || (type == DTDRegionTypes.COMMENT_END);
	}
}
