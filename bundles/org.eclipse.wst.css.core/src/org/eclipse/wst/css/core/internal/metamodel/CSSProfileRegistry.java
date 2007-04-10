/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodel;



import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.wst.css.core.internal.metamodelimpl.RegistryReader;



public class CSSProfileRegistry {
	public synchronized static CSSProfileRegistry getInstance() {
		if (fInstance == null) {
			fInstance = new CSSProfileRegistry();
		}
		return fInstance;
	}

	public Iterator getProfiles() {
		return Collections.unmodifiableCollection(fProfiles.values()).iterator();
	}

	public CSSProfile getProfile(String profileID) {
		return (CSSProfile) fProfiles.get(profileID);
	}

	public CSSProfile getDefaultProfile() {
		Iterator i = getProfiles();
		while (i.hasNext()) {
			CSSProfile profile = (CSSProfile) i.next();
			if (profile.isDefault()) {
				return profile;
			}
		}
		return null;
	}

	/**
	 * Constructor for CSSProfileRegistry.
	 */
	private CSSProfileRegistry() {
		super();
		fProfiles = new HashMap();
		Iterator i = new RegistryReader().enumProfiles();
		while (i.hasNext()) {
			Object profile = i.next();
			if (profile instanceof CSSProfile) {
				fProfiles.put(((CSSProfile) profile).getProfileID(), profile);
			}
		}
	}


	private static CSSProfileRegistry fInstance = null;
	private Map fProfiles = null;
}
