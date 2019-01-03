/*******************************************************************************
 * Copyright (c) 2008, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.XMLNestedValidatorContext
 *                                           modified in order to process JSON Objects.                                               
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.validation;

import java.util.HashSet;

import org.eclipse.wst.json.core.internal.validation.core.NestedValidatorContext;

/**
 * JSONNestedValidatorContext is used to store state data needed during an JSON
 * validation session.
 */
public class JSONNestedValidatorContext extends NestedValidatorContext {
	/**
	 * A set of inaccessible locations URIs (String).
	 */
	private HashSet inaccessibleLocationURIs = new HashSet();

	/**
	 * Determines if a location URI was marked as inaccessible.
	 * 
	 * @param locationURI
	 *            the location URI to test. Must not be null.
	 * @return true if a location URI was marked as inaccessible, false
	 *         otherwise.
	 */
	public boolean isURIMarkedInaccessible(String locationURI) {
		return locationURI != null
				&& inaccessibleLocationURIs.contains(locationURI);
	}

	/**
	 * Marks the given location URI as inaccessible.
	 * 
	 * @param locationURI
	 *            the location URI to mark as inaccessible. Must not be null.
	 */
	public void markURIInaccessible(String locationURI) {
		if (locationURI != null) {
			inaccessibleLocationURIs.add(locationURI);
		}
	}
}
