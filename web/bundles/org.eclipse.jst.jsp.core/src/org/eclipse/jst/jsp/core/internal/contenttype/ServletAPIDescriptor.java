/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
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
	public static ServletAPIDescriptor DEFAULT = new ServletAPIDescriptor("javax.servlet", 4);

	public ServletAPIDescriptor(String rootPackage, float apiVersion) {
		super();
		this.fRootPackage = rootPackage;
		this.fAPIversion = apiVersion;
	}

	String fRootPackage;
	float fAPIversion;

	public String getRootPackage() {
		return fRootPackage;
	}

	public void setRootPackage(String packageRoot) {
		fRootPackage = packageRoot;
	}

	public float getAPIversion() {
		return fAPIversion;
	}

	public void setAPIversion(float aPIversion) {
		fAPIversion = aPIversion;
	}
}
