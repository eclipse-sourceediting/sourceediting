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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.wst.xsd.ui.internal.provider.XSDAbstractAdapter;
import org.eclipse.wst.xsd.ui.internal.provider.XSDModelAdapterFactoryImpl;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSwitch;




public class XSDModelAdapterFactory
{  
  protected static XSDModelAdapterFactory instance;
  protected static XSDAdapterFactoryImpl xsdAdapterFactoryImpl = new XSDAdapterFactoryImpl();
                                              
  public static XSDModelAdapterFactory getInstance()
  {
    if (instance == null)
    {
      instance = new XSDModelAdapterFactory();
    }                                         
    return instance;
  }


  public static ModelAdapter getAdapter(Object o)
  {                             
    ModelAdapter result = null;
    if (o instanceof Notifier)
    {                                
      Notifier target = (Notifier)o;
		  Adapter adapter = xsdAdapterFactoryImpl.adapt(target);
      if (adapter instanceof XSDObjectAdapter)
      {
        result = (XSDObjectAdapter)adapter;
      }
    }                              
    else if (o instanceof ModelAdapter)
    {
      result = (ModelAdapter)o;
    }
    return result;
  }             
    

  public static XSDObjectAdapter getExisitingAdapter(Object xsdObject)
  {  
    XSDObjectAdapter result = null;
    if (xsdObject instanceof Notifier)
    {                                
      Notifier target = (Notifier)xsdObject;
		  Adapter adapter = EcoreUtil.getExistingAdapter(target,xsdAdapterFactoryImpl);
      if (adapter instanceof XSDObjectAdapter)
      {
        result = (XSDObjectAdapter)adapter;
      }
    }
    return result;
  }
   
  //
  //
  //
  public static class XSDAdapterFactoryImpl extends AdapterFactoryImpl
  {
    public Adapter createAdapter(Notifier target)
    {
      XSDSwitch xsdSwitch = new XSDSwitch()
      {                   
        public Object caseXSDElementDeclaration(XSDElementDeclaration object)
        {
          return new XSDElementDeclarationAdapter(object);
        }

        public Object caseXSDSchema(XSDSchema object) 
        {
          return new XSDSchemaAdapter(object);
        }

        public Object defaultCase(EObject object) 
        {
          return new XSDObjectAdapter();
        }         
      };
      Object o = xsdSwitch.doSwitch((EObject)target);

      Adapter result = null;
      if (o instanceof Adapter)
      {
        result  = (Adapter)o;
      }
      else
      {          
//        System.out.println("did not create adapter for target : " + target);
//        Thread.dumpStack();
      }
      return result;
    }      
                               

    public Adapter adapt(Notifier target)
    {
      return adapt(target, this);
    }
  }
    

 
  //
  //
  //
  protected static class XSDObjectAdapter extends AdapterImpl implements ModelAdapter
  {   
    protected List listenerList = new ArrayList();
    protected boolean isUpdating = false;
                                  
    public boolean isAdapterForType(Object type)
    {
      return type == xsdAdapterFactoryImpl;
    }                                             
                
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
      Object result = null;
      if (ModelAdapter.LABEL_PROPERTY.equals(propertyName))
      {
      	result = "";
      	// TODO... move this logic into each adapter
      	//
        if (modelObject instanceof XSDNamedComponent)
        {
      	  result = ((XSDNamedComponent)modelObject).getName();
        }
        else if (modelObject instanceof XSDSchemaDirective)
        {
       	  result = ((XSDSchemaDirective)modelObject).getSchemaLocation();
          if (result == null) result = "(" + XSDEditorPlugin.getXSDString("_UI_LABEL_NO_LOCATION_SPECIFIED") + ")";
          if (result.equals("")) result = "(" + XSDEditorPlugin.getXSDString("_UI_LABEL_NO_LOCATION_SPECIFIED") + ")";
          return result;
        }
      }
      else if ("drillDown".equals(propertyName))
      {
      	// TODO... move this logic into each adapter
      	//   
      	List list = XSDChildUtility.getModelChildren(modelObject);	
      	result = list.size() > 0 ? Boolean.TRUE : Boolean.FALSE;      	   
      }      
      else if (ModelAdapter.IMAGE_PROPERTY.equals(propertyName))
      {
      	// result = XSDEditorPlugin.getDefault().getImage("icons/XSDElement.gif");
        XSDModelAdapterFactoryImpl factory = new XSDModelAdapterFactoryImpl();
        Adapter adapter = factory.createAdapter((Notifier)modelObject);
        result = ((XSDAbstractAdapter)adapter).getImage(modelObject);
      }	
      return result;
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

