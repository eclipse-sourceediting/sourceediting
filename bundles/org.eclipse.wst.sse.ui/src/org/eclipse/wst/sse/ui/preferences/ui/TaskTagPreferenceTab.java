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
package org.eclipse.wst.sse.ui.preferences.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
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
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.util.StringUtils;
import org.eclipse.wst.sse.core.participants.TaskTagSeeker;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;

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
						return SSEUIMessages.TaskTagPreferenceTab_0; //$NON-NLS-1$
					}
					else if (fPriorities[i].intValue() == IMarker.PRIORITY_LOW) {
						return SSEUIMessages.TaskTagPreferenceTab_1; //$NON-NLS-1$
					}
					else {
						return SSEUIMessages.TaskTagPreferenceTab_2; //$NON-NLS-1$
					}
				}
			}
			return SSEUIMessages.TaskTagPreferenceTab_3; //$NON-NLS-1$
		}
	}

	public class TaskTagDialog extends Dialog {
		public int priority = IMarker.PRIORITY_NORMAL;
		Combo priorityCombo = null;
		Text tagText = null;
		public String text = ""; //$NON-NLS-1$

		public TaskTagDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(SSEUIMessages.TaskTagPreferenceTab_5); //$NON-NLS-1$
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
			label.setText(SSEUIMessages.TaskTagPreferenceTab_6); //$NON-NLS-1$
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
			label.setText(SSEUIMessages.TaskTagPreferenceTab_7); //$NON-NLS-1$
			label.setLayoutData(new GridData());
			priorityCombo = new Combo(composite, SWT.READ_ONLY | SWT.SINGLE);
			priorityCombo.setItems(new String[]{SSEUIMessages.TaskTagPreferenceTab_8, SSEUIMessages.TaskTagPreferenceTab_9, SSEUIMessages.TaskTagPreferenceTab_10}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

	Control fControl;

	Button fEnableCheckbox = null;

	boolean fEnableTaskTags = true;

	Integer[] fPriorities;

	String[] fTags;

	boolean isDirty = false;

	TableViewer valueTable = null;

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

		fEnableCheckbox = new Button(composite, SWT.CHECK);
		fEnableCheckbox.setSelection(fEnableTaskTags);
		fEnableCheckbox.setText(SSEUIMessages.TaskTagPreferenceTab_31); //$NON-NLS-1$
		fEnableCheckbox.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER, GridData.HORIZONTAL_ALIGN_BEGINNING, false, false, 2, 1));

		valueTable = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		valueTable.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn textColumn = new TableColumn(valueTable.getTable(), SWT.NONE, 0);
		textColumn.setText(SSEUIMessages.TaskTagPreferenceTab_12); //$NON-NLS-1$
		TableColumn priorityColumn = new TableColumn(valueTable.getTable(), SWT.NONE, 1);
		priorityColumn.setText(SSEUIMessages.TaskTagPreferenceTab_13); //$NON-NLS-1$
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
		addButton.setText(SSEUIMessages.TaskTagPreferenceTab_14); //$NON-NLS-1$
		addButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button editButton = new Button(buttons, SWT.PUSH);
		editButton.setText(SSEUIMessages.TaskTagPreferenceTab_15); //$NON-NLS-1$
		editButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText(SSEUIMessages.TaskTagPreferenceTab_16); //$NON-NLS-1$
		removeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));

		Label spacer = new Label(buttons, SWT.NONE);
		spacer.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button upButton = new Button(buttons, SWT.PUSH);
		upButton.setText(SSEUIMessages.TaskTagPreferenceTab_17); //$NON-NLS-1$
		upButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));
		final Button downButton = new Button(buttons, SWT.PUSH);
		downButton.setText(SSEUIMessages.TaskTagPreferenceTab_18); //$NON-NLS-1$
		downButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER));

		editButton.setEnabled(false);
		removeButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);

		Label warning = new Label(composite, SWT.NONE);
		warning.setLayoutData(new GridData());
		warning.setText(SSEUIMessages.TaskTagPreferenceTab_19); //$NON-NLS-1$

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
		fEnableCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fEnableTaskTags = fEnableCheckbox.getSelection();
				isDirty = true;
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
				if (!upButton.isEnabled()) {
					downButton.setFocus();
				}
			}
		});
		downButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveTagDown(valueTable.getTable().getSelectionIndex());
				upButton.setEnabled(valueTable.getTable().getSelectionIndex() > 0);
				downButton.setEnabled(valueTable.getTable().getSelectionIndex() < fTags.length - 1);
				if (!downButton.isEnabled()) {
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
		return SSEUIMessages.TaskTagPreferenceTab_20; //$NON-NLS-1$
	}

	private void loadPreferenceValues() {
		Plugin modelPlugin = SSECorePlugin.getDefault();
		String tags = modelPlugin.getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_TAGS);
		String priorities = modelPlugin.getPluginPreferences().getString(CommonModelPreferenceNames.TASK_TAG_PRIORITIES);
		loadTagsAndPriorities(tags, priorities);
		fEnableTaskTags = modelPlugin.getPluginPreferences().getBoolean(CommonModelPreferenceNames.TASK_TAG_ENABLE);
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
		isDirty = true;
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
		isDirty = true;
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

		// To optimize builder performance when the tags are all removed,
		// the markers MUST be removed NOW
		if (fTags.length == 0) {
			try {
				// When the tags are all removed, remove all the related
				// markers so the builder can skip the marker deletion step
				ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(TaskTagSeeker.getTaskMarkerType(), true, IResource.DEPTH_INFINITE);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
		else if (isDirty) {
			MessageDialog dialog = new MessageDialog(fControl.getShell(), SSEUIMessages.TaskTagPreferenceTab_22, fControl.getShell().getImage(), SSEUIMessages.TaskTagPreferenceTab_23, MessageDialog.QUESTION, new String[]{SSEUIMessages.TaskTagPreferenceTab_24, SSEUIMessages.TaskTagPreferenceTab_25, SSEUIMessages.TaskTagPreferenceTab_26}, 2); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			int button = dialog.open();
			if (button == 0) {
				Job buildJob = new Job(SSEUIMessages.TaskTagPreferenceTab_27) { //$NON-NLS-1$
					public Object getAdapter(Class adapter) {
						return null;
					}

					protected IStatus run(IProgressMonitor monitor) {
						IStatus status = null;
						IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
						int errorCount = 0;
						for (int i = 0; i < projects.length && !monitor.isCanceled(); i++) {
							try {
								projects[i].build(IncrementalProjectBuilder.FULL_BUILD, SSECorePlugin.STRUCTURED_BUILDER, new HashMap(), new SubProgressMonitor(monitor, projects.length));
							}
							catch (CoreException e) {
								Logger.logException(e);
								errorCount++;
							}
						}
						if (monitor.isCanceled()) {
							status = new Status(IStatus.CANCEL, SSEUIPlugin.ID, IStatus.OK, SSEUIMessages.TaskTagPreferenceTab_28, null); //$NON-NLS-1$
						}
						else if (errorCount == 0) {
							status = new Status(IStatus.OK, SSEUIPlugin.ID, IStatus.OK, SSEUIMessages.TaskTagPreferenceTab_29, null); //$NON-NLS-1$
						}
						else {
							status = new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.OK, SSEUIMessages.TaskTagPreferenceTab_30, null); //$NON-NLS-1$
						}
						return status;
					}
				};
				buildJob.schedule(500);
			}

		}
		isDirty = false;
	}

	public void performDefaults() {
		Plugin modelPlugin = SSECorePlugin.getDefault();
		String tags = modelPlugin.getPluginPreferences().getDefaultString(CommonModelPreferenceNames.TASK_TAG_TAGS);
		String priorities = modelPlugin.getPluginPreferences().getDefaultString(CommonModelPreferenceNames.TASK_TAG_PRIORITIES);
		loadTagsAndPriorities(tags, priorities);
		if (valueTable != null && valueTable.getControl() != null && !valueTable.getControl().isDisposed()) {
			valueTable.setInput(fTags);
		}
		fEnableTaskTags = modelPlugin.getPluginPreferences().getDefaultBoolean(CommonModelPreferenceNames.TASK_TAG_ENABLE);
		fEnableCheckbox.setSelection(fEnableTaskTags);

		isDirty = false;
	}

	public void performOk() {
		performApply();
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
		Plugin modelPlugin = SSECorePlugin.getDefault();
		modelPlugin.getPluginPreferences().setValue(CommonModelPreferenceNames.TASK_TAG_TAGS, tags);

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < fPriorities.length; i++) {
			buf.append(String.valueOf(fPriorities[i]));
			if (i < fPriorities.length - 1)
				buf.append(","); //$NON-NLS-1$
		}
		modelPlugin.getPluginPreferences().setValue(CommonModelPreferenceNames.TASK_TAG_PRIORITIES, buf.toString());
		modelPlugin.getPluginPreferences().setValue(CommonModelPreferenceNames.TASK_TAG_ENABLE, fEnableTaskTags);
	}
}
