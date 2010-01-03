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
package org.eclipse.wst.xsl.internal.debug.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.InputFileBlock;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.config.BaseLaunchHelper;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

/**
 * <table border=1>
 * <th>
 * <tr>
 * <td>XML Files</td>
 * <td>XSL Files</td>
 * <td>Action</td>
 * </tr>
 * </th>
 * <tbody>
 * <tr>
 * <td>1</td>
 * <td>0</td>
 * <td>Launch assuming embedded stylesheet instruction</td>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>&gt;=1</td>
 * <td>Open dialog - prompt for input file</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>&gt;=1</td>
 * <td>Launch</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <p>
 * The launch shortcut should not appear in the menu for any other combination
 * of files
 * </p>
 * <p>
 * In all cases, a check must be performed to find any existing launch
 * configuration that uses the selected files.
 * </p>
 * 
 * @author Doug
 * @since 1.0
 */
public class XSLLaunchShortcut implements ILaunchShortcut {
	private IFile xmlFile;
	private IPath xmlFilePath;
	private IFile[] xslFiles;

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			searchAndLaunch(ssel.toArray(), mode);
		}
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if (input != null) {
			IFile file = (IFile) input.getAdapter(IFile.class);
			if (file != null)
				searchAndLaunch(new Object[] { file }, mode);
		}
	}

	private void searchAndLaunch(Object[] objects, String mode) {
		if (fillFiles(objects)) {
			// ensure we have an input file
			if (xmlFile == null)
				promptForInput();
			if (xmlFile != null || xmlFilePath != null)
				launch(mode);
		}
	}

	private void promptForInput() {
		// prompt for input xml file
		StatusDialog dialog = new StatusDialog(getShell()) {
			private InputFileBlock inputFileBlock = new InputFileBlock(null);

			@Override
			protected Control createDialogArea(Composite parent) {
				Composite comp = (Composite) super.createDialogArea(parent);
				comp.setFont(parent.getFont());
				GridLayout layout = new GridLayout(1, false);
				comp.setLayout(layout);

				Label label = new Label(comp, SWT.NONE);
				label.setFont(comp.getFont());
				GridData gd = new GridData();
				gd.horizontalIndent = 5;
				gd.verticalIndent = 5;
				gd.widthHint = 380;
				label.setLayoutData(gd);
				label.setText(Messages.XSLLaunchShortcut_0);

				inputFileBlock.createControl(comp);
				return comp;
			}

			@Override
			protected void okPressed() {
				IResource res = inputFileBlock.getResource();
				if (res == null)
					xmlFilePath = new Path(inputFileBlock.getText());
				else if (ResourcesPlugin.getWorkspace().getRoot().exists(
						res.getFullPath())
						&& res.getType() == IResource.FILE)
					xmlFile = (IFile) res;
				super.okPressed();
			}

		};
		dialog.setHelpAvailable(false);
		dialog.setStatusLineAboveButtons(true);
		dialog.setTitle(Messages.XSLLaunchShortcut_1);
		dialog.open();
	}

	private boolean fillFiles(Object[] selections) {
		xmlFile = null;
		xmlFilePath = null;
		List<IFile> xslFileList = new ArrayList<IFile>();
		for (Object object : selections) {
			IResource resource = (IResource) object;
			if (resource.getType() == IResource.FILE) {
				IFile file = (IFile) resource;
				if (XSLCore.isXMLFile(file)) {
					if (XSLCore.isXSLFile(file))
						xslFileList.add(file);
					else if (xmlFile == null)
						xmlFile = file;
					else
						return false; // no action if we have more than than 1
										// xml file
				}
			}
		}
		xslFiles = xslFileList.toArray(new IFile[0]);
		return true;
	}

	private void launch(String mode) {
		if (xmlFile != null)
			xmlFilePath = xmlFile.getLocation();
		ILaunchConfiguration config = null;
		try {
			config = findOrCreateLaunchConfiguration();
			if (config != null)
				DebugUITools.launch(config, mode);
		} catch (CoreException e) {
			XSLDebugUIPlugin.log(e);
		}
	}

	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(
				XSLLaunchConfigurationConstants.ID_LAUNCH_CONFIG_TYPE);
	}

	private ILaunchConfiguration findOrCreateLaunchConfiguration()
			throws CoreException {
		ILaunchConfiguration[] configs = getLaunchManager()
				.getLaunchConfigurations(getConfigurationType());
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>(
				configs.length);
		for (ILaunchConfiguration config : configs) {
			String inputFile = config.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_INPUT_FILE,
					(String) null);
			try {
				inputFile = VariablesPlugin.getDefault()
						.getStringVariableManager().performStringSubstitution(
								inputFile);
			} catch (CoreException e) {
				// just ignore this one
				continue;
			}
			Path path = new Path(inputFile);
			// the source xml file must be the same
			if (path.equals(xmlFilePath)) {
				BaseLaunchHelper lh = new BaseLaunchHelper(config);
				// all the selected stylesheets must be in the pipeline
				boolean found = false;
				for (IFile stylesheet : xslFiles) {
					found = false;
					for (Iterator<LaunchTransform> iter = lh.getPipeline()
							.getTransformDefs().iterator(); iter.hasNext();) {
						LaunchTransform lt = iter.next();
						if (lt.getLocation().equals(stylesheet.getLocation())) {
							found = true;
							break;
						}
					}
					if (!found)
						break;
				}
				if (found)
					candidateConfigs.add(config);
			}
		}

		ILaunchConfiguration config = null;
		int candidateCount = candidateConfigs.size();
		if (candidateCount == 1)
			config = candidateConfigs.get(0);
		else if (candidateCount > 1)
			config = chooseConfiguration(candidateConfigs);
		else
			config = createConfiguration();

		return config;
	}

	private ILaunchConfiguration chooseConfiguration(
			List<ILaunchConfiguration> configList) {
		IDebugModelPresentation labelProvider = DebugUITools
				.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle(Messages.XSLLaunchShortcut_2);
		dialog.setMessage(Messages.XSLSelectExisting);
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	private ILaunchConfiguration createConfiguration() {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null,
					getLaunchManager()
							.generateUniqueLaunchConfigurationNameFrom(
									xmlFilePath.lastSegment()));
			if (xmlFile != null)
				wc
						.setAttribute(
								XSLLaunchConfigurationConstants.ATTR_INPUT_FILE,
								"${workspace_loc:" + xmlFile.getFullPath().toPortableString() + "}"); //$NON-NLS-1$ //$NON-NLS-2$
			else
				wc.setAttribute(
						XSLLaunchConfigurationConstants.ATTR_INPUT_FILE,
						xmlFilePath.toPortableString());

			wc
					.setAttribute(
							XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE,
							true);
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_OPEN_FILE,
					true);

			LaunchPipeline pipeline = new LaunchPipeline();
			for (IFile element : xslFiles) {
				pipeline.addTransformDef(new LaunchTransform(element
						.getFullPath().toPortableString(),
						LaunchTransform.RESOURCE_TYPE));
			}
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_PIPELINE,
					pipeline.toXML());
			if (xmlFile != null)
				wc.setMappedResources(new IResource[] { xmlFile.getProject() });
			config = wc.doSave();
		} catch (CoreException exception) {
			MessageDialog.openError(getShell(), Messages.XSLLaunchShortcut_6,
					exception.getStatus().getMessage());
		}
		return config;
	}

	protected Shell getShell() {
		return XSLDebugUIPlugin.getActiveWorkbenchShell();
	}
}
