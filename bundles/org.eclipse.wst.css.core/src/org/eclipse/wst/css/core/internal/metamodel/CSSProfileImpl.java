/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodel;



import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.metamodel.CSSMetaModel;
import org.eclipse.wst.css.core.metamodel.CSSProfile;
import org.osgi.framework.Bundle;



class CSSProfileImpl implements CSSProfile {

	/**
	 * Constructor for CSSMetaModelProfileInfoImpl.
	 */
	private CSSProfileImpl() {
		super();
	}

	/**
	 * Constructor for CSSMetaModelProfileInfoImpl.
	 */
	public CSSProfileImpl(String id, URL url) {
		super();
		fID = id;
		fURL = url;
	}

	/*
	 * @see CSSMetaModelProfileInfo#getProfileID()
	 */
	public String getProfileID() {
		return fID;
	}

	/*
	 * @see CSSMetaModelProfileInfo#getProfileName()
	 */
	public String getProfileName() {
		return (fName == null) ? fID : fName;
	}

	public CSSMetaModel getMetaModel() {
		if (fMetaModel == null) {
			try {
				InputStream input = getProfileURL().openStream();
				ProfileLoader loader = new ProfileLoader(getResourceBundle(), getLogging());
				fMetaModel = loader.loadProfile(input);
			}
			catch (IOException e) {
				Logger.logException("Cannot open stream for profile", e); //$NON-NLS-1$
			}
		}
		return fMetaModel;
	}

	private ResourceBundle getResourceBundle() {
		ClassLoader targetLoader = null;
		String pluginID = getOwnerPluginID();
		if (pluginID != null) {
			Bundle bundle = Platform.getBundle(pluginID);
			if (bundle != null) {
				targetLoader = bundle.getClass().getClassLoader();
			}
		}
		if (targetLoader == null) {
			targetLoader = this.getClass().getClassLoader();
		}

		String profileURLString = getProfileURL().toString();
		int lastSlashPos = profileURLString.lastIndexOf('/');
		if (1 < lastSlashPos) {
			String profileURLBase = profileURLString.substring(0, lastSlashPos + 1);
			try {
				URL[] urls = new URL[]{new URL(profileURLBase)};
				targetLoader = URLClassLoader.newInstance(urls, targetLoader);
			}
			catch (MalformedURLException e) {
			}
		}

		try {
			return ResourceBundle.getBundle("cssprofile", //$NON-NLS-1$
						Locale.getDefault(), targetLoader);
		}
		catch (MissingResourceException e) {
			return null;
		}
	}

	/*
	 * @see CSSMetaModelProfileInfo#getProfileURL()
	 */
	public URL getProfileURL() {
		return fURL;
	}

	void setProfileName(String name) {
		fName = name;
	}

	public boolean isDefault() {
		return fDefault;
	}

	void setDefault(boolean isDefault) {
		fDefault = isDefault;
	}

	public String getOwnerPluginID() {
		return fOwnerPluginID;
	}

	void setOwnerPluginID(String pluginID) {
		fOwnerPluginID = pluginID;
	}

	/**
	 * hidden option : logging
	 */
	void setLogging(boolean logging) {
		fLogging = logging;
	}

	boolean getLogging() {
		return fLogging;
	}


	String fID = null;
	String fName = null;
	URL fURL = null;
	CSSMetaModel fMetaModel = null;
	String fOwnerPluginID = null;
	boolean fDefault = false;
	boolean fLogging = false;
}