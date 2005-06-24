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
package org.eclipse.wst.sse.core.internal.provisional.text;

/**
 * ITextRegionCollection, as its name implies, is a collection of
 * ITextRegions.
 * 
 * @plannedfor 1.0
 */
public interface ITextRegionCollection extends ITextRegion {

	/**
	 * Used to determine if a region contains a particular offset, where
	 * offset is relative to the beginning of a document.
	 * 
	 * @param offset
	 * @return true if offset is greater than or equal to regions start and
	 *         less than its end offset.
	 */
	boolean containsOffset(int offset);

	/**
	 * Used to determine if a region contains a particular offset.
	 * 
	 * ISSUE: I need to figure out what this is really for! (that is, how to
	 * describe it, or if still needed).
	 * 
	 * @param offset
	 * @return true if offset is greater than or equal to regions start and
	 *         less than its end offset.
	 */
	boolean containsOffset(ITextRegion region, int offset);

	/**
	 * Returns the end offset of this region, relative to beginning of
	 * document.
	 * 
	 * For some subtypes, but not all, it is equivilent to getEnd().
	 * 
	 * @return the end offset of this region.
	 */
	int getEndOffset();

	/**
	 * Returns the end offset, relative to the beginning of the document of
	 * the contained region.
	 * 
	 * @param containedRegion
	 * @return the end offset of the contained region.
	 */
	int getEndOffset(ITextRegion containedRegion);

	/**
	 * Returns the first region of those contained by this region collection.
	 * 
	 * @return the first region.
	 */
	ITextRegion getFirstRegion();

	/**
	 * Returns the full text of this region, including whitespace.
	 * 
	 * @return the full text of this region, including whitespace.
	 */
	String getFullText();

	/**
	 * Returns the full text of the contained region, including whitespace.
	 * 
	 * @return the full text of the contained region, including whitespace.
	 */
	String getFullText(ITextRegion containedRegion);

	/**
	 * Returns the last region of those contained by this region collection.
	 * 
	 * @return the last region.
	 */
	ITextRegion getLastRegion();

	/**
	 * Returns the number of regions contained by this region.
	 * 
	 * @return the number of regions contained by this region.
	 */
	int getNumberOfRegions();

	/**
	 * Returns the region that contains offset. In the case of "nested"
	 * regions, returns the top most region.
	 * 
	 * @param offset
	 *            relative to beginning of document.
	 * @return the region that contains offset. In the case of "nested"
	 *         regions, returns the top most region.
	 */
	ITextRegion getRegionAtCharacterOffset(int offset);

	/**
	 * Returns the regions contained by this region.
	 * 
	 * Note: no assumptions should be made about the object identity of the
	 * regions returned. Put another way, due to memory use optimizations,
	 * even if the underlying text has not changed, the regions may or may not
	 * be the same ones returned from one call to the next.
	 * 
	 * @return the regions contained by this region.
	 */
	ITextRegionList getRegions();

	/**
	 * Returns the start offset of this region, relative to the beginning of
	 * the document.
	 * 
	 * @return the start offset of this region
	 */
	int getStartOffset();

	/**
	 * Returns the start offset of the contained region, relative to the
	 * beginning of the document.
	 * 
	 * @return the start offset of the contained region
	 */
	int getStartOffset(ITextRegion containedRegion);

	/**
	 * Returns the text of this region, not including white space.
	 * 
	 * @return the text of this region, not including white space.
	 */
	String getText();

	/**
	 * Returns the text of the contained region, not including white space.
	 * 
	 * @return the text of the contained region, not including white space.
	 */
	String getText(ITextRegion containedRegion);

	/**
	 * Returns the end offset of the text of this region, not including white
	 * space.
	 * 
	 * @return the end offset of the text of this region, not including white
	 *         space.
	 */
	int getTextEndOffset();

	/**
	 * Returns the end offset of the text of the contained region, not
	 * including white space.
	 * 
	 * @return the end offset of the text of the contained region, not
	 *         including white space.
	 */
	int getTextEndOffset(ITextRegion containedRegion);

	/**
	 * Assigns the collection contained in this region.
	 * 
	 * For use by parsers and reparsers only.
	 * 
	 * @param containedRegions
	 */
	void setRegions(ITextRegionList containedRegions);
}
