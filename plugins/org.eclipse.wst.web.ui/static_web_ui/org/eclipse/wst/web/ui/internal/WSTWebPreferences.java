/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/

package org.eclipse.wst.web.ui.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;

public class WSTWebPreferences {
	public interface Keys {
		final static String STATIC_WEB_CONTENT = "org.eclipse.jst.j2ee.preference.staticWebContentName"; //$NON-NLS-1$
	}
	public interface Values {
		final static String STATIC_WEB_CONTENT = "WebContent"; //$NON-NLS-1$
	}
	public interface Defaults {
		final static String STATIC_WEB_CONTENT = Values.STATIC_WEB_CONTENT;
	}
	private Plugin owner = null;
	private Preferences preferences = null;
	private boolean persistOnChange = false;

	public WSTWebPreferences(Plugin owner) {
		this.owner = owner;
	}
	protected void initializeDefaultPreferences() {
		getPreferences().setDefault(Keys.STATIC_WEB_CONTENT, Defaults.STATIC_WEB_CONTENT);
	}

	public String getStaticWebContentFolderName() {
		return getPreferences().getString(Keys.STATIC_WEB_CONTENT);
	}

	public void setStaticWebContentFolderName(String value) {
		getPreferences().setValue(Keys.STATIC_WEB_CONTENT, value);
		firePreferenceChanged();
	}

	public void firePreferenceChanged() {
		if (isPersistOnChange())
			persist();
	}

	public void persist() {
		getOwner().savePluginPreferences();
	}

	/**
	 * @return Returns the persistOnChange.
	 */
	public boolean isPersistOnChange() {
		return this.persistOnChange;
	}

	/**
	 * @param persistOnChange
	 *            The persistOnChange to set.
	 */
	public void setPersistOnChange(boolean persistOnChange) {
		this.persistOnChange = persistOnChange;
	}

	private Preferences getPreferences() {
		if (this.preferences == null)
			this.preferences = getOwner().getPluginPreferences();
		return this.preferences;
	}

	/**
	 * @return Returns the owner.
	 */
	private Plugin getOwner() {
		return this.owner;
	}
}
