/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Eugene Ostroukhov, eugene@genuitec.com - [203291] XSD Editor Source State
 *                                              not updated in Outline/Graph
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.text;

import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xsd.ui.internal.util.ModelReconcileAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XSDModelReconcileAdapter extends ModelReconcileAdapter
{
  protected XSDSchema schema;
  protected XSDModelDelayedReconciler delayedReconciler;
  
  public XSDModelReconcileAdapter(Document document, XSDSchema schema)
  {
    super(document);
    this.schema = schema;
    this.delayedReconciler = new XSDModelDelayedReconciler();
  }
  
  protected void handleNodeChanged(Node node)
  {
    // Make sure that the node is in the XSD Schema namespace. We don't need to 
    // reconcile the model for other nodes like text or nodes in other namespaces
    // like the ones normally occurring in annotations.
    
    try
    {
      if (!shouldReconcileModelFor(node))
      {
        return;
      }
    }
    catch (Exception e)
    {
    }

    if (node instanceof Element)
    {  
		Element element = (Element) node;
		if (delayedReconciler.shouldDelay(schema))
		{
			delayedReconciler.elementChanged(element, schema);
		}
		else
		{
	        XSDConcreteComponent concreteComponent = schema.getCorrespondingComponent(node);
			concreteComponent.elementChanged(element);
		}
	}
    else if (node instanceof Document)
    {
      // The document changed so we may need to fix up the 
      // definition's root element
      Document document = (Document)node;    
      Element schemaElement = document.getDocumentElement();
      if (schemaElement != null && schemaElement != schema.getElement())
      {   
        // here we handle the case where a new 'schema' element was added
        //(e.g. the file was totally blank and then we type in the root element)        
        //
        if (schemaElement.getLocalName().equals(XSDConstants.SCHEMA_ELEMENT_TAG))         
        {  
          //System.out.println("****** Setting new schema");
          schema.setElement(schemaElement);
        }
      }      
      else if (schemaElement != null)
      {       
        // handle the case where the definition element's content has changed
        // TODO (cs) do we really need to handle this case?
        schema.elementChanged(schemaElement);
      }      
      else if (schemaElement == null)
      {
        // if there's no root element clear out the schema
        //
        
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=203291
        // Add try-catch to work-around problem at 
        // XSDConcreteComponentImpl.forceNiceRemoveChild(XSDConcreteComponentImpl.java:1696)
        // otherwise the statements below don't get run
        try
        {
          schema.getContents().clear();
        }
        catch (NullPointerException e)
        {
          // workaround
        }

        // TODO (cs) I'm not sure why the above isn't enough
        // to clear out all of the component lists
        // for now I've just added a few more lines to do additional clearing
        //
        // See comment above for https://bugs.eclipse.org/bugs/show_bug.cgi?id=203291
        try { schema.getIncorporatedVersions().clear(); } catch (Exception e) { }
        try { schema.getElementDeclarations().clear(); } catch (Exception e) {}
        try { schema.getTypeDefinitions().clear(); } catch (Exception e) {}
        try { schema.getAttributeDeclarations().clear(); } catch (Exception e) {}
        try { schema.getModelGroupDefinitions().clear(); } catch (Exception e) {}
        try { schema.getAttributeGroupDefinitions().clear(); } catch (Exception e) {}     
        try { schema.setTargetNamespace(null); } catch (Exception e) {}
        try { schema.reset(); } catch (Exception e) {}

        schema.setElement(null);
      }      
    } 
  }

  /**
   * Checks if a change to the given node should trigger a model update.
   * 
   * @param changedNode
   *          the DOM node to test.
   * @return true if the change to the given node should trigger a model update.
   */
  protected boolean shouldReconcileModelFor(Node changedNode)
  {
    // No need to reconcile if the node is in a different namespace as it is the
    // case for nodes deeply nested in appinfo or documentation elements.

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=203291
    if (changedNode instanceof Document)
    {
      return true;
    }

    String nodeNamespace = changedNode.getNamespaceURI();
    String schemaNamespace = schema.getSchemaForSchemaNamespace();
    // If the document node changes, then the nodeNamespace is null
    // so we do want to reconcile.  This change is needed for
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=148842 and
    // read only file support.
    if (!schemaNamespace.equals(nodeNamespace) && nodeNamespace != null)
    {
      return false;
    }

    // When a child node is added to an appinfo or documentation element
    // No need to reconcile if the node is the first child of appinfo or documentation
    // elements.

    Node parentNode = changedNode.getParentNode();

    if (parentNode != null)
    {
      String nodeName = changedNode.getLocalName();

      if (XSDConstants.APPINFO_ELEMENT_TAG.equals(nodeName) || XSDConstants.DOCUMENTATION_ELEMENT_TAG.equals(nodeName))
      {
        return false;
      }
    }
    return true;
  }

  public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty)
  {
    if (!isDirty)
    {
      // TODO need a way to tell the views to stop refreshing (temporarily)
      //
      /*
      schema.reset();
      
      // For some reason type references don't get fixed up when an import is removed
      // even if we call schema.reset() explictly.  To work around this  we iterate thru
      // the type references and recompute them incase a schema did infact change
      //
      for (Iterator i = schema.getElementDeclarations().iterator(); i.hasNext(); )
      {  
        XSDElementDeclarationImpl ed = (XSDElementDeclarationImpl)i.next();
        //ed.elementAttributesChanged(ed.getElement());
        XSDTypeDefinition td = ed.getTypeDefinition();
        td = ed.resolveTypeDefinition(td.getSchema().getTargetNamespace(), td.getName());        
        ed.setTypeDefinition(td);
      }*/  
    }
  }  
}