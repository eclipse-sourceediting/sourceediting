/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.parser;



import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTagParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTokenizer;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionParserExtension;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.CharSequenceReader;
import org.eclipse.wst.sse.core.internal.text.IRegionComparible;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


/**
 * Takes input from the HTMLTokenizer and creates a tag list
 */

public class XMLSourceParser implements RegionParser, BlockTagParser, StructuredDocumentRegionParser, IRegionComparible, StructuredDocumentRegionParserExtension {
	// made public to aid access from inner classes in hierarchy.
	// TODO: in future, figure out how to solve without exposing data.
	public CharSequence fCharSequenceSource = null;
	private IDocument fDocumentInput;
	protected int fOffset = 0;
	// DMW: 2/12/03. Removed some state data, since not really needed,
	// and since it added a lot to overhead (since so many regions are
	// created.
	// protected IStructuredDocumentRegion fCurrentNode = null;
	// protected IStructuredDocumentRegion fNodes = null;
	// protected List fRegions = null;
	// protected Object fInput = null;
	protected String fStringInput = null;
	protected List fStructuredDocumentRegionHandlers;

	protected BlockTokenizer fTokenizer = null;
	protected long startTime;
	protected long stopTime;

	/**
	 * HTMLSourceParser constructor comment.
	 */
	public XMLSourceParser() {
		super();
		fStructuredDocumentRegionHandlers = new ArrayList();
	}

	/**
	 * This is a simple utility to count nodes. Used only for debug
	 * statements.
	 */
	protected int _countNodes(IStructuredDocumentRegion nodes) {
		int result = 0;
		IStructuredDocumentRegion countNode = nodes;
		while (countNode != null) {
			result++;
			countNode = countNode.getNext();
		}
		return result;
	}

	public void addBlockMarker(BlockMarker marker) {
		getTokenizer().addBlockMarker(marker);
	}

	public synchronized void addStructuredDocumentRegionHandler(StructuredDocumentRegionHandler handler) {
		if (fStructuredDocumentRegionHandlers == null)
			fStructuredDocumentRegionHandlers = new ArrayList();
		synchronized (fStructuredDocumentRegionHandlers) {
			fStructuredDocumentRegionHandlers.add(handler);
		}
	}

	public void beginBlockScan(String newTagName) {
		getTokenizer().beginBlockTagScan(newTagName);
	}

	/**
	 * @return IStructuredDocumentRegion
	 */
	protected IStructuredDocumentRegion createStructuredDocumentRegion(String type) {
		IStructuredDocumentRegion newNode = null;
		if (type == DOMRegionContext.BLOCK_TEXT)
			newNode = XMLStructuredRegionFactory.createRegion(XMLStructuredRegionFactory.XML_BLOCK);
		else
			newNode = XMLStructuredRegionFactory.createRegion(XMLStructuredRegionFactory.XML);
		return newNode;
	}

	protected void fireNodeParsed(IStructuredDocumentRegion fCurrentNode) {
		/*
		 * Never let an Exceptions from foreign code interfere with completion
		 * of parsing. To get an exception here is definitely a program error
		 * somewhere, but we can't afford to interrupt the flow of control. or
		 * backwards typing can result!
		 * 
		 * Protect the user's data above everything.
		 */
		Object[] handlers = null;
		synchronized (fStructuredDocumentRegionHandlers) {
			if (fStructuredDocumentRegionHandlers == null)
				return;

			handlers = fStructuredDocumentRegionHandlers.toArray();
		}
		if (fCurrentNode != null && handlers != null) {
			for (int i = 0; i < handlers.length; i++) {
				try {
					((StructuredDocumentRegionHandler) handlers[i]).nodeParsed(fCurrentNode);
				}
				catch (Exception e) {
					Logger.log(Logger.ERROR, "Error occurred while firing Node Parsed event", e); //$NON-NLS-1$
				}
			}
		}
	}

