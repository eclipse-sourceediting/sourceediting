/*******************************************************************************
 * Copyright (c) 2002, 2007 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;



/**
 *
 */
public class CMDocumentCache
{                                     
  public static final int STATUS_NOT_LOADED = 0;
  public static final int STATUS_LOADING    = 2;
  public static final int STATUS_LOADED     = 3;
  public static final int STATUS_ERROR      = 4;

  protected class Entry
  {
    public String uri;
    public int status = STATUS_NOT_LOADED;
    public float progress;
    public CMDocument cmDocument;

    public Entry(String uri)
    {                      
      this.uri = uri;         
    }

    public Entry(String uri, int status, CMDocument cmDocument)
    {                                     
      this.uri = uri;
      this.status = status;      
      this.cmDocument = cmDocument;
    }
  }

  protected Hashtable hashtable;
  protected List listenerList = new Vector();


  /**
   * temporarily public until caching problem is solved
   */
  public CMDocumentCache()
  {
    hashtable = new Hashtable();
  }

  public void addListener(CMDocumentCacheListener listener)
  {
    listenerList.add(listener);
  }

  public void removeListener(CMDocumentCacheListener listener)
  {
    listenerList.remove(listener);
  }   

  /**
   *
   */
  public CMDocument getCMDocument(String grammarURI)
  {
    CMDocument result = null;
    if (grammarURI != null)
    {  
      Entry entry = (Entry)hashtable.get(grammarURI);
      if (entry != null)
      {
        result = entry.cmDocument;
      }   
    }
    return result;
  }    

  /**
   *
   */
  public int getStatus(String grammarURI)
  {
    int result = STATUS_NOT_LOADED;
    if (grammarURI != null)
    {  
      Entry entry = (Entry)hashtable.get(grammarURI);
      if (entry != null)
      {
        result = entry.status;
      }      
      
    }
    return result;
  }            
            
  /**
   *
   */
  protected Entry lookupOrCreate(String grammarURI)
  {
    Entry entry = (Entry)hashtable.get(grammarURI);
    if (entry == null)
    {
      entry = new Entry(grammarURI);                       
      hashtable.put(grammarURI, entry);
    }
    return entry;
  }

    
  /**
   *
   */
  public void putCMDocument(String grammarURI, CMDocument cmDocument)
  {                                    
    if (grammarURI != null && cmDocument != null)
    {                           
      Entry entry = lookupOrCreate(grammarURI);
      int oldStatus = entry.status;
      entry.status = STATUS_LOADED;
      entry.cmDocument = cmDocument;  
      notifyCacheUpdated(grammarURI, oldStatus, entry.status, entry.cmDocument);  
    }
  }
     
  /**
   *
   */
  public void setStatus(String grammarURI, int status)
  {
    if (grammarURI != null)
    {
      Entry entry = lookupOrCreate(grammarURI);
      int oldStatus = entry.status;
      entry.status = status;
      notifyCacheUpdated(grammarURI, oldStatus, entry.status, entry.cmDocument);   
    }
  }
     
  /**
   *
   */
  public void clear()
  {
    hashtable.clear();
    notifyCacheCleared();
  }  

  /**
   *
   */
  protected void notifyCacheUpdated(String uri, int oldStatus, int newStatus, CMDocument cmDocument)
  {      
    List list = new Vector();
    list.addAll(listenerList);
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      CMDocumentCacheListener listener = (CMDocumentCacheListener)i.next();
      listener.cacheUpdated(this, uri, oldStatus, newStatus, cmDocument);
    }
  }

  /**
   *
   */
  protected void notifyCacheCleared()
  {     
    List list = new Vector();
    list.addAll(listenerList);
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      CMDocumentCacheListener listener = (CMDocumentCacheListener)i.next();
      listener.cacheCleared(this);
    }
  }
  
  public List getCMDocuments()
  {
  	List list = new ArrayList();  	
  	for (Iterator i = hashtable.values().iterator(); i.hasNext(); )
  	{
  		Entry entry = (Entry)i.next();
  		list.add(entry.cmDocument);
  	}
  	return list;
  }
}
