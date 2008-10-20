/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.preferences.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.validation.internal.ui.ValidateAction;
import org.eclipse.wst.validation.internal.ui.ValidationUIMessages;
import org.osgi.service.prefs.BackingStoreException;

public class HTMLValidationPreferencePage extends AbstractPreferencePage {
	
	private static final int[] SEVERITIES = {ValidationMessage.ERROR, ValidationMessage.WARNING, ValidationMessage.IGNORE};
	
	private List fCombos;
	private SelectionListener fSelectionListener;
	
	private IPreferencesService fPreferencesService = null;
	
	private IScopeContext[] fScopeContext = null;
	
	private class ComboData {
		private String fKey;
		private int[] fSeverities;
		private int fIndex;
		int originalSeverity = -2;
		
		public ComboData(String key, int[] severities, int index) {
			fKey = key;
			fSeverities = severities;
			fIndex = index;
		}
		
		public String getKey() {
			return fKey;
		}
		
		public void setIndex(int index) {
			fIndex = index;
		}
		
		public int getIndex() {
			return fIndex;
		}
		
		/**
		 * Sets the severity index based on <code>severity</code>.
		 * If the severity doesn't exist, the index is set to -1.
		 * 
		 * @param severity the severity level
		 */
		public void setSeverity(int severity) {
			for(int i = 0; fSeverities != null && i < fSeverities.length; i++) {
				if(fSeverities[i] == severity) {
					fIndex = i;
					return;
				}
			}
			
			fIndex = -1;
		}
		
		public int getSeverity() {
			return (fIndex >= 0 && fSeverities != null && fIndex < fSeverities.length) ? fSeverities[fIndex] : -1;
		}
		
		boolean isChanged() {
			return fSeverities[fIndex] != originalSeverity;
		}
	}
	
	public HTMLValidationPreferencePage() {
		super();
		fCombos = new ArrayList();
		fPreferencesService = Platform.getPreferencesService();
	}
	
	protected Control createContents(Composite parent) {
		final Composite page = new Composite(parent, SWT.NULL);
		
		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		page.setLayout(layout);
		
		final Composite content = createValidationSection(page);

		GridData gridData= new GridData(GridData.FILL, GridData.FILL, true, true);
		content.setLayoutData(gridData);
		
		return page;
	}
	
