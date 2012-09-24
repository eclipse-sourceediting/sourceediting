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
 * ITextRegionContainer contains other regions, like a ITextRegionCollection
 * but is itself a region in an ITextRegionCollection, so its "parent" region
 * is maintained.
 * 
 * @plannedfor 1.0
 */
public interface ITextRegionContainer extends ITextRegionCollection {

	/**
	 * Returns the parent region.
	 * 
	 * @return the parent region.
	 */
	ITextRegionCollection getParent();

	/**
	 * Sets the parent region.
	 * 
	 * For use by parsers and reparsers only.
	 * 
	 * @param parent
	 *            the ITextRegionCollection this region is contained in.
	 */
	void setParent(ITextRegionCollection parent);
}
