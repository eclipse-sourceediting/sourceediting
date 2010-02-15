/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelPausibleOperationImpl;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;
import org.eclipse.wst.web.ui.internal.Logger;
import org.eclipse.wst.web.ui.internal.WSTWebUIPlugin;

public abstract class NewProjectDataModelFacetWizard extends ModifyFacetedProjectWizard implements INewWizard, IFacetProjectCreationDataModelProperties {

	protected IDataModel model = null;
	protected IFacetedProjectTemplate template;
	private IWizardPage[] beginingPages;
	private IConfigurationElement configurationElement;

	public NewProjectDataModelFacetWizard(IDataModel model) 
	{
		this.model = ( model == null ? createDataModel() : model );
		this.template = getTemplate();
		
		setFacetedProjectWorkingCopy((IFacetedProjectWorkingCopy)this.model.getProperty(FACETED_PROJECT_WORKING_COPY));
		getFacetedProjectWorkingCopy().setFixedProjectFacets( this.template.getFixedProjectFacets() );
		setDefaultPageImageDescriptor(getDefaultPageImageDescriptor());
		setShowFacetsSelectionPage( false );
	}
	
	public NewProjectDataModelFacetWizard() 
	{
	    this( null );
	}

	public IDataModel getDataModel() {
		return model;
	}

	protected abstract IDataModel createDataModel();

	protected abstract ImageDescriptor getDefaultPageImageDescriptor();

	protected abstract IFacetedProjectTemplate getTemplate();

	/**
	 * Returns the first page that shows up before the facets page. If multiple pages are required,
	 * also override {@link #createBeginingPages()}.
	 * 
	 * @return
	 */
	protected abstract IWizardPage createFirstPage();

	/**
	 * Subclasses should override to add more than one page before the facets page. If only one page
	 * is required, then use {@link #createFirstPage()}. The default implementation will return the
	 * result of {@link #createFirstPage()}.
	 * 
	 * @return
	 */
	protected IWizardPage[] createBeginingPages() {
		return new IWizardPage[]{createFirstPage()};
	}

