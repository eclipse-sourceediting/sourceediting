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
package org.eclipse.wst.xsd.ui.internal.actions;
import java.util.List;

import org.eclipse.gef.ui.parts.AbstractEditPartViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

// issue (cs) can we remove this?
//
public class CreateElementAction extends Action
{
  protected String description;
  protected Element parentNode;

  protected ISelectionProvider selectionProvider;
  protected XSDSchema xsdSchema;

  protected Object sourceContext;

  /**
   * Constructor for CreateElementAction.
   */
  public CreateElementAction()
  {
    super();
  }
  /**
   * Constructor for CreateElementAction.
   * @param text
   */
  public CreateElementAction(String text)
  {
    super(text);
  }
  /**
   * Constructor for CreateElementAction.
   * @param text
   * @param image
   */
  public CreateElementAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public void setXSDSchema(XSDSchema xsdSchema)
  {
    this.xsdSchema = xsdSchema;
  }
  
  public void setSelectionProvider(ISelectionProvider selectionProvider)
  {
    this.selectionProvider = selectionProvider;
  }

  public void setSourceContext(Object sourceContext)
  {
    this.sourceContext = sourceContext;
  }
  
  /**
   * Gets the parentNode.
   * @return Returns a Element
   */
  public Element getParentNode()
  {
    return parentNode;
  }

  /**
   * Sets the parentNode.
   * @param parentNode The parentNode to set
   */
  public void setParentNode(Element parentNode)
  {
    this.parentNode = parentNode;
  }

  boolean isGlobal = false;
  
  public void setIsGlobal(boolean isGlobal)
  {
    this.isGlobal = isGlobal;
  }
  
  public boolean getIsGlobal()
  {
    return isGlobal;
  }

  protected Node relativeNode;
  protected String elementTag;
  public void setElementTag(String elementTag)
  {
    this.elementTag = elementTag;
  }
  
  public DocumentImpl getDocument()
  {
    return (DocumentImpl) getParentNode().getOwnerDocument();
  }
    
  public void beginRecording(String description)
  {
    getDocument().getModel().beginRecording(this, description);
  }
  
  public void endRecording()
  {
    DocumentImpl doc = getDocument();
    
    doc.getModel().endRecording(this);    
  }
  
