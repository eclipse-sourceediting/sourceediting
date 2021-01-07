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

import java.util.Collection;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;

@Deprecated
public class XMLCatalogTableViewer extends TableViewer {


	protected static Image dtdFileImage = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_DTDFILE);

	protected static String ERROR_STATE_KEY = "errorstatekey"; //$NON-NLS-1$
	protected static Image errorImage = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OVR_ERROR);

	protected static Image unknownFileImage = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_TXTEXT);
	protected static Image xsdFileImage = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_XSDFILE);

	private XMLCatalogViewerFilter fExtensionsFilter;

	// protected ImageFactory imageFactory = new ImageFactory();

	public XMLCatalogTableViewer(Composite parent, String[] columnProperties) {
		super(parent, SWT.FULL_SELECTION | SWT.BORDER);

		Table table = getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableLayout layout = new TableLayout();
		for (int i = 0; i < columnProperties.length; i++) {
			TableColumn column = new TableColumn(table, i);
			column.setText(columnProperties[i]);
			column.setAlignment(SWT.LEFT);
			layout.addColumnData(new ColumnWeightData(50, true));
		}
		table.setLayout(layout);
		table.setLinesVisible(false);

		setColumnProperties(columnProperties);

		setContentProvider(new XMLCatalogEntryContentProvider());
		setLabelProvider(new XMLCatalogEntryLabelProvider());
	}

	public Collection getXMLCatalogEntries() {
		return null;
	}

	public void setFilterExtensions(String[] extensions) {
		if (fExtensionsFilter != null) {
			removeFilter(fExtensionsFilter);
		}
		addFilter(fExtensionsFilter = new XMLCatalogViewerFilter(extensions));
	}
}
