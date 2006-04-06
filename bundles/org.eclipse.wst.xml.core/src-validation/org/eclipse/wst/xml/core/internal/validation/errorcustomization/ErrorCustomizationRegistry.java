/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The error customization regsitry manages all registered
 * IErrorMessageCustomizers by namespace.
 */
public class ErrorCustomizationRegistry 
{
  protected static ErrorCustomizationRegistry registry = null;
  protected Map customizers = new HashMap();
  
  /**
   * Restricted constructor in keeping with the singleton pattern.
   */
  protected ErrorCustomizationRegistry()
  {
  }
  
  /**
   * Get the one and only instance of the registry.
   * 
   * @return
   * 		The one and only instance of the registry.
   */
  public static ErrorCustomizationRegistry getInstance()
  {
	if(registry == null)
	{
	  registry = new ErrorCustomizationRegistry();
	}
	return registry;
  }
  
  /**
   * Add a customizer to the registry. 
   * 
   * @param namespace
   * 		The namespace the customizer will act on.
   * @param customizer
   * 		The error customizer to register.
   */
  public void addErrorMessageCustomizer(String namespace, IErrorMessageCustomizer customizer)
  {
	// To register a customizer for the no namespace use an empty string.
	if(namespace == null)
	{
	  namespace = ""; //$NON-NLS-1$
	}
	List customizersForNS = (List)customizers.get(namespace);
	if(customizersForNS == null)
	{
	  customizersForNS = new ArrayList();
	  customizers.put(namespace, customizersForNS);
	}
	if(customizer != null)
	{
	  customizersForNS.add(customizer);
	}
  }
  
  /**
   * Get the error customizers for a given namespace.
   * 
   * @param namespace
   * 		The namespace for which to retrieve the customizers.
   * @return
   * 		An array of customizers registered for this namespace.
   */
  public IErrorMessageCustomizer[] getCustomizers(String namespace)
  {
	if(namespace == null)
	{
	  namespace = ""; //$NON-NLS-1$
	}
	List customizersForNS = (List)customizers.get(namespace);
	if(customizersForNS == null)
	{
	  customizersForNS = new ArrayList();
	}
	return (IErrorMessageCustomizer[])customizersForNS.toArray(new IErrorMessageCustomizer[customizersForNS.size()]);
  }
}
