/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.preferences.XMLCompletionProposalCategoriesConfiguration
 *                                           modified in order to process JSON Objects.          
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.sse.ui.preferences.AbstractCompletionProposalCategoriesConfiguration;

/**
 * <p>
 * The readable and writable completion proposal categories configuration for
 * the JSON content type
 * </p>
 */
public class JSONCompletionProposalCategoriesConfiguration extends
		AbstractCompletionProposalCategoriesConfiguration {

	/** the ID of the preference page where users can change the preferences */
	private static final String PREFERENCES_PAGE_ID = "org.eclipse.wst.sse.ui.preferences.json.contentassist"; //$NON-NLS-1$

	/**
	 * <p>
	 * Creates the configuration
	 * </p>
	 */
	public JSONCompletionProposalCategoriesConfiguration() {
		// nothing to do.
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return JSONUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public boolean hasAssociatedPropertiesPage() {
		return true;
	}

	@Override
	public String getPropertiesPageID() {
		return PREFERENCES_PAGE_ID;
	}

	@Override
	protected String getShouldNotDisplayOnDefaultPagePrefKey() {
		return JSONUIPreferenceNames.CONTENT_ASSIST_DO_NOT_DISPLAY_ON_DEFAULT_PAGE;
	}

	@Override
	protected String getShouldNotDisplayOnOwnPagePrefKey() {
		return JSONUIPreferenceNames.CONTENT_ASSIST_DO_NOT_DISPLAY_ON_OWN_PAGE;
	}

	@Override
	protected String getPageSortOrderPrefKey() {
		return JSONUIPreferenceNames.CONTENT_ASSIST_OWN_PAGE_SORT_ORDER;
	}

	@Override
	protected String getDefaultPageSortOrderPrefKey() {
		return JSONUIPreferenceNames.CONTENT_ASSIST_DEFAULT_PAGE_SORT_ORDER;
	}
}
