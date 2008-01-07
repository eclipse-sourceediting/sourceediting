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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.InputFileBlock;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;
import org.eclipse.wst.xsl.launching.config.LaunchHelper;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class XSLLaunchShortcut implements ILaunchShortcut
{
	private IFile xmlFile;
	private IFile[] xslFiles;
	
	public void launch(ISelection selection, String mode)
	{
		xmlFile = null;
		xslFiles = new IFile[0];
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection ssel = (IStructuredSelection)selection;
			fillFiles(ssel.toArray());
			launch(mode);
		}
	}

	public void launch(IEditorPart editor, String mode)
	{
		// don't think this route is possible
	}
	
	private void fillFiles(Object[] selections)
	{
		List<IFile> xslFileList = new ArrayList<IFile>();
		for (Object object : selections)
		{
			IResource resource = (IResource)object;
			if (resource.getType() == IResource.FILE)
			{
				IFile file = (IFile)resource;
				if (XSLTRuntime.isXMLFile(file))
				{
					if (XSLTRuntime.isXSLFile(file))
						xslFileList.add(file);
					else if (xmlFile!=null)
						xmlFile = file;
				}
			}
		}
		xslFiles = xslFileList.toArray(new IFile[0]);
	}

	private void launch(String mode)
	{
		ILaunchConfiguration config = null;
		try
		{
			config = findLaunchConfiguration();
		}
		catch (CoreException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		if (config != null)
		{
			DebugUITools.launch(config, mode);
		}
		else
		{
			// open the launch configuration dialog

		}
	}

	private ILaunchManager getLaunchManager()
	{
		return DebugPlugin.getDefault().getLaunchManager();
	}

	private ILaunchConfigurationType getConfigurationType()
	{
		return getLaunchManager().getLaunchConfigurationType(XSLLaunchConfigurationConstants.ID_LAUNCH_CONFIG_TYPE);
	}

	private ILaunchConfiguration findLaunchConfiguration() throws CoreException
	{
		ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(getConfigurationType());
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
		if (xmlFile!=null)
		{
			for (ILaunchConfiguration config : configs)
			{
				String inputFile = config.getAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, (String) null);
				inputFile = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(inputFile);
				Path path = new Path(inputFile);
				// the source xml file must be the same
				if (path.equals(xmlFile.getLocation()))
				{
					LaunchHelper lh = new LaunchHelper(config);
					boolean found = true;
					// all the selected stylesheets must be in the pipeline
					for (IFile stylesheet : xslFiles)
					{
						found = false;
						for (Iterator<?> iter = lh.getPipeline().getTransformDefs().iterator(); iter.hasNext();)
						{
							LaunchTransform lt = (LaunchTransform) iter.next();
							if (lt.getLocation().equals(stylesheet.getLocation()))
							{
								found = true;
								break;
							}
						}
						if (!found)
							break;
					}
					if (found)
					{
						candidateConfigs.add(config);
					}
				}
			}
		}

		ILaunchConfiguration config = null;
		int candidateCount = candidateConfigs.size();
		if (candidateCount == 1)
		{
			config = (ILaunchConfiguration) candidateConfigs.get(0);
		}
		else if (candidateCount > 1)
		{
			// Prompt the user to choose a config
			config = chooseConfiguration(candidateConfigs);
		}
		else
		{
			if (xmlFile == null)
			{
				final InputFileBlock inputFileBlock = new InputFileBlock(null);
				// TODO prompt for input xml file
				new StatusDialog(getShell()){
					
					@Override
					protected Control createDialogArea(Composite parent)
					{
						Composite comp = (Composite)super.createDialogArea(parent);
						GridLayout layout = new GridLayout(1, false);
						comp.setLayout(layout);
						inputFileBlock.createControl(comp);
						return comp;
					}
				};
				xmlFile = (IFile)inputFileBlock.getResource();
			}
			if (xmlFile != null)
				config = createConfiguration();
		}
		return config;
	}

	protected ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList)
	{
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("Select a Launch Configuration");
		dialog.setMessage("&Select existing configuration:");
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK)
		{
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	private ILaunchConfiguration createConfiguration()
	{
		ILaunchConfiguration config = null;
		try
		{
			ILaunchConfigurationType configType = getConfigurationType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(xmlFile.getName()));
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, "${workspace_loc}" + xmlFile.getFullPath().toString());
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_FEATURES_FROM_PREFERENCES, true);
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE, true);
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR_WORKING_DIR, true);

			LaunchPipeline pipeline = new LaunchPipeline();
			for (IFile element : xslFiles)
			{
				pipeline.addTransformDef(new LaunchTransform(element.getFullPath().toPortableString(), LaunchTransform.RESOURCE_TYPE));
			}
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_PIPELINE, pipeline.toXML());

			wc.setMappedResources(new IResource[]{ xmlFile.getProject() });
			config = wc.doSave();
		}
		catch (CoreException exception)
		{
			MessageDialog.openError(getShell(), "Error", exception.getStatus().getMessage());
		}
		return config;
	}

	protected Shell getShell()
	{
		return XSLDebugUIPlugin.getActiveWorkbenchShell();
	}
}