	@Override
	public void addPages() {
		beginingPages = createBeginingPages();
		for (int i = 0; i < beginingPages.length; i++) {
			addPage(beginingPages[i]);
		}

		super.addPages();

        getFacetedProjectWorkingCopy().addListener
        (
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    facetSelectionChangedEvent();
                }
            },
            IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED
        );
    }

	@Override
	public void createPageControls(Composite container) {
		super.createPageControls(container);

        final IPreset preset = this.template.getInitialPreset();
        final IRuntime runtime = (IRuntime) model.getProperty( FACET_RUNTIME );

        if( preset == null )
        {
            // If no preset is specified, select the runtime and it's default
            // facets.
            
            setRuntimeAndDefaultFacets( runtime );
        }
        else
        {
            // If preset is specified, select the runtime only if supports all
            // of the facets included in the preset.

            getFacetedProjectWorkingCopy().setSelectedPreset( preset.getId() );
            
            boolean supports = false;
            
            if( runtime != null )
            {
                supports = true;
                
                for( Iterator itr = preset.getProjectFacets().iterator(); itr.hasNext(); )
                {
                    final IProjectFacetVersion fv = (IProjectFacetVersion) itr.next();
                    
                    if( ! runtime.supports( fv ) )
                    {
                        supports = false;
                        break;
                    }
                }
            }
            
            if( supports )
            {
                getFacetedProjectWorkingCopy().setTargetedRuntimes( Collections.singleton( runtime ) );
            }
            else
            {
                model.setProperty( FACET_RUNTIME, null );
            }
        }
        
        synchRuntimes();
	}

	@Override
	public IWizardPage[] getPages() {
		final IWizardPage[] base = super.getPages();
		final IWizardPage[] pages = new IWizardPage[base.length + beginingPages.length];

		for (int i = 0; i < beginingPages.length; i++) {
			pages[i] = beginingPages[i];
		}

		System.arraycopy(base, 0, pages, beginingPages.length, base.length);

		return pages;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	protected void synchRuntimes() 
    {
        final Boolean[] suppressBackEvents = { Boolean.FALSE };
        
		model.addListener(new IDataModelListener() {
			public void propertyChanged(DataModelEvent event) {
				if (IDataModel.VALUE_CHG == event.getFlag() || IDataModel.DEFAULT_CHG == event.getFlag()) {
					if (FACET_RUNTIME.equals(event.getPropertyName())) {
                        if( ! suppressBackEvents[ 0 ].booleanValue() ) {
                            IRuntime runtime = (IRuntime) event.getProperty();
                            setRuntimeAndDefaultFacets( runtime );
                        }
					}
				}
			}
		});

        getFacetedProjectWorkingCopy().addListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    suppressBackEvents[ 0 ] = Boolean.TRUE;
                    model.setProperty(FACET_RUNTIME, getFacetedProjectWorkingCopy().getPrimaryRuntime());
                    suppressBackEvents[ 0 ] = Boolean.FALSE;
                }
            },
            IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED
        );
	}
    
    protected void setRuntimeAndDefaultFacets( final IRuntime runtime )
    {
        final IFacetedProjectWorkingCopy dm = getFacetedProjectWorkingCopy();

        dm.setTargetedRuntimes( Collections.<IRuntime>emptySet() );

        if( runtime != null )
        {
            final Set<IProjectFacetVersion> minFacets = new HashSet<IProjectFacetVersion>();

            try
            {
                for( IProjectFacet f : dm.getFixedProjectFacets() )
                {
                    minFacets.add( f.getLatestSupportedVersion( runtime ) );
                }
            }
            catch( CoreException e )
            {
                throw new RuntimeException( e );
            }
            
            dm.setProjectFacets( minFacets );
            
            dm.setTargetedRuntimes( Collections.singleton( runtime ) );
        }
        
        dm.setSelectedPreset( FacetedProjectFramework.DEFAULT_CONFIGURATION_PRESET_ID );
    }
    
	public String getProjectName() {
		return model.getStringProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME);
	}

	@Override
	protected void performFinish(final IProgressMonitor monitor)

	throws CoreException

	{
		monitor.beginTask("", 10); //$NON-NLS-1$
		storeDefaultSettings();
		try {
			super.performFinish(new SubProgressMonitor(monitor, 8));

            try {
                getFacetProjectNotificationOperation().execute(new NullProgressMonitor(), null);
            } catch (ExecutionException e) {
                String msg = e.getMessage();
                if( msg == null ) msg = ""; //$NON-NLS-1$
                final IStatus st = new Status( IStatus.ERROR, WSTWebUIPlugin.PLUGIN_ID, 0, msg, e );
                throw new CoreException( st );
            }
        } finally {
			monitor.done();
		}
	}

	@Override
	public boolean performFinish() {
		if (super.performFinish() == false) {
			return false;
		}

		try {
			postPerformFinish();
		} catch (InvocationTargetException e) {
			Logger.logException(e);
		}

		return true;
	}

	/**
	 * <p>
	 * Override to return the final perspective ID (if any). The final perspective ID can be
	 * hardcoded by the subclass or determined programmatically (possibly using the value of a field
	 * on the Wizard's WTP Operation Data Model).
	 * </p>
	 * <p>
	 * The default implementation returns no perspective id unless overriden by product definition
	 * via the "wtp.project.final.perspective" property.
	 * </p>
	 * 
	 * @return Returns the ID of the Perspective which is preferred by this wizard upon completion.
	 */

	protected String getFinalPerspectiveID() {
		return null;
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
		return (configurationElement != null) ? configurationElement.getDeclaringExtension().getNamespace() : ""; //$NON-NLS-1$
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
		String projName = getProjectName();
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		IWorkbench workbench = WSTWebUIPlugin.getDefault().getWorkbench();
		
		// add to the selected working sets
		if (newProject != null && 
				beginingPages != null && 
				beginingPages.length > 0 && 
				beginingPages[0] instanceof DataModelFacetCreationWizardPage) {
			DataModelFacetCreationWizardPage mainPage = (DataModelFacetCreationWizardPage) beginingPages[0];
			IWorkingSet[] workingSets = mainPage.getSelectedWorkingSets();
			workbench.getWorkingSetManager().addToWorkingSets(newProject, workingSets);
		}
		
		// open the "final" perspective
		if (getFinalPerspectiveID() != null && getFinalPerspectiveID().length() > 0) {
			final IConfigurationElement element = new DelegateConfigurationElement(configurationElement) {
				@Override
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

		// select and reveal
		BasicNewResourceWizard.selectAndReveal(newProject, workbench.getActiveWorkbenchWindow());
	}

	protected IDataModelOperation getFacetProjectNotificationOperation() {
		return new DataModelPausibleOperationImpl(new AbstractDataModelOperation(this.model) {
			@Override
			public String getID() {
				return NewProjectDataModelFacetWizard.class.getName();
			}

			@Override
			public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
				return AbstractDataModelProvider.OK_STATUS;
			}
		});
	}

	/**
	 * Need to keep the model in sync with the UI. This method will pickup changes coming from the
	 * UI and push them into the model
	 */
	protected void facetSelectionChangedEvent() {
	    Set actions = getFacetedProjectWorkingCopy().getProjectFacetActions();
		Iterator iterator = actions.iterator();
		Set activeIds = new HashSet();
		while (iterator.hasNext()) {
			IFacetedProject.Action action = (IFacetedProject.Action) iterator.next();
			String id = action.getProjectFacetVersion().getProjectFacet().getId();
			activeIds.add(id);
		}
		// First handle all the actions tracked by IDataModels
		FacetDataModelMap dataModelMap = (FacetDataModelMap) model.getProperty(FACET_DM_MAP);
		iterator = dataModelMap.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			IDataModel configDM = (IDataModel) dataModelMap.get(id);
			boolean active = activeIds.contains(id);
			configDM.setBooleanProperty(IFacetDataModelProperties.SHOULD_EXECUTE, active);
			activeIds.remove(id);
		}
		// Now handle the actions not tracked by IDataModels
		FacetActionMap actionMap = (FacetActionMap) model.getProperty(FACET_ACTION_MAP);
		actionMap.clear();
		iterator = actions.iterator();
		while (iterator.hasNext()) {
			IFacetedProject.Action action = (IFacetedProject.Action) iterator.next();
			String id = action.getProjectFacetVersion().getProjectFacet().getId();
			if (activeIds.contains(id)) {
				actionMap.add(action);
			}
		}
		model.notifyPropertyChange(FACET_RUNTIME, IDataModel.VALID_VALUES_CHG);
	}

	protected void storeDefaultSettings() {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++)
			storeDefaultSettings(pages[i], i);
	}

	/**
	 * Subclasses may override if they need to do something special when storing the default
	 * settings for a particular page.
	 * 
	 * @param page
	 * @param pageIndex
	 */
	protected void storeDefaultSettings(IWizardPage page, int pageIndex) {
		if (page instanceof DataModelWizardPage)
			((DataModelWizardPage) page).storeDefaultSettings();
	}

	@Override
	public void dispose() {
		if(this.model != null){
			this.model.dispose();
		}
		super.dispose();
	}
	
}