	public BlockMarker getBlockMarker(String tagName) {
		List markers = getTokenizer().getBlockMarkers();
		for (int i = 0; i < markers.size(); i++) {
			BlockMarker marker = (BlockMarker) markers.get(i);
			if (marker.isCaseSensitive()) {
				if (marker.getTagName().equals(tagName))
					return marker;
			}
			else {
				if (marker.getTagName().equalsIgnoreCase(tagName))
					return marker;
			}
		}
		return null;
	}

	public List getBlockMarkers() {
		return getTokenizer().getBlockMarkers();
	}

	/**
	 * @return IStructuredDocumentRegion
	 */
	public IStructuredDocumentRegion getDocumentRegions() {
		IStructuredDocumentRegion headnode = null;
		if (headnode == null) {
			if (Debug.perfTest) {
				startTime = System.currentTimeMillis();
			}
			headnode = parseNodes();
			if (Debug.perfTest) {
				stopTime = System.currentTimeMillis();
				System.out.println(" -- creating nodes of IStructuredDocument -- "); //$NON-NLS-1$
				System.out.println(" Time parse and init all regions: " + (stopTime - startTime) + " (msecs)"); //$NON-NLS-2$//$NON-NLS-1$
				// System.out.println(" for " + fRegions.size() + "
				// Regions");//$NON-NLS-2$//$NON-NLS-1$
				System.out.println("      and " + _countNodes(headnode) + " Nodes"); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		return headnode;
	}

	protected ITextRegion getNextRegion() {
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

	/**
	 * Return the full list of known regions. Typically getNodes should be
	 * used instead of this method.
	 */
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

	/**
	 * @deprecated - use the add/remove methods instead
	 * @return java.util.List
	 */
	public List getStructuredDocumentRegionHandlers() {
		if (fStructuredDocumentRegionHandlers == null) {
			fStructuredDocumentRegionHandlers = new ArrayList(0);
		}
		return fStructuredDocumentRegionHandlers;
	}

	/**
	 * Returns text from the current input. Text is only valid before
	 * getNodes() has been called and only when a raw String or DocumentReader
	 * is given as the input.
	 */
	public String getText(int offset, int length) {
		String text = null;
		if (fCharSequenceSource != null) {
			int start = fOffset + offset;
			int end = start + length;
			text = fCharSequenceSource.subSequence(start, end).toString();
		}
		else if (fDocumentInput != null) {
			try {
				text = fDocumentInput.get(offset, length);
			}
			catch (BadLocationException e) {
				text = ""; //$NON-NLS-1$
			}
		}
		else {
			if (fStringInput == null || fStringInput.length() == 0 || offset + length > fStringInput.length() || offset < 0) {
				text = ""; //$NON-NLS-1$
			}
			else {
				// offset is entirely valid during parsing as the parse
				// numbers haven't been adjusted.
				text = fStringInput.substring(offset, offset + length);
			}
		}
		return text;
	}

	protected BlockTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new XMLTokenizer();
		}
		return fTokenizer;
	}


	public RegionParser newInstance() {
		XMLSourceParser newInstance = new XMLSourceParser();
		newInstance.setTokenizer(getTokenizer().newInstance());
		return newInstance;
	}

	protected IStructuredDocumentRegion parseNodes() {
		// regions are initially reported as complete offsets within the
		// scanned input
		// they are adjusted here to be indexes from the currentNode's start
		// offset
		IStructuredDocumentRegion headNode = null;
		IStructuredDocumentRegion lastNode = null;
		ITextRegion region = null;
		IStructuredDocumentRegion currentNode = null;
		String type = null;

		while ((region = getNextRegion()) != null) {
			type = region.getType();
			// these types (might) demand a IStructuredDocumentRegion for each
			// of them
			if (type == DOMRegionContext.BLOCK_TEXT) {
				if (currentNode != null && currentNode.getLastRegion().getType() == DOMRegionContext.BLOCK_TEXT) {
					// multiple block texts indicated embedded containers; no
					// new IStructuredDocumentRegion
					currentNode.addRegion(region);
					currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
					region.adjustStart(-currentNode.getStart());
					// DW 4/16/2003 regions no longer have parents
					// region.setParent(currentNode);
				}
				else {
					// not continuing a IStructuredDocumentRegion
					if (currentNode != null) {
						// ensure that any existing node is at least
						// terminated
						if (!currentNode.isEnded()) {
							currentNode.setLength(region.getStart() - currentNode.getStart());
							// fCurrentNode.setTextLength(region.getStart() -
							// fCurrentNode.getStart());
						}
						lastNode = currentNode;
					}
					fireNodeParsed(currentNode);
					currentNode = createStructuredDocumentRegion(type);
					if (lastNode != null) {
						lastNode.setNext(currentNode);
					}
					currentNode.setPrevious(lastNode);
					currentNode.setStart(region.getStart());
					currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
					currentNode.setEnded(true);
					region.adjustStart(-currentNode.getStart());
					currentNode.addRegion(region);
					// DW 4/16/2003 regions no longer have parents
					// region.setParent(currentNode);
				}
			}
			// the following contexts OPEN new StructuredDocumentRegions
			else if ((currentNode != null && currentNode.isEnded()) || (type == DOMRegionContext.XML_CONTENT) || (type == DOMRegionContext.XML_CHAR_REFERENCE) || (type == DOMRegionContext.XML_ENTITY_REFERENCE) || (type == DOMRegionContext.XML_PI_OPEN) || (type == DOMRegionContext.XML_TAG_OPEN) || (type == DOMRegionContext.XML_END_TAG_OPEN) || (type == DOMRegionContext.XML_COMMENT_OPEN) || (type == DOMRegionContext.XML_CDATA_OPEN) || (type == DOMRegionContext.XML_DECLARATION_OPEN)) {
				if (currentNode != null) {
					// ensure that any existing node is at least terminated
					if (!currentNode.isEnded()) {
						currentNode.setLength(region.getStart() - currentNode.getStart());
						// fCurrentNode.setTextLength(region.getStart() -
						// fCurrentNode.getStart());
					}
					lastNode = currentNode;
				}
				fireNodeParsed(currentNode);
				currentNode = createStructuredDocumentRegion(type);
				if (lastNode != null) {
					lastNode.setNext(currentNode);
				}
				currentNode.setPrevious(lastNode);
				currentNode.setStart(region.getStart());
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
			}
			// the following contexts neither open nor close
			// StructuredDocumentRegions; just add to them
			else if ((type == DOMRegionContext.XML_TAG_NAME) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) || (type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) || (type == DOMRegionContext.XML_COMMENT_TEXT) || (type == DOMRegionContext.XML_PI_CONTENT) || (type == DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET)) {
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
			}
			// the following contexts close off StructuredDocumentRegions
			// cleanly
			else if ((type == DOMRegionContext.XML_PI_CLOSE) || (type == DOMRegionContext.XML_TAG_CLOSE) || (type == DOMRegionContext.XML_EMPTY_TAG_CLOSE) || (type == DOMRegionContext.XML_COMMENT_CLOSE) || (type == DOMRegionContext.XML_DECLARATION_CLOSE) || (type == DOMRegionContext.XML_CDATA_CLOSE)) {
				currentNode.setEnded(true);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				currentNode.addRegion(region);
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
			}
			// this is extremely rare, but valid
			else if (type == DOMRegionContext.WHITE_SPACE) {
				ITextRegion lastRegion = currentNode.getLastRegion();
				// pack the embedded container with this region
				if (lastRegion instanceof ITextRegionContainer) {
					ITextRegionContainer container = (ITextRegionContainer) lastRegion;
					container.getRegions().add(region);
					// containers must have parent set ...
					// setting for EACH subregion is redundent, but not sure
					// where else to do, so will do here for now.
					container.setParent(currentNode);
					// DW 4/16/2003 regions no longer have parents
					// region.setParent(container);
					region.adjustStart(container.getLength() - region.getStart());
				}
				currentNode.getLastRegion().adjustLength(region.getLength());
				currentNode.adjustLength(region.getLength());
			}
			else if (type == DOMRegionContext.UNDEFINED && currentNode != null) {
				// skip on a very-first region situation as the default
				// behavior is good enough
				// combine with previous if also undefined
				if (currentNode.getLastRegion() != null && currentNode.getLastRegion().getType() == DOMRegionContext.UNDEFINED) {
					currentNode.getLastRegion().adjustLength(region.getLength());
					currentNode.adjustLength(region.getLength());
				}
				// previous wasn't undefined
				else {
					currentNode.addRegion(region);
					currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
					region.adjustStart(-currentNode.getStart());
				}
			}
			else {
				// if an unknown type is the first region in the document,
				// ensure that a node exists
				if (currentNode == null) {
					currentNode = createStructuredDocumentRegion(type);
					currentNode.setStart(region.getStart());
				}
				currentNode.addRegion(region);
				currentNode.setLength(region.getStart() + region.getLength() - currentNode.getStart());
				region.adjustStart(-currentNode.getStart());
				// DW 4/16/2003 regions no longer have parents
				// region.setParent(currentNode);
				if (Debug.debugTokenizer)
					System.out.println(getClass().getName() + " found region of not specifically handled type " + region.getType() + " @ " + region.getStart() + "[" + region.getLength() + "]"); //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
			}

			// these regions also get their own node, so close them cleanly
			// NOTE: these regions have new StructuredDocumentRegions created
			// for them above; it may
			// be more readable if that is handled here as well, but the
			// current layout
			// ensures that they open StructuredDocumentRegions the same way
			if ((type == DOMRegionContext.XML_CONTENT) || (type == DOMRegionContext.XML_CHAR_REFERENCE) || (type == DOMRegionContext.XML_ENTITY_REFERENCE)) {
				currentNode.setEnded(true);
			}
			if (headNode == null && currentNode != null) {
				headNode = currentNode;
			}
		}
		if (currentNode != null) {
			fireNodeParsed(currentNode);
			currentNode.setPrevious(lastNode);
		}
		// fStringInput = null;
		primReset();
		return headNode;
	}

