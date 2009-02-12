/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation;


import java.util.HashSet;

import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;


/**
 * XMLNestedValidatorContext is used to store state data needed during an XML
 * validation session.
 */
public class XMLNestedValidatorContext extends NestedValidatorContext
{
  /**
   * A set of inaccessible locations URIs (String).
   */
  private HashSet inaccessibleLocationURIs = new HashSet();

  /**
   * Determines if a location URI was marked as inaccessible.
   * 
   * @param locationURI
   *          the location URI to test. Must not be null.
   * @return true if a location URI was marked as inaccessible, false otherwise.
   */
  public boolean isURIMarkedInaccessible(String locationURI)
  {
    return locationURI != null && inaccessibleLocationURIs.contains(locationURI);
  }

  /**
   * Marks the given location URI as inaccessible.
   * 
   * @param locationURI
   *          the location URI to mark as inaccessible. Must not be null.
   */
  public void markURIInaccessible(String locationURI)
  {
    if (locationURI != null)
    {
      inaccessibleLocationURIs.add(locationURI);
    }
  }
}
