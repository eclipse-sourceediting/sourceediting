/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Detects hyperlinks for XSD files. Used by the XSD text editor to provide a
 * "Go to declaration" functionality similar with the one provided by the Java
 * editor.
 */
public class XSDHyperlinkDetector extends BaseHyperlinkDetector
{
  /**
   * Determines whether an attribute is "linkable" that is, the component it
   * points to can be the target of a "go to definition" navigation. Derived
   * classes should override.
   * 
   * @param name the attribute name. Must not be null.
   * @return true if the attribute is linkable, false otherwise.
   */
  protected boolean isLinkableAttribute(String name)
  {
    boolean isLinkable = name.equals(XSDConstants.TYPE_ATTRIBUTE) ||
      name.equals(XSDConstants.REFER_ATTRIBUTE) || 
      name.equals(XSDConstants.REF_ATTRIBUTE) || 
      name.equals(XSDConstants.BASE_ATTRIBUTE) || 
      name.equals(XSDConstants.SCHEMALOCATION_ATTRIBUTE) || 
      name.equals(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE) ||
      name.equals(XSDConstants.ITEMTYPE_ATTRIBUTE) ||
      name.equals(XSDConstants.MEMBERTYPES_ATTRIBUTE)
      ;

    return isLinkable;
  }

  /**
   * Creates a hyperlink based on the selected node. Derived classes should
   * override.
   * 
   * @param document the source document.
   * @param node the node under the cursor.
   * @param region the text region to use to create the hyperlink.
   * @return a new IHyperlink for the node or null if one cannot be created.
   */
  protected IHyperlink createHyperlink(IDocument document, IDOMNode node, IRegion region)
  {
    XSDSchema xsdSchema = getXSDSchema(document);

    if (xsdSchema == null)
    {
      return null;
    }

    XSDConcreteComponent targetComponent = getTargetXSDComponent(xsdSchema, node);

    if (targetComponent != null)
    {
      IRegion nodeRegion = getHyperlinkRegion(node);

      return new XSDHyperlink(nodeRegion, targetComponent);
    }

    return null;
  }

  /**
   * Finds the XSD component for the given node.
   * 
   * @param xsdSchema cannot be null
   * @param node cannot be null
   * @return XSDConcreteComponent
   */
  private XSDConcreteComponent getTargetXSDComponent(XSDSchema xsdSchema, IDOMNode node)
  {
    XSDConcreteComponent component = null;

    XSDConcreteComponent xsdComp = xsdSchema.getCorrespondingComponent((Node) node);
    if (xsdComp instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration elementDecl = (XSDElementDeclaration) xsdComp;
      if (elementDecl.isElementDeclarationReference())
      {
        component = elementDecl.getResolvedElementDeclaration();
      }
      else
      {
        XSDConcreteComponent typeDef = null;
        if (elementDecl.getAnonymousTypeDefinition() == null)
        {
          typeDef = elementDecl.getTypeDefinition();
        }

        XSDConcreteComponent subGroupAffiliation = elementDecl.getSubstitutionGroupAffiliation();

        if (typeDef != null && subGroupAffiliation != null)
        {
          // we have 2 things we can navigate to, if the
          // cursor is anywhere on the substitution
          // attribute
          // then jump to that, otherwise just go to the
          // typeDef.
          if (node instanceof Attr && ((Attr) node).getLocalName().equals(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE))
          {
            component = subGroupAffiliation;
          }
          else
          {
            // try to reveal the type now. On success,
            // then we return true.
            // if we fail, set the substitution group
            // as
            // the object to reveal as a backup plan.
            // ISSUE: how to set backup?
            // if (revealObject(typeDef)) {
            component = typeDef;
            // }
            // else {
            // objectToReveal = subGroupAffiliation;
            // }
          }
        }
        else
        {
          // one or more of these is null. If the
          // typeDef is
          // non-null, use it. Otherwise
          // try and use the substitution group
          component = typeDef != null ? typeDef : subGroupAffiliation;
        }
      }
    }
    else if (xsdComp instanceof XSDModelGroupDefinition)
    {
      XSDModelGroupDefinition elementDecl = (XSDModelGroupDefinition) xsdComp;
      if (elementDecl.isModelGroupDefinitionReference())
      {
        component = elementDecl.getResolvedModelGroupDefinition();
      }
    }
    else if (xsdComp instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration attrDecl = (XSDAttributeDeclaration) xsdComp;
      if (attrDecl.isAttributeDeclarationReference())
      {
        component = attrDecl.getResolvedAttributeDeclaration();
      }
      else if (attrDecl.getAnonymousTypeDefinition() == null)
      {
        component = attrDecl.getTypeDefinition();
      }
    }
    else if (xsdComp instanceof XSDAttributeGroupDefinition)
    {
      XSDAttributeGroupDefinition attrGroupDef = (XSDAttributeGroupDefinition) xsdComp;
      if (attrGroupDef.isAttributeGroupDefinitionReference())
      {
        component = attrGroupDef.getResolvedAttributeGroupDefinition();
      }
    }
    else if (xsdComp instanceof XSDIdentityConstraintDefinition)
    {
      XSDIdentityConstraintDefinition idConstraintDef = (XSDIdentityConstraintDefinition) xsdComp;
      if (idConstraintDef.getReferencedKey() != null)
      {
        component = idConstraintDef.getReferencedKey();
      }
    }
    else if (xsdComp instanceof XSDSimpleTypeDefinition)
    {
      XSDSimpleTypeDefinition typeDef = (XSDSimpleTypeDefinition) xsdComp;

      // Simple types can be one of restriction, list or union.
      // TODO Is there a better way of determining what type we have?
      
      component = typeDef.getBaseTypeDefinition();
      
      if (component == null)
      {
        component = typeDef.getItemTypeDefinition();

        if (component == null)
        {
          // if itemType attribute is not set, then check
          // for memberTypes
          List memberTypes = typeDef.getMemberTypeDefinitions();
          if (memberTypes != null && memberTypes.size() > 0)
          {
            // ISSUE: What if there are more than one type?
            // This could be a case for multiple hyperlinks at the same location?
            component = (XSDConcreteComponent) memberTypes.get(0);
          }
        }
      }
    }
    else if (xsdComp instanceof XSDTypeDefinition)
    {
      XSDTypeDefinition typeDef = (XSDTypeDefinition) xsdComp;
      component = typeDef.getBaseType();
    }
    else if (xsdComp instanceof XSDSchemaDirective)
    {
      XSDSchemaDirective directive = (XSDSchemaDirective) xsdComp;
      component = directive.getResolvedSchema();
    }
    return component;
  }

  /**
   * Gets the xsd schema from document
   * 
   * @param document
   * @return XSDSchema or null of one does not exist yet for document
   */
  private XSDSchema getXSDSchema(IDocument document)
  {
    XSDSchema schema = null;
    IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
    if (model != null)
    {
      try
      {
        if (model instanceof IDOMModel)
        {
          IDOMDocument domDoc = ((IDOMModel) model).getDocument();
          if (domDoc != null)
          {
            XSDModelAdapter modelAdapter = (XSDModelAdapter) domDoc.getExistingAdapter(XSDModelAdapter.class);
            /*
             * ISSUE: Didn't want to go through initializing schema if it does
             * not already exist, so just attempted to get existing adapter. If
             * doesn't exist, just don't bother working.
             */
            if (modelAdapter != null)
              schema = modelAdapter.getSchema();
          }
        }
      }
      finally
      {
        model.releaseFromRead();
      }
    }
    return schema;
  }
}