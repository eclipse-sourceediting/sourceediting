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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.config.LaunchHelper;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class XSLLaunchShortcut implements ILaunchShortcut
{
	public void launch(ISelection selection, String mode)
	{
		if (selection instanceof IStructuredSelection)
		{
			searchAndLaunch(((IStructuredSelection) selection).toArray(), mode);
		}
	}

	public void launch(IEditorPart editor, String mode)
	{
		// don't think this route is possible
	}

	public void searchAndLaunch(Object[] search, String mode)
	{
		int index = findSourceXMLIndex(search);
		IFile inputXML = (IFile) search[index];
		IFile[] transforms = new IFile[search.length - 1];
		System.arraycopy(search, 0, transforms, 0, index);
		System.arraycopy(search, index + 1, transforms, index, transforms.length - index);
		launch(inputXML, transforms, mode);
	}

	private int findSourceXMLIndex(Object[] selection)
	{
		for (int i = 0; i < selection.length; i++)
		{
			IResource resource = (IResource) selection[i];
			if ("xml".equalsIgnoreCase(resource.getFileExtension()))
			{
				return i;
			}
		}
		return -1;
	}

	protected void launch(IFile sourceXML, IFile[] stylesheet, String mode)
	{
		ILaunchConfiguration config = findLaunchConfiguration(sourceXML, stylesheet, getConfigurationType());
		if (config != null)
		{
			DebugUITools.launch(config, mode);
		}
		else
		{
			// open the launch configuration dialog

		}
	}

	protected ILaunchManager getLaunchManager()
	{
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected ILaunchConfigurationType getConfigurationType()
	{
		return getLaunchManager().getLaunchConfigurationType(XSLLaunchConfigurationConstants.ID_LAUNCH_CONFIG_TYPE);
	}

	protected ILaunchConfiguration findLaunchConfiguration(IFile sourceXML, IFile[] stylesheets, ILaunchConfigurationType configType)
	{
		List<ILaunchConfiguration> candidateConfigs = Collections.emptyList();
		if (sourceXML != null)
		{
			try
			{
				ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
				candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
				for (ILaunchConfiguration config : configs)
				{
					String inputFile = config.getAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, (String) null);
					inputFile = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(inputFile);
					Path path = new Path(inputFile);
					// the source xml file must be the same
					if (path.equals(sourceXML.getLocation()))
					{
						LaunchHelper lh = new LaunchHelper(config);
						boolean found = true;
						// all the selected stylesheets must be in the pipeline
						for (IFile stylesheet : stylesheets)
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
			catch (CoreException e)
			{
				XSLDebugUIPlugin.log(e);
			}
		}

		// If there are no existing configs associated with the IType, create
		// one.
		// If there is exactly one config associated with the IType, return it.
		// Otherwise, if there is more than one config associated with the
		// IType, prompt the
		// user to choose one.
		int candidateCount = candidateConfigs.size();
		if (candidateCount == 1 && sourceXML != null && stylesheets.length > 0)
		{
			return (ILaunchConfiguration) candidateConfigs.get(0);
		}
		else if (candidateCount == 0 && sourceXML != null && stylesheets.length > 0)
		{
			return createConfiguration(sourceXML, stylesheets);
		}
		else if (candidateCount > 1)
		{
			// Prompt the user to choose a config. A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching
			// anything.
			ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
			if (config != null)
			{
				return config;
			}
		}
		return null;
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

	protected ILaunchConfiguration createConfiguration(IFile sourceXML, IFile[] stylesheets)
	{
		ILaunchConfiguration config = null;
		try
		{
			ILaunchConfigurationType configType = getConfigurationType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(sourceXML.getName()));
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, "${workspace_loc}" + sourceXML.getFullPath().toString());
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_FEATURES_FROM_PREFERENCES, true);
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE, true);
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR_WORKING_DIR, true);

			LaunchPipeline pipeline = new LaunchPipeline();
			for (IFile element : stylesheets)
			{
				pipeline.addTransformDef(new LaunchTransform(element.getFullPath().toPortableString(), LaunchTransform.RESOURCE_TYPE));
			}
			wc.setAttribute(XSLLaunchConfigurationConstants.ATTR_PIPELINE, pipeline.toXML());

			wc.setMappedResources(new IResource[]
			{ sourceXML.getProject() });
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
