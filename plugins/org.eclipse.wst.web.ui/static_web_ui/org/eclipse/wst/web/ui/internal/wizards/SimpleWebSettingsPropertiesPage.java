/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.ui.internal.wizards;

import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.web.internal.ResourceHandler;
import org.eclipse.wst.web.internal.SimpleWebNatureRuntimeUtilities;
import org.eclipse.wst.web.internal.WebPropertiesUtil;
import org.eclipse.wst.web.internal.operation.IStaticWebNature;


public class SimpleWebSettingsPropertiesPage extends PropertyPage
{

	private Text contextRootField = null;
	private Text fWebContentNameField = null;
	private SimpleContextRootComposite staticContextRoot = null;
	private String oldContextRoot = null;
	private String fOldWebContentName = null;

	private Label projectTypeLabel = null;
	private PropertyChangeListener targetListener;

	private IProject fProject = null;

	public SimpleWebSettingsPropertiesPage()
	{
		super();
	}

	/**
	 * Creates and returns the SWT control for the customized body of this
	 * preference page under the given parent composite.
	 * <p>
	 * This framework method must be implemented by concrete subclasses.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the new control
	 */
	protected Control createContents(Composite parent)
	{
		Control retVal = null;

		fProject = getWebProject();
		if( fProject != null )
		{
			IStaticWebNature webNature = SimpleWebNatureRuntimeUtilities
					.getRuntime(fProject);

			if( webNature != null )
			{
				updateFields(webNature);

				// container specification group
				Composite containerGroup = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.numColumns = 2;
				containerGroup.setLayout(layout);
				containerGroup.setLayoutData(new GridData(
						GridData.HORIZONTAL_ALIGN_FILL
								| GridData.GRAB_HORIZONTAL));

				Listener listener = new Listener()
				{
					public void handleEvent(Event e)
					{
						validateFields();
					}
				};

				createStaticControls(containerGroup, listener);

				retVal = containerGroup;

			}
			else
			{
				Label closedProjectLabel = new Label(parent, SWT.NONE);
				closedProjectLabel
						.setText(ResourceHandler
								.getString("StaticWebSettingsPropertiesPage.Not_available_for_closed_projects")); //$NON-NLS-1$
				retVal = closedProjectLabel;
			}
		}
		return retVal;
	}

	/**
	 * The <code>PreferencePage</code> implementation of this
	 * <code>IDialogPage</code> method creates a description label and button
	 * bar for the page. It calls <code>createContents</code> to create the
	 * custom contents of the page. If the Web Project is closed, we need to
	 * avoid creating the buttons and the contents.
	 */
	public void createControl(Composite parent)
	{
		fProject = getWebProject();
		if( fProject != null && fProject.isOpen() )
		{
			super.createControl(parent);
		}
		else
		{
			Label closedProjectLabel = new Label(parent, SWT.NONE);
			closedProjectLabel
					.setText(ResourceHandler
							.getString("StaticWebSettingsPropertiesPage.Not_available_for_closed_projects")); //$NON-NLS-1$
			setControl(closedProjectLabel);
		}
	}

