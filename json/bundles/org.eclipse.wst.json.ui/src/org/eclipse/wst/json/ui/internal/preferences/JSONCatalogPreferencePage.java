/*************************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import java.net.URI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.internal.schema.catalog.EntryParser;
import org.eclipse.wst.json.core.internal.schema.catalog.UserEntries;
import org.eclipse.wst.json.core.internal.schema.catalog.UserEntry;
import org.eclipse.wst.json.schemaprocessor.internal.JSONSchemaProcessor;
import org.eclipse.wst.json.ui.internal.JSONUIMessages;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;

public class JSONCatalogPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TreeViewer viewer;
	private UserEntries entries;
	private UserEntry selectedEntry;

	public void init(IWorkbench workbench) {
	}

	@Override
	protected void performDefaults() {
		storePreferences();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		storePreferences();
		return super.performOk();
	}

	private void storePreferences() {
		IEclipsePreferences prefs = getPreferences();
		try {
			String value = new EntryParser().serialize(entries.getEntries());
			prefs.put(EntryParser.JSON_CATALOG_ENTRIES, value);
			JSONCorePlugin.getDefault().clearCatalogCache();
			JSONSchemaProcessor.clearCache();
		} catch (Exception e) {
			logException(e);
		}
	}

	private static IEclipsePreferences getPreferences() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE
				  .getNode("org.eclipse.wst.json.ui"); //$NON-NLS-1$
		return preferences;
	}

	private static void logException(Exception e) {
		IStatus status = new Status(IStatus.ERROR, JSONUIPlugin.PLUGIN_ID, e
				.getLocalizedMessage(), e);
		JSONUIPlugin.getDefault().getLog().log(status);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		Group entriesGroup = new Group(composite, SWT.NONE);
		entriesGroup.setText(JSONUIMessages.JSON_Catalog_Entries);
		GridLayout gl = new GridLayout(2, false);
		entriesGroup.setLayout(gl);
		entriesGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new TreeViewer(entriesGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new EntriesContentProvider());
		viewer.setLabelProvider(new EntriesLabelProvider());
		entries = new UserEntries();
		entries.getEntries().addAll(EntryParser.getUserEntries());
		viewer.setInput(entries);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.expandAll();

		Composite buttonComposite = new Composite(entriesGroup, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, false));
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setText(JSONUIMessages.Add);
		addButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				EntryDialog dialog = new EntryDialog(getShell(), null, entries);
				int ok = dialog.open();
				if (ok == Window.OK) {
					String fileMatch = dialog.getFileMatch();
					if (fileMatch != null) {
						URI url = dialog.getURL();
						UserEntry entry = new UserEntry();
						entry.setUrl(url);
						entry.setFileMatch(fileMatch);
						entries.add(entry);
						viewer.refresh();
					}
				}
			}
		});
		final Button editButton = new Button(buttonComposite, SWT.PUSH);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.setText(JSONUIMessages.Edit);
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (selectedEntry == null) {
					return;
				}
				EntryDialog dialog = new EntryDialog(getShell(), selectedEntry, entries);
				int ok = dialog.open();
				if (ok == Window.OK) {
					String fileMatch = dialog.getFileMatch();
					if (fileMatch != null) {
						URI url = dialog.getURL();
						UserEntry entry = selectedEntry;
						entry.setUrl(url);
						entry.setFileMatch(fileMatch);
						viewer.refresh();
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		final Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText(JSONUIMessages.Remove);
		removeButton.setEnabled(false);

		removeButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (selectedEntry != null) {
					entries.getEntries().remove(selectedEntry);
					viewer.refresh();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				editButton.setEnabled(false);
				removeButton.setEnabled(false);
				selectedEntry = null;
				ISelection selection = event.getSelection();
				if (selection instanceof ITreeSelection) {
					ITreeSelection treeSelection = (ITreeSelection) selection;
					Object object = treeSelection.getFirstElement();
					if (object instanceof UserEntry) {
						selectedEntry = (UserEntry) object;
						editButton.setEnabled(true);
						removeButton.setEnabled(true);
					}
				}
			}
		});

		return composite;
	}

	class EntriesContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof UserEntries) {
				return ((UserEntries) parentElement).getEntries().toArray();
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof UserEntries;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	class EntriesLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof UserEntry) {
				UserEntry entry = (UserEntry) element;
				String result = entry.getFileMatch();
				return result;
			}
			return super.getText(element);
		}

	}

}
