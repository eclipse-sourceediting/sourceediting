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
package org.eclipse.wst.sse.core.text;

public interface ITextRegionCollection extends ITextRegion {

	boolean containsOffset(int i);

	boolean containsOffset(ITextRegion region, int i);

	void equatePositions(ITextRegion region);

	int getEndOffset();

	int getEndOffset(ITextRegion containedRegion);

	ITextRegion getFirstRegion();

	String getFullText();

	String getFullText(ITextRegion containedRegion);

	ITextRegion getLastRegion();

	int getLength();

	int getNumberOfRegions();

	ITextRegion getRegionAtCharacterOffset(int offset);

	ITextRegionList getRegions();

	int getStartOffset();

	int getStartOffset(ITextRegion containedRegion);

	String getText();

	String getText(ITextRegion containedRegion);

	int getTextEndOffset();

	int getTextEndOffset(ITextRegion containedRegion);

	/**
	 * For use by parsers and reparsers only.
	 */
	void setRegions(ITextRegionList embeddedRegions);
}
