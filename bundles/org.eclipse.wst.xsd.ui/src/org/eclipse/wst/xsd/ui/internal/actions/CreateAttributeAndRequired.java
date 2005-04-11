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
package org.eclipse.wst.xsd.ui.internal.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.eclipse.wst.xml.core.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

/*
 * Class which creates an Attribute and necessary Elements required before
 * an Attribute can be added.  For example, if we wish to add an Attribute
 * to a GlobalElement without a ComplexType, a ComplexType will be created.
 */
public class CreateAttributeAndRequired extends Action {
	String elementTag;
	String label;
	List attributes;
	XSDSchema xsdSchema;
	ISelectionProvider selectionProvider;
	Object parent;
	
	public CreateAttributeAndRequired(String elementTag, String label, List attributes, XSDSchema xsdSchema, ISelectionProvider selProvider, Object parent) {
		super(label);
		
		this.elementTag = elementTag;
		this.label = label;
		this.attributes = attributes;
		this.xsdSchema = xsdSchema;
		this.selectionProvider = selProvider;
		this.parent = parent;
	}

	public void run() {
		if (parent instanceof XSDElementDeclaration) {
			XSDElementDeclaration ed = (XSDElementDeclaration) parent;
			beginRecording(ed.getElement());
	        ed.setTypeDefinition(null);
	      	XSDComplexTypeDefinition td = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
	        ed.setAnonymousTypeDefinition(td);
			
		    CreateElementAction action = new CreateElementAction(label);		    
		    action.setElementTag(elementTag);
		    action.setAttributes(attributes);
		    action.setParentNode(td.getElement());
		    action.setRelativeNode(null);
		    action.setXSDSchema(xsdSchema);
		    action.setSelectionProvider(selectionProvider);
		    action.run();
		    
		    formatChild(td.getElement());
        if (td.getAttributeContents().size() > 0)
        {
		      selectObject(td.getAttributeContents().get(0));
        }
		    endRecording(ed.getElement());
		}
	}

	protected void beginRecording(Element element) {
		((DocumentImpl) element.getOwnerDocument()).getModel().beginRecording(this, getText());
	}
	
	protected void endRecording(Element element) {
		((DocumentImpl) element.getOwnerDocument()).getModel().endRecording(this);
	}
	
	public void selectObject(Object object) {
	    if (selectionProvider != null)
	    {
	        selectionProvider.setSelection(new StructuredSelection(object));
	    }
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
}

