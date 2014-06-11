/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Debug;


public class CSSSourceParser implements RegionParser {
	public static final int MODE_STYLESHEET = 0;
	public static final int MODE_DECLARATION = 1;
	public static final int MODE_DECLARATION_VALUE = 2;

	private long fStartTime;
	private long fStopTime;
	private ICSSTokenizer fTokenizer;

	public void setParserMode(int mode) {
		int initialState;
		int bufsize;
		switch (mode) {
			case MODE_STYLESHEET :
				initialState = CSSTokenizer.YYINITIAL;
				bufsize = CSSTokenizer.BUFFER_SIZE_NORMAL;
				break;
			case MODE_DECLARATION :
				initialState = CSSTokenizer.ST_DECLARATION;
				bufsize = CSSTokenizer.BUFFER_SIZE_NORMAL;
				break;
			case MODE_DECLARATION_VALUE :
				initialState = CSSTokenizer.ST_DECLARATION_PRE_VALUE;
				bufsize = CSSTokenizer.BUFFER_SIZE_SMALL;
				break;
			default :
				return;
		}
		if (0 < initialState) {
			ICSSTokenizer tokenizer = getTokenizer();
			tokenizer.setInitialState(initialState);
			tokenizer.setInitialBufferSize(bufsize);
		}
	}

