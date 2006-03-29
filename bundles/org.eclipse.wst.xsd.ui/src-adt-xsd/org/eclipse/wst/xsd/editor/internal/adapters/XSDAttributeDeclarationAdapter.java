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
package org.eclipse.wst.xsd.editor.internal.adapters;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.wst.xsd.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.adt.facade.IModel;
import org.eclipse.xsd.XSDSchema;

public class XSDAttributeDeclarationAdapter extends XSDBaseAttributeAdapter implements IActionProvider
{
  protected XSDAttributeDeclaration getXSDAttributeDeclaration()
  {
    return (XSDAttributeDeclaration)target;
  }
 
  protected XSDAttributeDeclaration getResolvedXSDAttributeDeclaration()
  {
    return getXSDAttributeDeclaration().getResolvedAttributeDeclaration();
  }
  
  public boolean isGlobal()
  {
    return getXSDAttributeDeclaration().eContainer() instanceof XSDSchema;
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDAttributeDeclaration().getSchema());
    return (IModel)adapter;
  }  
}
