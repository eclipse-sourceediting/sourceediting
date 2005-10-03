/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.graph.model;
                                   
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;


public class Category implements ModelAdapter
{
  public final static int ATTRIBUTES = 1;
  public final static int ELEMENTS = 2;
  public final static int TYPES = 3;
  public final static int GROUPS = 5;
  public final static int DIRECTIVES = 6;
  public final static int NOTATIONS = 7;
  public final static int ATTRIBUTE_GROUPS = 8;
  public final static int IDENTITY_CONSTRAINTS = 9;
  public final static int ANNOTATIONS = 10;

  
  protected XSDSchema schema;
  protected int groupType;

  //public void modelNameChanged();
  public Category(XSDSchema schema, int groupType)
  {
    this.schema = schema;
    this.groupType = groupType;
  }       

  public int getGroupType()
  {
    return groupType;
  }   

  public XSDSchema getXSDSchema()
  {
    return schema;
  }

  public String getName()
  {
    String name = "";
    switch (groupType)
    {
      case ATTRIBUTES       : { name = XSDEditorPlugin.getXSDString("_UI_GRAPH_ATTRIBUTES"); break; }
      case NOTATIONS        : { name = XSDEditorPlugin.getXSDString("_UI_GRAPH_NOTATIONS"); break; }
      case ELEMENTS         : { name = XSDEditorPlugin.getXSDString("_UI_GRAPH_ELEMENTS"); break; }
      case TYPES             : { name = XSDEditorPlugin.getXSDString("_UI_GRAPH_TYPES"); break; }    
      case GROUPS           : { name = XSDEditorPlugin.getXSDString("_UI_GRAPH_GROUPS"); break; }      
      case DIRECTIVES       : { name = XSDEditorPlugin.getXSDString("_UI_GRAPH_DIRECTIVES"); break; }
    }      
    return name;
  }
              
  public List getChildren()
  {                      
    List list = Collections.EMPTY_LIST;
    switch (groupType)
    {
      case ATTRIBUTES       : { list = getAttributeList(); break; }
      case NOTATIONS        : { list = getNotations(); break; }
      case ELEMENTS         : { list = getGlobalElements(); break; }
      case TYPES            : { list = getTypes();  break; }
      case GROUPS           : { list = getGroups(); break; }
      case DIRECTIVES       : { list = getDirectives(); break; }
    }      
    return list;
  }
  
  private boolean isSameNamespace(String ns1, String ns2)
  {
    if (ns1 == null) ns1 = "";
    if (ns2 == null) ns2 = "";
    
    if (ns1.equals(ns2))
    {
      return true;
    }
    return false;
  }
  
  protected List getGlobalElements()
  {
    List elements = schema.getElementDeclarations();
    List list = new ArrayList();
    for (Iterator i = elements.iterator(); i.hasNext(); )
    {
      XSDElementDeclaration elem = (XSDElementDeclaration)i.next();
      if (isSameNamespace(elem.getTargetNamespace(),schema.getTargetNamespace()))
      {
        list.add(elem);
      }
    }                
    return list;
  }

  protected List getTypes()
  {
    List allTypes = schema.getTypeDefinitions();
    List list = new ArrayList();
    for (Iterator i = allTypes.iterator(); i.hasNext(); )
    {
      XSDTypeDefinition td = (XSDTypeDefinition)i.next();
      if (td instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)td;
        if (isSameNamespace(ct.getTargetNamespace(),schema.getTargetNamespace()))
        {
          list.add(ct);
        }
      }
    }                

//    List simpleTypes = schema.getTypeDefinitions();
    for (Iterator i = allTypes.iterator(); i.hasNext(); )
    {
      XSDTypeDefinition td = (XSDTypeDefinition)i.next();
      if (td instanceof XSDSimpleTypeDefinition)
      {
        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)td;
        if (isSameNamespace(st.getTargetNamespace(),schema.getTargetNamespace()))
        {
          list.add(st);
        }
      }
    }                
    return list;
  }
  
  protected List getGroups()
  {
    List groups = schema.getModelGroupDefinitions();
    List list = new ArrayList();
    for (Iterator i = groups.iterator(); i.hasNext(); )
    {
      XSDModelGroupDefinition group = (XSDModelGroupDefinition)i.next();
      if (isSameNamespace(group.getTargetNamespace(),schema.getTargetNamespace()))
      {
        list.add(group);
      }
    }                
    return list;
  }
  
  protected List getDirectives()
  {                 
    List list = new ArrayList();
    for (Iterator i = schema.getContents().iterator(); i.hasNext(); )
    {
      Object o = i.next();
      if (o instanceof XSDSchemaDirective)
      {
        list.add(o);
      }
    } 
    return list;
  }

  protected List getAttributeList()
  {
    List attributesList = new ArrayList();
    for (Iterator iter = schema.getAttributeDeclarations().iterator(); iter.hasNext(); )
    {
      Object o = iter.next();
      if (o instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration attr = (XSDAttributeDeclaration)o;
        if (attr != null)
        {
          if (attr.getTargetNamespace() != null)
          {
            if (!(attr.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema-instance")))
            {
              if (isSameNamespace(attr.getTargetNamespace(), schema.getTargetNamespace()))
              {
                attributesList.add(attr);
              }
            }
          }
          else
          {
            if (isSameNamespace(attr.getTargetNamespace(),schema.getTargetNamespace()))
            {
              attributesList.add(attr);
            }
          }
        }
      }
    }
    return attributesList;
  }

  protected List getNotations()
  {
    List notations = schema.getNotationDeclarations();
    List list = new ArrayList();
    for (Iterator i = notations.iterator(); i.hasNext(); )
    {
      XSDNotationDeclaration notation = (XSDNotationDeclaration)i.next();
      if (isSameNamespace(notation.getTargetNamespace(),schema.getTargetNamespace()))
      {
        list.add(notation);
      }
    }                
    return list;
  }
  
  //
  protected List listenerList = new ArrayList();
                                                                                           
  public void addListener(ModelAdapterListener l)
  {
    listenerList.add(l);
  }  

  public void removeListener(ModelAdapterListener l)
  {
    listenerList.remove(l);
  } 

  public Object getProperty(Object modelObject, String propertyName)
  {
    return null;
  }

  public void firePropertyChanged(Object modelObject, String propertyName)
  {
    List newList = new ArrayList();
    newList.addAll(listenerList);
    for (Iterator i = newList.iterator(); i.hasNext(); )
    {
      ModelAdapterListener l = (ModelAdapterListener)i.next();                  
      try
      {
        l.propertyChanged(modelObject, propertyName);
      }
      catch (Exception e)
      {
      }
    }
  }  
}
