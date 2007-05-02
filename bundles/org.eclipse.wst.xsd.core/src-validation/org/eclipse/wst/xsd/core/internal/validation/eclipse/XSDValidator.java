/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.core.internal.validation.eclipse;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;


/**
 * An XSD validator specific to Eclipse. This validator will wrap the internal
 * XSD validator an provide automatic URI resolution support.
 * Using this class is equivalent to using the internal XSD validator and registering
 * the URI resolver from the URI resolution framework.
 */
public class XSDValidator extends org.eclipse.wst.xsd.core.internal.validation.XSDValidator
{
  private static XSDValidator instance = null;
  
  /**
   * Return the one and only instance of the XSD validator. The validator
   * can be reused and cannot be customized so there should only be one instance of it.
   * 
   * @return The one and only instance of the XSD validator.
   */
  public static XSDValidator getInstance()
  {
    if(instance == null)
    {
      instance = new XSDValidator();
    }
    return instance;
  }
  /**
   * Constructor. Create the XSD validator and set the URI resolver.
   */
  protected XSDValidator()
  {
    this.setURIResolver(URIResolverPlugin.createResolver());
  }
}
