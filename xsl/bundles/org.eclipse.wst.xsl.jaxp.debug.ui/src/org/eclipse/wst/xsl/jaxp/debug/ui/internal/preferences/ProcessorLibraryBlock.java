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
package org.eclipse.wst.xsl.jaxp.debug.ui.internal.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor.InstallStandin;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor.JarContentProvider;
import org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor.JarLabelProvider;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;

public class ProcessorLibraryBlock implements SelectionListener, ISelectionChangedListener
{
	protected static final String LAST_PATH_SETTING = "LAST_PATH_SETTING"; //$NON-NLS-1$
	protected static final String LAST_WORKSPACE_PATH_SETTING = "LAST_WORKSPACE_PATH_SETTING"; //$NON-NLS-1$
	protected static final String DIALOG_SETTINGS_PREFIX = "ProcessorLibraryBlock"; //$NON-NLS-1$
	protected InstallStandin install;
	protected IProcessorType installType;
	protected AddProcessorDialog addDialog = null;
	protected TableViewer tableViewer;
	private Button removeButton;
	private Button addButton;
	private Button addWorkspaceButton;

	private final ISelectionStatusValidator validator = new ISelectionStatusValidator()
	{
		public IStatus validate(Object[] selection)
		{
			if (selection.length == 0)
			{
				return new Status(IStatus.ERROR, XSLDebugUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
			}
			for (Object element : selection)
			{
				if (element instanceof IFolder)
					return new Status(IStatus.ERROR, XSLDebugUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				else if (element instanceof IFile)
				{
					// IFile file = (IFile) selection[i];
					// TODO check that the file is not already on the classpath
				}
			}
			return new Status(IStatus.OK, XSLDebugUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		}
	};

	public ProcessorLibraryBlock(AddProcessorDialog dialog)
	{
		addDialog = dialog;
	}

	public Control createControl(Composite parent)
	{
		Font font = parent.getFont();

		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		topLayout.marginHeight = 0;
		topLayout.marginWidth = 0;
		comp.setLayout(topLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);

		tableViewer = new TableViewer(comp);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 6;
		tableViewer.getControl().setLayoutData(gd);
		JarContentProvider fLibraryContentProvider = new JarContentProvider();
		tableViewer.setContentProvider(fLibraryContentProvider);
		tableViewer.setLabelProvider(new JarLabelProvider());
		tableViewer.addSelectionChangedListener(this);

		Composite pathButtonComp = new Composite(comp, SWT.NONE);
		GridLayout pathButtonLayout = new GridLayout();
		pathButtonLayout.marginHeight = 0;
		pathButtonLayout.marginWidth = 0;
		pathButtonComp.setLayout(pathButtonLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		pathButtonComp.setLayoutData(gd);
		pathButtonComp.setFont(font);

		addWorkspaceButton = createPushButton(pathButtonComp, Messages.ProcessorLibraryBlock_AddWorkspaceButton);
		addWorkspaceButton.addSelectionListener(this);

		addButton = createPushButton(pathButtonComp, Messages.ProcessorLibraryBlock_AddButton);
		addButton.addSelectionListener(this);

		removeButton = createPushButton(pathButtonComp, Messages.ProcessorLibraryBlock_RemoveButton);
		removeButton.addSelectionListener(this);

		return comp;
	}

	protected Button createPushButton(Composite parent, String label)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);
		addDialog.setButtonLayoutData(button);
		return button;
	}

	protected void createVerticalSpacer(Composite comp, int colSpan)
	{
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = colSpan;
		label.setLayoutData(gd);
	}

	public void initializeFrom(InstallStandin standin, IProcessorType type)
	{
		install = standin;
		installType = type;
		if (install != null)
			tableViewer.setInput(install);
		update();
	}

	/**
	 * Updates buttons and status based on current libraries
	 */
	public void update()
	{
		updateButtons();
		IStatus status = Status.OK_STATUS;
		if (install != null && install.getProcessorJars().length == 0)
		{
			status = new Status(IStatus.INFO, XSLDebugUIPlugin.PLUGIN_ID, 0, Messages.ProcessorLibraryBlock_6, null);
		}
		addDialog.setSystemLibraryStatus(status);
		addDialog.updateStatusLine();
	}

	public void performApply(InstallStandin standin)
	{
		standin.setProcessorJars(install.getProcessorJars());
	}

	protected IProcessorInstall getVMInstall()
	{
		return install;
	}

	protected IProcessorType getVMInstallType()
	{
		return installType;
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == removeButton)
		{
			remove((IStructuredSelection) tableViewer.getSelection());
		}
		else if (source == addButton)
		{
			addExternal((IStructuredSelection) tableViewer.getSelection());
		}
		else if (source == addWorkspaceButton)
		{
			addWorkspace((IStructuredSelection) tableViewer.getSelection());
		}
		update();
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	private void addExternal(IStructuredSelection selection)
	{
		IDialogSettings dialogSettings = XSLDebugUIPlugin.getDefault().getDialogSettings();
		String lastUsedPath = dialogSettings.get(LAST_PATH_SETTING);
		if (lastUsedPath == null)
		{
			lastUsedPath = ""; //$NON-NLS-1$
		}
		FileDialog dialog = new FileDialog(tableViewer.getControl().getShell(), SWT.MULTI);
		dialog.setText(Messages.ProcessorLibraryBlock_FileDialog_Title);
		dialog.setFilterExtensions(new String[]
		{ "*.jar;*.zip" }); //$NON-NLS-1$
		dialog.setFilterPath(lastUsedPath);
		String res = dialog.open();
		if (res == null)
		{
			return;
		}
		String[] fileNames = dialog.getFileNames();
		int nChosen = fileNames.length;

		IPath filterPath = new Path(dialog.getFilterPath());
		IProcessorJar[] libs = new IProcessorJar[nChosen];
		for (int i = 0; i < nChosen; i++)
		{
			libs[i] = JAXPRuntime.createProcessorJar(filterPath.append(fileNames[i]).makeAbsolute());
		}
		dialogSettings.put(LAST_PATH_SETTING, filterPath.toOSString());

		IProcessorJar[] currentJars = install.getProcessorJars();
		IProcessorJar[] newJars = new IProcessorJar[currentJars.length + libs.length];
		System.arraycopy(currentJars, 0, newJars, 0, currentJars.length);
		System.arraycopy(libs, 0, newJars, currentJars.length, libs.length);
		install.setProcessorJars(newJars);

		tableViewer.add(libs);
	}

	private void addWorkspace(IStructuredSelection selection)
	{
		IDialogSettings dialogSettings = XSLDebugUIPlugin.getDefault().getDialogSettings();
		String lastUsedPath = dialogSettings.get(LAST_WORKSPACE_PATH_SETTING);
		IPath lastPath = null;
		if (lastUsedPath != null)
		{
			lastPath = Path.fromPortableString(lastUsedPath);
		}

		// IResource currentResource = getResource();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(tableViewer.getControl().getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setTitle(Messages.ProcessorLibraryBlock_WorkspaceFileDialog_Title);
		dialog.setMessage(Messages.ProcessorLibraryBlock_WorkspaceFileDialog_Message);
		dialog.setValidator(validator);
		dialog.addFilter(new ViewerFilter()
		{
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{
				if (element instanceof IContainer)
					return true;
				else if (element instanceof IFile)
				{
					IFile file = (IFile) element;
					String extension = file.getFileExtension();
					if (extension == null)
						return false;
					return extension.equals("jar"); //$NON-NLS-1$
				}
				return false;
			}
		});
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		if (lastPath != null)
			dialog.setInitialSelection(lastPath);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setAllowMultiple(true);

		if (dialog.open() == Window.OK)
		{
			Object[] elements = dialog.getResult();
			if (elements.length > 0)
			{
				IProcessorJar[] libs = new IProcessorJar[elements.length];
				for (int i = 0; i < elements.length; i++)
				{
					IFile jar = (IFile) elements[i];
					libs[i] = JAXPRuntime.createProcessorJar(jar.getFullPath());
				}
				IProcessorJar[] currentJars = install.getProcessorJars();
				IProcessorJar[] newJars = new IProcessorJar[currentJars.length + libs.length];
				System.arraycopy(currentJars, 0, newJars, 0, currentJars.length);
				System.arraycopy(libs, 0, newJars, currentJars.length, libs.length);
				install.setProcessorJars(newJars);

				tableViewer.add(libs);

				lastPath = libs[0].getPath();
				lastPath = lastPath.uptoSegment(lastPath.segmentCount());
				dialogSettings.put(LAST_WORKSPACE_PATH_SETTING, lastPath.toPortableString());
			}
		}
	}

	private void remove(IStructuredSelection selection)
	{
		List<IProcessorJar> currentJars = new ArrayList<IProcessorJar>(Arrays.asList(install.getProcessorJars()));
		for (Iterator<?> iter = selection.iterator(); iter.hasNext();)
		{
			currentJars.remove(iter.next());
		}
		install.setProcessorJars(currentJars.toArray(new IProcessorJar[0]));
		tableViewer.remove(selection.toArray());
	}

	public void selectionChanged(SelectionChangedEvent event)
	{
		updateButtons();
	}

	private void updateButtons()
	{
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		removeButton.setEnabled(!selection.isEmpty());
	}
}
