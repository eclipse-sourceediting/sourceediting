/*******************************************************************************
 * Copyright (c) 2001, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.dialogs;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;

public class XMLCatalogViewerFilter extends ViewerFilter {

	protected String[] extensions;

	public XMLCatalogViewerFilter() {
	}


	public XMLCatalogViewerFilter(String[] extensions) {
		this.extensions = extensions;
	}

	public boolean isFilterProperty(Object element, Object property) {
		return false;
	}

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		boolean isCatalogEntry = (element instanceof ICatalogEntry);
		boolean result = !isCatalogEntry;
		if (isCatalogEntry) {
			ICatalogEntry catalogEntry = (ICatalogEntry) element;
			for (int i = 0; i < extensions.length; i++) {
				if (catalogEntry.getURI().endsWith(extensions[i])) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
