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
package org.eclipse.wst.xsd.ui.internal.editor;

import java.util.List;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.design.figures.IExtendedFigureFactory;

public class XSDEditorExtensionProperties
{
  XSDAdapterFactory adapterFactory;
  IExtendedFigureFactory figureFactory;
  EditPartFactory editPartFactory;
  List actionList;

  public XSDEditorExtensionProperties()
  {

  }

  public void setActionList(List actionList)
  {
    this.actionList = actionList;
  }

  public void setAdapterFactory(XSDAdapterFactory adapterFactory)
  {
    this.adapterFactory = adapterFactory;
  }

  public void setEditPartFactoryList(EditPartFactory editPartFactory)
  {
    this.editPartFactory = editPartFactory;
  }

  public void setFigureFactoryList(IExtendedFigureFactory figureFactory)
  {
    this.figureFactory = figureFactory;
  }

  public List getActionList()
  {
    return actionList;
  }

  public XSDAdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  public EditPartFactory getEditPartFactory()
  {
    return editPartFactory;
  }

  public IExtendedFigureFactory getFigureFactory()
  {
    return figureFactory;
  }
}
