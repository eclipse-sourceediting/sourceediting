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
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDRedefineContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class XSDSchemaDirectiveAdapter extends XSDBaseAdapter
{
  public Image getImage()
  {
    XSDSchemaDirective object = (XSDSchemaDirective) target;
    if (object instanceof XSDImport)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDImport.gif"); //$NON-NLS-1$
    }
    else if (object instanceof XSDInclude)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDInclude.gif"); //$NON-NLS-1$
    }
    else if (object instanceof XSDRedefine)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDRedefine.gif"); //$NON-NLS-1$
    }
    return null;
  }

  public String getText()
  {
    XSDSchemaDirective directive = (XSDSchemaDirective) target;
    String result = directive.getSchemaLocation();
    if (result == null)
      result = "(" + Messages._UI_LABEL_NO_LOCATION_SPECIFIED + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    if (result.equals("")) //$NON-NLS-1$
      result = "(" + Messages._UI_LABEL_NO_LOCATION_SPECIFIED + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    return result;

  }

  public ITreeElement[] getChildren()
  {
    List list = new ArrayList();
    if (target instanceof XSDRedefine)
    {
      XSDRedefine redefine = (XSDRedefine) target;
      for (Iterator i = redefine.getContents().iterator(); i.hasNext();)
      {
        XSDRedefineContent redefineContent = (XSDRedefineContent) i.next();
        if (redefineContent instanceof XSDAttributeGroupDefinition)
        {
          list.add((XSDAttributeGroupDefinition) redefine);
        }
        else if (redefineContent instanceof XSDModelGroupDefinition)
        {
          list.add((XSDModelGroupDefinition) redefineContent);
        }
        else if (redefineContent instanceof XSDRedefinableComponent)
        {
          XSDRedefinableComponent comp = (XSDRedefinableComponent) redefineContent;
          if (comp instanceof XSDAttributeGroupDefinition)
          {
            list.add((XSDAttributeGroupDefinition) comp);
          }
          else if (comp instanceof XSDModelGroupDefinition)
          {
            list.add((XSDModelGroupDefinition) comp);
          }
          else if (comp instanceof XSDComplexTypeDefinition)
          {
            list.add((XSDComplexTypeDefinition) comp);
          }
          else if (comp instanceof XSDSimpleTypeDefinition)
          {
            list.add((XSDSimpleTypeDefinition) comp);
          }
        }
        else if (redefineContent instanceof XSDComplexTypeDefinition)
        {
          list.add((XSDComplexTypeDefinition) redefineContent);
        }
        else if (redefineContent instanceof XSDSimpleTypeDefinition)
        {
          list.add((XSDSimpleTypeDefinition) redefineContent);
        }
      }

    }
    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);
  }
}