  public Element createAndAddNewChildElement()
  {
    String prefix = parentNode.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":"); //$NON-NLS-1$ //$NON-NLS-2$
    Element childNode = getDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + elementTag);
    if (getAttributes() != null)
    {
      List attributes = getAttributes();
      for (int i = 0; i < attributes.size(); i++)
      {
        DOMAttribute attr = (DOMAttribute) attributes.get(i);
        childNode.setAttribute(attr.getName(), attr.getValue());
      }
    }
    if (getRelativeNode() == null)
    {
      parentNode.appendChild(childNode);
    }
    else
    {
      parentNode.insertBefore(childNode,getRelativeNode());
    }
    
    if (isGlobal && getRelativeNode() == null)
    {
      Text textNode = getDocument().createTextNode("\n\n"); //$NON-NLS-1$
      parentNode.appendChild(textNode);
    }
    else if (isGlobal && getRelativeNode() != null)
    {
      Text textNode = getDocument().createTextNode("\n\n"); //$NON-NLS-1$
      parentNode.insertBefore(textNode, getRelativeNode());
    }

    formatChild(childNode);
    
    return childNode;
  }    
    
  protected void formatChild(Element child)
  {
    if (child instanceof IDOMNode)
    {
      IDOMModel model = ((IDOMNode)child).getModel();
      try
      {
        // tell the model that we are about to make a big model change
        model.aboutToChangeModel();
        
	      IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();
		    formatProcessor.formatNode(child);
      }
      finally
      {
        // tell the model that we are done with the big model change
        model.changedModel(); 
      }
    }
  }
  /*
   * @see IAction#run()
   */
  public void run()
  {
    beginRecording(getDescription());
    final Element child = createAndAddNewChildElement();
    endRecording();

    if (selectionProvider != null)
    {
      final XSDConcreteComponent comp = xsdSchema.getCorrespondingComponent(child);
//      selectionProvider.setSelection(new StructuredSelection(comp));
      
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        if (comp instanceof XSDAttributeDeclaration)
        {
          if (((XSDAttributeDeclaration)comp).getContainer() instanceof XSDAttributeUse)
          {
            if (comp.getContainer().getContainer() instanceof XSDAttributeGroupDefinition)
            {
              selectionProvider.setSelection(new StructuredSelection(comp.getContainer()));
            }
            else if (comp.getContainer().getContainer() instanceof XSDComplexTypeDefinition)
            {
              if (XSDDOMHelper.inputEquals(child, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true))
              {
                selectionProvider.setSelection(new StructuredSelection(comp.getContainer()));
              }
              else
              {
                selectionProvider.setSelection(new StructuredSelection(comp));
              }
            }
            else
            {
              selectionProvider.setSelection(new StructuredSelection(comp));
            }
          }
          else
          {
            selectionProvider.setSelection(new StructuredSelection(comp));
          }
        }
        else
        {
          selectionProvider.setSelection(new StructuredSelection(comp));
        }
        if (comp instanceof XSDNamedComponent)
        {
          if (sourceContext instanceof AbstractEditPartViewer)
          {
//            AbstractEditPartViewer viewer = (AbstractEditPartViewer)sourceContext;
          
//            Object obj = viewer.getSelectedEditParts().get(0);
            
//            if (obj instanceof GraphicalEditPart)
//            {
//              if (obj instanceof ElementDeclarationEditPart)
//              {
//                XSDElementDeclaration elem = ((ElementDeclarationEditPart)obj).getXSDElementDeclaration();
//                if (!elem.isElementDeclarationReference())
//                {
//                  ((ElementDeclarationEditPart)obj).doEditName();
//                }
//              }
//              else if (obj instanceof ModelGroupDefinitionEditPart)
//              {
//                XSDModelGroupDefinition group = ((ModelGroupDefinitionEditPart)obj).getXSDModelGroupDefinition();
//                if (!group.isModelGroupDefinitionReference())
//                {
//                  ((ModelGroupDefinitionEditPart)obj).doEditName();
//                }
//              }
//              else if (obj instanceof ComplexTypeDefinitionEditPart)
//              {
//                XSDComplexTypeDefinition ct = ((ComplexTypeDefinitionEditPart)obj).getXSDComplexTypeDefinition();
//                if (ct.getName() != null)
//                {
//                  ((ComplexTypeDefinitionEditPart)obj).doEditName();
//                }
//              }
//              else if (obj instanceof TopLevelComponentEditPart)
//              {
//                ((TopLevelComponentEditPart)obj).doEditName();
//              }
//            }

          }
        }
      }
    };
    Display.getDefault().timerExec(50,runnable);
    }
  }

  /**
   * Gets the relativeNode.
   * @return Returns a Element
   */
  public Node getRelativeNode()
  {
    return relativeNode;
  }

  /**
   * Sets the relativeNode.
   * @param relativeNode The relativeNode to set
   */
  public void setRelativeNode(Node relativeNode)
  {
    this.relativeNode = relativeNode;
  }

  /**
   * Gets the description.
   * @return Returns a String
   */
  public String getDescription()
  {
    if (description == null)
    {
      return getText();
    }
    return description;
  }

  /**
   * Sets the description.
   * @param description The description to set
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  protected List attributes;
  /**
   * Gets the nameAttribute.
   * @return Returns a String
   */
  public List getAttributes()
  {
    return attributes;
  }

  /**
   * Sets the attributes.
   * @param attributes The attributes to set
   */
  public void setAttributes(List attributes)
  {
    this.attributes = attributes;
  }
}
