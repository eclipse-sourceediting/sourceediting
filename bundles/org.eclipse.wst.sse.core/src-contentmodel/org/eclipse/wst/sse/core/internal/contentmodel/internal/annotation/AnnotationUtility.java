/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.internal.contentmodel.internal.annotation;

import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.sse.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.sse.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.sse.core.internal.contentmodel.annotation.AnnotationMap;


/**
 * 
 */
public class AnnotationUtility
{
  public static void loadAnnotationsForGrammar(String publicId, CMDocument cmDocument)
  {
    List annotationFiles = ContentModelManager.getInstance().getAnnotationFiles(publicId);
    AnnotationMap map = (AnnotationMap) cmDocument.getProperty("annotationMap");
    if (map != null)
    {
      for (Iterator i = annotationFiles.iterator(); i.hasNext();)
      {
        try
        {
          String annotationFileURI = (String) i.next();
          AnnotationFileParser parser = new AnnotationFileParser();
          parser.parse(map, annotationFileURI);
        }
        catch (Exception e)
        {
        }
      }
    }
  }
}
