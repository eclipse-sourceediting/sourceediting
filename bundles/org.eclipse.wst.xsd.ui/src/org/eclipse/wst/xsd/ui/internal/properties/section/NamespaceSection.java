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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.XSDEditNamespacesAction;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.TargetNamespaceChangeHandler;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDSchemaHelper;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NamespaceSection extends AbstractSection
{
	  IWorkbenchPart part;
	  Text prefixText;
	  Text targetNamespaceText;
    Button editButton;
    StyledText errorText;
    Color red;
	  
	  /**
	   * 
	   */
	  public NamespaceSection()
	  {
	    super();
	  }

		/**
		 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
		 */
		public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
		{
			super.createControls(parent, factory);
			Composite composite = getWidgetFactory().createFlatFormComposite(parent);
      
			// Create Prefix Label
			CLabel prefixLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_SCHEMA_PREFIX")); //$NON-NLS-1$

      int leftCoordinate = getStandardLabelWidth(composite, 
          new String[] {XSDEditorPlugin.getXSDString("_UI_LABEL_TARGET_NAME_SPACE"), XSDEditorPlugin.getXSDString("_UI_LABEL_SCHEMA_PREFIX")});
      
			// Create Prefix Text
			prefixText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
			FormData prefixTextData = new FormData();
			// prefixTextData.left = new FormAttachment(0, 115);
      prefixTextData.left = new FormAttachment(0, leftCoordinate);
			prefixTextData.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
			prefixText.setLayoutData(prefixTextData);
			prefixText.addListener(SWT.Modify, this);		    
		    
			FormData data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(prefixText, -ITabbedPropertyConstants.HSPACE);
      data.top = new FormAttachment(prefixText, 0, SWT.CENTER);
			prefixLabel.setLayoutData(data);

			// Create TargetNamespace Label
			CLabel targetNamespaceLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_TARGET_NAME_SPACE")); //$NON-NLS-1$
      
      // Create TargetNamespace Text
      targetNamespaceText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
      
			FormData tnsLabelData = new FormData();
			tnsLabelData.left = new FormAttachment(0, 0);
      tnsLabelData.right = new FormAttachment(targetNamespaceText, -ITabbedPropertyConstants.HSPACE); 
			tnsLabelData.top = new FormAttachment(targetNamespaceText, 0, SWT.CENTER);
			targetNamespaceLabel.setLayoutData(tnsLabelData);

      FormData tnsTextData = new FormData();
      // tnsTextData.left = new FormAttachment(0, 115);
      tnsTextData.left = new FormAttachment(0, leftCoordinate);
      tnsTextData.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
      tnsTextData.top = new FormAttachment(prefixText, +ITabbedPropertyConstants.VSPACE);
      targetNamespaceText.setLayoutData(tnsTextData);
      targetNamespaceText.addListener(SWT.Modify, this);
      
      // Advanced Button
      editButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_SECTION_ADVANCED_ATTRIBUTES") + "...", SWT.PUSH);

      FormData buttonFormData = new FormData();
      buttonFormData.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
      buttonFormData.top = new FormAttachment(targetNamespaceText, +ITabbedPropertyConstants.VSPACE);
      editButton.setLayoutData(buttonFormData);
      editButton.addSelectionListener(this);

      // error text
      errorText = new StyledText(composite, SWT.FLAT);
      errorText.setEditable(false);
      errorText.setEnabled(false);
      errorText.setText("");
      
      data = new FormData();
      data.left = new FormAttachment(targetNamespaceText, 0, SWT.LEFT);
      data.right = new FormAttachment(100, 0);
      data.top = new FormAttachment(editButton, 0);
      errorText.setLayoutData(data);
    }

		/*
		 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
		 */
		public void refresh()
		{
      if (doRefresh)
      {
        // hack...open bug against properties
        if (prefixText.isDisposed() || targetNamespaceText.isDisposed())
        {
          return;
        }
        if (prefixText.isFocusControl() || targetNamespaceText.isFocusControl())
        {
          return;
        }

        setListenerEnabled(false);
	    	 
			  Element element = xsdSchema.getElement();
	    	
        if (element != null)
        {
  		    // Handle prefixText
  			  TypesHelper helper = new TypesHelper(xsdSchema);
    		  String aPrefix = helper.getPrefix(element.getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE), false);
        
          if (aPrefix != null && aPrefix.length() > 0)
  	      {
  	  	  	prefixText.setText(aPrefix);
  	  	  }
  	  	  else
          {
  			  	prefixText.setText(""); //$NON-NLS-1$
  			  }
  	    	 
  				// Handle TargetNamespaceText
  				String tns = element.getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE);
  			  if (tns != null && tns.length() > 0)
          {
   	        targetNamespaceText.setText(tns);
   	      }
  			  else
          {
  				  targetNamespaceText.setText(""); //$NON-NLS-1$
  			  }
          errorText.setText("");
        }
				setListenerEnabled(true);
			}
		}
		
		public void doHandleEvent(Event event)
		{
      errorText.setText("");
      String prefixValue = prefixText.getText();
      String tnsValue = targetNamespaceText.getText();
      if (tnsValue.trim().length() == 0)
      {
        if (prefixValue.trim().length() > 0)
        {
          errorText.setText(XSDEditorPlugin.getXSDString("_ERROR_TARGET_NAMESPACE_AND_PREFIX"));
          int length = errorText.getText().length();
          red = new Color(null, 255, 0, 0);
          StyleRange style = new StyleRange(0, length, red, targetNamespaceText.getBackground());
          errorText.setStyleRange(style);
          return;
        }
      }

		  if (event.widget == prefixText)
		  {
        updateNamespaceInfo(prefixValue, tnsValue);
		  }
		  else if (event.widget == targetNamespaceText)
      {
		  	TypesHelper helper = new TypesHelper(xsdSchema);
			  String aPrefix = helper.getPrefix(xsdSchema.getElement().getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE), false);
		  	// updateNamespaceInfo(aPrefix, tnsValue);
        updateNamespaceInfo(prefixValue, tnsValue);
		  }
		}
		
		public void doWidgetSelected(SelectionEvent e) {
			if (e.widget == editButton) {
				XSDEditNamespacesAction nsAction = new XSDEditNamespacesAction(XSDEditorPlugin.getXSDString("_UI_ACTION_EDIT_NAMESPACES"), xsdSchema.getElement(), null, xsdSchema); //$NON-NLS-1$ 
				nsAction.run();
				refresh();				
			}			
		}

	  /* (non-Javadoc)
	   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
	   */
	  public boolean shouldUseExtraSpace()
	  {
	    return true;
	  }

	  private void updateNamespaceInfo(String newPrefix, String newTargetNamespace)
	  {
	    Element element = xsdSchema.getElement();
	    DocumentImpl doc = (DocumentImpl)element.getOwnerDocument();

	    String modelTargetNamespace = xsdSchema.getTargetNamespace();
	    String oldNamespace = xsdSchema.getTargetNamespace();
	    
	    TypesHelper helper = new TypesHelper(xsdSchema);
   		String oldPrefix = helper.getPrefix(element.getAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE), false);
	    
	    if (modelTargetNamespace == null)
	    {
	      modelTargetNamespace = ""; //$NON-NLS-1$
	    }
	        
	    String targetNamespace = newTargetNamespace.trim(); 
	    String prefix = newPrefix.trim();

	    if (!validatePrefix(prefix) || !validateTargetNamespace(targetNamespace))
	    {
	      return;
	    }
	        
	    if (prefix.length() > 0 && targetNamespace.length() == 0)
	    {
	       // can't have blank targetnamespace and yet specify a prefix
	       return;
	    }

	    doc.getModel().beginRecording(this, XSDEditorPlugin.getXSDString("_UI_TARGETNAMESPACE_CHANGE")); //$NON-NLS-1$
	    String xsdForXSDPrefix = xsdSchema.getSchemaForSchemaQNamePrefix();
	    Map map = xsdSchema.getQNamePrefixToNamespaceMap();