	protected void createStaticControls(Composite parent, Listener listener)
	{
		WorkbenchHelp.setHelp(parent, "com.ibm.etools.webtools.webp1100"); //$NON-NLS-1$

		IStaticWebNature webNature = (IStaticWebNature) SimpleWebNatureRuntimeUtilities
				.getRuntime(fProject);

		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.heightHint = 15;

		staticContextRoot = new SimpleContextRootComposite(parent);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		staticContextRoot.setLayoutData(data);
		staticContextRoot.setContextRoot(webNature.getContextRoot());
		staticContextRoot.addModifyListener(listener);

		// Create Web content folder name label
		Label webContentNameLabel = new Label(parent, SWT.CHECK);
		webContentNameLabel
				.setText(ResourceHandler
						.getString("StaticWebSettingsPropertiesPage.Web_Content_Label")); //$NON-NLS-1$

		// Create Web content folder name field
		fWebContentNameField = new Text(parent, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		fWebContentNameField.setLayoutData(data);
		fWebContentNameField.setText(fOldWebContentName);
		fWebContentNameField.addListener(SWT.Modify, listener);
	}

	public String getStaticContextRoot()
	{
		return (staticContextRoot != null) ? staticContextRoot.getContextRoot()
				: null;
	}

	public String getWebContentName()
	{
		return (fWebContentNameField != null) ? fWebContentNameField.getText()
				: null;
	}

	protected void validateFields()
	{
		String msg = WebPropertiesUtil
				.validateContextRoot(getStaticContextRoot());
/*		if( msg == null )
				msg = WebPropertiesUtil.validateWebContentName(
						getWebContentName(), fProject, null);
*/
		if( msg != null )
		{
			setValid(false);
			setErrorMessage(msg);
		}
		else
		{
			setValid(true);
			setErrorMessage(null);
		}
	}

	/**
	 * Returns the highlighted item in the workbench.
	 */
	private IProject getWebProject()
	{
		Object element = getElement();

		if( element instanceof IProject )
		{
			return (IProject) element;
		}

		return null;
	}

	private boolean hasStaticContextRootChanged()
	{
		if( oldContextRoot == null ) return true;
		return !oldContextRoot.equals(getStaticContextRoot());
	}

	private boolean hasWebContentNameChanged()
	{
		if( fOldWebContentName == null ) return true;
		return !fOldWebContentName.equals(getWebContentName());
	}

	/**
	 * Performs special processing when this page's Defaults button has been
	 * pressed.
	 * <p>
	 * This is a framework hook method for sublcasses to do special things when
	 * the Defaults button has been pressed. Subclasses may override, but should
	 * call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults()
	{
		super.performDefaults();

		if( contextRootField != null )
				contextRootField.setText(oldContextRoot);

		if( fWebContentNameField != null )
				fWebContentNameField.setText(fOldWebContentName);

		if( staticContextRoot != null )
				staticContextRoot.setContextRoot(oldContextRoot);
	}

	public boolean performOk()
	{
		boolean retVal = true;

		try
		{
			if( hasUpdatedStaticInformation() )
			{
				IStaticWebNature nature = SimpleWebNatureRuntimeUtilities
						.getRuntime(fProject);

				if( !oldContextRoot.equals(getStaticContextRoot()) )
						nature.setContextRoot(staticContextRoot
								.getContextRoot());

				if( !fOldWebContentName.equals(getWebContentName()) )
				{
					moveWebContentFolder(fProject, getWebContentName(),
							new NullProgressMonitor());
					nature.setModuleServerRootName(getWebContentName());
				}
			}
		}
		catch( CoreException e )
		{
			// TODO prolly want to do something better here
		}

		return retVal;
	}

	/**
	 * Moves the web content folder to the name indicated only if that path
	 * doesn't already exist in the project.
	 * 
	 * @param project
	 *            The web project to be updated.
	 * @param webContentName
	 *            The new web content name
	 * @param progressMonitor
	 *            Indicates progress
	 * @throws CoreException
	 *             The exception that occured during move operation
	 */
	public static void moveWebContentFolder(IProject project,
			String webContentName, IProgressMonitor progressMonitor)
			throws CoreException
	{
		IStaticWebNature webNature = SimpleWebNatureRuntimeUtilities
				.getRuntime(project);
		IPath newPath = new Path(webContentName);
		if( !project.exists(newPath) )
		{
			IContainer webContentRoot = webNature.getModuleServerRoot();
			webContentRoot.move(newPath, IResource.FORCE
					| IResource.KEEP_HISTORY, new SubProgressMonitor(
					progressMonitor, 1));
		}
	}

	// the old values need to be updated after apply has been hit, not just
	// when the page is first created
	private void updateFields(IStaticWebNature nature)
	{

		//		IBaseWebNature nature =
		// WebNatureRuntimeUtilities.getRuntime(fProject);
		oldContextRoot = nature.getContextRoot();
		fOldWebContentName = nature.getModuleServerRootName();
	}
	
	protected boolean hasUpdatedStaticInformation()
	{
		return hasStaticContextRootChanged() || hasWebContentNameChanged();
	}

}