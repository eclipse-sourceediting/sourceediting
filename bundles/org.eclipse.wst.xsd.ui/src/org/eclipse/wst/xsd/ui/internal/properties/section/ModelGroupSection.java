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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.ui.properties.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDModelGroup;
import org.w3c.dom.Element;

public class ModelGroupSection extends AbstractSection
{
  CCombo modelGroupCombo;
  private String[] modelGroupComboValues = { "sequence", "choice", "all" }; //$NON-NLS-1$
  
  /**
   * 
   */
  public ModelGroupSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		modelGroupCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, 0);
		modelGroupCombo.setLayoutData(data);
		modelGroupCombo.addSelectionListener(this);
		modelGroupCombo.setItems(modelGroupComboValues);

		CLabel cLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_KIND")); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(modelGroupCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(modelGroupCombo, 0, SWT.CENTER);
		cLabel.setLayoutData(data);

	}
 	
	/*
	 * @see org.eclipse.wst.common.ui.properties.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
	  setListenerEnabled(false);
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
	  Object input = getInput();
	  if (input != null)
	  {
      if (input instanceof XSDModelGroup)
	    {
	      XSDModelGroup particle = (XSDModelGroup)input;
	      String modelType = particle.getCompositor().getName();
	      modelGroupCombo.setText(modelType);
	    }
	  }
	  setListenerEnabled(true);
	}

  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == modelGroupCombo)
    {
      Object input = getInput();
	    if (input instanceof XSDModelGroup)
	    {
	      XSDModelGroup particle = (XSDModelGroup)input;

        Element element = particle.getElement();
        Element parent = (Element)element.getParentNode();
        beginRecording(XSDEditorPlugin.getXSDString("_UI_GROUP_SCOPE_CHANGE"), parent); //$NON-NLS-1$
//        changeContentModel(parent, modelGroupCombo.getText());
        particle.setCompositor(XSDCompositor.get(modelGroupCombo.getText()));      
        endRecording(parent);
        refresh();
	    }
    }
  }

//  private void changeContentModel(Element parent, String contentModel)
//  {
//    Document doc = parent.getOwnerDocument();
//    XSDDOMHelper domHelper = getDomHelper();
//  
//    String prefix = parent.getPrefix();
//    prefix = prefix == null ? "" : prefix + ":";
//    
//    Element contentModelElement = domHelper.getContentModelFromParent(parent);
//  
//    if (contentModelElement.getLocalName().equals(contentModel))
//    {
//      return; // it's already the content model 
//    }
//  
//    Element newNode = doc.createElementNS(XSDDOMHelper.XMLSchemaURI, prefix + contentModel);
//  
//    if (contentModelElement.hasChildNodes())
//    {        
//      NodeList nodes = contentModelElement.getChildNodes();
//      // use clones so we don't have a refresh problem
//      for (int i = 0; i < nodes.getLength(); i++)
//      {
//        Node node = nodes.item(i);
//        newNode.appendChild(node.cloneNode(true)); 
//      }
//    }
//    parent.replaceChild(newNode, contentModelElement);
//  }

}