	public IStructuredDocumentRegion getDocumentRegions() {
		IStructuredDocumentRegion headnode = null;
		if (headnode == null) {
			if (Debug.perfTest) {
				fStartTime = System.currentTimeMillis();
			}
			headnode = parseNodes();
			if (Debug.perfTest) {
				fStopTime = System.currentTimeMillis();
				System.out.println(" -- creating nodes of IStructuredDocument -- "); //$NON-NLS-1$
				System.out.println(" Time parse and init all regions: " + (fStopTime - fStartTime) + " (msecs)"); //$NON-NLS-2$//$NON-NLS-1$
				// System.out.println(" for " + fRegions.size() + "
				// Regions");//$NON-NLS-2$//$NON-NLS-1$
				System.out.println("      and " + _countNodes(headnode) + " Nodes"); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		return headnode;
	}

	public List getRegions() {
		IStructuredDocumentRegion headNode = null;
		if (!getTokenizer().isEOF()) {
			headNode = getDocumentRegions();
			// throw new IllegalStateException("parsing has not finished");
		}
		// for memory recovery, we assume if someone
		// requests all regions, we can reset our big
		// memory consuming objects
		// but the new "getRegions" method is then more expensive.
		// I don't think its used much, though.
		List localRegionsList = getRegions(headNode);
		primReset();
		return localRegionsList;
	}

	/**
	 * Method getRegions.
	 * 
	 * @param headNode
	 * @return List
	 */
	protected List getRegions(IStructuredDocumentRegion headNode) {
		List allRegions = new ArrayList();
		IStructuredDocumentRegion currentNode = headNode;
		while (currentNode != null) {
			ITextRegionList nodeRegions = currentNode.getRegions();
			for (int i = 0; i < nodeRegions.size(); i++) {
				allRegions.add(nodeRegions.get(i));
			}
			currentNode = currentNode.getNext();
		}
		return allRegions;
	}

	public void reset(Reader reader) {
		primReset();
		getTokenizer().reset(reader, 0);
	}

	public void reset(Reader reader, int offset) {
		reset(reader);
	}

	public void reset(String input) {
		reset(new StringReader(input));
	}

	public void reset(String input, int offset) {
		reset(input);
	}

	public RegionParser newInstance() {
		return new CSSSourceParser();
	}

	private IStructuredDocumentRegion parseNodes() {
		// regions are initially reported as complete offsets within the
		// scanned input
		// they are adjusted here to be indexes from the currentNode's start
		// offset
		IStructuredDocumentRegion headNode = null;
		IStructuredDocumentRegion lastNode = null;
		ITextRegion region = null;
		IStructuredDocumentRegion currentNode = null;
		String type = null;
		String currentRegionType = null;

		while ((region = getNextRegion()) != null) {
			type = region.getType();
			if (mustBeStart(type, currentRegionType) && currentNode != null) {
				currentNode.setEnded(true);
			}

			if ((currentNode != null && currentNode.isEnded()) || currentNode == null) {
				if (currentNode != null && !currentNode.isEnded()) {
					currentNode.setEnded(true);
				}
				lastNode = currentNode;
				currentNode = createStructuredDocumentRegion(type);
				currentRegionType = type;
				if (lastNode != null) {
					lastNode.setNext(currentNode);
				}
				currentNode.setPrevious(lastNode);
				currentNode.setStart(region.getStart());
			}

			currentNode.addRegion(region);
			currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
			region.adjustStart(-currentNode.getStart());

			if (mustBeEnd(type)) {
				currentNode.setEnded(true);
			}

			if (headNode == null && currentNode != null) {
				headNode = currentNode;
			}
		}

		if (currentNode != null && !currentNode.isEnded()) {
			currentNode.setEnded(true);
		}

		primReset();
		return headNode;
	}

	private IStructuredDocumentRegion createStructuredDocumentRegion(String type) {
		return CSSStructuredDocumentRegionFactory.createRegion(type);
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	protected boolean mustBeStart(String type, String docRegionType) {
		return ((type == CSSRegionContexts.CSS_DELIMITER || type == CSSRegionContexts.CSS_LBRACE || type == CSSRegionContexts.CSS_RBRACE || type == CSSRegionContexts.CSS_IMPORT || type == CSSRegionContexts.CSS_PAGE || type == CSSRegionContexts.CSS_MEDIA || type == CSSRegionContexts.CSS_FONT_FACE || type == CSSRegionContexts.CSS_CHARSET || type == CSSRegionContexts.CSS_ATKEYWORD || type == CSSRegionContexts.CSS_DECLARATION_PROPERTY || type == CSSRegionContexts.CSS_DECLARATION_DELIMITER) || (docRegionType == CSSRegionContexts.CSS_DECLARATION_PROPERTY && type == CSSRegionContexts.CSS_S) || (!CSSRegionUtil.isSelectorBegginingType(docRegionType) && (type == CSSRegionContexts.CSS_SELECTOR_ELEMENT_NAME || type == CSSRegionContexts.CSS_SELECTOR_UNIVERSAL || type == CSSRegionContexts.CSS_SELECTOR_PSEUDO || type == CSSRegionContexts.CSS_SELECTOR_CLASS || type == CSSRegionContexts.CSS_SELECTOR_ID || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_START)));
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	protected boolean mustBeEnd(String type) {
		return (type == CSSRegionContexts.CSS_DELIMITER || type == CSSRegionContexts.CSS_LBRACE || type == CSSRegionContexts.CSS_RBRACE || type == CSSRegionContexts.CSS_DECLARATION_DELIMITER);
	}

	private ITextRegion getNextRegion() {
		ITextRegion region = null;
		try {
			region = getTokenizer().getNextToken();
			// DMW: 2/12/03 Removed state
			// if (region != null) {
			// fRegions.add(region);
			// }
			return region;
		}
		catch (StackOverflowError e) {
			Logger.logException(getClass().getName() + ": input could not be parsed correctly at position " + getTokenizer().getOffset(), e); //$NON-NLS-1$
			throw e;
		}
		catch (Exception e) {
			Logger.logException(getClass().getName() + ": input could not be parsed correctly at position " + getTokenizer().getOffset() + " (" + e.getLocalizedMessage() + ")", e); //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		}
		return null;
	}

	private void primReset() {

		getTokenizer().reset(new char[0]);
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	public ICSSTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new CSSTokenizer();
		}
		return fTokenizer;
	}


	/**
	 * This is a simple utility to count nodes. Used only for debug
	 * statements.
	 */
	private int _countNodes(IStructuredDocumentRegion nodes) {
		int result = 0;
		IStructuredDocumentRegion countNode = nodes;
		while (countNode != null) {
			result++;
			countNode = countNode.getNext();
		}
		return result;
	}

}
