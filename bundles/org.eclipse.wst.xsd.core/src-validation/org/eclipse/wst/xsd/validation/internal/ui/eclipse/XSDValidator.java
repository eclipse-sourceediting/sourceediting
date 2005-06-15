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

package org.eclipse.wst.xsd.validation.internal.ui.eclipse;

import java.io.InputStream;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.xml.validation.internal.core.ValidationReport;


/**
 * An XSD validator specific to Eclipse. This validator will wrap the internal
 * XSD validator an provide automatic URI resolver support.
 * Using this class is equivalent to using the internal XSD validator and registering
 * the URI resolver.
 * 
 * @author Lawrence Mandel, IBM
 */
public class XSDValidator
{
  private static XSDValidator instance = null;
  private org.eclipse.wst.xsd.validation.internal.XSDValidator validator = null;
  
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
    validator = new org.eclipse.wst.xsd.validation.internal.XSDValidator();
    validator.setURIResolver(URIResolverPlugin.createResolver());
  }
  /**
   * Validate the file at the given URI.
   * 
   * @param uri The URI of the file to validate.
   */
  /*public ValidationReport validate(String uri)
  {
    return validator.validate(uri);
  }*/
  public ValidationReport validate(String uri, InputStream inputStream)
  {
    return validator.validate(uri, inputStream);
  }
}
