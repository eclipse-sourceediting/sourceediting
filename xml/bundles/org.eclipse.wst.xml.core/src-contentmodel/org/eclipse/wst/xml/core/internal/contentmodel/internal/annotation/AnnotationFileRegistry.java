/*******************************************************************************
 * Copyright (c) 2002, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to associate one or more annotation files with a grammar
 * file.
 * 
 */
public class AnnotationFileRegistry {
	private Map map = new HashMap();

	public AnnotationFileRegistry() {
		new AnnotationFileRegistryReader(this).readRegistry();
	}
	
	public synchronized List getAnnotationFilesInfos(String publicId) {
		List theList = (List) map.get(publicId);
		return theList != null ? theList : new ArrayList();
	}
		
	public synchronized void addAnnotationFile(String publicId, AnnotationFileInfo fileInfo) {
		List fileInfos = (List) map.get(publicId);
		if (fileInfos == null) {
			fileInfos = new ArrayList();
			map.put(publicId, fileInfos);
		}
		synchronized (fileInfos) {
			fileInfos.add(fileInfo);
		}
	}

	public synchronized void removeAnnotationFile(String publicId, AnnotationFileInfo fileInfo) {
		List fileInfos = (List) map.get(publicId);
		if (fileInfos != null) {
			synchronized (fileInfos) {
				fileInfos.remove(fileInfo);
			}
		}
	}
}
