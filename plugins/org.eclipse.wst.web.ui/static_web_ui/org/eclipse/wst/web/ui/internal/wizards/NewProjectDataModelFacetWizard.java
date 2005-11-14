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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
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

public abstract class NewProjectDataModelFacetWizard extends AddRemoveFacetsWizard implements INewWizard, IFacetProjectCreationDataModelProperties {

	protected IDataModel model = null;
	private final IFacetedProjectTemplate template;
	private IWizardPage firstPage;

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

		Set facetVersions = new HashSet();
		FacetDataModelMap map = (FacetDataModelMap) model.getProperty(FACET_DM_MAP);
		for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
			IDataModel model = (IDataModel) iterator.next();
			facetVersions.add(model.getProperty(IFacetDataModelProperties.FACET_VERSION));
		}
		this.facetsSelectionPage.setInitialSelection(facetVersions);


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
				return super.performFinish();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public Object getConfig(IProjectFacetVersion fv, Type type, String pjname) throws CoreException {
		FacetDataModelMap map = (FacetDataModelMap) model.getProperty(FACET_DM_MAP);
		IDataModel configDM = (IDataModel) map.get(fv.getProjectFacet().getId());
		if (configDM == null) {
			configDM = (IDataModel) fv.createActionConfig(type, pjname);
			map.add(configDM);
		}
		configDM.setProperty(IFacetDataModelProperties.FACET_VERSION, fv);
		return configDM;
	}

}
