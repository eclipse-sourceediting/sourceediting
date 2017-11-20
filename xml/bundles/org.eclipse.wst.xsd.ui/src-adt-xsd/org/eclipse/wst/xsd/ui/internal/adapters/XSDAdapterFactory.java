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
package org.eclipse.wst.xsd.ui.internal.adapters;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDSwitch;

public class XSDAdapterFactory extends AdapterFactoryImpl
{
  protected static XSDAdapterFactory instance;
  
  public static XSDAdapterFactory getInstance()
  {
    if (instance == null)
    {
      // first use the one defined by the configuration
      instance = XSDEditorPlugin.getPlugin().getXSDEditorConfiguration().getAdapterFactory();
      // if there isn't one, then use the default
      if (instance == null)
        instance = new XSDAdapterFactory();
    }
    return instance;
  }
  
  public Adapter createAdapter(Notifier target)
  {
    XSDSwitch xsdSwitch = new XSDSwitch()
    {
      public Object caseXSDSchemaDirective(XSDSchemaDirective object)
      {
        return new XSDSchemaDirectiveAdapter();
      }
      
      public Object caseXSDWildcard(XSDWildcard object)
      {
        return new XSDWildcardAdapter();
      }
      
      public Object caseXSDAttributeGroupDefinition(XSDAttributeGroupDefinition object)
      {
        return new XSDAttributeGroupDefinitionAdapter();
      }

      public Object caseXSDModelGroupDefinition(XSDModelGroupDefinition object)
      {
        return new XSDModelGroupDefinitionAdapter();
      }
      
      public Object caseXSDAttributeDeclaration(XSDAttributeDeclaration object)
      {
        return new XSDAttributeDeclarationAdapter();
      }

      public Object caseXSDAttributeUse(XSDAttributeUse object)
      {
        return new XSDAttributeUseAdapter();
      }
      
      public Object caseXSDParticle(XSDParticle object)
      {
        return new XSDParticleAdapter();
      }

      public Object caseXSDElementDeclaration(XSDElementDeclaration object)
      {
        return new XSDElementDeclarationAdapter();
      }
      
      public Object caseXSDSimpleTypeDefinition(XSDSimpleTypeDefinition object)
      {
        return new XSDSimpleTypeDefinitionAdapter();
      }
      
      public Object caseXSDComplexTypeDefinition(XSDComplexTypeDefinition object)
      {
        // we don't like exposing the 'anyType' type as a visible complex type
        // so we adapt it in a specialized way so that it's treated as simple type
        // that way it doesn't show up as a reference from a field
        //
        if ("anyType".equals(object.getName())) //$NON-NLS-1$
        {
          return new XSDAnyTypeDefinitionAdapter(); 
        }  
        else
        {             
          return new XSDComplexTypeDefinitionAdapter();
        }  
      }
      
      public Object caseXSDModelGroup(XSDModelGroup object)
      {
        return new XSDModelGroupAdapter();
      }

      public Object caseXSDSchema(XSDSchema object)
      {
        return new XSDSchemaAdapter();
      }
      
      public Object caseXSDEnumerationFacet(XSDEnumerationFacet object)
      {
        return new XSDEnumerationFacetAdapter();
      }
      
      public Object caseXSDRedefine(XSDRedefine object)
      {
        return new XSDRedefineAdapter();
      }
    };
    Object o = xsdSwitch.doSwitch((EObject) target);
    Adapter result = null;
    if (o instanceof Adapter)
    {
      result = (Adapter) o;
    }
    else
    {
//      Thread.dumpStack();
    }
    return result;
  }

  public Adapter adapt(Notifier target)
  {
    return adapt(target, this);
  }
}