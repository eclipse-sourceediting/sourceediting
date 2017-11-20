/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     Stuart Harper - bug 264788 - added "open files" selector
 *     David Carver (STAR) - bug 264788 - pulled up getFileExtensions from InputFileBlock
 *     David Carver (Intalio) - clean up find bugs
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui;

import java.io.File;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.wst.xsl.core.internal.util.XMLContentType;

/**
 * A block that shows a text box with buttons for browsing workspace or the
 * filesystem in order to populate the text box with a file path.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public abstract class ResourceSelectionBlock extends
		AbstractLaunchConfigurationTab {
	protected static final int ERROR_DIRECTORY_NOT_SPECIFIED = 1;
	protected static final int ERROR_DIRECTORY_DOES_NOT_EXIST = 2;
	protected static final int GROUP_NAME = 3;
	protected static final int USE_DEFAULT_RADIO = 4;
	protected static final int USE_OTHER_RADIO = 5;
	protected static final int DIRECTORY_DIALOG_MESSAGE = 6;
	protected static final int WORKSPACE_DIALOG_MESSAGE = 7;
	protected static final int VARIABLES_BUTTON = 8;
	protected static final int FILE_SYSTEM_BUTTON = 9;
	protected static final int WORKSPACE_BUTTON = 10;
	protected static final int WORKSPACE_DIALOG_TITLE = 11;
	protected static final int OPENFILES_BUTTON = 12;
	protected static final int OPENFILES_DIALOG_TITLE = 13;

	protected Button fWorkspaceButton;
	protected Button fFileSystemButton;
	protected Button fVariablesButton;
	protected Button fOpenFilesButton;
	protected Button useDefaultCheckButton;
	protected Text resourceText;
	protected WidgetListener widgetListener = new WidgetListener();
	private ILaunchConfiguration fLaunchConfiguration;
	protected final boolean showDefault;
	private final int resourceType;
	private final boolean mustExist;
	protected boolean required;
	protected String defaultResource;
	protected String resource;
	protected String fileLabel = Messages.ResourceSelectionBlock_0;

	private final ISelectionStatusValidator validator = new ISelectionStatusValidator() {
		public IStatus validate(Object[] selection) {
			if (selection.length == 0) {
				return new Status(IStatus.ERROR, XSLDebugUIPlugin.PLUGIN_ID, 0,
						"", null); //$NON-NLS-1$
			}
			for (int i = 0; i < selection.length; i++) {
				if (resourceType == IResource.FOLDER
						&& !(selection[i] instanceof IContainer))
					return new Status(IStatus.ERROR,
							XSLDebugUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				else if (resourceType == IResource.FILE
						&& !(selection[i] instanceof IFile))
					return new Status(IStatus.ERROR,
							XSLDebugUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
			}
			return new Status(IStatus.OK, XSLDebugUIPlugin.PLUGIN_ID, 0,
					"", null); //$NON-NLS-1$
		}
	};

	class WidgetListener extends SelectionAdapter implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			textModified();
			updateLaunchConfigurationDialog();
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			Object source = e.getSource();
			if (source == fWorkspaceButton) {
				handleWorkspaceResourceBrowseButtonSelected();
			} else if (source == fFileSystemButton) {
				handleExternalResourceBrowseButtonSelected();
			} else if (source == fVariablesButton) {
				handleResourceVariablesButtonSelected();
			} else if (source == useDefaultCheckButton) {
				updateResourceText(useDefaultCheckButton.getSelection());
			} else if (source == fOpenFilesButton) {
				handleOpenFilesResourceBrowseButtonSelected();
			}
		}
	}

	/**
	 * Same as <code>new ResourceSelectionBlock(true)</code>
	 */
	public ResourceSelectionBlock() {
		this(true);
	}

	/**
	 * Same as
	 * <code>new ResourceSelectionBlock(IResource.FOLDER,showDefault)</code>
	 * 
	 * @param showDefault
	 *            true if this should have a 'Show Default' button
	 */
	public ResourceSelectionBlock(boolean showDefault) {
		this(IResource.FOLDER, showDefault);
	}

	/**
	 * Same as
	 * <code>new ResourceSelectionBlock(resourceType,showDefault,true)</code>
	 * 
	 * @param resourceType
	 *            the type of resource to select - IResource.FOLDER or
	 *            IResource.FILE
	 * @param showDefault
	 *            true if this should have a 'Show Default' button
	 */
	public ResourceSelectionBlock(int resourceType, boolean showDefault) {
		this(resourceType, showDefault, true);
	}

	/**
	 * Same as
	 * <code>new ResourceSelectionBlock(resourceType,showDefault,required,true)</code>
	 * 
	 * @param resourceType
	 *            the type of resource to select - IResource.FOLDER or
	 *            IResource.FILE
	 * @param showDefault
	 *            true if this should have a 'Show Default' button
	 * @param required
	 *            true if a blank text box is invalid
	 */
	public ResourceSelectionBlock(int resourceType, boolean showDefault,
			boolean required) {
		this(resourceType, showDefault, required, true);
	}

	/**
	 * Create a new instance of this.
	 * 
	 * @param resourceType
	 *            the type of resource to select - IResource.FOLDER or
	 *            IResource.FILE
	 * @param showDefault
	 *            true if this should have a 'Show Default' button
	 * @param required
	 *            true if a blank text box is invalid
	 * @param mustExist
	 *            true of the selected resource must already exist
	 */
	public ResourceSelectionBlock(int resourceType, boolean showDefault,
			boolean required, boolean mustExist) {
		super();
		this.showDefault = showDefault;
		this.resourceType = resourceType;
		this.required = required;
		this.mustExist = mustExist;
	}

	public void createControl(Composite parent) {
		Composite group = createContainer(parent);
		setControl(group);
		createContents(group);
	}

	protected Composite createContainer(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(getMessage(GROUP_NAME));
		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp...
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		groupLayout.makeColumnsEqualWidth = false;
		group.setLayout(groupLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setFont(parent.getFont());
		return group;
	}

	protected void createContents(Composite parent) {
		createCheckboxAndText(parent);
		createButtons(parent);
	}

	protected void createCheckboxAndText(Composite parent) {
		if (showDefault) {
			useDefaultCheckButton = createCheckButton(parent,
					getMessage(USE_DEFAULT_RADIO));
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = 2;
			useDefaultCheckButton.setLayoutData(gd);
			useDefaultCheckButton.addSelectionListener(widgetListener);
		}

		Composite specificFileComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		if (showDefault)
			layout.marginLeft = 20;
		else
			layout.marginLeft = 0;
		layout.marginHeight = 0;
		specificFileComp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		specificFileComp.setLayoutData(gd);

		if (showDefault) {
			Label label = new Label(specificFileComp, SWT.NONE);
			label.setText(fileLabel);
		}

		resourceText = new Text(specificFileComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = showDefault ? 1 : 2;
		resourceText.setLayoutData(gd);
		resourceText.setFont(parent.getFont());
		resourceText.addModifyListener(widgetListener);
	}

	protected void createButtons(Composite parent) {
		// filler
		new Label(parent, SWT.NONE);

		Composite buttonComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComp.setLayout(layout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 1;
		buttonComp.setLayoutData(gd);
		buttonComp.setFont(parent.getFont());

		fWorkspaceButton = createPushButton(buttonComp,
				getMessage(WORKSPACE_BUTTON), null);
		fWorkspaceButton.addSelectionListener(widgetListener);

		fFileSystemButton = createPushButton(buttonComp,
				getMessage(FILE_SYSTEM_BUTTON), null);
		fFileSystemButton.addSelectionListener(widgetListener);

		fVariablesButton = createPushButton(buttonComp,
				getMessage(VARIABLES_BUTTON), null);
		fVariablesButton.addSelectionListener(widgetListener);

		fOpenFilesButton = createPushButton(buttonComp,
				getMessage(OPENFILES_BUTTON), null);
		fOpenFilesButton.addSelectionListener(widgetListener);
	}

	protected void updateResourceText(boolean useDefault) {
		if (useDefault) {
			resourceText
					.setText(defaultResource == null ? "" : defaultResource); //$NON-NLS-1$
		} else {
			resourceText.setText(resource == null ? "" : resource); //$NON-NLS-1$
		}
		resourceText.setEnabled(!useDefault);
		fFileSystemButton.setEnabled(!useDefault);
		fVariablesButton.setEnabled(!useDefault);
		fWorkspaceButton.setEnabled(!useDefault);
	}

	@Override
	public void dispose() {
	}

	protected void handleExternalResourceBrowseButtonSelected() {
		String currentWorkingDir = getText();
		String selected = null;
		if (resourceType == IResource.FOLDER) {
			DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setMessage(getMessage(DIRECTORY_DIALOG_MESSAGE));
			if (!currentWorkingDir.trim().equals("")) //$NON-NLS-1$
			{
				File path = new File(currentWorkingDir);
				if (path.exists()) {
					dialog.setFilterPath(currentWorkingDir);
				}
			}
			selected = dialog.open();
		} else {
			FileDialog dialog = new FileDialog(getShell());
			if (!currentWorkingDir.trim().equals("")) //$NON-NLS-1$
			{
				File path = new File(currentWorkingDir);
				if (path.exists()) {
					dialog.setFilterPath(currentWorkingDir);
				}
			}
			String[] fileExtensions = getFileExtensions();
			if (fileExtensions != null) {
				String[] filterExtensions = new String[fileExtensions.length];
				for (int i = 0; i < fileExtensions.length; i++) {
					String ext = fileExtensions[i];
					filterExtensions[i] = "*." + ext; //$NON-NLS-1$
				}
				dialog.setFilterExtensions(filterExtensions);
			}
			selected = dialog.open();
		}
		if (selected != null) {
			resourceText.setText(selected);
		}
	}

	protected String[] getFileExtensions() {
		return new XMLContentType().getFileExtensions();
	}

	protected void handleOpenFilesResourceBrowseButtonSelected() {
		String path = openFileListResourceDialog();
		if (path != null)
			setText("${workspace_loc:" + path + "}"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Opens a dialog displaying a list of all XML files in the editor and
	 * allows the user to select one of them.
	 * 
	 * @return The path to the selected XML file or null if none was chosen.
	 */
	protected String openFileListResourceDialog() {
		IEditorReference[] editors = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();

		String[] paths = filterOpenEditorsByFileExtension(editors);

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), new LabelProvider());

		dialog.setTitle(getMessage(OPENFILES_DIALOG_TITLE));
		dialog.setElements(paths);
		dialog.open();

		return (String) dialog.getFirstResult();

	}

	private String[] filterOpenEditorsByFileExtension(IEditorReference[] editors) {
		String[] paths = new String[editors.length];
		String[] fileExts = getFileExtensions();

		for (int i = 0; i < editors.length; i++) {
			IEditorReference currentEditor = editors[i];
			IEditorPart editorPart = currentEditor.getEditor(true);
			IFile file = (IFile) editorPart.getEditorInput().getAdapter(
					IFile.class);
			if (file != null) {
				IPath path = file.getFullPath();
				paths[i] = getEditorPath(path, fileExts);
			}
		}
		return paths;
	}

	private String getEditorPath(IPath filePath, String[] fileExts) {
		if (fileExts == null || fileExts.length == 0) {
			return filePath.toOSString();
		}

		String path = null;
		for (int cnt = 0; cnt < fileExts.length; cnt++) {
			if (filePath.getFileExtension().equals(fileExts[cnt])) {
				path = filePath.toOSString();
				break;
			}
		}
		return path;
	}

	protected void handleWorkspaceResourceBrowseButtonSelected() {
		IPath path = openWorkspaceResourceDialog();
		if (path != null)
			setText("${workspace_loc:" + path.toString() + "}"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected IPath openWorkspaceResourceDialog() {
		IResource currentResource = getResource();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle(getMessage(WORKSPACE_DIALOG_TITLE));
		dialog.setMessage(getMessage(WORKSPACE_DIALOG_MESSAGE));
		dialog.setValidator(validator);
		dialog.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IContainer)
					return true;
				if (resourceType != IResource.FILE)
					return false;
				IFile file = (IFile) element;
				String[] extensions = getFileExtensions();
				if (extensions == null)
					return true;
				String fileExt = file.getFileExtension();
				if (fileExt != null) {
					for (String ext : extensions) {
						if (fileExt.equalsIgnoreCase(ext))
							return true;
					}
				}
				return false;
			}
		});
		ViewerFilter filter = getResourceFilter();
		if (filter != null)
			dialog.addFilter(filter);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setInitialSelection(currentResource);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setAllowMultiple(false);

		if (dialog.open() == Window.OK) {
			Object[] elements = dialog.getResult();
			if (elements.length > 0)
				return ((IResource) elements[0]).getFullPath();
		}
		return null;
	}

	protected ViewerFilter getResourceFilter() {
		return null;
	}

	/**
	 * Returns the selected workspace container,or <code>null</code>
	 */
	protected IResource getResource() {
		String path = getText();
		if (path.length() > 0) {
			IResource res = null;
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			try {
				if (path.startsWith("${workspace_loc:")) //$NON-NLS-1$
				{
					IStringVariableManager manager = VariablesPlugin
							.getDefault().getStringVariableManager();
					path = manager.performStringSubstitution(path, false);
				}
				File f = new File(path);
				if (resourceType == IResource.FOLDER) {
					IContainer[] containers = root
							.findContainersForLocationURI(f.toURI());
					if (containers.length > 0) {
						res = containers[0];
					}
				} else if (resourceType == IResource.FILE) {
					IFile[] files = root.findFilesForLocationURI(f.toURI());
					if (files.length > 0) {
						res = files[0];
					}
				}
				return res;
			} catch (CoreException e) {
				XSLDebugUIPlugin.log(e);
			}
		}
		return null;
	}

	protected void handleResourceVariablesButtonSelected() {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(
				getShell());
		dialog.open();
		String variableText = dialog.getVariableExpression();
		if (variableText != null) {
			resourceText.insert(variableText);
		}
	}

	@Override
	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		setMessage(null);
		// if variables are present, we cannot resolve the directory
		String workingDirPath = getText();
		if (workingDirPath.indexOf("${") >= 0) //$NON-NLS-1$
		{
			IStringVariableManager manager = VariablesPlugin.getDefault()
					.getStringVariableManager();
			try {
				manager.validateStringVariables(workingDirPath);
				if (mustExist) {
					String path = manager
							.performStringSubstitution(workingDirPath);
					validateResource(path);
				}
			} catch (CoreException e) {
				setErrorMessage(e.getMessage());
				return false;
			}
		} else if (mustExist && workingDirPath.length() > 0) {
			return validateResource(workingDirPath);
		} else if (required && workingDirPath.length() == 0) {
			setErrorMessage(getMessage(ERROR_DIRECTORY_NOT_SPECIFIED));
		}
		return true;
	}

	protected boolean validateResource(String workingDirPath) {
		if (resourceType == IResource.FOLDER) {
			IContainer container = (IContainer) getResource();
			if (container == null) {
				File dir = new File(workingDirPath);
				if (dir.isDirectory()) {
					return true;
				}
			} else
				return true;
		} else if (resourceType == IResource.FILE) {
			File file = new File(workingDirPath);
			if (file.isFile()) {
				return true;
			}
		}
		setErrorMessage(getMessage(ERROR_DIRECTORY_DOES_NOT_EXIST));
		return false;
	}

	protected abstract String getMessage(int type);

	protected void textModified() {
	}

	protected String getText() {
		return resourceText.getText().trim();
	}

	protected void setText(String text) {
		resourceText.setText(text);
	}

	protected void setLaunchConfiguration(ILaunchConfiguration config) {
		fLaunchConfiguration = config;
	}

	protected ILaunchConfiguration getLaunchConfiguration() {
		return fLaunchConfiguration;
	}
}