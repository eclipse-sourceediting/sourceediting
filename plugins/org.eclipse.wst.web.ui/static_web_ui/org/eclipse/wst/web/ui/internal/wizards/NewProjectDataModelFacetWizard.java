/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.web.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.FacetProjectCreationOperation;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.AddRemoveFacetsWizard;
import org.eclipse.wst.common.project.facet.ui.internal.ConflictingFacetsFilter;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionPanel;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;
import org.eclipse.wst.web.ui.internal.WSTWebUIPlugin;

public abstract class NewProjectDataModelFacetWizard extends AddRemoveFacetsWizard implements INewWizard, IFacetProjectCreationDataModelProperties {

	protected IDataModel model = null;
	private final IFacetedProjectTemplate template;
	private IWizardPage firstPage;
	private IConfigurationElement configurationElement;

	public NewProjectDataModelFacetWizard(IDataModel model){
		super(null);
		this.model = model;
		template = getTemplate();
		this.setDefaultPageImageDescriptor(getDefaultPageImageDescriptor());
	}
	
	public NewProjectDataModelFacetWizard() {
		super(null);
		model = createDataModel();
		template = getTemplate();
		this.setDefaultPageImageDescriptor(getDefaultPageImageDescriptor());
	}
	
	public IDataModel getDataModel(){
		return model;
	}

	protected abstract IDataModel createDataModel();

	protected abstract ImageDescriptor getDefaultPageImageDescriptor();

	protected abstract IFacetedProjectTemplate getTemplate();

	protected abstract IWizardPage createFirstPage();

	public void addPages() {
		firstPage = createFirstPage();
		addPage(firstPage);

		super.addPages();
		final Set fixed = this.template.getFixedProjectFacets();

		this.facetsSelectionPage.setFixedProjectFacets(fixed);

        // Disabling this as it interfers with the facet selection based on the
        // runtime.
        
		/*Set facetVersions = new HashSet();
		FacetDataModelMap map = (FacetDataModelMap) model.getProperty(FACET_DM_MAP);
		for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
			IDataModel model = (IDataModel) iterator.next();
			facetVersions.add(model.getProperty(IFacetDataModelProperties.FACET_VERSION));
		}
		this.facetsSelectionPage.setInitialSelection(facetVersions);*/


		final ConflictingFacetsFilter filter = new ConflictingFacetsFilter(fixed);

		this.facetsSelectionPage.setFilters(new FacetsSelectionPanel.IFilter[]{filter});

		synchRuntimes();
	}

	public IWizardPage[] getPages() {
		final IWizardPage[] base = super.getPages();
		final IWizardPage[] pages = new IWizardPage[base.length + 1];

		pages[0] = this.firstPage;
		System.arraycopy(base, 0, pages, 1, base.length);

		return pages;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	protected void synchRuntimes() {
		model.addListener(new IDataModelListener() {
			public void propertyChanged(DataModelEvent event) {
				if (FACET_RUNTIME.equals(event.getPropertyName())) {
					setRuntime((IRuntime) event.getProperty());
				}
			}
		});

		addRuntimeListener(new Listener() {
			public void handleEvent(final Event event) {
				model.setProperty(FACET_RUNTIME, getRuntime());
			}
		});
	}

	public String getProjectName() {
		return model.getStringProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME);
	}

