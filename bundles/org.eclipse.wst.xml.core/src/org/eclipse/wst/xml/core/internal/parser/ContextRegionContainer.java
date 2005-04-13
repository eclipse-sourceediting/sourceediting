/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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



import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.text.TextRegionListImpl;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.Logger;


public class ContextRegionContainer implements ITextRegionContainer {
	protected int length;
	protected ITextRegionCollection parent;
	protected ITextRegionList regions;
	protected int start;
	protected int textLength;
	protected String type;

	public ContextRegionContainer() {
		super();
		regions = new TextRegionListImpl();

	}

	/**
	 * these "deep" parenting is not normal, but just in case.
	 */
	private IStructuredDocument _getParentDocument() {
		// go up enough parents to get to document
		ITextRegionCollection parent = getParent();
		while (!(parent instanceof IStructuredDocumentRegion)) {
			// would be an error not to be container, but
			// won't check for it now
			parent = ((ITextRegionContainer) parent).getParent();
		}
		return ((IStructuredDocumentRegion) parent).getParentDocument();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjust(int)
	 */
	public void adjust(int i) {

		start += i;
		// I erroneously added length and textLength
		// may want to rename this method to adjustStart
		//length += i;
		//textLength += i;

	}

	public void adjustLength(int i) {
		length += i;
	}

	public void adjustStart(int i) {
		start += i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustTextLength(int)
	 */
	public void adjustTextLength(int i) {
		textLength += i;

	}

	public boolean containsOffset(int i) {

		return getStartOffset() <= i && i < getEndOffset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#containsOffset(com.ibm.sed.structured.text.ITextRegion,
	 *      int)
	 */
	public boolean containsOffset(ITextRegion containedRegion, int offset) {
		return getStartOffset(containedRegion) <= offset && offset < getEndOffset(containedRegion);
	}

	/**
	 * This method is just to equate positions. clients may (will probably)
	 * still need to make calls to equate regions, parent, etc.
	 */
	public void equatePositions(ITextRegion region) {
		start = region.getStart();
		length = region.getLength();
		textLength = region.getTextLength();
	}

	public int getEnd() {
		return start + length;
	}

	public int getEndOffset() {
		// our startOffset take into account our parent, and our start
		return getStartOffset() + getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getEndOffset(com.ibm.sed.structured.text.ITextRegion)
	 */
	public int getEndOffset(ITextRegion containedRegion) {
		return getStartOffset(containedRegion) + containedRegion.getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getFirstRegion()
	 */
	public ITextRegion getFirstRegion() {
		return getRegions().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getFullText()
	 */
	public String getFullText() {
		// CMVC > 252430, 245586
		// unit test > com.ibm.sed.tests.other.UnitTests.testDeepEmbeddedJSP3
		// this code modified on 6/25/03 (pa)

		//		String result = null;
		//		try {
		//			result = _getParentDocument().get(start, length);
		//		} catch (BadLocationException e) {
		//			Logger.logException("program error: unreachable exception", e);
		// //$NON-NLS-1$
		//		}
		//		return result;
		return getParent().getFullText(this);
	}

	public String getFullText(org.eclipse.wst.sse.core.text.ITextRegion aRegion) {
		// Must be proxied here since aRegion should always be a child of
		// *this* container and indexed from
		// this container's offset
		//	try {
		return parent.getFullText().substring(start + aRegion.getStart(), start + aRegion.getEnd());
		//} catch (Exception e) {
		//if (com.ibm.sed.util.Debug.debugStructuredDocument)
		//e.printStackTrace();
		//}
		//return "";//$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getLastRegion()
	 */
	public ITextRegion getLastRegion() {
		return getRegions().get(getRegions().size() - 1);
	}

	public int getLength() {
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getNumberOfRegions()
	 */
	public int getNumberOfRegions() {
		return getRegions().size();
	}

	public ITextRegionCollection getParent() {
		return parent;
	}

	/**
	 * The parameter offset refers to the overall offset in the document.
	 */
	public ITextRegion getRegionAtCharacterOffset(int offset) {
		ITextRegion result = null;
		if (regions != null) {
			// transform the requested offset to the "scale" that
			// regions are stored in, which are all relative to the
			// start point.
			//int transformedOffset = offset - getStartOffset();
			//
			int length = getRegions().size();
			for (int i = 0; i < length; i++) {
				ITextRegion region = getRegions().get(i);
				if (org.eclipse.wst.sse.core.internal.util.Debug.debugStructuredDocument) {
					System.out.println("region(s) in IStructuredDocumentRegion::getRegionAtCharacterOffset: " + region); //$NON-NLS-1$
					System.out.println("       requested offset: " + offset); //$NON-NLS-1$
					//System.out.println(" transformedOffset: " +
					// transformedOffset); //$NON-NLS-1$
					System.out.println("       region start: " + region.getStart()); //$NON-NLS-1$
					System.out.println("       region end: " + region.getEnd()); //$NON-NLS-1$
					System.out.println("       region type: " + region.getType()); //$NON-NLS-1$
					System.out.println("       region class: " + region.getClass()); //$NON-NLS-1$

				}
				if ((getStartOffset(region) <= offset) && (offset < getEndOffset(region))) {
					result = region;
					break;
				}
			}
		}
		return result;
	}

	public ITextRegionList getRegions() {
		return regions;
	}

	public int getStart() {
		return start;
	}

	public int getStartOffset() {
		return getParent().getStartOffset() + getStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getStartOffset(com.ibm.sed.structured.text.ITextRegion)
	 */
	public int getStartOffset(ITextRegion containedRegion) {
		return getStartOffset() + containedRegion.getStart();
	}

	/**
	 * same as getFullText for this region type ... do we need to take white
	 * space off?
	 */

	public String getText() {
		String result = null;
		try {
			IStructuredDocument parentDocument = _getParentDocument();
			result = parentDocument.get(start, length);
		} catch (BadLocationException e) {
			Logger.logException("program error: unreachable exception", e); //$NON-NLS-1$
		}
		return result;
	}

	public String getText(org.eclipse.wst.sse.core.text.ITextRegion aRegion) {
		// Must be proxied here since aRegion should always be a child of
		// *this* container and indexed from
		// this container's offset
		//	com.ibm.sed.util.Assert.isTrue(regions.contains(aRegion));
		//try {
		return parent.getText().substring(start + aRegion.getStart(), start + aRegion.getTextEnd());
		//} catch (Exception e) {
		//if (com.ibm.sed.util.Debug.debugStructuredDocument)
		//com.ibm.sed.util.Logger.log(e);
		//}
		// return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#getTextEnd()
	 */
	public int getTextEnd() {
		//		int result = 0;
		//		ITextRegion lastRegion = (ITextRegion) regions.get(regions.size() -
		// 1);
		//		result = getStartOffset(lastRegion) + lastRegion.getTextLength();
		//		return result;
		return start + textLength;
	}

	public int getTextEndOffset() {
		ITextRegion region = regions.get(regions.size() - 1);
		// our startOffset take into account our parent, and our start
		// (pa) 10/4 changed to be based on text end
		//           it used to return incorrect value for embedded region containers
		//

		// TODO CRITICAL -- need to re-work this work around, so doesn't
		// depend on XMLRegionContext
		//		// this is a workaround for 226823///////////
		//		for (int i = regions.size() - 1; i >= 0 && region.getType() ==
		// XMLRegionContext.WHITE_SPACE; i--)
		//			region = (ITextRegion) regions.get(i);
		//		/////////////////////////////////////////////

		return getStartOffset() + region.getTextEnd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getTextEndOffset(com.ibm.sed.structured.text.ITextRegion)
	 */
	public int getTextEndOffset(ITextRegion containedRegion) {
		int result = 0;
		if (regions != null) {
			int length = getRegions().size();
			for (int i = 0; i < length; i++) {
				ITextRegion region = getRegions().get(i);
				if (region == containedRegion) {
					result = getStartOffset(region) + region.getTextEnd();
					break;
				}
			}
		}
		return result;
	}

	public int getTextLength() {
		return textLength;
	}

	public String getType() {
		return type;
	}

	public void setLength(int i) {
		length = i;
	}

	public void setParent(ITextRegionCollection parentRegion) {
		parent = parentRegion;
	}

	public void setRegions(ITextRegionList containedRegions) {
		regions = containedRegions;
	}

	public void setStart(int i) {
		start = i;
	}

	public void setTextLength(int i) {
		textLength = i;
	}

	public void setType(String string) {
		type = string;
	}

	public String toString() {
		String className = getClass().getName();
		String shortClassName = className.substring(className.lastIndexOf(".") + 1); //$NON-NLS-1$
		String result = "Container!!! " + shortClassName + "--> " + getType() + ": " + getStart() + "-" + getTextEnd() + (getTextEnd() != getEnd() ? ("/" + getEnd()) : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		return result;
	}

	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		org.eclipse.wst.sse.core.events.RegionChangedEvent result = null;
		// FUTURE_TO_DO: need to implement region level parsing in
		// ITextRegionContainer::updateModel
		// never being called?
		return result;
	}

}
