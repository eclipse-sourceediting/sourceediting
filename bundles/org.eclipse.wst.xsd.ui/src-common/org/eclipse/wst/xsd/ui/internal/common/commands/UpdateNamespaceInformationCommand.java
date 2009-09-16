/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.xerces.util.XMLChar;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.nsedit.TargetNamespaceChangeHandler;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateNamespaceInformationCommand extends BaseCommand
{
  protected XSDSchema xsdSchema;
  protected String newPrefix, newTargetNamespace;
  
  public UpdateNamespaceInformationCommand(String label, XSDSchema xsdSchema, String newPrefix, String newTargetNamespace)
  {
    super(label);
    this.xsdSchema = xsdSchema;
    this.newPrefix = newPrefix;
    this.newTargetNamespace = newTargetNamespace;
  }

  public void execute()
  {
    ensureSchemaElement(xsdSchema);
    
    Element element = xsdSchema.getElement();
    try
    {
      //DocumentImpl doc = (DocumentImpl) element.getOwnerDocument();

      String modelTargetNamespace = xsdSchema.getTargetNamespace();
      String oldNamespace = xsdSchema.getTargetNamespace();

      TypesHelper helper = new TypesHelper(xsdSchema);
      String oldPrefix = helper.getPrefix(element.getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE), false);

      if (modelTargetNamespace == null)
      {
        modelTargetNamespace = ""; //$NON-NLS-1$
      }

      String targetNamespace = newTargetNamespace.trim();
      String prefix = newPrefix.trim();

      if (!validatePrefix(prefix) || !validateTargetNamespace(targetNamespace))
      {
        return;
      }

      if (prefix.length() > 0 && targetNamespace.length() == 0)
      {
        // can't have blank targetnamespace and yet specify a prefix
        return;
      }

      //doc.getModel().beginRecording(this, XSDEditorPlugin.getXSDString("_UI_TARGETNAMESPACE_CHANGE")); //$NON-NLS-1$
      beginRecording(element);
      
      String xsdForXSDPrefix = xsdSchema.getSchemaForSchemaQNamePrefix();
      Map map = xsdSchema.getQNamePrefixToNamespaceMap();

      // Check if prefix is blank
      // if it is, then make sure we have a prefix
      // for schema for schema
      if (prefix.length() == 0)
      {
        // if prefix for schema for schema is blank
        // then set it to value specified in preference
        // and update ALL nodes with this prefix
        if (xsdForXSDPrefix == null || (xsdForXSDPrefix != null && xsdForXSDPrefix.trim().length() == 0))
        {
          // get preference prefix
          xsdForXSDPrefix = XSDEditorPlugin.getPlugin().getXMLSchemaPrefix();
          // get a unique prefix by checking what's in the map

          xsdForXSDPrefix = getUniqueSchemaForSchemaPrefix(xsdForXSDPrefix, map);
          element.setAttribute("xmlns:" + xsdForXSDPrefix, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001); //$NON-NLS-1$

          updateAllNodes(element, xsdForXSDPrefix);

          // remove the old xmlns attribute for the schema for schema
          if (element.hasAttribute("xmlns")  && //$NON-NLS-1$
              element.getAttribute("xmlns").equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001)) //$NON-NLS-1$
          {
            element.removeAttribute("xmlns"); //$NON-NLS-1$
          }
        }
      }

      if (targetNamespace.length() > 0 || (targetNamespace.length() == 0 && prefix.length() == 0))
      {
        // clean up the old prefix for this schema
        if (oldPrefix != null && oldPrefix.length() > 0)
        {
          element.removeAttribute("xmlns:" + oldPrefix); //$NON-NLS-1$
          // element.setAttribute("xmlns:" + prefix, targetNamespace);
          // java.util.Map prefixToNameSpaceMap =
          // xsdSchema.getQNamePrefixToNamespaceMap();
          // prefixToNameSpaceMap.remove(oldPrefix);
        }
        else
        // if no prefix
        {
          if (element.hasAttribute("xmlns")) //$NON-NLS-1$
          {
            if (!element.getAttribute("xmlns").equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001)) //$NON-NLS-1$
            {
              element.removeAttribute("xmlns"); //$NON-NLS-1$
            }
          }
        }
      }

      if (targetNamespace.length() > 0)
      {
        if (!modelTargetNamespace.equals(targetNamespace))
        {
          element.setAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE, targetNamespace);
        }
        // now set the new xmlns:prefix attribute
        if (prefix.length() > 0)
        {
          element.setAttribute("xmlns:" + prefix, targetNamespace); //$NON-NLS-1$
        }
        else
        {
          element.setAttribute("xmlns", targetNamespace); //$NON-NLS-1$
        }
        // set the targetNamespace attribute
      }
      else
      // else targetNamespace is blank
      {
        if (prefix.length() == 0)
        {
          element.removeAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE);
        }
      }

      // do our own referential integrity
      TargetNamespaceChangeHandler targetNamespaceChangeHandler = new TargetNamespaceChangeHandler(xsdSchema, oldNamespace, targetNamespace);
      targetNamespaceChangeHandler.resolve();

      updateElement(xsdSchema);

      //doc.getModel().endRecording(this);
    }
    finally
    {
      endRecording();
    }
  }
  
  
  // issue (cs) I don't have a clue why we need to call this method
  //
  private static void updateElement(XSDConcreteComponent concreteComp)
  {
    try
    {
      concreteComp.updateElement();
    }
    catch (Exception e)
    {
      for (Iterator containments = concreteComp.eClass().getEAllReferences().iterator(); containments.hasNext(); )
      {
        EReference eReference = (EReference)containments.next();
        if (eReference.isContainment())
        {
          if (eReference.isMany())
          {
            for (Iterator objects = ((Collection)concreteComp.eGet(eReference)).iterator(); objects.hasNext(); )
            {
              XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)objects.next();
              try
              {
                xsdConcreteComponent.updateElement();
              }
              catch (Exception ex) {}
            }
          }
          else
          {
            XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent)concreteComp.eGet(eReference);
            if (xsdConcreteComponent != null)
            {
              try
              {
                xsdConcreteComponent.updateElement();
              }
              catch (Exception ex) {}
            }
          }
        }
      }
    }
  }      
  private String getUniqueSchemaForSchemaPrefix(String xsdForXSDPrefix, Map map)
  {
    if (xsdForXSDPrefix == null || (xsdForXSDPrefix != null && xsdForXSDPrefix.trim().length() == 0))
    {
      xsdForXSDPrefix = "xsd"; //$NON-NLS-1$
    }
    // ensure prefix is unique
    int prefixExtension = 1;
    while (map.containsKey(xsdForXSDPrefix) && prefixExtension < 100)
    {
      xsdForXSDPrefix = xsdForXSDPrefix + String.valueOf(prefixExtension);
      prefixExtension++;
    }
    return xsdForXSDPrefix;
  }

  private void updateAllNodes(Element element, String prefix)
  {
    element.setPrefix(prefix);
    NodeList list = element.getChildNodes();
    if (list != null)
    {
      for (int i = 0; i < list.getLength(); i++)
      {
        Node child = list.item(i);
        if (child != null && child instanceof Element)
        {
          child.setPrefix(prefix);
          if (child.hasChildNodes())
          {
            updateAllNodes((Element) child, prefix);
          }
        }
      }
    }
  }

  private boolean validateTargetNamespace(String ns)
  {
    // will allow blank namespace !!
    if (ns.equals("")) //$NON-NLS-1$
    {
      return true;
    }

    String errorMessage = null;
    try
    {
      URI testURI = new URI(ns);
      testURI.isAbsolute();
    }
    catch (URISyntaxException e)
    {
      errorMessage = XSDEditorPlugin.getXSDString("_WARN_INVALID_TARGET_NAMESPACE"); //$NON-NLS-1$
    }

    if (errorMessage == null || errorMessage.length() == 0)
    {
      return true;
    }
    return false;
  }
  
  protected boolean validatePrefix(String prefix)
  {
    if (prefix != null && prefix.equals("")) return true;
    return XMLChar.isValidNCName(prefix);
  }
  
  public void redo()
  {
    execute();
  }
  
  public void undo()
  {
  }
  
}
