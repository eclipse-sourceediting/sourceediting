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
package org.eclipse.wst.sse.ui.preferences.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.sse.core.ModelPlugin;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


public class TaskTagPreferenceTab implements IPreferenceTab {

	public class InternalTableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public InternalTableLabelProvider() {
			super();
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex < 1)
				return element.toString();
			for (int i = 0; i < fTags.length; i++) {
				if (fTags[i].equals(element)) {
					if (fPriorities[i].intValue() == IMarker.PRIORITY_HIGH) {
						return ResourceHandler.getString("TaskTagPreferenceTab.0"); //$NON-NLS-1$
					}
					else if (fPriorities[i].intValue() == IMarker.PRIORITY_LOW) {
						return ResourceHandler.getString("TaskTagPreferenceTab.1"); //$NON-NLS-1$
					}
					else {
						return ResourceHandler.getString("TaskTagPreferenceTab.2"); //$NON-NLS-1$
					}
				}
			}
			return ResourceHandler.getString("TaskTagPreferenceTab.3"); //$NON-NLS-1$
		}
	}

	public class TaskTagDialog extends Dialog {
		public int priority = IMarker.PRIORITY_NORMAL;
		private Combo priorityCombo = null;
		private Text tagText = null;
		public String text = ""; //$NON-NLS-1$

		public TaskTagDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(ResourceHandler.getString("TaskTagPreferenceTab.5")); //$NON-NLS-1$
		}

		protected Control createButtonBar(Composite parent) {
			Control c = super.createButtonBar(parent);
			getButton(IDialogConstants.OK_ID).setEnabled(text.length() > 0);
			return c;
		}

		protected Control createDialogArea(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			Label label = new Label(composite, SWT.NONE);
			label.setText(ResourceHandler.getString("TaskTagPreferenceTab.6")); //$NON-NLS-1$
			label.setLayoutData(new GridData());
			tagText = new Text(composite, SWT.BORDER);
			tagText.setText(text);
			tagText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			tagText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					getButton(IDialogConstants.OK_ID).setEnabled(tagText.getText().length() > 0 && !Arrays.asList(fTags).contains(tagText.getText()));
				}
			});

			label = new Label(composite, SWT.NONE);
			label.setText(ResourceHandler.getString("TaskTagPreferenceTab.7")); //$NON-NLS-1$
			label.setLayoutData(new GridData());
			priorityCombo = new Combo(composite, SWT.READ_ONLY | SWT.SINGLE);
			priorityCombo.setItems(new String[]{ResourceHandler.getString("TaskTagPreferenceTab.8"), ResourceHandler.getString("TaskTagPreferenceTab.9"), ResourceHandler.getString("TaskTagPreferenceTab.10")}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			priorityCombo.select(2 - priority);
			priorityCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			priorityCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					priority = 2 - priorityCombo.getSelectionIndex();
				}
			});
			return composite;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
		 */
		protected void okPressed() {
			text = tagText.getText();
			priority = 2 - priorityCombo.getSelectionIndex();
			super.okPressed();
		}
	}

	private Control fControl;

	private Integer[] fPriorities;

	private String[] fTags;

	private boolean isDirty = false;

	protected TableViewer valueTable = null;

	/**
	 *  
	 */
	public TaskTagPreferenceTab() {
		super();
	}

	/**
	 * 
	 */
	protected void addTag() {
		TaskTagDialog dlg = new TaskTagDialog(fControl.getShell());
		String tag = ""; //$NON-NLS-1$
		int priority = IMarker.PRIORITY_NORMAL;
		int result = dlg.open();
		if (result == Window.OK) {
			isDirty = true;
			tag = dlg.text;
			priority = dlg.priority;
			List newTags = new ArrayList(Arrays.asList(fTags));
			newTags.add(tag);
			fTags = (String[]) newTags.toArray(new String[0]);
			List newPriorities = new ArrayList(Arrays.asList(fPriorities));
			newPriorities.add(new Integer(priority));
			fPriorities = (Integer[]) newPriorities.toArray(new Integer[0]);
			valueTable.setInput(fTags);
			valueTable.getTable().setSelection(fTags.length - 1);
		}
	}

	public Control createContents(Composite parent) {
		loadPreferenceValues();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		fControl = composite;

		valueTable = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		valueTable.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn textColumn = new TableColumn(valueTable.getTable(), SWT.NONE, 0);
		textColumn.setText(ResourceHandler.getString("TaskTagPreferenceTab.12")); //$NON-NLS-1$
		TableColumn priorityColumn = new TableColumn(valueTable.getTable(), SWT.NONE, 1);
		priorityColumn.setText(ResourceHandler.getString("TaskTagPreferenceTab.13")); //$NON-NLS-1$
		valueTable.setContentProvider(new ArrayContentProvider());
		valueTable.setLabelProvider(new InternalTableLabelProvider());
		valueTable.getTable().setLinesVisible(true);
		valueTable.getTable().setHeaderVisible(true);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, 140, true));
		layout.addColumnData(new ColumnWeightData(1, 140, true));
		valueTable.getTable().setLayout(layout);

		Composite buttons = new Composite(composite, SWT.NONE);
		buttons.setLayout(new GridLayout());
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		final Button addButton = new Button(buttons, SWT.PUSH);
		addButton.setText(ResourceHandler.getString("TaskTagPreferenceTab.14")); //$NON-NLS-1$
		addButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button editButton = new Button(buttons, SWT.PUSH);
		editButton.setText(ResourceHandler.getString("TaskTagPreferenceTab.15")); //$NON-NLS-1$
		editButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText(ResourceHandler.getString("TaskTagPreferenceTab.16")); //$NON-NLS-1$
		removeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));

		Label spacer = new Label(buttons, SWT.NONE);
		spacer.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button upButton = new Button(buttons, SWT.PUSH);
		upButton.setText(ResourceHandler.getString("TaskTagPreferenceTab.17")); //$NON-NLS-1$
		upButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button downButton = new Button(buttons, SWT.PUSH);
		downButton.setText(ResourceHandler.getString("TaskTagPreferenceTab.18")); //$NON-NLS-1$
		downButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));

		editButton.setEnabled(false);
		removeButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);

		Label warning = new Label(composite, SWT.NONE);
		warning.setLayoutData(new GridData());
		warning.setText(ResourceHandler.getString("TaskTagPreferenceTab.19")); //$NON-NLS-1$

		valueTable.addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				boolean enabledForSelection = !event.getSelection().isEmpty();
				editButton.setEnabled(enabledForSelection);
				removeButton.setEnabled(enabledForSelection);
				if (valueTable.getTable() != null && !valueTable.getTable().isDisposed()) {
					upButton.setEnabled(enabledForSelection && valueTable.getTable().getSelectionIndex() > 0);
					downButton.setEnabled(enabledForSelection && valueTable.getTable().getSelectionIndex() < fTags.length - 1);
				}
				else {
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				}
			}
		});
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addTag();
			}
		});
		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editTag(valueTable.getTable().getSelectionIndex());
			}
		});
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeTag(valueTable.getTable().getSelectionIndex());
			}
		});
		upButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveTagUp(valueTable.getTable().getSelectionIndex());
				upButton.setEnabled(valueTable.getTable().getSelectionIndex() > 0);
				downButton.setEnabled(valueTable.getTable().getSelectionIndex() < fTags.length - 1);
				if(!upButton.isEnabled()) {
					downButton.setFocus();
				}
			}
		});
		downButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveTagDown(valueTable.getTable().getSelectionIndex());
				upButton.setEnabled(valueTable.getTable().getSelectionIndex() > 0);
				downButton.setEnabled(valueTable.getTable().getSelectionIndex() < fTags.length - 1);
				if(!downButton.isEnabled()) {
					upButton.setFocus();
				}
			}
		});


		valueTable.setInput(fTags);

		WorkbenchHelp.setHelp(composite, IHelpContextIds.PREFWEBX_TASKTAGS_HELPID);
		return composite;
	}

	/**
	 * @param selection
	 */
	protected void editTag(int i) {
		if (i < 0) {
			return;
		}

		int selection = valueTable.getTable().getSelectionIndex();
		TaskTagDialog dlg = new TaskTagDialog(fControl.getShell());
		dlg.text = fTags[selection];
		dlg.priority = fPriorities[selection].intValue();
		int result = dlg.open();
		if (result == Window.OK) {
			isDirty = true;
			fTags[selection] = dlg.text;
			fPriorities[selection] = new Integer(dlg.priority);
			valueTable.refresh();
		}
	}
	
	public String getTitle() {
		return ResourceHandler.getString("TaskTagPreferenceTab.20"); //$NON-NLS-1$
	}

	private void loadPreferenceValues() {
		String tags = ModelPlugin.getDefault().getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_TAGS);
		String priorities = ModelPlugin.getDefault().getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_PRIORITIES);
		loadTagsAndPriorities(tags, priorities);
	}

	/**
	 * @param tags
	 * @param priorities
	 */
	private void loadTagsAndPriorities(String tags, String priorities) {
		fTags = StringUtils.unpack(tags);

		StringTokenizer toker = null;
		List list = new ArrayList();

		toker = new StringTokenizer(priorities, ","); //$NON-NLS-1$
		while (toker.hasMoreTokens()) {
			Integer number = null;
			try {
				number = Integer.valueOf(toker.nextToken());
			}
			catch (NumberFormatException e) {
				number = new Integer(IMarker.PRIORITY_NORMAL);
			}
			list.add(number);
		}
		fPriorities = (Integer[]) list.toArray(new Integer[0]);
	}

	protected void moveTagDown(int i) {
		String tag = fTags[i];
		Integer priority = fPriorities[i];
		fTags[i] = fTags[i + 1];
		fPriorities[i] = fPriorities[i + 1];
		fTags[i + 1] = tag;
		fPriorities[i + 1] = priority;
		valueTable.refresh(fTags);
		valueTable.getTable().select(i + 1);
	}

	protected void moveTagUp(int i) {
		String tag = fTags[i];
		Integer priority = fPriorities[i];
		fTags[i] = fTags[i - 1];
		fPriorities[i] = fPriorities[i - 1];
		fTags[i - 1] = tag;
		fPriorities[i - 1] = priority;
		valueTable.refresh(fTags);
		valueTable.getTable().select(i - 1);
	}

	public void performApply() {
		save();
		isDirty = false;
	}

	public void performDefaults() {
		String tags = ModelPlugin.getDefault().getPluginPreferences().getDefaultString(CommonModelPreferenceNames.TASK_TAG_TAGS);
		String priorities = ModelPlugin.getDefault().getPluginPreferences().getDefaultString(CommonModelPreferenceNames.TASK_TAG_PRIORITIES);
		loadTagsAndPriorities(tags, priorities);
		if (valueTable != null && valueTable.getControl() != null && !valueTable.getControl().isDisposed()) {
			valueTable.setInput(fTags);
		}
		isDirty = false;
	}

	public void performOk() {
		save();
		if (isDirty) {
			MessageDialog dialog = new MessageDialog(fControl.getShell(), ResourceHandler.getString("TaskTagPreferenceTab.22"), fControl.getShell().getImage(), ResourceHandler.getString("TaskTagPreferenceTab.23"), MessageDialog.QUESTION, new String[]{ResourceHandler.getString("TaskTagPreferenceTab.24"), ResourceHandler.getString("TaskTagPreferenceTab.25"), ResourceHandler.getString("TaskTagPreferenceTab.26")}, 2); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			int button = dialog.open();
			if (button == 0) {
				Job buildJob = new Job(ResourceHandler.getString("TaskTagPreferenceTab.27")) { //$NON-NLS-1$

					public Object getAdapter(Class adapter) {
						return null;
					}

					protected IStatus run(IProgressMonitor monitor) {
						IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
						int errorCount = 0;
						for (int i = 0; i < projects.length && !monitor.isCanceled(); i++) {
							try {
								projects[i].build(IncrementalProjectBuilder.FULL_BUILD, ModelPlugin.STRUCTURED_BUILDER, new HashMap(), new SubProgressMonitor(monitor, projects.length));
							}
							catch (CoreException e) {
								Logger.logException(e);
								errorCount++;
							}
						}
						IStatus status = null;
						if (monitor.isCanceled()) {
							status = new Status(IStatus.CANCEL, EditorPlugin.ID, IStatus.OK, ResourceHandler.getString("TaskTagPreferenceTab.28"), null); //$NON-NLS-1$
						}
						else if (errorCount == 0) {
							status = new Status(IStatus.OK, EditorPlugin.ID, IStatus.OK, ResourceHandler.getString("TaskTagPreferenceTab.29"), null); //$NON-NLS-1$
						}
						else {
							status = new Status(IStatus.ERROR, EditorPlugin.ID, IStatus.OK, ResourceHandler.getString("TaskTagPreferenceTab.30"), null); //$NON-NLS-1$
						}
						return status;
					}
				};
				buildJob.schedule(500);
			}
		}
		isDirty = false;
	}

	/**
	 * @param selection
	 */
	protected void removeTag(int i) {
		if (i < 0) {
			return;
		}
		isDirty = true;
		List tags = new ArrayList(Arrays.asList(fTags));
		List priorities = new ArrayList(Arrays.asList(fPriorities));
		tags.remove(i);
		priorities.remove(i);
		fTags = (String[]) tags.toArray(new String[0]);
		fPriorities = (Integer[]) priorities.toArray(new Integer[0]);
		valueTable.setInput(fTags);
	}

	/**
	 *  
	 */
	private void save() {
		String tags = StringUtils.pack(fTags);
		ModelPlugin.getDefault().getPluginPreferences().setValue(CommonModelPreferenceNames.TASK_TAG_TAGS, tags);

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < fPriorities.length; i++) {
			buf.append(String.valueOf(fPriorities[i]));
			if (i < fPriorities.length - 1)
				buf.append(","); //$NON-NLS-1$
		}
		ModelPlugin.getDefault().getPluginPreferences().setValue(CommonModelPreferenceNames.TASK_TAG_PRIORITIES, buf.toString());
	}
}
