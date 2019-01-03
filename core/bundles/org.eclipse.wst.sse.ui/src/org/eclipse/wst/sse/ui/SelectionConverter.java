/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.sse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * Responsible for converting between text ranges in a document and the domain
 * objects covering that range, and determining the text ranges represented by
 * domain objects. The domain objects handled are those used in structured
 * selections and being sent across the selection service.
 *
 * Obtained as an adapter on the IStructuredModel
 *
 * @since 1.5
 */
public class SelectionConverter {
	/**
	 * @param model
	 * @param start
	 * @param end
	 * @return the most specific mapping of this text selection to domain
	 *         objects
	 */
	public Object[] getElements(IStructuredModel model, int start, int end) {
		Object[] localSelectedStructures = null;
		if (model != null) {
			IndexedRegion region = model.getIndexedRegion(start);
			if (region != null) {
				if (end <= region.getEndOffset()) {
					// single selection
					localSelectedStructures = new Object[1];
					localSelectedStructures[0] = region;
				}
				else {
					// multiple selection
					int maxLength = model.getStructuredDocument().getLength();
					List<IndexedRegion> structures = new ArrayList<>(2);
					while (region != null && region.getEndOffset() <= end && region.getEndOffset() < maxLength) {
						structures.add(region);
						region = model.getIndexedRegion(region.getEndOffset() + 1);
					}
					localSelectedStructures = structures.toArray();
				}
			}
		}
		if (localSelectedStructures == null) {
			localSelectedStructures = new Object[0];
		}
		return localSelectedStructures;
	}

	/**
	 * @param o
	 *            - a domain object being used in structured selections
	 * @return a {@link Region} containing the object's start and length, used
	 *         for operations requiring the entire text span
	 */
	public IRegion getRegion(Object o) {
		if (o instanceof ITextRegion) {
			int start = ((ITextRegion) o).getStart();
			return new Region(start, ((ITextRegion) o).getEnd() - start);
		}
		else if (o instanceof IRegion) {
			return new Region(((IRegion) o).getOffset(), ((IRegion) o).getLength());
		}
		int startOffset = ((IndexedRegion) o).getStartOffset();
		return new Region(startOffset, ((IndexedRegion) o).getEndOffset() - startOffset);
	}

	/**
	 * @param o
	 *            - a domain object being used in structured selections
	 * @return a {@link Region} containing a start and length, used for
	 *         operations where using the entire text span would be
	 *         undesirable
	 */
	public IRegion getSelectionRegion(Object o) {
		if (o instanceof ITextRegion) {
			int start = ((ITextRegion) o).getStart();
			return new Region(start, 0);
		}
		else if (o instanceof IRegion) {
			return new Region(((IRegion) o).getOffset(), 0);
		}
		int startOffset = ((IndexedRegion) o).getStartOffset();
		return new Region(startOffset, 0);
	}
}
