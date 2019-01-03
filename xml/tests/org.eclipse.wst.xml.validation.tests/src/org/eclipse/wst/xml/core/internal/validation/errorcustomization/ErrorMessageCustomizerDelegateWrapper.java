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
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

import org.osgi.framework.Bundle;

/**
 * A wrapper of the class ErrorMessageCustomizerDelegate to facilitate testing.
 */
public class ErrorMessageCustomizerDelegateWrapper extends ErrorMessageCustomizerDelegate 
{
  /**
   * Constructor.
   */
  public ErrorMessageCustomizerDelegateWrapper(Bundle bundle, String classname)
  {
	super(bundle, classname);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.errorcustomization.ErrorMessageCustomizerDelegate#loadCustomizer()
   */
  public void loadCustomizer()
  {
	super.loadCustomizer();
  }
  
  public IErrorMessageCustomizer getCustomizer()
  {
	return customizer;
  }

}
