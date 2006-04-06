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
package org.eclipse.wst.xml.core.internal.validation.eclipse;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.validation.errorcustomization.ErrorCustomizationRegistry;
import org.eclipse.wst.xml.core.internal.validation.errorcustomization.ErrorMessageCustomizerDelegate;
import org.osgi.framework.Bundle;
/**
 * This class reads the plug-in manifests and registers each extension
 * error customizer with the ErrorCustomizationRegistry.
 */
public class ErrorCustomizationPluginRegistryReader {
	
	  protected static final String PLUGIN_ID = "org.eclipse.wst.xml.core"; //$NON-NLS-1$
	  protected static final String ATT_CLASS = "class"; //$NON-NLS-1$
	  protected static final String ATT_NAMESPACE = "namespace"; //$NON-NLS-1$
	  protected static final String EXTENSION_POINT_ID = "errorCustomizer"; //$NON-NLS-1$
	  protected static final String tagName = EXTENSION_POINT_ID;

	  /**
	   * Read from plugin registry for the errorcustomizer extension.
	   */
	  public void readRegistry()
	  {
	    IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
	    IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
	    if (point != null)
	    {
	      IConfigurationElement[] elements = point.getConfigurationElements();
	      for (int i = 0; i < elements.length; i++)
	      {
	        readElement(elements[i]);
	      }
	    }
	  }

	  /**
	   * readElement() - parse and deal with an extension like:
	   *
	   * <extension point="org.eclipse.wst.xml.core.errorCustomizer"
	   *            id="specificErrorCustomizer"
	   *            name="Specific Error Customizer">
	   *   <errorCustomizer
	   *   			namespace="http://specificnamespace"
	   *   			class="org.eclipse.wst.xml.core.MySpecificErrorCustomizer/>
	   * </extension>
	   */
	  protected void readElement(IConfigurationElement element)
	  {
	    if (element.getName().equals(tagName))
	    {
	      String errorCustomizerClass = element.getAttribute(ATT_CLASS);
	      String namespace = element.getAttribute(ATT_NAMESPACE);

	      if (errorCustomizerClass != null)
	      {
	        try
	        {
	          Bundle pluginBundle = Platform.getBundle(element.getDeclaringExtension().getContributor().getName());
	          ErrorMessageCustomizerDelegate delegate = new ErrorMessageCustomizerDelegate(pluginBundle, errorCustomizerClass);
	          ErrorCustomizationRegistry.getInstance().addErrorMessageCustomizer(namespace, delegate);
	        }
	        catch (Exception e)
	        {
	        }
	      }
	    }
	  }
}
