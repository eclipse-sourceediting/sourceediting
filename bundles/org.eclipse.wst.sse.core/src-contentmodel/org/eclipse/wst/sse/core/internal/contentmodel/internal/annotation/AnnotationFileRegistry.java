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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to associate one or more annotation files with a grammar file.
 *
 */
public class AnnotationFileRegistry
{
  protected Map map = new HashMap();

  public AnnotationFileRegistry()
  {
    new AnnotationFileRegistryReader(this).readRegistry();
  }

  public synchronized List getAnnotationFiles(String publicId)
  {
    Map fileTable = (Map) map.get(publicId);
    return fileTable != null ? new ArrayList(fileTable.values()) : new ArrayList();
  }

  public synchronized void addAnnotationFile(String publicId, String annotationFileURI)
  {
    Map fileTable = (Map) map.get(publicId);
    if (fileTable == null)
    {
      fileTable = new HashMap();
      map.put(publicId, fileTable);
    }
    fileTable.put(annotationFileURI, annotationFileURI);
  }

  public synchronized void removeAnnotationFile(String publicId, String annotationFileURI)
  {
    Map fileTable = (Map) map.get(publicId);
    if (fileTable != null)
    {
      fileTable.remove(annotationFileURI);
    }
  }
}
