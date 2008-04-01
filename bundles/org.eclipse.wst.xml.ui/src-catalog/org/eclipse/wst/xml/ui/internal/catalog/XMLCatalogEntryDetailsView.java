/*******************************************************************************
 * Copyright (c) 2002, 2008 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.internal.catalog;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;



public class XMLCatalogEntryDetailsView {
	protected Text detailsText;
	protected ScrollBar verticalScroll, horizontalScroll;

	public XMLCatalogEntryDetailsView(Composite parent) {
		Color color = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);

		detailsText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 85;
		detailsText.setLayoutData(data);

		verticalScroll = detailsText.getVerticalBar();
		// verticalScroll.setVisible(false);
		horizontalScroll = detailsText.getHorizontalBar();
		detailsText.setEditable(false);
		detailsText.setBackground(color);
	}

	public void setCatalogElement(ICatalogEntry entry) {
		if (entry == null) {
			detailsText.setText(""); //$NON-NLS-1$
			return;
		}

		String value = getDisplayValue(entry != null ? entry.getURI() : ""); //$NON-NLS-1$
		String line1 = XMLCatalogMessages.UI_LABEL_DETAILS_URI_COLON + "\t\t" + value; //$NON-NLS-1$

		String line0;
		if (value.startsWith("jar:file:")) {
			String jarFile = URIUtils.convertURIToLocation(URIHelper.ensureURIProtocolFormat(value.substring("jar:".length(), value.indexOf('!'))));
			String internalFile = URIUtils.convertURIToLocation(URIHelper.ensureURIProtocolFormat("file://" + value.substring(value.indexOf('!') + 1)));
			line0 = XMLCatalogMessages.UI_LABEL_DETAILS_URI_LOCATION + "\t" + internalFile + " " + XMLCatalogMessages.UI_LABEL_DETAILS_IN_JAR_FILE + " " + jarFile;
		}
		else {
			value = URIUtils.convertURIToLocation(value);
			line0 = XMLCatalogMessages.UI_LABEL_DETAILS_URI_LOCATION + "\t" + value; //$NON-NLS-1$

		}

		value = entry != null ? getKeyTypeValue(entry) : ""; //$NON-NLS-1$
		String line2 = XMLCatalogMessages.UI_KEY_TYPE_DETAILS_COLON + "\t" + value; //$NON-NLS-1$

		value = getDisplayValue(entry != null ? entry.getKey() : ""); //$NON-NLS-1$
		String line3 = XMLCatalogMessages.UI_LABEL_DETAILS_KEY_COLON + "\t\t" + value; //$NON-NLS-1$

		String entireString = "\n" + line0 + "\n" + line1 + "\n" + line2 + "\n" + line3; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		detailsText.setText(entireString);
	}

	public void setCatalogElement(INextCatalog nextCatalog) {
		String value = getDisplayValue(nextCatalog != null ? nextCatalog.getCatalogLocation() : ""); //$NON-NLS-1$
		String line1 = XMLCatalogMessages.UI_LABEL_DETAILS_URI_COLON + "\t\t" + value; //$NON-NLS-1$

		String line0 = XMLCatalogMessages.UI_LABEL_DETAILS_URI_LOCATION + "\t" + URIUtils.convertURIToLocation(value);

		String entireString = "\n" + line0 + "\n" + line1; //$NON-NLS-1$
		detailsText.setText(entireString);
	}

	protected String getDisplayValue(String string) {
		return string != null ? string : ""; //$NON-NLS-1$
	}

	protected String getKeyTypeValue(ICatalogEntry entry) {
		String result = null;
		if ((entry.getURI() != null) && entry.getURI().endsWith("xsd")) //$NON-NLS-1$
		{
			result = (entry.getEntryType() == ICatalogEntry.ENTRY_TYPE_URI) ? XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_XSD_PUBLIC : XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_XSD_SYSTEM;
		}
		else {
			switch (entry.getEntryType()) {
				case ICatalogEntry.ENTRY_TYPE_PUBLIC :
					result = XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_PUBLIC;
					break;
				case ICatalogEntry.ENTRY_TYPE_SYSTEM :
					result = XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_SYSTEM;
					break;
				default :
					result = XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_URI;
					break;
			}

		}
		return result;
	}
}
