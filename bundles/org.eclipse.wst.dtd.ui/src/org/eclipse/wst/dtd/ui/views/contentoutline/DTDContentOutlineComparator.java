/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.views.contentoutline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.wst.dtd.core.internal.CMNode;

/**
 * Comparator for the outline. Allows sorting of regular DTD elements, but not
 * their content models since the ordering there is important.
 */
class DTDContentOutlineComparator extends ViewerComparator {

	public DTDContentOutlineComparator() {
		super();
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof CMNode && e2 instanceof CMNode)
			return 0;
		return super.compare(viewer, e1, e2);
	}
	
	public boolean isSorterProperty(Object element, String property) {
		return super.isSorterProperty(element, property);
	}
}
