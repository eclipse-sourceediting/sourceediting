/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation.eclipse;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;

/**
 * An XML validator specific to Eclipse. This validator will wrap the internal
 * XML validator an provide automatic URI resolution support.
 * Using this class is equivalent to using the internal XML validator and registering
 * the URI resolver.
 */
public class XMLValidator extends org.eclipse.wst.xml.core.internal.validation.XMLValidator
{
  private static XMLValidator instance = null;
  
  /**
   * Return the one and only instance of the XML validator. The validator
   * can be reused and cannot be customized so there should only be one instance of it.
   * 
   * @return The one and only instance of the XML validator.
   */
  public static XMLValidator getInstance()
  {
    if(instance == null)
    {
      instance = new XMLValidator();
    }
    return instance;
  }
  /**
   * Constructor. Create the XML validator and set the URI resolver.
   */
  protected XMLValidator()
  {
    setURIResolver(URIResolverPlugin.createResolver());
  }
}