	public boolean performFinish() {
		IStatus status = model.validate();
		if (status.isOK()) {
			try {
				FacetProjectCreationOperation operation = new FacetProjectCreationOperation(model);
				this.fproj = operation.createProject(new NullProgressMonitor());
				boolean success =  super.performFinish();
				if(success){
					final Set fixed = this.template.getFixedProjectFacets();
			        this.fproj.setFixedProjectFacets( fixed );
				}
				return success;
				
			} catch (CoreException e) {
				e.printStackTrace();
			} finally {
				try {
					postPerformFinish();
				} catch (InvocationTargetException e) {
					Logger.getLogger().logError(e);
				}
			}
		}
		return false;
	}
	/**
	 * <p>
	 * Override to return the final perspective ID (if any). The final perspective ID can be
	 * hardcoded by the subclass or determined programmatically (possibly using the value of a field
	 * on the Wizard's WTP Operation Data Model).
	 * </p>
	 * <p>
	 * Default return value is <b>null </b>.
	 * </p>
	 * 
	 * @return Returns the ID of the Perspective which is preferred by this wizard upon completion.
	 */
	protected String getFinalPerspectiveID() {
		return "org.eclipse.jst.j2ee.J2EEPerspective";
	}
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The configuration element is saved to use when the wizard completes in order to change the
	 * current perspective using either (1) the value specified by {@link #getFinalPerspectiveID()}
	 * or (2) the value specified by the finalPerspective attribute in the Wizard's configuration
	 * element.
	 * </p>
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement aConfigurationElement, String aPropertyName, Object theData) throws CoreException {
		configurationElement = aConfigurationElement;
		doSetInitializeData(aConfigurationElement, aPropertyName, theData);

	}
	/**
	 * <p>
	 * Override method for clients that wish to take advantage of the information provided by
	 * {@see #setInitializationData(IConfigurationElement, String, Object)}.
	 * </p>
	 * 
	 * @param aConfigurationElement
	 *            The configuration element provided from the templated method.
	 * @param aPropertyName
	 *            The property name provided from the templated method.
	 * @param theData
	 *            The data provided from the templated method.
	 */
	protected void doSetInitializeData(IConfigurationElement aConfigurationElement, String aPropertyName, Object theData) {
		// Default do nothing
	}

	/**
	 * <p>
	 * Returns the an id component used for Activity filtering.
	 * </p>
	 * 
	 * <p>
	 * The Plugin ID is determined from the configuration element specified in
	 * {@see #setInitializationData(IConfigurationElement, String, Object)}.
	 * </p>
	 * 
	 * @return Returns the plugin id associated with this wizard
	 */
	public final String getPluginId() {
		return (configurationElement != null) ? configurationElement.getDeclaringExtension().getDeclaringPluginDescriptor().getUniqueIdentifier() : ""; //$NON-NLS-1$
	}
	/**
	 * 
	 * <p>
	 * Invoked after the user has clicked the "Finish" button of the wizard. The default
	 * implementation will attempt to update the final perspective to the value specified by
	 * {@link #getFinalPerspectiveID() }
	 * </p>
	 * 
	 * @throws InvocationTargetException
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.ui.wizard.WTPWizard#postPerformFinish()
	 */
	protected void postPerformFinish() throws InvocationTargetException {
		
		if (getFinalPerspectiveID() != null && getFinalPerspectiveID().length() > 0) {

			IConfigurationElement element = new DelegateConfigurationElement(configurationElement) {
				public String getAttribute(String aName) {
					if (aName.equals("finalPerspective")) { //$NON-NLS-1$
						return getFinalPerspectiveID();
					}
					return super.getAttribute(aName);
				}
			};
			BasicNewProjectResourceWizard.updatePerspective(element);
		} else
			BasicNewProjectResourceWizard.updatePerspective(configurationElement);
		String projName = getProjectName();
		BasicNewResourceWizard.selectAndReveal(ProjectUtilities.getProject(projName), WSTWebUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow());
	}

	public Object getConfig(IProjectFacetVersion fv, Type type, String pjname) throws CoreException {
		FacetDataModelMap map = (FacetDataModelMap) model.getProperty(FACET_DM_MAP);
		IDataModel configDM = (IDataModel) map.get(fv.getProjectFacet().getId());
		if (configDM == null) {
            final Object config = fv.createActionConfig(type,pjname);
            if( config == null || ! ( config instanceof IDataModel ) )
                return null;
			configDM = (IDataModel) config;
			map.add(configDM);
		}
		configDM.setProperty(IFacetDataModelProperties.FACET_VERSION, fv);
		return configDM;
	}

}
