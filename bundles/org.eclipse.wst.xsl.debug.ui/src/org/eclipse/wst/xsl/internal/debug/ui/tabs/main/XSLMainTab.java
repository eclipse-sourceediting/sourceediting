/*******************************************************************************
 * Copyright (c) 2007,2008 Chase Technology Ltd - http://www.chasetechnology.co.uk and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - STAR - bug 223557 - Added Images contributed by Holger Voorman
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.tabs.main;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.debug.internal.util.XSLDebugPluginImages;
import org.eclipse.wst.xsl.debug.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.XSLLaunchConfigurationTab;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class XSLMainTab extends XSLLaunchConfigurationTab {
	private final InputFileBlock inputFileBlock;
	private final TransformsBlock transformsBlock;
	private final ParametersBlock parametersBlock;
	public LaunchPipeline pipeline;

	// private RenderBlock renderBlock;
	// private OutputBlock outputBlock;

	public XSLMainTab() {
		IResource[] resourceContext = getContext();
		IFile inputFile = getXMLInput(resourceContext);

		inputFileBlock = new InputFileBlock(inputFile);
		transformsBlock = new TransformsBlock();
		parametersBlock = new ParametersBlock(transformsBlock);
		// renderBlock = new RenderBlock();
		// outputBlock = new OutputBlock();

		setBlocks(new ILaunchConfigurationTab[] { inputFileBlock,
				transformsBlock, parametersBlock }); // ,renderBlock,outputBlock});
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite comp = (Composite) getControl();
		GridLayout layout = new GridLayout(1, false);
		comp.setLayout(layout);

		inputFileBlock.createControl(comp);
		transformsBlock.createControl(comp);
		parametersBlock.createControl(comp);
		// renderBlock.createControl(comp);
		// outputBlock.createControl(comp);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		LaunchPipeline lp = new LaunchPipeline();
		IResource[] resourceContext = getContext();
		IFile[] stylesheets = getXSLStylesheets(resourceContext);
		for (IFile file : stylesheets) {
			LaunchTransform lt = new LaunchTransform(file.getFullPath()
					.toPortableString(), LaunchTransform.RESOURCE_TYPE);
			lp.addTransformDef(lt);
		}
		savePipeline(configuration, lp);
		super.setDefaults(configuration);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		pipeline = null;
		try {
			String s = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_PIPELINE,
					(String) null);
			if (s != null && s.length() > 0) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(s
						.getBytes());
				pipeline = LaunchPipeline.fromXML(inputStream);
			} else {
				pipeline = new LaunchPipeline();
			}
		} catch (CoreException e) {
			XSLDebugUIPlugin.log(e);
		}
		transformsBlock.setPipeline(pipeline);
		super.initializeFrom(configuration);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		savePipeline(configuration, pipeline);
	}

	private void savePipeline(ILaunchConfigurationWorkingCopy configuration,
			LaunchPipeline pipeline) {
		try {
			configuration.setAttribute(
					XSLLaunchConfigurationConstants.ATTR_PIPELINE, pipeline
							.toXML());
		} catch (CoreException e) {
			XSLDebugUIPlugin.log(e);
		}
	}

	public String getName() {
		return Messages.XSLMainTab_TabName;
	}

	private static IResource[] getContext() {

		// IProject[] projects =
		// ResourcesPlugin.getWorkspace().getRoot().getProjects();
		// project.setInput( projects);
		// String s = configuration.getAttribute(
		// IXSLTLaunchConfiguration.PROJECT, "");
		// IProject project = null;
		// if( s.equals(""))
		// {
		// if( projects.length>0)
		// {
		// project = projects[ 0];
		// }
		// }
		// else
		// project = ResourcesPlugin.getWorkspace().getRoot().getProject( s);

		IWorkbenchPage page = XSLDebugUIPlugin.getActivePage();
		List<Object> resources = new ArrayList<Object>();
		if (page != null) {
			// use selections to find the project
			ISelection selection = page.getSelection();
			if (selection != null && !selection.isEmpty()
					&& selection instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) selection;
				for (Iterator<?> iter = ss.iterator(); iter.hasNext();) {
					Object element = iter.next();
					if (element instanceof IResource)
						resources.add(element);
				}
				return resources.toArray(new IResource[0]);
			}
			// use current editor to find the project
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				IEditorInput input = part.getEditorInput();
				IFile file = (IFile) input.getAdapter(IFile.class);
				if (file != null)
					return new IResource[] { file };
			}
		}
		return new IResource[0];
	}

	private IFile getXMLInput(IResource[] context) {
		for (IResource resource : context) {
			if (resource instanceof IFile
					&& ("xml".equalsIgnoreCase(resource.getFileExtension()) || "xhtml".equalsIgnoreCase(resource.getFileExtension()))) //$NON-NLS-1$ //$NON-NLS-2$
				return (IFile) resource;
		}
		return null;
	}

	private IFile[] getXSLStylesheets(IResource[] context) {
		List<IResource> stylesheets = new ArrayList<IResource>();
		for (IResource resource : context) {
			if (resource instanceof IFile
					&& XSLCore.isXSLFile((IFile) resource))
				stylesheets.add(resource);
		}
		return stylesheets.toArray(new IFile[0]);

	}

	@Override
	public Image getImage() {
		return XSLPluginImageHelper.getInstance().getImage(
				XSLDebugPluginImages.IMG_MAIN_TAB);
	}
}
