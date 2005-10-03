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
package org.eclipse.wst.xsd.ui.internal.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.Disposable;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDAdapterFactory;
import org.eclipse.xsd.util.XSDSwitch;

public class XSDModelAdapterFactoryImpl extends XSDAdapterFactory
  implements IChangeNotifier, IDisposable
{
  static XSDModelAdapterFactoryImpl instance;
  
  public static XSDModelAdapterFactoryImpl getInstance()
  {
    if (instance == null)
    {
      instance = new XSDModelAdapterFactoryImpl();
    }                                         
    return instance;
  }
  
  protected IChangeNotifier changeNotifier = new ChangeNotifier();
  
  protected Disposable disposable = new Disposable();
  
  protected Collection supportedTypes = new ArrayList();
  
  protected boolean showReferences = false;
  protected boolean showInherited = false;
  
  /**
   * 
   */
  public XSDModelAdapterFactoryImpl()
  {
    super();
    supportedTypes.add(ILabelProvider.class);
  }

  public void setShowReferences(boolean b)
  {
    showReferences = b;
  }
  
  public boolean getShowReferences()
  {
    return showReferences;
  }
  
  public void setShowInherited(boolean b)
  {
    showInherited= b;
  }
  
  public boolean getShowInherited()
  {
    return showInherited;
  }
  
  protected XSDSwitch modelSwitch = new XSDSwitch()
  {
      public Object caseXSDAnnotation(XSDAnnotation object)
      {
        return createXSDAnnotationAdapter();
      }
      public Object caseXSDAttributeDeclaration(XSDAttributeDeclaration object)
      {
        return createXSDAttributeDeclarationAdapter();
      }
      public Object caseXSDAttributeGroupDefinition(XSDAttributeGroupDefinition object)
      {
        return createXSDAttributeGroupDefinitionAdapter();
      }
      public Object caseXSDAttributeUse(XSDAttributeUse object)
      {
        return createXSDAttributeUseAdapter();
      }
      public Object caseXSDComplexTypeDefinition(XSDComplexTypeDefinition object)
      {
        return createXSDComplexTypeDefinitionAdapter();
      }
      public Object caseXSDElementDeclaration(XSDElementDeclaration object)
      {
        return createXSDElementDeclarationAdapter();
      }
      public Object caseXSDModelGroup(XSDModelGroup object)
      {
        return createXSDModelGroupAdapter();
      }
      public Object caseXSDModelGroupDefinition(XSDModelGroupDefinition object)
      {
        return createXSDModelGroupDefinitionAdapter();
      }
      public Object caseXSDNotationDeclaration(XSDNotationDeclaration object)
      {
        return createXSDNotationDeclarationAdapter();
      }
      public Object caseXSDParticle(XSDParticle object)
      {
        return createXSDParticleAdapter();
      }
//      public Object caseXSDParticleContent(XSDParticleContent object)
//      {
//        return createXSDParticleContentAdapter();
//      }
      public Object caseXSDSchema(XSDSchema object)
      {
        return createXSDSchemaAdapter();
      }
      public Object caseXSDImport(XSDImport object)
      {
        // return createXSDImportAdapter();
        return createXSDSchemaDirectiveAdapter();
      }
      public Object caseXSDInclude(XSDInclude object)
      {
        // return createXSDIncludeAdapter();
        return createXSDSchemaDirectiveAdapter();
      }
      public Object caseXSDRedefine(XSDRedefine object)
      {
        // return createXSDRedefineAdapter();
        return createXSDSchemaDirectiveAdapter();
      }
      public Object caseXSDSimpleTypeDefinition(XSDSimpleTypeDefinition object)
      {
        return createXSDSimpleTypeDefinitionAdapter();
      
      }
      public Object caseXSDWildcard(XSDWildcard object)
      {
        return createXSDWildcardAdapter();
      }
      public Object defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
  };

  public Adapter createAdapter(Notifier target)
  {
    Adapter adapter = null;
    if (target instanceof EObject)
    {
      adapter = (Adapter)modelSwitch.doSwitch((EObject)target);
    }
    return adapter;
  }

  /* create adapters */
  
  public Adapter createXSDAnnotationAdapter()
  {
    return new XSDAnnotationAdapter(this);
  }

  public Adapter createXSDAttributeDeclarationAdapter()
  {
    return new XSDAttributeDeclarationAdapter(this);
  }

  public Adapter createXSDAttributeGroupDefinitionAdapter()
  {
    return new XSDAttributeGroupDefinitionAdapter(this);
  }

  public Adapter createXSDAttributeUseAdapter()
  {
    return new XSDAttributeUseAdapter(this);
  }

  public Adapter createXSDComplexTypeDefinitionAdapter()
  {
	return new XSDComplexTypeDefinitionAdapter(this); 
  }

  public Adapter createXSDElementDeclarationAdapter()
  {
    return new XSDElementDeclarationAdapter(this);
  }

  public Adapter createXSDModelGroupAdapter()
  {
    return new XSDModelGroupAdapter(this);
  }


  public Adapter createXSDModelGroupDefinitionAdapter()
  {
    return new XSDModelGroupDefinitionAdapter(this);
  }

  public Adapter createXSDNotationDeclarationAdapter()
  {
    return new XSDNotationDeclarationAdapter(this);
  }

  public Adapter createXSDWildcardAdapter()
  {
    return new XSDWildcardAdapter(this);
  }
  
  public Adapter createXSDParticleAdapter()
  {
    return new XSDParticleAdapter(this);
  }
//
//  protected XSDParticleContentAdapter xsdParticleContentAdapter;
//  public Adapter createXSDParticleContentAdapter()
//  {
//    if (xsdParticleContentAdapter == null)
//    {
//      xsdParticleContentAdapter = new XSDParticleContentAdapter(this);
//    }
//    return xsdParticleContentAdapter;
//  }

  public Adapter createXSDSchemaAdapter()
  {
    return new XSDSchemaAdapter(this);
  }
  
  public Adapter createXSDSchemaDirectiveAdapter()
  {
    return new XSDSchemaDirectiveAdapter(this);
  }

  public Adapter createXSDSimpleTypeDefinitionAdapter()
  {
    return new XSDSimpleTypeDefinitionAdapter(this);
  }

  public Adapter createEObjectAdapter()
  {
    return null;
  }

  public boolean isFactoryForType(Object type)
  {
    return super.isFactoryForType(type) || supportedTypes.contains(type);
  }

  /**
   * This implementation substitutes the factory itself as the key for the adapter.
   */
  public Adapter adapt(Notifier notifier, Object type)
  {
    return super.adapt(notifier, this);
  }

  public Object adapt(Object object, Object type)
  {
    // This is a kludge to deal with enumerators, which crash the doSwitch.
    //
    if (object instanceof EObject && ((EObject)object).eClass() == null)
    {
      return null;
    }

    if (isFactoryForType(type))
    {
      Object adapter = super.adapt(object, type);
      if (!(type instanceof Class) || (((Class)type).isInstance(adapter)))
      {
        return adapter;
      }
    }

    return null;
  }

  public Adapter adaptNew(Notifier object, Object type)
  {
    Adapter result = super.adaptNew(object, type);
    disposable.add(result);
    return result;
  }

  /**
   * This adds a listener.
   */
  public void addListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.addListener(notifyChangedListener);
  }

  /**
   * This removes a listener.
   */
  public void removeListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.removeListener(notifyChangedListener);
  }

  /**
   * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
   */
  public void fireNotifyChanged(Notification notification)
  {
    Display display = Display.getDefault();
    if (display != null)
    {
      if (display.getThread() == Thread.currentThread ())
      {
        changeNotifier.fireNotifyChanged(notification);
      }
    }

//    if (parentAdapterFactory != null)
//    {
//      parentAdapterFactory.fireNotifyChanged(notification);
//    }
  }

  public void dispose()
  {
    instance = null;
    disposable.dispose();
  }

}
