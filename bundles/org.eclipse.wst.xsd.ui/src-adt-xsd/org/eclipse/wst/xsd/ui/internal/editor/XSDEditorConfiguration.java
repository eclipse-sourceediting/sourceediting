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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.wst.xsd.ui.internal.actions.IXSDToolbarAction;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.design.figures.IExtendedFigureFactory;

public class XSDEditorConfiguration
{
  public static final String XSDEDITORCONFIGURATIONEXTENSIONID = "org.eclipse.wst.xsd.ui.XSDEditorExtensionConfiguration"; //$NON-NLS-1$
  public static final String INTERNALEDITORCONFIGURATION_EXTENSIONID = "org.eclipse.wst.xsd.ui.internalEditorConfiguration"; //$NON-NLS-1$
  public static final String CLASSNAME = "class"; //$NON-NLS-1$
  public static final String ADAPTERFACTORY = "adapterFactory"; //$NON-NLS-1$
  public static final String TOOLBARACTION = "toolbarAction"; //$NON-NLS-1$
  public static final String FIGUREFACTORY = "figureFactory"; //$NON-NLS-1$
  public static final String EDITPARTFACTORY = "editPartFactory"; //$NON-NLS-1$

  List definedExtensionsList = null;

  public XSDEditorConfiguration()
  {

  }

  public XSDAdapterFactory getAdapterFactory()
  {
    if (definedExtensionsList == null)
    {
      readXSDConfigurationRegistry();
    }
    if (!definedExtensionsList.isEmpty())
    {
      return ((XSDEditorExtensionProperties) definedExtensionsList.get(0)).getAdapterFactory();
    }
    return null;
  }

  public EditPartFactory getEditPartFactory()
  {
    if (definedExtensionsList == null)
    {
      readXSDConfigurationRegistry();
    }
    if (!definedExtensionsList.isEmpty())
    {
      return ((XSDEditorExtensionProperties) definedExtensionsList.get(0)).getEditPartFactory();
    }
    return null;
  }

  public IExtendedFigureFactory getFigureFactory()
  {
    if (definedExtensionsList == null)
    {
      readXSDConfigurationRegistry();
    }
    if (!definedExtensionsList.isEmpty())
    {
      return ((XSDEditorExtensionProperties) definedExtensionsList.get(0)).getFigureFactory();
    }
    return null;
  }

  public List getToolbarActions()
  {
    if (definedExtensionsList == null)
    {
      readXSDConfigurationRegistry();
    }
    if (!definedExtensionsList.isEmpty())
    {
      return ((XSDEditorExtensionProperties) definedExtensionsList.get(0)).getActionList();
    }
    return Collections.EMPTY_LIST;
  }

  protected Object loadClass(IConfigurationElement element, String classString)
  {
    String pluginId = element.getDeclaringExtension().getContributor().getName();

    try
    {
      Class theClass = Platform.getBundle(pluginId).loadClass(classString);
      Object instance = theClass.newInstance();

      return instance;
    }
    catch (Exception e)
    {

    }
    return null;
  }

  public void readXSDConfigurationRegistry()
  {
    definedExtensionsList = new ArrayList();
    updateList(INTERNALEDITORCONFIGURATION_EXTENSIONID);
    updateList(XSDEDITORCONFIGURATIONEXTENSIONID);
  }
  
  private void updateList(String ID)
  {
    IConfigurationElement[] xsdEditorExtensionList = Platform.getExtensionRegistry().getConfigurationElementsFor(ID);
    boolean definedExtensionsExist = (xsdEditorExtensionList != null && xsdEditorExtensionList.length > 0);
    
    if (definedExtensionsExist)
    {

      for (int i = 0; i < xsdEditorExtensionList.length; i++)
      {
        XSDEditorExtensionProperties properties = new XSDEditorExtensionProperties();
        definedExtensionsList.add(properties);
  
        IConfigurationElement element = xsdEditorExtensionList[i];
        String adapterFactoryClass = element.getAttribute(ADAPTERFACTORY);
        if (adapterFactoryClass != null)
        {
          Object object = loadClass(element, adapterFactoryClass);
          XSDAdapterFactory adapterFactory = null;
          if (object instanceof XSDAdapterFactory)
          {
            adapterFactory = (XSDAdapterFactory) object;
            properties.setAdapterFactory(adapterFactory);
          }
        }
  
        String figureFactoryClass = element.getAttribute(FIGUREFACTORY);
        if (figureFactoryClass != null)
        {
          Object object = loadClass(element, figureFactoryClass);
          IExtendedFigureFactory figureFactory = null;
          if (object instanceof IExtendedFigureFactory)
          {
            figureFactory = (IExtendedFigureFactory) object;
            properties.setFigureFactoryList(figureFactory);
          }
        }
  
        IConfigurationElement[] toolbarActions = element.getChildren(TOOLBARACTION);
        List actionList = new ArrayList();
        if (toolbarActions != null)
        {
          for (int j = 0; j < toolbarActions.length; j++)
          {
            IConfigurationElement actionElement = toolbarActions[j];
            String actionClass = actionElement.getAttribute(CLASSNAME);
            IXSDToolbarAction action = null;
            if (actionClass != null)
            {
              Object object = loadClass(actionElement, actionClass);
              if (object instanceof IXSDToolbarAction)
              {
                action = (IXSDToolbarAction) object;
                actionList.add(action);
              }
            }
          }
        }
        properties.setActionList(actionList);
  
        String editPartFactoryClass = element.getAttribute(EDITPARTFACTORY);
        if (editPartFactoryClass != null)
        {
          Object object = loadClass(element, editPartFactoryClass);
          EditPartFactory editPartFactory = null;
          if (object instanceof EditPartFactory)
          {
            editPartFactory = (EditPartFactory) object;
            properties.setEditPartFactoryList(editPartFactory);
          }
        }
      }
    }
  }
}