    public void notifyChanged(Notification msg)
    {        
      firePropertyChanged(msg.getNotifier(), null);
    }     
  }
       

     
  //
  //
  //
  protected static class XSDElementDeclarationAdapter extends XSDObjectAdapter implements ModelAdapterListener
  {
    protected XSDElementDeclaration ed;
    protected XSDTypeDefinition typeDefinition;          

    public XSDElementDeclarationAdapter(XSDElementDeclaration ed)
    {
      this.ed = ed;
      updateTypeDefinition();
    } 
    
    protected void updateTypeDefinition()
    {
      XSDTypeDefinition td = ed.getTypeDefinition();
      td = (td instanceof XSDComplexTypeDefinition) ? td : null;
      if (td != typeDefinition)
      {                     
        if (typeDefinition != null)
        {
          XSDObjectAdapter adapter = (XSDObjectAdapter)xsdAdapterFactoryImpl.adapt(typeDefinition);
          adapter.removeListener(this);
        }
        typeDefinition = td;
        if (typeDefinition != null)
        {
          XSDObjectAdapter adapter = (XSDObjectAdapter)xsdAdapterFactoryImpl.adapt(typeDefinition);
          adapter.addListener(this);
        }
      }
    } 


    public void notifyChanged(Notification msg)
    {
      updateTypeDefinition();    
      firePropertyChanged(msg.getNotifier(), null);                                                                          
    } 


    public void propertyChanged(Object object, String property)
    {                         
      // here we propagate typeDefinition changes to our listeners
      firePropertyChanged(object, property);
    }
  }                         
  

  //
  //
  protected static class XSDSchemaAdapter extends XSDObjectAdapter
  { 
    protected XSDSchema schema;
    protected List groups;            

    public XSDSchemaAdapter(XSDSchema schema)
    {
      this.schema = schema;                         
      groups = new ArrayList();                                       
      groups.add(new Category(schema, Category.DIRECTIVES));
      groups.add(new Category(schema, Category.ATTRIBUTES)); 
      //groups.add(new Category(schema, Category.ATTRIBUTE_GROUPS));
      groups.add(new Category(schema, Category.ELEMENTS));
      groups.add(new Category(schema, Category.TYPES));
      //groups.add(new Category(schema, Category.SIMPLE_TYPES));
      groups.add(new Category(schema, Category.GROUPS));
    }

    public void notifyChanged(Notification msg)
    {
      super.notifyChanged(msg);
      for (Iterator i = groups.iterator(); i.hasNext(); )
      {
        ModelAdapter group = (ModelAdapter)i.next();
        group.firePropertyChanged(group, null);
      }
    }

    public Object getProperty(Object modelObject, String propertyName)
    {
      Object result = null;
      if ("groups".equals(propertyName))
      { 
        /*
        List list = new ArrayList();
        for (Iterator i = groups.iterator(); i.hasNext(); )
        {
          Category group = (Category)i.next();
          if (group.getChildren().size() > 0)
          {
            list.add(group);
          }
        }
        result = list;*/
        result = groups;
      }      
      if (result == null)
      {
        result = super.getProperty(modelObject, propertyName);
      }
      return result;
    }
  }


  public static void addModelAdapterListener(Object modelObject, ModelAdapterListener listener)
  {                                                            
    ModelAdapter modelAdapter = getModelAdapter(modelObject);
    if (modelAdapter != null)
    {
      modelAdapter.addListener(listener);
    }
  }    


  public static void removeModelAdapterListener(Object modelObject, ModelAdapterListener listener)
  {                               
    ModelAdapter modelAdapter = getModelAdapter(modelObject);
    if (modelAdapter != null)
    {
      modelAdapter.removeListener(listener);
    }
  }   

  protected static ModelAdapter getModelAdapter(Object modelObject)
  {
    ModelAdapter modelAdapter = null;                         
    if (modelObject instanceof Notifier)
    {
      modelAdapter = (ModelAdapter)xsdAdapterFactoryImpl.adapt((Notifier)modelObject);
    } 
    else if (modelObject instanceof ModelAdapter)
    {
      modelAdapter = (ModelAdapter)modelObject;
    } 
    return modelAdapter;
  }      
}
    


