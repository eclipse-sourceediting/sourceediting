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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.osgi.framework.Bundle;

/**
 * An error message customizer delegate is used to allow for
 * lazy loading of error customizers.
 *
 */
public class ErrorMessageCustomizerDelegate implements IErrorMessageCustomizer
{
  protected Bundle bundle = null;
  protected String classname = null;
  protected IErrorMessageCustomizer customizer = null;
  
  public ErrorMessageCustomizerDelegate(Bundle bundle, String classname)
  {
	this.bundle = bundle;
	this.classname = classname;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.errorcustomization.IErrorMessageCustomizer#customizeMessage(org.eclipse.wst.xml.core.internal.validation.errorcustomization.ElementInformation, java.lang.String, java.lang.Object[])
   */
  public String customizeMessage(ElementInformation elementInfo, String errorKey, Object[] arguments) 
  {
	if(customizer == null)
	{
	  loadCustomizer();
	}
	if(customizer == null)
	{
	  return null;
	}
	return customizer.customizeMessage(elementInfo, errorKey, arguments);
  }
  
  /**
   * Load the customizer class.
   */
  protected void loadCustomizer()
  {
	try
	{
	  Class customizerClass = bundle.loadClass(classname);
	  customizer = (IErrorMessageCustomizer)customizerClass.newInstance();
	}
	catch(Exception e)
	{
	  XMLCorePlugin.getDefault().getLog().log(
			  new Status(IStatus.WARNING, 
					     XMLCorePlugin.getDefault().getBundle().getSymbolicName(), 
					     IStatus.OK, 
					     "The XML validator error customizer was unable to load class " + classname, e)); //$NON-NLS-1$
	}
  }
  
  
}