	protected void primReset() {
		// fNodes = null;
		// fRegions = null;
		// fInput = null;
		fStringInput = null;
		fCharSequenceSource = null;
		fDocumentInput = null;
		fOffset = 0;
		// fCurrentNode = null;
		// DMW: also reset tokenizer so it doesn't hold on
		// to large arrays
		getTokenizer().reset(new char[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.IRegionComparible#regionMatches(int,
	 *      int, java.lang.String)
	 */
	public boolean regionMatches(int offset, int length, String stringToCompare) {
		// by definition
		if (stringToCompare == null)
			return false;

		int ajustedOffset = fOffset + offset;
		boolean result = false;
		if (fCharSequenceSource != null && fCharSequenceSource instanceof IRegionComparible) {
			result = ((IRegionComparible) fCharSequenceSource).regionMatches(ajustedOffset, length, stringToCompare);
		}
		else {
			// old fashioned ways
			String test = null;
			if (fCharSequenceSource != null) {
				test = fCharSequenceSource.subSequence(ajustedOffset, ajustedOffset + length).toString();
			}
			else if (fStringInput != null) {
				test = fStringInput.substring(ajustedOffset, ajustedOffset + length);
			}
			result = stringToCompare.equals(test);
		}
		return result;
	}

	public boolean regionMatchesIgnoreCase(int offset, int length, String stringToCompare) {
		// by definition
		if (stringToCompare == null)
			return false;

		int ajustedOffset = fOffset + offset;
		boolean result = false;
		if (fCharSequenceSource != null && fCharSequenceSource instanceof IRegionComparible) {
			result = ((IRegionComparible) fCharSequenceSource).regionMatchesIgnoreCase(ajustedOffset, length, stringToCompare);
		}
		else {
			// old fashioned ways
			String test = null;
			if (fCharSequenceSource != null) {
				test = fCharSequenceSource.subSequence(ajustedOffset, ajustedOffset + length).toString();
			}
			else if (fStringInput != null) {
				test = fStringInput.substring(ajustedOffset, ajustedOffset + length);
			}
			result = stringToCompare.equalsIgnoreCase(test);
		}
		return result;
	}

	public void removeBlockMarker(BlockMarker marker) {
		getTokenizer().removeBlockMarker(marker);
	}

	public void removeBlockMarker(String tagName) {
		getTokenizer().removeBlockMarker(tagName);
	}

	public void removeStructuredDocumentRegionHandler(StructuredDocumentRegionHandler handler) {
		if (fStructuredDocumentRegionHandlers == null)
			return;
		synchronized (fStructuredDocumentRegionHandlers) {
			fStructuredDocumentRegionHandlers.remove(handler);
		}
	}

	/**
	 * Resets the input.
	 */
	public void reset(java.io.FileInputStream instream) {
		primReset();
		// fInput = instream;
		getTokenizer().reset(instream);
	}

	/**
	 * Resets the input.
	 */
	public void reset(java.io.Reader reader) {
		reset(reader, 0);
	}

	/**
	 * Resets the input.
	 */
	public void reset(java.io.Reader reader, int position) {
		primReset();
		fOffset = position;
		getTokenizer().reset(reader, position);
		if (reader instanceof DocumentReader) {
			IDocument doc = ((DocumentReader) reader).getDocument();
			if (doc instanceof CharSequence) {
				fCharSequenceSource = (CharSequence) doc;
			}
			else {
				// old fashioned IDocument
				fDocumentInput = ((DocumentReader) reader).getDocument();
			}

		}
		else if (reader instanceof CharSequenceReader) {
			fCharSequenceSource = ((CharSequenceReader) reader).getOriginalSource();
		}
	}

	/**
	 * Resets the input. Use this version to allow text to be retrieved
	 * <em>during</em> parsing, such as by the
	 * StructuredDocumentRegionHandler.
	 */
	public void reset(String sourceString) {
		reset(new StringReader(sourceString));
		fStringInput = sourceString;
	}

	/**
	 * Resets the input. Use this version to allow text to be retrieved
	 * <em>during</em> parsing, such as by the
	 * StructuredDocumentRegionHandler.
	 */
	public void reset(String sourceString, int position) {
		StringReader reader = new StringReader(sourceString);
		reset(reader, position);
		fStringInput = sourceString;
	}

	public void resetHandlers() {
		Object[] handlers = null;
		synchronized (fStructuredDocumentRegionHandlers) {
			if (fStructuredDocumentRegionHandlers == null)
				return;

			handlers = fStructuredDocumentRegionHandlers.toArray();
		}
		for (int i = 0; i < handlers.length; i++) {
			try {
				((StructuredDocumentRegionHandler) handlers[i]).resetNodes();
			}
			catch (Exception e) {
				Logger.log(Logger.ERROR, "Error occurred while resetting handlers", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * 
	 * @param List
	 */
	public void setStructuredDocumentRegionHandlers(List newStructuredDocumentRegionHandlers) {
		fStructuredDocumentRegionHandlers = newStructuredDocumentRegionHandlers;
	}

	protected void setTokenizer(BlockTokenizer newTokenizer) {
		// DMW: changed from private to protected, so subclass could use in
		// creation of 'newInstance'.
		fTokenizer = newTokenizer;
	}
}
