/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation;

/**
 * Contains information about annotation files
 */
public class AnnotationFileInfo {
	private String fAnnotationFileLocation;
	private String fBundleId;

	public AnnotationFileInfo(String annotationFileLocation, String bundleId) {
		fAnnotationFileLocation = annotationFileLocation;
		fBundleId = bundleId;
	}

	/**
	 * Get the location of the annotation file as originally specified.
	 * 
	 * @return String
	 */
	public String getAnnotationFileLocation() {
		return fAnnotationFileLocation;
	}

	/**
	 * Get the bundle id where the annotation file is located.
	 * 
	 * @return String
	 */
	public String getBundleId() {
		return fBundleId;
	}
}
