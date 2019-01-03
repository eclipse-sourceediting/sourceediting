/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.core.internal.validation.eclipse;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;

/**
 * Wrapper for the Eclipse XSD validator class to allow for testing.
 */
public class XSDValidatorWrapper extends XSDValidator 
{
  /**
   * Constructor.
   */
  public XSDValidatorWrapper()
  {
	super();
  }
  
  /**
   * Get the URI resolver registered on the XML validator.
   * 
   * @return The URI resolver registered on the XML validator.
   */
  public URIResolver getURIResolver()
  {
	return uriresolver;
  }
}