	private Composite createValidationSection(Composite page) {
		int nColumns = 3;
		new Label(page, SWT.NONE).setText(HTMLUIMessages.Validation_description);

		Group validationGroup = createGroup(page, nColumns);
		validationGroup.setText(HTMLUIMessages.Label_Elements);
		
		String[] errorWarningIgnoreLabel = new String[] { HTMLUIMessages.Validation_Error, HTMLUIMessages.Validation_Warning, HTMLUIMessages.Validation_Ignore };
		
		addComboBox(validationGroup, HTMLUIMessages.Validation_Missing_Start_Tag, HTMLCorePreferenceNames.ELEM_MISSING_START, SEVERITIES, errorWarningIgnoreLabel, 0);
		addComboBox(validationGroup, HTMLUIMessages.Validation_Missing_End_Tag, HTMLCorePreferenceNames.ELEM_MISSING_END, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		return page;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractSettingsPage#performOk()
	 */
	public boolean performOk() {
		if(super.performOk() && shouldRevalidateOnSettingsChange()) {
			MessageBox mb = new MessageBox(this.getShell(), SWT.APPLICATION_MODAL | SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_INFORMATION | SWT.RIGHT);
			mb.setText(HTMLUIMessages.Validation_Title);
			/* Choose which message to use based on if its project or workspace settings */
			mb.setMessage(HTMLUIMessages.Validation_Workspace);
			switch(mb.open()) {
				case SWT.CANCEL:
					return false;
				case SWT.YES:
					Job validationJob = new Job(ValidationUIMessages.RunValidationDialogTitle) {
					
						protected IStatus run(IProgressMonitor monitor) {
							ValidateAction vaction = new ValidateAction();
							vaction.selectionChanged(new StructuredSelection(ResourcesPlugin.getWorkspace().getRoot().getProjects()));
							vaction.run();
							return Status.OK_STATUS;
						}
					
					};
					validationJob.schedule();
				case SWT.NO:
				default:
					return true;
			}
		}
		return true;
	}
	
	protected boolean shouldRevalidateOnSettingsChange() {
		Iterator it = fCombos.iterator();

		while (it.hasNext()) {
			ComboData data = (ComboData) ((Combo) it.next()).getData();
			if (data.isChanged())
				return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		resetSeverities();
		super.performDefaults();
	}
	
	protected void resetSeverities() {
		IEclipsePreferences defaultContext = new DefaultScope().getNode(getPreferenceNodeQualifier());
		for(int i = 0; i < fCombos.size(); i++) {
			ComboData data = (ComboData)((Combo)fCombos.get(i)).getData();
			int severity = defaultContext.getInt(data.getKey(), ValidationMessage.WARNING);
			data.setSeverity(severity);
			((Combo)fCombos.get(i)).select(data.getIndex());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		super.dispose();
	}
	
	protected String getPreferenceNodeQualifier() {
		return HTMLCorePlugin.getDefault().getBundle().getSymbolicName();
	}
	
	protected Combo addComboBox(Composite parent, String label, String key, int[] values, String[] valueLabels, int indent) {
		GridData gd= new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);
		gd.horizontalIndent= indent;
				
		Label labelControl= new Label(parent, SWT.LEFT);
		labelControl.setFont(JFaceResources.getDialogFont());
		labelControl.setText(label);
		labelControl.setLayoutData(gd);
				
		Combo comboBox= newComboControl(parent, key, values, valueLabels);
		comboBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		return comboBox;
	}
	
	/**
	 * Creates a combo box and associates the combo data with the
	 * combo box.
	 * 
	 * @param composite the composite to create the combo box in
	 * @param key the unique key to identify the combo box
	 * @param values the values represented by the combo options
	 * @param valueLabels the values displayed in the combo box
	 * 
	 * @return the generated combo box
	 */
	protected Combo newComboControl(Composite composite, String key, int[] values, String[] valueLabels) {
		ComboData data = new ComboData(key, values, -1);
		
		Combo comboBox= new Combo(composite, SWT.READ_ONLY);
		comboBox.setItems(valueLabels);
		comboBox.setData(data);
		comboBox.addSelectionListener(getSelectionListener());
		comboBox.setFont(JFaceResources.getDialogFont());
			
		int severity = -1;
		if (key != null)
			severity = fPreferencesService.getInt(getPreferenceNodeQualifier(), key, ValidationMessage.WARNING, createPreferenceScopes());

		if (severity == ValidationMessage.ERROR || severity == ValidationMessage.WARNING || severity == ValidationMessage.IGNORE) {
			data.setSeverity(severity);
			data.originalSeverity = severity;
		}
		
		if(data.getIndex() >= 0)
			comboBox.select(data.getIndex());
		
		fCombos.add(comboBox);
		return comboBox;
	}
	
	protected SelectionListener getSelectionListener() {
		if (fSelectionListener == null) {
			fSelectionListener= new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
	
				public void widgetSelected(SelectionEvent e) {
					controlChanged(e.widget);
				}
			};
		}
		return fSelectionListener;
	}
	
	protected IScopeContext[] createPreferenceScopes() {
		if (fScopeContext == null)
			fScopeContext = new IScopeContext[]{new InstanceScope(), new DefaultScope()};
		return fScopeContext;
	}
	
	protected void controlChanged(Widget widget) {
		ComboData data= (ComboData) widget.getData();
		if (widget instanceof Combo) {
			data.setIndex(((Combo)widget).getSelectionIndex());
		} else {
			return;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractSettingsPage#storeValues()
	 */
	protected void storeValues() {
		if(fCombos == null || fCombos.size() == 0)
			return;
		
		Iterator it = fCombos.iterator();
		
		IScopeContext[] contexts = createPreferenceScopes();

		while (it.hasNext()) {
			ComboData data = (ComboData) ((Combo)it.next()).getData();
			if (data.getKey() != null)
				contexts[0].getNode(getPreferenceNodeQualifier()).putInt(data.getKey(), data.getSeverity());
		}
		
		for (int i = 0; i < contexts.length; i++) {
			try {
				contexts[i].getNode(getPreferenceNodeQualifier()).flush();
			}
			catch (BackingStoreException e) {
			}
		}
	}
}
