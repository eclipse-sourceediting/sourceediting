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
package org.eclipse.wst.xml.ui.internal.dialogs;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.uriresolver.internal.XMLCatalog;
import org.eclipse.wst.xml.uriresolver.internal.XMLCatalogEntry;

public class SelectXMLCatalogIdPanel extends Composite {
	protected int catalogEntryType;
	protected boolean doTableSizeHack = false;

	protected XMLCatalogTableViewer tableViewer;
	protected XMLCatalog xmlCatalog;

	public SelectXMLCatalogIdPanel(Composite parent, XMLCatalog xmlCatalog) {
		super(parent, SWT.NONE);
		this.xmlCatalog = xmlCatalog;

		GridLayout gridLayout = new GridLayout();
		this.setLayout(gridLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 200;
		gd.widthHint = 700;
		this.setLayoutData(gd);

		Label label = new Label(this, SWT.NONE);
		label.setText(XMLUIMessages._UI_LABEL_XML_CATALOG_COLON); //$NON-NLS-1$

		tableViewer = createTableViewer(this);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.setInput("dummy"); //$NON-NLS-1$
	}

	protected XMLCatalogTableViewer createTableViewer(Composite parent) {
		String headings[] = new String[2];
		headings[0] = XMLUIMessages._UI_LABEL_KEY; //$NON-NLS-1$
		headings[1] = XMLUIMessages._UI_LABEL_URI; //$NON-NLS-1$

		XMLCatalogTableViewer theTableViewer = new XMLCatalogTableViewer(parent, headings) {

			protected void addXMLCatalogEntries(List list, Collection collection) {
				for (Iterator i = collection.iterator(); i.hasNext();) {
					XMLCatalogEntry entry = (XMLCatalogEntry) i.next();
					if (catalogEntryType == 0) {
						list.add(entry);
					} else if (catalogEntryType == entry.getType()) {
						list.add(entry);
					}
				}
			}

			public Collection getXMLCatalogEntries() {
				List result = null;

				if (xmlCatalog == null || doTableSizeHack) {
					// this lets us create a table with an initial height of
					// 10 rows
					// otherwise we get stuck with 0 row heigh table... that's
					// too small
					doTableSizeHack = false;
					result = new Vector();
					for (int i = 0; i < 6; i++) {
						result.add(""); //$NON-NLS-1$
					}
				} else {
					result = new Vector();

					addXMLCatalogEntries(result, xmlCatalog.getChildCatalog(XMLCatalog.SYSTEM_CATALOG_ID).getEntries());
					addXMLCatalogEntries(result, xmlCatalog.getChildCatalog(XMLCatalog.USER_CATALOG_ID).getEntries());
				}
				return result;
			}
		};
		return theTableViewer;
	}


	public String getId() {
		XMLCatalogEntry entry = getXMLCatalogEntry();
		return entry != null ? entry.getKey() : null;
	}

	public XMLCatalogTableViewer getTableViewer() {
		return tableViewer;
	}

	public String getURI() {
		XMLCatalogEntry entry = getXMLCatalogEntry();
		return entry != null ? entry.getURI() : null;
	}

	public XMLCatalogEntry getXMLCatalogEntry() {
		XMLCatalogEntry result = null;
		ISelection selection = tableViewer.getSelection();
		Object selectedObject = (selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection).getFirstElement() : null;
		if (selectedObject instanceof XMLCatalogEntry) {
			result = (XMLCatalogEntry) selectedObject;
		}
		return result;
	}

	public void setCatalogEntryType(int catalogEntryType) {
		this.catalogEntryType = catalogEntryType;
		tableViewer.refresh();
	}
}
