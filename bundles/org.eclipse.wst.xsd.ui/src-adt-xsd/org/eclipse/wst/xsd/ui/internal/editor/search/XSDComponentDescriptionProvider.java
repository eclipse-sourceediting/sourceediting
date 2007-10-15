/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.editor.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentDescriptionProvider;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class XSDComponentDescriptionProvider extends LabelProvider implements IComponentDescriptionProvider
{
  public boolean isApplicable(Object component)
  {
    // TODO (cs) if this provider is used in a multi language context
    // we'll need to provide some logic here
    return true;
  }

  private static final Image SIMPLE_TYPE_IMAGE =  XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
  private static final Image COMPLEX_TYPE_IMAGE = XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif");
  private static final Image ELEMENT_IMAGE = XSDEditorPlugin.getXSDImage("icons/XSDElement.gif");
  private static final Image ATTRIBUTE_IMAGE = XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif");
  //private final static Image BUILT_IN_TYPE)IMAGE = 
    
  public String getQualifier(Object component)
  {
    String result = null;
    if (component instanceof ComponentSpecification)
    {
      result = ((ComponentSpecification)component).getQualifier();
    }  
    else if (component instanceof XSDNamedComponent)
    {
      result = ((XSDNamedComponent)component).getTargetNamespace(); 
    }  
    else if (component instanceof SearchMatch)
    {
      QualifiedName qualifiedName = getQualifiedNameForSearchMatch((SearchMatch)component);
      if (qualifiedName != null)
      {  
        result = qualifiedName.getNamespace();
      }    
    }  
    return result;
  }
  
  // TODO... this will be much easier with Hiroshi's proposed SearchMatch changes
  //
  private QualifiedName getQualifiedNameForSearchMatch(SearchMatch match)
  {
    QualifiedName qualifiedName = null;
    Object o = match.map.get("name");
    if (o != null && o instanceof QualifiedName)
    {  
      qualifiedName = (QualifiedName)o;
    }      
    return qualifiedName;
  }

  public String getName(Object component)
  {
    String result = null;
    if (component instanceof ComponentSpecification)
    {
      result = ((ComponentSpecification)component).getName();
    }  
    else if (component instanceof XSDNamedComponent)
    {
      result = ((XSDNamedComponent)component).getName(); 
    }  
    else if (component instanceof SearchMatch)
    {
      QualifiedName qualifiedName = getQualifiedNameForSearchMatch((SearchMatch)component);
      if (qualifiedName != null)
      {  
        result = qualifiedName.getLocalName();
      }    
    }      
    return result;
  }

  public IFile getFile(Object component)
  {
    IFile result = null;
    if (component instanceof ComponentSpecification)
    {
      result = ((ComponentSpecification)component).getFile();
    }  
    else if (component instanceof SearchMatch)
    {
      result = ((SearchMatch)component).getFile();
    }  
    else if (component instanceof XSDConcreteComponent)
    {
      XSDConcreteComponent concreteComponent = (XSDConcreteComponent)component;
      XSDSchema schema = concreteComponent.getSchema();
      if (schema != null)
      {
        // TODO (cs) revisit and test more
        //
        String location = schema.getSchemaLocation();
        String platformResource = "platform:/resource";
        if (location != null && location.startsWith(platformResource))
        {
          Path path = new Path(location.substring(platformResource.length()));
          result = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        }  
      }  
    }
    return result;
  }

  public ILabelProvider getLabelProvider()
  {
    return this;
  }
  
  public String getText(Object element)
  {
    String result = "";
    String name = getName(element);
    if (name != null)
    {
      result += name;
      /*
      String qualifier = getQualifier(element);
      if (qualifier != null)
      {
        result += " - " + qualifier;
      }  
      IFile file = getFile(element);
      if (file != null)
      {
        result += "  (" + file.getProject().getName() + ")";
      }*/ 
    }
    return result;
  } 
  
  public Image getImage(Object component)
  {
    Image result = null; 
    if (component instanceof SearchMatch)
    {
      SearchMatch searchMatch = (SearchMatch)component;
      QualifiedName qualifiedName = (QualifiedName)searchMatch.map.get("metaName");
      if ( qualifiedName != null ){
    	  if ( qualifiedName.equals(IXSDSearchConstants.SIMPLE_TYPE_META_NAME))
    		  result = SIMPLE_TYPE_IMAGE;
    	  else if ( qualifiedName.equals(IXSDSearchConstants.COMPLEX_TYPE_META_NAME))
    		  result = COMPLEX_TYPE_IMAGE;
    	  else if ( qualifiedName.equals(IXSDSearchConstants.ELEMENT_META_NAME))
    		  result = ELEMENT_IMAGE;
    	  else if ( qualifiedName.equals(IXSDSearchConstants.ATTRIBUTE_META_NAME))
    	    result = ATTRIBUTE_IMAGE;
      }
    }      
    else if (component instanceof XSDComplexTypeDefinition)
      result = COMPLEX_TYPE_IMAGE;      
    else if (component instanceof XSDSimpleTypeDefinition)
      result = SIMPLE_TYPE_IMAGE;
    else if (component instanceof XSDElementDeclaration)
      result = ELEMENT_IMAGE;
    else if (component instanceof XSDAttributeDeclaration)
      result = ATTRIBUTE_IMAGE;
    return result;
  }

  public Image getFileIcon(Object component) {
	return XSDEditorPlugin.getXSDImage("icons/XSDFile.gif");
  }
}
