/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.ui.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;
import org.eclipse.wst.ui.viewers.SelectSingleFileView;
import org.eclipse.wst.xml.ui.ui.XMLCommonResources;
import org.eclipse.wst.xml.uriresolver.XMLCatalog;
import org.eclipse.wst.xml.uriresolver.XMLCatalogEntry;
import org.eclipse.wst.xml.uriresolver.XMLCatalogPlugin;



public class SelectFileOrXMLCatalogIdPanel extends Composite implements SelectionListener {

	protected Button[] radioButton;
	protected PageBook pageBook;
	protected MySelectSingleFileView selectSingleFileView;
	protected SelectXMLCatalogIdPanel selectXMLCatalogIdPanel;
	protected Listener listener;

	/**
	 * TODO: Change the name of this interface; "Listener" is used by SWT.
	 */
	public interface Listener {
		void completionStateChanged();
	}

	public SelectFileOrXMLCatalogIdPanel(Composite parent) {
		super(parent, SWT.NONE);

		// container group
		setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		gd.widthHint = 400;
		setLayoutData(gd);

		radioButton = new Button[2];
		radioButton[0] = new Button(this, SWT.RADIO);
		radioButton[0].setText(XMLCommonResources.getInstance().getString("_UI_RADIO_BUTTON_SELECT_FROM_WORKSPACE")); //$NON-NLS-1$
		radioButton[0].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		radioButton[0].setSelection(true);
		radioButton[0].addSelectionListener(this);

		radioButton[1] = new Button(this, SWT.RADIO);
		radioButton[1].setText(XMLCommonResources.getInstance().getString("_UI_RADIO_BUTTON_SELECT_FROM_CATALOG")); //$NON-NLS-1$
		radioButton[1].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		radioButton[1].addSelectionListener(this);

		pageBook = new PageBook(this, SWT.NONE);
		pageBook.setLayoutData(new GridData(GridData.FILL_BOTH));

		selectSingleFileView = new MySelectSingleFileView(pageBook);

		XMLCatalog xmlCatalog = XMLCatalogPlugin.getInstance().getDefaultXMLCatalog();
		selectXMLCatalogIdPanel = new SelectXMLCatalogIdPanel(pageBook, xmlCatalog);
		selectXMLCatalogIdPanel.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateCompletionStateChange();
			}
		});
		pageBook.showPage(selectSingleFileView.getControl());
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void updateCompletionStateChange() {
		if (listener != null) {
			listener.completionStateChanged();
		}
	}

	public void setFilterExtensions(String[] filterExtensions) {
		selectSingleFileView.resetFilters();
		selectSingleFileView.addFilterExtensions(filterExtensions);

		selectXMLCatalogIdPanel.getTableViewer().setFilterExtensions(filterExtensions);
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == radioButton[0]) {
			pageBook.showPage(selectSingleFileView.getControl());
		}
		else {
			pageBook.showPage(selectXMLCatalogIdPanel);
		}
		updateCompletionStateChange();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void setVisibleHelper(boolean isVisible) {
		selectSingleFileView.setVisibleHelper(isVisible);
	}

	public void setCatalogEntryType(int catalogEntryType) {
		selectXMLCatalogIdPanel.setCatalogEntryType(catalogEntryType);
	}

	protected class MySelectSingleFileView extends SelectSingleFileView implements SelectSingleFileView.Listener {
		protected Control control;

		public MySelectSingleFileView(Composite parent) {
			super(null, true);
			//String[] ext = {".dtd"};
			//addFilterExtensions(ext);
			control = createControl(parent);
			control.setLayoutData(new GridData(GridData.FILL_BOTH));
			MySelectSingleFileView.this.setListener(this);
		}

		public void setVisibleHelper(boolean isVisible) {
			super.setVisibleHelper(isVisible);
		}

		public Control getControl() {
			return control;
		}

		public void setControlComplete(boolean isComplete) {
			updateCompletionStateChange();
		}
	}

	public IFile getFile() {
		IFile result = null;
		if (radioButton[0].getSelection()) {
			result = selectSingleFileView.getFile();
		}
		return result;
	}

	public String getXMLCatalogId() {
		String result = null;
		if (radioButton[1].getSelection()) {
			result = selectXMLCatalogIdPanel.getId();
		}
		return result;
	}

	public XMLCatalogEntry getXMLCatalogEntry() {
		XMLCatalogEntry result = null;
		if (radioButton[1].getSelection()) {
			result = selectXMLCatalogIdPanel.getXMLCatalogEntry();
		}
		return result;
	}

	public String getXMLCatalogURI() {
		String result = null;
		if (radioButton[1].getSelection()) {
			result = selectXMLCatalogIdPanel.getURI();
		}
		return result;
	}
}
