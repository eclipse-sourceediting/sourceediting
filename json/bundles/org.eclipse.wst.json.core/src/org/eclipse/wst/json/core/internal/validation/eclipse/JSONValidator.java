/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.eclipse.XMLValidator
 *                                           modified in order to process JSON Objects.                                     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.validation.eclipse;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;

public class JSONValidator extends
		org.eclipse.wst.json.core.internal.validation.JSONValidator {

	private static JSONValidator instance = null;

	/**
	 * Return the one and only instance of the JSON validator. The validator can
	 * be reused and cannot be customized so there should only be one instance
	 * of it.
	 * 
	 * @return The one and only instance of the JSON validator.
	 */
	public static JSONValidator getInstance() {
		if (instance == null) {
			instance = new JSONValidator();
		}
		return instance;
	}

	/**
	 * Constructor. Create the JSON validator, set the URI resolver and get the
	 * extension error customizers from the registry.
	 */
	protected JSONValidator() {
		setURIResolver(URIResolverPlugin.createResolver());
		// new ErrorCustomizationPluginRegistryReader().readRegistry();
	}

}
