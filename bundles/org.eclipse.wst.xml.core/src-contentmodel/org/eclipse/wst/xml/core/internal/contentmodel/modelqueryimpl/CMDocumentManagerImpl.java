/*******************************************************************************
 * Copyright (c) 2002, 2010 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl;
                          
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCoreMessages;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.annotation.AnnotationUtility;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManagerListener;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentReferenceProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.GlobalCMDocumentCache.GlobalCacheQueryResponse;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;

import com.ibm.icu.text.MessageFormat;

/**
 *
 */
public class CMDocumentManagerImpl implements CMDocumentManager
{
  protected CMDocumentCache cmDocumentCache;
  protected CMDocumentReferenceProvider cmDocumentReferenceProvider;
  protected List listenerList = new Vector(); 
  protected Hashtable propertyTable = new Hashtable();
  protected Hashtable publicIdTable = new Hashtable();
  private boolean globalCMDocumentCacheEnabled ;

       
  public CMDocumentManagerImpl(CMDocumentCache cmDocumentCache, CMDocumentReferenceProvider cmDocumentReferenceProvider)
  {
    this.cmDocumentCache = cmDocumentCache;                                                                            
    this.cmDocumentReferenceProvider = cmDocumentReferenceProvider;
    setPropertyEnabled(PROPERTY_AUTO_LOAD, true);
    setPropertyEnabled(PROPERTY_USE_CACHED_RESOLVED_URI, false);
    setPropertyEnabled(PROPERTY_PERFORM_URI_RESOLUTION, true);
    initializeGlobalCMDocumentCacheSettings();
  }         

       
  public CMDocumentCache getCMDocumentCache()
  {
    return cmDocumentCache;
  }

 
  public void setPropertyEnabled(String propertyName, boolean value)
  {
    propertyTable.put(propertyName, value ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
    for (Iterator i = listenerList.iterator(); i.hasNext(); )
    {
      CMDocumentManagerListener listener = (CMDocumentManagerListener)i.next();
      listener.propertyChanged(this, propertyName);
    }
  }                                        

        
  public boolean getPropertyEnabled(String propertyName)
  {
    Object object = propertyTable.get(propertyName);
    return object != null && object.equals("true"); //$NON-NLS-1$
  }


  public void addListener(CMDocumentManagerListener listener)
  {
    listenerList.add(listener);
    cmDocumentCache.addListener(listener);
  }


  public void removeListener(CMDocumentManagerListener listener)
  {
    listenerList.remove(listener);
    cmDocumentCache.removeListener(listener);
  }                       
   
                   
  protected String lookupResolvedURI(String publicId)
  {
    String key = publicId != null ? publicId : ""; //$NON-NLS-1$
    return (String)publicIdTable.get(key);
  }
    

  protected String lookupOrCreateResolvedURI(String publicId, String systemId)
  {                    
    String resolvedURI = null;                  

    String key = publicId != null ? publicId : ""; //$NON-NLS-1$

    if (getPropertyEnabled(PROPERTY_USE_CACHED_RESOLVED_URI))
    {
      resolvedURI = (String)publicIdTable.get(key);
    }   

    if (resolvedURI == null)
    {
      resolvedURI = cmDocumentReferenceProvider.resolveGrammarURI(publicId, systemId);
      if (resolvedURI == null)
      {
        resolvedURI = ""; //$NON-NLS-1$
      }
      publicIdTable.put(key, resolvedURI);     
    }                       
  
    return resolvedURI;
  }


  public int getCMDocumentStatus(String publicId)
  {                                           
    int status = CMDocumentCache.STATUS_NOT_LOADED; 
    String resolvedURI = lookupResolvedURI(publicId);
    if (resolvedURI != null)
    {                      
      status = cmDocumentCache.getStatus(resolvedURI);
    }
    return status;
  }    
              

  public CMDocument getCMDocument(String publicId)
  {                                           
    CMDocument result = null;
    String resolvedURI = lookupResolvedURI(publicId);
    if (resolvedURI != null)
    {                      
      result = cmDocumentCache.getCMDocument(resolvedURI);
    }
    return result;
  }    
  

  /* (non-Javadoc)
 * @see org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager#getCMDocument(java.lang.String, java.lang.String, java.lang.String)
 */
public CMDocument getCMDocument(String publicId, String systemId, String type)
  {                
    CMDocument cmDocument = null;                           
    String resolvedURI = null;

    if (getPropertyEnabled(PROPERTY_AUTO_LOAD))
    {
      // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=136399
      
      if (getPropertyEnabled(PROPERTY_PERFORM_URI_RESOLUTION))
      {
        resolvedURI = lookupOrCreateResolvedURI(publicId, systemId);
      }
      else
      {
        resolvedURI = systemId;
      }
    }    
    else
    {
      resolvedURI = lookupResolvedURI(publicId);
    } 

    if (resolvedURI != null)
    {                   
      int status = cmDocumentCache.getStatus(resolvedURI);
      if (status == CMDocumentCache.STATUS_LOADED)
      {                      
        cmDocument = cmDocumentCache.getCMDocument(resolvedURI);
      }   
      else if (status == CMDocumentCache.STATUS_NOT_LOADED)
      {     
        if (getPropertyEnabled(PROPERTY_AUTO_LOAD))
        {
          cmDocument = loadCMDocument(publicId, resolvedURI, type, getPropertyEnabled(PROPERTY_ASYNC_LOAD));
        }
      }
    }
    return cmDocument;   
  } 
  
  public void addCMDocumentReference(String publicId, String systemId, String type)
  {
    String resolvedURI = lookupOrCreateResolvedURI(publicId, systemId);
    if (resolvedURI != null && resolvedURI.length() > 0)
    {                                                                      
      int status = cmDocumentCache.getStatus(resolvedURI);
      if (status == CMDocumentCache.STATUS_NOT_LOADED)
      {                      
        loadCMDocument(publicId, resolvedURI, type, getPropertyEnabled(PROPERTY_ASYNC_LOAD));
      }         
    } 
  }
     

  public void addCMDocument(String publicId, String systemId, String resolvedURI, String type, CMDocument cmDocument)
  {
    String key = publicId != null ? publicId : ""; //$NON-NLS-1$
    publicIdTable.put(key, resolvedURI);
    cmDocumentCache.putCMDocument(resolvedURI, cmDocument);
  }


  protected CMDocument loadCMDocument(final String publicId, final String resolvedURI, final String type, boolean async)
  {                                                      
    CMDocument result = null;
                         
    //System.out.println("about to build CMDocument(" + publicId + ", " + unresolvedURI + " = " + resolvedURI + ")");
    if (async)
    {     
      cmDocumentCache.setStatus(resolvedURI, CMDocumentCache.STATUS_LOADING);
      //Thread thread = new Thread(new AsyncBuildOperation(publicId, resolvedURI, type));
      //thread.start();
      Job job = new Job(XMLCoreMessages.loading + resolvedURI)
      {
        public boolean belongsTo(Object family)
        {
          boolean result = (family == CMDocumentManager.class);
          return result;
        }
        
        protected IStatus run(IProgressMonitor monitor)
        {
          try
          {
        	  buildCMDocument(publicId, resolvedURI, type);
          }
          catch (Exception e)
          {
        	  Logger.logException(MessageFormat.format(XMLCoreMessages.CMDocument_load_exception, new Object[]{resolvedURI, publicId}), e);
          }
          return Status.OK_STATUS;
        }
      };
      job.schedule();
    }
    else
    {                
      result = buildCMDocument(publicId, resolvedURI, type);
    }          
    return result;
  } 

    

  public synchronized CMDocument buildCMDocument(String publicId, String resolvedURI, String type)
  {                                     
    cmDocumentCache.setStatus(resolvedURI, CMDocumentCache.STATUS_LOADING);
    boolean documentCacheable = false;
    if(globalCMDocumentCacheEnabled) {
    	GlobalCacheQueryResponse response = GlobalCMDocumentCache.getInstance().getCMDocument(resolvedURI);
    	CMDocument cachedCMDocument = response.getCachedCMDocument();
    	documentCacheable = response.isDocumentCacheable();
    	if(cachedCMDocument != null) {
    		cmDocumentCache.putCMDocument(resolvedURI, cachedCMDocument);
    		return cachedCMDocument;
    	}
    }

    CMDocument result = null;         
    if (resolvedURI != null && resolvedURI.length() > 0)
    {
      // TODO... pass the TYPE thru to the CMDocumentBuilder
      result = ContentModelManager.getInstance().createCMDocument(resolvedURI, type);
    }
    if (result != null)
    { 
      // load the annotation files for the document 
      if (publicId != null)
      {    
        AnnotationUtility.loadAnnotationsForGrammar(publicId, result);
      }
      if(globalCMDocumentCacheEnabled && documentCacheable) {
    	  GlobalCMDocumentCache.getInstance().putCMDocument(resolvedURI, result);
      }
      cmDocumentCache.putCMDocument(resolvedURI, result);
    }
    else
    {
      cmDocumentCache.setStatus(resolvedURI, CMDocumentCache.STATUS_ERROR);
    }
    return result;
  } 

  public void removeAllReferences()
  {
    // TODO... initiate a timed release of the entries in the CMDocumentCache
    publicIdTable = new Hashtable();
  }
  
  private void initializeGlobalCMDocumentCacheSettings() {
	  Preferences preferences = XMLCorePlugin.getDefault().getPluginPreferences();
	  if(preferences != null) {
		  globalCMDocumentCacheEnabled = preferences.getBoolean(XMLCorePreferenceNames.CMDOCUMENT_GLOBAL_CACHE_ENABLED);
	  }
  }

}                                            
