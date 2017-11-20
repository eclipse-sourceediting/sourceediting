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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;


/**
 * todo... add more interface methods
 */
public interface CMDocumentCacheListener
{
  /** Tells the client that the cache has been cleared.
   *  This gives clients an opportunity to flush any state that depends on the CMDocument
   *  since this CMDocument will be recomputed on a subsequent 'lookup' request
   */
  public void cacheCleared(CMDocumentCache cache); 

  /** 
   *  Tells the client that the cache has been updated.  
   */
  public void cacheUpdated(CMDocumentCache cache, String uri, int oldStatus, int newStatus, CMDocument cmDocument);
}
