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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDSwitch;
              

public class XSDChildUtility
{              
  static public List getModelChildren(Object model)
  {
    XSDChildVisitor visitor = new XSDChildVisitor(model);
    visitor.visitXSDObject(model);
    return visitor.list;
  }

  static public List getImmediateDerivedTypes(XSDComplexTypeDefinition complexType)
  {
    ArrayList typesDerivedFrom = new ArrayList();

    // A handy convenience method quickly gets all 
    // typeDefinitions within our schema; note that 
    // whether or not this returns types in included, 
    // imported, or redefined schemas is subject to change
    List typedefs = complexType.getSchema().getTypeDefinitions();

    for (Iterator iter = typedefs.iterator(); iter.hasNext(); )
    {
      XSDTypeDefinition typedef = (XSDTypeDefinition)iter.next();
      // Walk the baseTypes from this typedef seeing if any 
      // of them match the requested one
      if (complexType.equals(typedef.getBaseType()))
      {
        // We found it, return the original one and continue
        typesDerivedFrom.add(typedef);
      }
    }
    return typesDerivedFrom;
  }
  // TODO... use the XSDVisitor defined in xsdeditor.util instead
  //          
  public static class XSDChildVisitor extends XSDVisitor
  {
    Object root;
    List list = new ArrayList();

    public XSDChildVisitor(Object root)
    {
      this.root = root;
    }                  

    public void visitXSDModelGroup(XSDModelGroup xsdModelGroup)
    {
      if (xsdModelGroup != root)
      {
        list.add(xsdModelGroup); 
      }                         
      else
      {
        super.visitXSDModelGroup(xsdModelGroup);
      }
    }

    public void visitXSDModelGroupDefinition(XSDModelGroupDefinition xsdModelGroupDefinition)
    {
      if (xsdModelGroupDefinition != root)
      {
        list.add(xsdModelGroupDefinition);
      }                         
      else
      {
        super.visitXSDModelGroupDefinition(xsdModelGroupDefinition);
      }
    }

    public void visitXSDElementDeclaration(XSDElementDeclaration xsdElementDeclaration)
    {
      if (xsdElementDeclaration != root)
      {
        list.add(xsdElementDeclaration);
        
      }                         
      else
      {
        super.visitXSDElementDeclaration(xsdElementDeclaration);
      }
    }

    public void visitXSDComplexTypeDefinition(XSDComplexTypeDefinition xsdComplexTypeDefinition)
    {
      if (xsdComplexTypeDefinition != root)
      {                                    
        if (xsdComplexTypeDefinition.getName() != null || getModelChildren(xsdComplexTypeDefinition).size() > 0)
        {
          list.add(xsdComplexTypeDefinition);
        }
      }                         
      else
      {
        super.visitXSDComplexTypeDefinition(xsdComplexTypeDefinition);
      }
    }    

    public void visitXSDWildcard(XSDWildcard xsdWildCard)
    {
      if (xsdWildCard != root)
      {                                    
        list.add(xsdWildCard);        
      }                         
      else
      {
        super.visitXSDWildcard(xsdWildCard);
      }
    }
  }
               

  public static class XSDVisitor
  { 
    int indent = 0;
                 
    public void visitXSDObject(Object object)
    {           
      if (object == null)
        return;

      XSDSwitch theSwitch = new XSDSwitch()
      {   
        public Object caseXSDComplexTypeDefinition(XSDComplexTypeDefinition object)
        {
          visitXSDComplexTypeDefinition(object);
          return null;
        } 

        public Object caseXSDAttributeUse(XSDAttributeUse object)
        {
          visitXSDAttributeUse(object);
          return null;
        }

        public Object caseXSDElementDeclaration(XSDElementDeclaration object)
        {
          visitXSDElementDeclaration(object);
          return null;
        }

        public Object caseXSDModelGroupDefinition(XSDModelGroupDefinition object)
        {
          visitXSDModelGroupDefinition(object);
          return super.caseXSDModelGroupDefinition(object);
        }

        public Object caseXSDModelGroup(XSDModelGroup object)
        {
          visitXSDModelGroup(object);
          return super.caseXSDModelGroup(object);
        }

        public Object caseXSDParticle(XSDParticle object)
        { 
          visitXSDParticle(object);
          return null;
        } 

        public Object caseXSDSchema(XSDSchema object)
        { 
          visitXSDSchema(object);
          return null;
        } 

        public Object caseXSDWildcard(XSDWildcard object)
        { 
          visitXSDWildcard(object);
          return null;
        } 
      };
      theSwitch.doSwitch((EObject)object);
    }
         
    public void visitXSDAttributeUse(XSDAttributeUse xsdAttributeUse)
    {  
//      printIndented("@" + xsdAttributeUse.getAttributeDeclaration().getName());
    }

    public void visitXSDSchema(XSDSchema xsdSchema)
    {         
      indent += 2;
      for (Iterator iterator = xsdSchema.getElementDeclarations().iterator(); iterator.hasNext(); )
      {
        visitXSDObject(iterator.next());
      }
      indent -= 2;
    }

    public void visitXSDElementDeclaration(XSDElementDeclaration xsdElementDeclaration)
    {      
      indent += 2;         
      XSDTypeDefinition td = xsdElementDeclaration.getTypeDefinition();
      if (td == null)
      {
        td = xsdElementDeclaration.getAnonymousTypeDefinition();
      }                       
      visitXSDObject(td);
      indent -= 2;
    }
   
    public void visitXSDComplexTypeDefinition(XSDComplexTypeDefinition xsdComplexTypeDefinition)
    {
      indent += 2;
      for (Iterator i = xsdComplexTypeDefinition.getAttributeUses().iterator(); i.hasNext(); )
      {        
        visitXSDObject(i.next());
      }
      visitXSDObject(xsdComplexTypeDefinition.getContent());
      indent -= 2;
    }

    public void visitXSDModelGroup(XSDModelGroup xsdModelGroup)
    {
      indent += 2;
      for (Iterator iterator = xsdModelGroup.getContents().iterator(); iterator.hasNext(); )
      {
        visitXSDObject(iterator.next());
      } 
      indent -= 2;
    }     

    public void visitXSDModelGroupDefinition(XSDModelGroupDefinition xsdModelGroupDefinition)
    {
      indent += 2;
      visitXSDObject(xsdModelGroupDefinition.getResolvedModelGroupDefinition().getModelGroup());
      indent -= 2;
    }

    public void visitXSDParticle(XSDParticle xsdParticle)
    {
      indent += 2;                 
      if (xsdParticle.getContent() != null)
        visitXSDObject(xsdParticle.getContent());
      indent -= 2;
    } 

    public void visitXSDWildcard(XSDWildcard object)
    { 

    }

    public void printIndented(String string)
    { 
      //String spaces = "";
      //for (int i = 0; i < indent; i++)
      //{
      //  spaces += " ";
      //}               
     
    }
  } 
}
