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
package org.eclipse.wst.sse.core.text;

public interface ITextRegionCollection extends ITextRegion {
	/**
	 * For use by parsers and reparsers only.
	 */
	void setRegions(ITextRegionList embeddedRegions);

	ITextRegionList getRegions();

	int getNumberOfRegions();

	ITextRegion getFirstRegion();

	ITextRegion getLastRegion();

	boolean containsOffset(ITextRegion region, int i);

	boolean containsOffset(int i);

	int getStartOffset();

	int getStartOffset(ITextRegion containedRegion);

	int getLength();

	int getEndOffset();

	int getEndOffset(ITextRegion containedRegion);

	int getTextEndOffset();

	int getTextEndOffset(ITextRegion containedRegion);

	String getText();

	String getText(ITextRegion containedRegion);

	String getFullText();

	String getFullText(ITextRegion containedRegion);

	ITextRegion getRegionAtCharacterOffset(int offset);

	void equatePositions(ITextRegion region);
}
