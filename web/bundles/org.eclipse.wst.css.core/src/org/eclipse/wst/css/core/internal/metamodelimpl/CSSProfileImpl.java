/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;



import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.internal.metamodel.CSSMetaModel;
import org.eclipse.wst.css.core.internal.metamodel.CSSProfile;
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
		this();
		fID = id;
		fURL = url;
	}

	/**
	 * Constructor for CSSMetaModelProfileInfoImpl.
	 */
	CSSProfileImpl(String id, URL url, String relativeURI) {
		super();
		fID = id;
		fURL = url;
		fRelativeURI = relativeURI;
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
		ResourceBundle resourceBundle = null;

		Bundle bundle = null;
		String pluginID = getOwnerPluginID();
		if (pluginID != null) {
			bundle = Platform.getBundle(pluginID);
			if (bundle != null) {
				// needed to work around FileLocator.openStream not looking for
				// files with Java naming conventions (BUG103345)
				IPath[] paths = getResourceBundlePaths();
				if (paths != null) {
					InputStream inStream = null;
					int i = 0;
					while (i < paths.length && inStream == null) {
						IPath path = paths[i];
						try {
							inStream = FileLocator.openStream(bundle, path, true);
							if (inStream != null)
								resourceBundle = new FallbackPropertyResourceBundle(inStream, i + 1 < paths.length ? paths[i + 1] : null);
							else
								++i;
						}
						catch (IOException e) {
							// unable to open stream with current path so just
							// try next path
							++i;
						}
						finally {
							if (inStream != null)
								try {
									inStream.close();
								}
								catch (IOException e) {
									Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
								}
						}
					}
				}
			}
		}
		return resourceBundle;
	}

	/**
	 * Returns an array of potential resource bundle paths or null if there
	 * are none
	 * 
	 * @return IPath[] or null
	 */
	private IPath[] getResourceBundlePaths() {
		IPath[] paths = new IPath[0];

		if (fRelativeURI != null) {
			// original path = location of profile.xml - profile.xml
			IPath originalPath = Path.fromOSString(fRelativeURI).removeLastSegments(1);

			String baseName = "cssprofile"; //$NON-NLS-1$ 
			Vector names = calculateBundleNames(baseName, Locale.getDefault());

			int a = 0;
			paths = new IPath[names.size()];
			for (int i = names.size(); i > 0; --i) {
				String bundleName = (String) names.get(i - 1);
				IPath path = originalPath.append(bundleName).addFileExtension("properties"); //$NON-NLS-1$
				paths[a] = path;
				++a;
			}
		}
		return paths;
	}

	/**
	 * Calculate the bundles along the search path from the base bundle to the
	 * bundle specified by baseName and locale.<br />
	 * 
	 * @param baseName
	 *            the base bundle name
	 * @param locale
	 *            the locale
	 * @param names
	 *            the vector used to return the names of the bundles along the
	 *            search path.
	 * 
	 */
	private Vector calculateBundleNames(String baseName, Locale locale) {
	    // this method can be deleted after BUG103345 is fixed
		final Vector result = new Vector(4); // default size 4
		final String language = locale.getLanguage();
		final int languageLength = language.length();
		final String country = locale.getCountry();
		final int countryLength = country.length();
		final String variant = locale.getVariant();
		final int variantLength = variant.length();

		result.addElement(baseName); // at least add base name
		if (languageLength + countryLength + variantLength == 0) {
			// The locale is "", "", "".
			return result;
		}
		final StringBuffer temp = new StringBuffer(baseName);
		temp.append('_');
		temp.append(language);
		if (languageLength > 0) {
			result.addElement(temp.toString());
		}

		if (countryLength + variantLength == 0) {
			return result;
		}
		temp.append('_');
		temp.append(country);
		if (countryLength > 0) {
			result.addElement(temp.toString());
		}

		if (variantLength == 0) {
			return result;
		}
		temp.append('_');
		temp.append(variant);
		result.addElement(temp.toString());

		return result;
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
	private String fRelativeURI = null;

	/**
	 * 
	 * A property resource bundle that can fall back to a different resource bundle
	 * in the case where there isn't a string for a key.
	 * 
	 */
	class FallbackPropertyResourceBundle extends PropertyResourceBundle {

		private PropertyResourceBundle fallback = null;
		private IPath fallbackPath;

		public FallbackPropertyResourceBundle(InputStream stream, IPath fallbackPath) throws IOException {
			super(stream);
			this.fallbackPath = fallbackPath;
		}

		/**
		 * Returns the property value for a specified key. Returns null if a property
		 * doesn't exist
		 * 
		 * @param key the key to look up
		 * @return the string property or null if it doesn't exist
		 */
		public String getProperty(String key) {
			String property = null;
			try {
				property = getString(key);
			}
			catch (MissingResourceException e) {
				if (fallback == null && fallbackPath != null) {
					final Bundle bundle = Platform.getBundle(getOwnerPluginID());
					if (bundle != null) {
						InputStream stream = null;
						try {
							stream = FileLocator.openStream(bundle, fallbackPath, true);
							if (stream != null) {
								fallback = new PropertyResourceBundle(stream);
							}
						}
						catch (IOException ioe) {
						}
						finally {
							if (stream != null) {
								try {
									stream.close();
								}
								catch (IOException ioe) {
								}
							}
						}
					}
				}
				if (fallback != null) {
					try {
						property = fallback.getString(key);
					}
					catch (MissingResourceException mre) {
					}
				}
			}
			return property;
		}
	}
}
