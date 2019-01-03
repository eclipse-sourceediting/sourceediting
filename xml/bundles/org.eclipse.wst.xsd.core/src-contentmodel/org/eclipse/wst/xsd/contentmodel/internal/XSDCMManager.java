/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.xsd.contentmodel.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;

public class XSDCMManager extends Plugin 
{
  private static XSDCMManager instance;
  
  public XSDCMManager() 
  {
    super();
  }
  
  public static XSDCMManager getInstance() {
    if (instance == null) {
      instance = new XSDCMManager();
    }
    return instance;
  }


  public void startup() throws CoreException 
  {
    XSDTypeUtil.initialize();
    //ContentModelManager.getInstance().setInferredGrammarFactory(new InferredGrammarFactoryImpl());
  }
}
