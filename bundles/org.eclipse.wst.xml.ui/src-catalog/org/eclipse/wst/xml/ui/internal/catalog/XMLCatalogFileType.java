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
package org.eclipse.wst.xml.ui.internal.catalog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Image;


public class XMLCatalogFileType 
{
  public String description;
  public List extensions = new ArrayList();
  public String iconFileName;
  public Image icon; 
    
  public void addExtensions(String contributedExtensions)
  {
	List list = parseExtensions(contributedExtensions);
	for (Iterator i = list.iterator(); i.hasNext(); )
	{
	  String extension = (String)i.next();
	  if (!extensions.contains(extension))
	  {
		extensions.add(extension);
	  }
	}
  }  
  
  protected List parseExtensions(String string)
  {
	List list = new ArrayList();
	for (StringTokenizer st = new StringTokenizer(string, ", "); st.hasMoreTokens(); )
	{
	  String token = st.nextToken();
	  if (token != null)
	  {          	
		list.add(token); 
	  }        	  	 
	}  
	return list;
  } 
}
