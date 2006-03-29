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
package org.eclipse.wst.dtd.core.internal.validation.eclipse;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;

/**
 * An DTD validator specific to Eclipse. This validator will wrap the internal
 * DTD validator to provide automatic URI resolution support.
 * Using this class is equivalent to using the internal DTD validator and registering
 * the URI resolver from the URI resolution framework.
 */
public class DTDValidator extends
		org.eclipse.wst.dtd.core.internal.validation.DTDValidator 
{
  private static DTDValidator _instance = null;
  
  /**
   * Constructor. Registers URI resolution framework with the DTD validator.
   */
  protected DTDValidator()
  {
	super();
	setURIResolver(URIResolverPlugin.createResolver());
  }

  /**
   * Get the one and only instance of the Eclipse specific DTD validator.
   * 
   * @return
   * 		The one and only instance of the Eclipse specific DTD validator.
   */
  public static DTDValidator getInstance() 
  {
	if (_instance == null) 
	{
	  _instance = new DTDValidator();
	}
	return _instance;
  }

}
