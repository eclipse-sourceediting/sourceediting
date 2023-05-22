/*******************************************************************************
 * Copyright (c) 2020, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contenttype;

/**
 * Represents the root package (javax vs. jakarta) and API version for a
 * project. Typically this will have been discovered against actual libraries.
 */
public class ServletAPIDescriptor {
	public enum ORIGIN {
		/**
		 * This descriptor is based on the types and methods found on the Java Build Path
		 **/
		BUILD_PATH,
		/**
		 * This descriptor is based on the project's Dynamic Web Module facet
		 **/
		FACET,
		/**
		 * This descriptor is based on the project's Web Fragment Module facet
		 **/
		FFACET,
		/**
		 * This descriptor is merely a set of defaults.
		 **/
		DEFAULT
	}
	public static final ServletAPIDescriptor DEFAULT = new ServletAPIDescriptor("jakarta.servlet", 6, ORIGIN.DEFAULT);
	private ORIGIN fOrigin;

	String fRootPackage;

	float fAPIversion;
	public ServletAPIDescriptor(String rootPackage, float apiVersion, ORIGIN origin) {
		super();
		this.fRootPackage = rootPackage;
		this.fAPIversion = apiVersion;
		this.fOrigin = origin;
	}

	public float getAPIversion() {
		return fAPIversion;
	}

	public ORIGIN getOrigin() {
		return fOrigin;
	}

	public String getRootPackage() {
		return fRootPackage;
	}

	public void setAPIversion(float aPIversion) {
		fAPIversion = aPIversion;
	}

	public void setOrigin(ORIGIN origin) {
		fOrigin = origin;
	}

	public void setRootPackage(String packageRoot) {
		fRootPackage = packageRoot;
	}
}
