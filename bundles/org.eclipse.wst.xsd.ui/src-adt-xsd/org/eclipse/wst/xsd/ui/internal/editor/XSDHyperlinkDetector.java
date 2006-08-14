/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;

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
    XSDConcreteComponent xsdComponent = xsdSchema.getCorrespondingComponent(node);

    String attributeName = null;
    
    if (node instanceof Attr)
    {
      Attr attribute = (Attr)node;
      attributeName = attribute != null ? attribute.getName(): null;
    }
    
    XSDHyperlinkTargetLocator xsdHyperlinkTargetLocator = new XSDHyperlinkTargetLocator();
    XSDConcreteComponent component = xsdHyperlinkTargetLocator.locate(xsdComponent, attributeName); 
    
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