// --------------------------------------------------------------------------------------------
// todo... revist this stuff
// --------------------------------------------------------------------------------------------

  /*                   
  public static void handleTypeChange(XSDTypeDefinition td, XSDSchema schema, int change)
  {                                    
    try
    {
      TypeFindingSchemaVisitor visitor = new TypeFindingSchemaVisitor(schema, td, change == Notification.ADD);
      List list = visitor.findElementsUsingType(schema);          
      
      if (change == Notification.REMOVE)          
      {
        visitor.removeMatchingReferences();
      }
      else if (change == Notification.ADD)          
      {
        visitor.setMatchingReferences();
      }
      else
      {
        visitor.cleanUpTypeMismatches();        
      }
      
      for (Iterator i = list.iterator(); i.hasNext(); )
      {
        Object o = i.next();  
        XSDObjectAdapter adapter = getExisitingAdapter(o);
        if (adapter != null)
        { 
          adapter.fireChildrenChangedNotification();
        }
      }          
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
          

  protected static class TypeFindingSchemaVisitor extends org.eclipse.wst.xsd.utility.XSDVisitor
  {                           
    protected XSDTypeDefinition td;
    protected List list = new ArrayList();   
    protected XSDSchema schema;                            
    protected boolean matchByName;

    public TypeFindingSchemaVisitor(XSDSchema schema, XSDTypeDefinition td, boolean matchByName)
    {
      this.td = td;                                 
      this.schema = schema;
      this.matchByName = matchByName;
    }

    public void visitElementDeclaration(XSDElementDeclaration ed)
    {                                  
      if (ed.getTypeDefinition() == td)
      {                
        list.add(ed);        
      }  
      else if (matchByName)
      {          
        String[] name = getDOMName(ed);
        if (name[0].equals(td.getTargetNamespace()) && name[1].equals(td.getName()))
        {
          list.add(ed);
        }
      }
    } 

    public List findElementsUsingType(XSDSchema schema)
    {
      if (td != null)
      {
        visitSchema(schema);
      }    
      return list;
    }   

    public List getMatchingTypeList()
    {
      return list;
    } 

    public String[] getDOMName(XSDElementDeclaration ed)
    {      
      String[] result = new String[2];
      org.w3c.dom.Element domElement = ed.getElement();
      String typeName = domElement.getAttribute("type");
      if (typeName != null && !typeName.endsWith(td.getName()))
      {             
        int index = typeName.indexOf(":");                                      
        String prefix = index == -1 ? "" : typeName.substring(0, index);   
        result[0] = (String)schema.getQNamePrefixToNamespaceMap().get(prefix);
        if (result[0] == null) result[0] = "";
        if (result[1] == null) result[1] = "";
        result[1] = index == -1 ? typeName : typeName.substring(index + 1);
      }  
      else
      {
        result[0] = "";
        result[1] = "";
      }
      return result;
    }
    
    
    public void cleanUpTypeMismatches()
    {
      for (Iterator i = list.iterator(); i.hasNext(); )
      {
        XSDElementDeclaration ed = (XSDElementDeclaration)i.next();
        XSDTypeDefinition candidateTd = ed.getTypeDefinition();
        if (candidateTd != null && candidateTd.getName() != null)
        {
          String[] result = getDOMName(ed);
          ed.setTypeDefinition((XSDTypeDefinition)schema.resolveComplexTypeDefinition(result[0], result[1]));         
        }
      }
    }
    
    public void removeMatchingReferences()
    {
      for (Iterator i = list.iterator(); i.hasNext(); )
      {
        XSDElementDeclaration ed = (XSDElementDeclaration)i.next();
        String[] result = getDOMName(ed);          
        if (ed.getElement() != null)
        {
          // NOTE ... this forces the model to reset the ed's XSDTypeDefinition without causing the
          // DOM element's 'type' attribute to be set to null
          ed.elementAttributesChanged(ed.getElement());
        }
      }
    }
    
    public void setMatchingReferences()
    {
      for (Iterator i = list.iterator(); i.hasNext(); )
      {
        XSDElementDeclaration ed = (XSDElementDeclaration)i.next();
        ed.setTypeDefinition(td);
      }
    }    
  } */   
