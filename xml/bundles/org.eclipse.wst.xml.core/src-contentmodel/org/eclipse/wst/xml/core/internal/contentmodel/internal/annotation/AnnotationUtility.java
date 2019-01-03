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

import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.annotation.AnnotationMap;


/**
 * 
 */
public class AnnotationUtility {
	public static void loadAnnotationsForGrammar(String publicId, CMDocument cmDocument) {
		List annotationFiles = ContentModelManager.getInstance().getAnnotationFilesInfos(publicId);
		AnnotationMap map = (AnnotationMap) cmDocument.getProperty("annotationMap"); //$NON-NLS-1$
		if (map != null) {
			synchronized (annotationFiles) {
				for (Iterator i = annotationFiles.iterator(); i.hasNext();) {
					try {
						AnnotationFileInfo annotationFileInfo = (AnnotationFileInfo) i.next();
						AnnotationFileParser parser = new AnnotationFileParser();
						parser.parse(map, annotationFileInfo);
					}
					catch (Exception e) {
						Logger.logException(e);
					}
				}
			}
		}
	}
}
