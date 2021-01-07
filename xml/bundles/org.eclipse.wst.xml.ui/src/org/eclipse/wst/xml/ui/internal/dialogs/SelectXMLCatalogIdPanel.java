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

import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;


public class SelectXMLCatalogIdPanel extends Composite {
	protected int catalogEntryType;
	protected boolean doTableSizeHack = false;

	protected StructuredViewer tableViewer;
	protected ICatalog fXmlCatalog;
	private ViewerFilter fExtensionsFilter;

	public SelectXMLCatalogIdPanel(Composite parent, ICatalog xmlCatalog) {
		super(parent, SWT.NONE);
		this.fXmlCatalog = xmlCatalog;

		GridLayout gridLayout = new GridLayout();
		this.setLayout(gridLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 700;
		this.setLayoutData(gd);

		Label label = new Label(this, SWT.NONE);
		label.setText(XMLUIMessages._UI_LABEL_XML_CATALOG_COLON);

		tableViewer = createCatalogViewer(this);
		tableViewer.setInput("dummy");

		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	void addXMLCatalogEntries(List<ICatalogEntry> list, ICatalogEntry[] entries) {
		for (int i = 0; i < entries.length; i++) {
			ICatalogEntry entry = entries[i];
			if (catalogEntryType == 0) {
				list.add(entry);
			}
			else if (catalogEntryType == entry.getEntryType()) {
				list.add(entry);
			}
		}
	}

	void processCatalog(List<ICatalogEntry> result, ICatalog catalog) {
        addXMLCatalogEntries(result, catalog.getCatalogEntries());
        INextCatalog[] nextCatalogs = catalog.getNextCatalogs();
        for (int i = 0; i < nextCatalogs.length; i++) {
            ICatalog nextCatalog = nextCatalogs[i].getReferencedCatalog();
            if (nextCatalog != null) {
                processCatalog(result, nextCatalog);
            }
        }
	}

	protected StructuredViewer createCatalogViewer(Composite parent) {
		String columnProperties[] = new String[2];
		columnProperties[0] = XMLUIMessages._UI_LABEL_KEY;
		columnProperties[1] = XMLUIMessages._UI_LABEL_URI;

		FilteredTree viewerCreator = new FilteredTree(parent, SWT.FULL_SELECTION | SWT.BORDER, new PatternFilter(), true, true);
		viewerCreator.getViewer().setContentProvider(new XMLCatalogEntryContentProvider());
		viewerCreator.getViewer().setLabelProvider(new XMLCatalogEntryLabelProvider());

		viewerCreator.getViewer().getTree().setLinesVisible(true);
		viewerCreator.getViewer().getTree().setHeaderVisible(true);
		viewerCreator.getViewer().getTree().setLinesVisible(true);

		TableLayout layout = new TableLayout();
		for (int i = 0; i < columnProperties.length; i++) {
			TreeColumn column = new TreeColumn(viewerCreator.getViewer().getTree(), i);
			column.setText(columnProperties[i]);
			column.setAlignment(SWT.LEFT);
			layout.addColumnData(new ColumnWeightData(50, true));
		}
		viewerCreator.getViewer().getTree().setLayout(layout);
		viewerCreator.getViewer().getTree().setLinesVisible(false);
		viewerCreator.getViewer().setColumnProperties(columnProperties);
		return viewerCreator.getViewer();
	}

	public String getId() {
		ICatalogEntry entry = getXMLCatalogEntry();
		return entry != null ? entry.getKey() : null;
	}

	public StructuredViewer getCatalogViewer() {
		return tableViewer;
	}

	public String getURI() {
		ICatalogEntry entry = getXMLCatalogEntry();
		return entry != null ? entry.getURI() : null;
	}

	public ICatalogEntry getXMLCatalogEntry() {
		ICatalogEntry result = null;
		ISelection selection = tableViewer.getSelection();
		Object selectedObject = (selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection).getFirstElement() : null;
		if (selectedObject instanceof ICatalogEntry) {
			result = (ICatalogEntry) selectedObject;
		}
		return result;
	}

	public void setCatalogEntryType(int catalogEntryType) {
		this.catalogEntryType = catalogEntryType;
		tableViewer.refresh();
	}
	void setFilterExtensions(String[] extensions) {
		if (fExtensionsFilter != null) {
			getCatalogViewer().removeFilter(fExtensionsFilter);
		}
		getCatalogViewer().addFilter(fExtensionsFilter = new XMLCatalogViewerFilter(extensions));
	}
}
