/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


public class AttributeEnumList extends DTDNode {

	private ArrayList list = new ArrayList();

	public AttributeEnumList(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode);
	}

	public String getImagePath() {
		// never exposed in tree
		return null;
	}

	// return the items that are in this enumerated list
	public List getItems() {
		list.clear();
		RegionIterator iter = iterator();
		while (iter.hasNext()) {
			ITextRegion name = getNextRegion(iter, DTDRegionTypes.NAME);
			if (name != null) {
				list.add(getStructuredDTDDocumentRegion().getText(name));
			}
		}
		return list;
	}

	public void setItems(Object requestor, String[] items) {
		if (items != null) {
			String text = "("; //$NON-NLS-1$
			for (int i = 0; i < items.length; i++) {
				if (i > 0) {
					text += " | " + items[i]; //$NON-NLS-1$
				}
				else {
					text += items[i];
				}
			}
			text += ")"; //$NON-NLS-1$
			replaceText(requestor, getStartOffset(), getNodeLength(), text);
		}
	}

	public void setItems(String[] items) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ATTR_ENUM_ITEMS); //$NON-NLS-1$
		setItems(this, items);
		endRecording(this);
	}
}