//	 For debugging
//	        System.out.println("1. SW Map is " + map.values());
//	        System.out.println("1. SW Map keys are " + map.keySet());

	    // Check if prefix is blank
	    // if it is, then make sure we have a prefix 
	    // for schema for schema
	    if (prefix.length() == 0)
	    {
	      // if prefix for schema for schema is blank
	      // then set it to value specified in preference
	      // and update ALL nodes with this prefix
	      if (xsdForXSDPrefix == null || (xsdForXSDPrefix != null && xsdForXSDPrefix.trim().length() == 0))
	      {
	        // get preference prefix
	        xsdForXSDPrefix = XSDEditorPlugin.getPlugin().getXMLSchemaPrefix();
	        // get a unique prefix by checking what's in the map

	        xsdForXSDPrefix = getUniqueSchemaForSchemaPrefix(xsdForXSDPrefix, map);
	        element.setAttribute("xmlns:" + xsdForXSDPrefix, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001); //$NON-NLS-1$

	        updateAllNodes(element, xsdForXSDPrefix);
	            
	        // remove the old xmlns attribute for the schema for schema
	        if (element.getAttribute("xmlns") != null && //$NON-NLS-1$
	            element.getAttribute("xmlns").equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001)) //$NON-NLS-1$
	        {
	          element.removeAttribute("xmlns"); //$NON-NLS-1$
	        }
	      }
	    }

	    if (targetNamespace.length() > 0 ||
	       (targetNamespace.length() == 0 && prefix.length() == 0))
	    {
	      // clean up the old prefix for this schema
	      if (oldPrefix != null && oldPrefix.length() > 0)
	      {
	        element.removeAttribute("xmlns:"+oldPrefix); //$NON-NLS-1$
//	            element.setAttribute("xmlns:" + prefix, targetNamespace);
//	            java.util.Map prefixToNameSpaceMap = xsdSchema.getQNamePrefixToNamespaceMap();
//	            prefixToNameSpaceMap.remove(oldPrefix);
	      }
	      else // if no prefix
	      {
	        if (element.getAttribute("xmlns") != null) //$NON-NLS-1$
	        {
	          if (!element.getAttribute("xmlns").equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001)) //$NON-NLS-1$
	           {
	            element.removeAttribute("xmlns"); //$NON-NLS-1$
	          }
	        }
	      }
	    }

	    if (targetNamespace.length() > 0)
	    {
	      if (!modelTargetNamespace.equals(targetNamespace))
	      {
	        element.setAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE, targetNamespace);
	      }
	      // now set the new xmlns:prefix attribute
	      if (prefix.length() > 0)
	      {
	        element.setAttribute("xmlns:" + prefix, targetNamespace); //$NON-NLS-1$
	      }
	      else
	      {
	        element.setAttribute("xmlns", targetNamespace); //$NON-NLS-1$
	      }
	      // set the targetNamespace attribute
	    }
	    else // else targetNamespace is blank
	    {
	      if (prefix.length() == 0)
	      {
	        element.removeAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE);
	      }
	    }

