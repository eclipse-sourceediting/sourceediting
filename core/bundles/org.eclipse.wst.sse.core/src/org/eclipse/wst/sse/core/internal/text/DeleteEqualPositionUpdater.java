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
package org.eclipse.wst.sse.core.internal.text;

import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;

/**
 * Follows the behavior of DefaultPositionUpdater except in addition to
 * deleting/overwriting text which completely contains the position deletes
 * the position, deleting text that equals the text in position also deletes
 * the position.
 * 
 * @see org.eclipse.jface.text.DefaultPositionUpdater
 */
public class DeleteEqualPositionUpdater extends DefaultPositionUpdater {

	/**
	 * @param category
	 */
	public DeleteEqualPositionUpdater(String category) {
		super(category);
	}

	/**
	 * Determines whether the currently investigated position has been deleted
	 * by the replace operation specified in the current event. If so, it
	 * deletes the position and removes it from the document's position
	 * category.
	 * 
	 * NOTE: position is deleted if current event completely overwrites
	 * position OR if current event deletes the area surrounding/including the
	 * position
	 * 
	 * @return <code>true</code> if position has been deleted
	 */
	protected boolean notDeleted() {
		// position is deleted if current event completely overwrites position
		// OR if
		// current event deletes the area surrounding/including the position
		if ((fOffset < fPosition.offset && (fPosition.offset + fPosition.length < fOffset + fLength)) || (fOffset <= fPosition.offset && (fPosition.offset + fPosition.length <= fOffset + fLength) && fReplaceLength == 0)) {

			fPosition.delete();

			try {
				fDocument.removePosition(getCategory(), fPosition);
			} catch (BadPositionCategoryException x) {
			}

			return false;
		}

		return true;
	}

}
