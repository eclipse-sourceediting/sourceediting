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

package org.eclipse.wst.xml.tests.encoding;


public class GenerateFiles {

	public static final String getMainDirectoryBasedOnVMName() {
		String mainDirectory = "testfiles/genedFiles-" + getJavaVersion();
		return mainDirectory;
	}

	private static final String getJavaVersion() {
		String name = null; //System.getProperty("java.fullversion");
		if (name == null) {
			name = System.getProperty("java.version") + " (" + System.getProperty("java.runtime.version") + ")";
		}
		return name;
	}

}
