/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.tabs.main;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wst.xsl.internal.debug.ui.AbstractTableBlock;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIConstants;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.actions.AbstractStylesheetAction;
import org.eclipse.wst.xsl.internal.debug.ui.actions.AddExternalFileAction;
import org.eclipse.wst.xsl.internal.debug.ui.actions.AddWorkspaceFileAction;
import org.eclipse.wst.xsl.internal.debug.ui.actions.MoveDownAction;
import org.eclipse.wst.xsl.internal.debug.ui.actions.MoveUpAction;
import org.eclipse.wst.xsl.internal.debug.ui.actions.RemoveAction;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;

public class TransformsBlock extends AbstractTableBlock implements
		IStylesheetEntriesChangedListener {
	protected static final String DIALOG_SETTINGS_PREFIX = "TransformsBlock"; //$NON-NLS-1$
	private StylesheetViewer stylesheetViewer;
	private LaunchPipeline pipeline;

	public TransformsBlock() {
		super();
	}

	public void createControl(Composite parent) {
		Font font = parent.getFont();

		Group group = new Group(parent, SWT.NONE);
		group.setText(getName());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setFont(font);

		setControl(group);

		stylesheetViewer = new StylesheetViewer(group);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		stylesheetViewer.getViewer().getTable().setLayoutData(gd);
		stylesheetViewer.addEntriesChangedListener(this);
		stylesheetViewer.getViewer().getControl().setFont(font);
		stylesheetViewer.getViewer().setLabelProvider(
				new StylesheetLabelProvider());
		stylesheetViewer.getViewer().setContentProvider(
				new StylesheetContentProvider());
		stylesheetViewer.getViewer().getTable().addKeyListener(
				new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent event) {
						if (event.character == SWT.DEL && event.stateMask == 0) {
							RemoveAction ra = new RemoveAction(stylesheetViewer);
							ra.run();
							updateLaunchConfigurationDialog();
						}
					}
				});

		Composite upDownButtonComp = new Composite(group, SWT.NONE);
		GridLayout upDownButtonLayout = new GridLayout();
		upDownButtonLayout.marginHeight = 0;
		upDownButtonLayout.marginWidth = 0;
		upDownButtonComp.setLayout(upDownButtonLayout);
		gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		upDownButtonComp.setLayoutData(gd);
		upDownButtonComp.setFont(font);

		createArrowButton(upDownButtonComp, new MoveUpAction(stylesheetViewer),
				SWT.UP);
		Label spacer = new Label(upDownButtonComp, SWT.NONE);
		gd = new GridData(SWT.NONE, SWT.FILL, false, true);
		spacer.setLayoutData(gd);
		createArrowButton(upDownButtonComp,
				new MoveDownAction(stylesheetViewer), SWT.DOWN);

		Composite pathButtonComp = new Composite(group, SWT.NONE);
		GridLayout pathButtonLayout = new GridLayout();
		pathButtonLayout.marginHeight = 0;
		pathButtonLayout.marginWidth = 0;
		pathButtonComp.setLayout(pathButtonLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.HORIZONTAL_ALIGN_FILL);
		pathButtonComp.setLayoutData(gd);
		pathButtonComp.setFont(font);

		createButton(pathButtonComp, new AddWorkspaceFileAction(
				stylesheetViewer));
		createButton(pathButtonComp, new AddExternalFileAction(
				stylesheetViewer, DIALOG_SETTINGS_PREFIX));
		createButton(pathButtonComp, new RemoveAction(stylesheetViewer));
	}

	public Viewer getStylesheetViewer() {
		return stylesheetViewer == null ? null : stylesheetViewer.getViewer();
	}

	protected Button createArrowButton(Composite pathButtonComp,
			AbstractStylesheetAction action, int updown) {
		Button b = new Button(pathButtonComp, SWT.ARROW | updown);
		GridData gd = new GridData();
		b.setLayoutData(gd);
		action.setButton(b);
		return b;
	}

	protected Button createButton(Composite pathButtonComp,
			AbstractStylesheetAction action) {
		Button button = createPushButton(pathButtonComp, action.getText(), null);
		action.setButton(button);
		return button;
	}

	public String getName() {
		return Messages.TransformsBlock_Name;
	}

	public void setPipeline(LaunchPipeline pipeline) {
		this.pipeline = pipeline;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		stylesheetViewer.getViewer().setInput(pipeline);
		if (pipeline.getTransformDefs().size() > 0) {
			stylesheetViewer.getViewer()
					.setSelection(
							new StructuredSelection(pipeline.getTransformDefs()
									.get(0)));
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void entriesChanged(StylesheetViewer viewer) {
		setDirty(true);
		updateLaunchConfigurationDialog();
	}

	@Override
	protected void setSortColumn(int column) {
		switch (column) {
		// case 1:
		// sortByName();
		// break;
		// case 2:
		// sortByType();
		// break;
		}
		super.setSortColumn(column);
	}

	@Override
	protected Table getTable() {
		return stylesheetViewer == null ? null : stylesheetViewer.getViewer()
				.getTable();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		return XSLDebugUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getQualifier() {
		return XSLDebugUIConstants.MAIN_TRANSFORMS_BLOCK;
	}

	@Override
	public void dispose() {
		if (stylesheetViewer != null)
			stylesheetViewer.removeEntriesChangedListener(this);
		super.dispose();
	}
}
