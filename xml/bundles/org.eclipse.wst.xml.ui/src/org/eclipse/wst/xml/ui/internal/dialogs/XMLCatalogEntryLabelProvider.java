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
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.dialogs;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.ui.internal.catalog.ImageFactory;

public class XMLCatalogEntryLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object object, int columnIndex) {
		Image result = null;
		if (columnIndex == 0) {
			Image base = null;
			if (object instanceof ICatalogEntry) {
				ICatalogEntry catalogEntry = (ICatalogEntry) object;
				String uri = catalogEntry.getURI();
				if (uri.endsWith("dtd")) { //$NON-NLS-1$
					base = XMLCatalogTableViewer.dtdFileImage;
				}
				else if (uri.endsWith("xsd")) { //$NON-NLS-1$
					base = XMLCatalogTableViewer.xsdFileImage;
				}
				else {
					base = XMLCatalogTableViewer.unknownFileImage;
				}

				if (base != null) {
					if (URIHelper.isReadableURI(uri, false)) {
						result = base;
					}
					else {
						// TODO... SSE port
						result = base;// imageFactory.createCompositeImage(base,
						// errorImage,
						// ImageFactory.BOTTOM_LEFT);
					}
				}
			}
			else if (object instanceof String) {
				result = ImageFactory.INSTANCE.getImage("icons/obj16/xmlcatalog_obj.gif");
			}
		}
		return result;
	}
	@Override
	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public String getColumnText(Object object, int columnIndex) {
		String result = null;
		if (object instanceof ICatalogEntry) {
			ICatalogEntry catalogEntry = (ICatalogEntry) object;
			result = columnIndex == 0 ? catalogEntry.getKey() : catalogEntry.getURI();
			result = URIHelper.removePlatformResourceProtocol(result);
		}
		return result != null ? result : (columnIndex == 0 ? object.toString() : ""); //$NON-NLS-1$
	}
}