//	    System.out.println("1.5 SW Map is " + map.values());
//	    System.out.println("1.5 SW Map keys are " + map.keySet());
	    
	    // do our own referential integrity
	    TargetNamespaceChangeHandler targetNamespaceChangeHandler = new TargetNamespaceChangeHandler(xsdSchema, oldNamespace, targetNamespace);
	    targetNamespaceChangeHandler.resolve();
	    
	    XSDSchemaHelper.updateElement(xsdSchema);
	    
	    doc.getModel().endRecording(this);

//	 For debugging
//	        map = xsdSchema.getQNamePrefixToNamespaceMap();
//	        System.out.println("2. SW Map is " + map.values());
//	        System.out.println("2. SW Map keys are " + map.keySet());
	  }

	  private String getUniqueSchemaForSchemaPrefix(String xsdForXSDPrefix, Map map)
	  {
	    if (xsdForXSDPrefix == null || (xsdForXSDPrefix != null && xsdForXSDPrefix.trim().length() == 0))
	     {       
	      xsdForXSDPrefix = "xsd"; //$NON-NLS-1$
	    }
	    // ensure prefix is unique
	    int prefixExtension = 1;
	    while (map.containsKey(xsdForXSDPrefix) && prefixExtension < 100)
	     {
	      xsdForXSDPrefix = xsdForXSDPrefix + String.valueOf(prefixExtension);
	      prefixExtension++;
	    }
	    return xsdForXSDPrefix;
	  }
	  
	  private void updateAllNodes(Element element, String prefix)
	  {
	    element.setPrefix(prefix);
	    NodeList list = element.getChildNodes();
	    if (list != null)
	    {
	      for (int i=0; i < list.getLength(); i++)
	      {
	        Node child = list.item(i);
	        if (child != null && child instanceof Element)
	        {
	          child.setPrefix(prefix);
	          if (child.hasChildNodes())
	          {
	            updateAllNodes((Element)child, prefix);
	          }
	        }
	      }
	    }   
	  }
	  
	  private boolean validateTargetNamespace(String ns)
	  {
	    // will allow blank namespace !!
	    if (ns.equals(""))
	     {
	      return true;
	    }
	    
	    String errorMessage = null;
      try
      {
        URI testURI = new URI(ns);
      }
      catch (URISyntaxException e)
      {
	      errorMessage = XSDEditorPlugin.getXSDString("_WARN_INVALID_TARGET_NAMESPACE"); //$NON-NLS-1$
	    }
	    
	    if (errorMessage == null || errorMessage.length() == 0)
	     {
	      return true;
	    }
	    return false;
	  }
    
    public void dispose()
    {
      super.dispose();
      if (red != null)
      {
        red.dispose();
        red = null;
      }
    }
}
