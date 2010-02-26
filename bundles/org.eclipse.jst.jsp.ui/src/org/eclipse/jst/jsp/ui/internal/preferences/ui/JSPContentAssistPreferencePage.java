/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.preferences.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.contentassist.CompletionProposoalCatigoriesConfigurationRegistry;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.sse.ui.preferences.CodeAssistCyclingConfigurationBlock;
import org.eclipse.wst.sse.ui.preferences.ICompletionProposalCategoriesConfigurationWriter;

/**
 * <p>Defines the preference page for allowing the user to change the content
 * assist preferences</p>
 */
public class JSPContentAssistPreferencePage extends AbstractPreferencePage implements
		IWorkbenchPreferencePage {

	private static final String JSP_CONTENT_TYPE_ID = "org.eclipse.jst.jsp.core.jspsource"; //$NON-NLS-1$
	
	/** configuration block for changing preference having to do with the content assist categories */
	private CodeAssistCyclingConfigurationBlock fConfigurationBlock;
	
	/**
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		final Composite composite = super.createComposite(parent, 1);
		
		createContentsForCyclingGroup(composite);
		
		setSize(composite);
		loadPreferences();
		
		return composite;
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		performDefaultsForCyclingGroup();

		validateValues();
		enableValues();

		super.performDefaults();
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage#initializeValues()
	 */
	protected void initializeValues() {
		initializeValuesForCyclingGroup();
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage#storeValues()
	 */
	protected void storeValues() {
		storeValuesForCyclingGroup();
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * <p>Create the contents for the content assist cycling preference group</p>
	 * @param parent {@link Composite} parent of the group
	 */
	private void createContentsForCyclingGroup(Composite parent) {
		ICompletionProposalCategoriesConfigurationWriter configurationWriter = CompletionProposoalCatigoriesConfigurationRegistry.getDefault().getWritableConfiguration(JSP_CONTENT_TYPE_ID);
		
		if(configurationWriter != null) {
			fConfigurationBlock = new CodeAssistCyclingConfigurationBlock(JSP_CONTENT_TYPE_ID, configurationWriter);
			fConfigurationBlock.createContents(parent, null);
		} else {
			Logger.log(Logger.ERROR, "There should be an ICompletionProposalCategoriesConfigurationWriter" + //$NON-NLS-1$
					" specified for the JSP content type, but can't fine it, thus can't create user" + //$NON-NLS-1$
					" preference block for editing proposal categories preferences."); //$NON-NLS-1$
		}
	}
	
	/**
	 * <p>Store the values for the cycling group</p>
	 */
	private void storeValuesForCyclingGroup() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.storeValues();
		}
	}
	
	/**
	 * <p>Initialize the values for the cycling group</p>
	 */
	private void initializeValuesForCyclingGroup() {
		if(fConfigurationBlock != null) {
			fConfigurationBlock.initializeValues();
		}
	}
	
	/**
	 * <p>Load the defaults of the cycling group</p>
	 */
	private void performDefaultsForCyclingGroup() {
		if(fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
	}
}
