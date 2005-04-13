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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDSetTypeHelper;
import org.eclipse.wst.xsd.ui.internal.widgets.TypeSection;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TypesSection extends AbstractSection
{
  Text typeCombo;
  Button button;

  String type;
  Object typeObject;
  CLabel typeLabel;
  Table table;
  TypeSection typeSection;
  boolean showAnonymous = true;
  String previousStringType = "";
  boolean isAnonymous;
  int previousType;
  boolean showComplexTypes = true;
  
  /**
   * 
   */
  public TypesSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite = getWidgetFactory().createFlatFormComposite(parent);
    
		FormData data;

		typeCombo = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    typeCombo.setEditable(false);
		typeCombo.addListener(SWT.Modify, this);
    
    button = getWidgetFactory().createButton(composite, "", SWT.PUSH); //$NON-NLS-1$
    button.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif")); //$NON-NLS-1$

    typeLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_TYPE") + ":"); //$NON-NLS-1$

    button.addSelectionListener(this);
		FormData data2 = new FormData();
		data2.left = new FormAttachment(100, -rightMarginSpace + 2);
		data2.right = new FormAttachment(100,0);
		data2.top = new FormAttachment(typeCombo, 0, SWT.CENTER);
		button.setLayoutData(data2);

    data = new FormData();
    data.left = new FormAttachment(0, 100);
    data.right = new FormAttachment(button, 0);
    typeCombo.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
	  Object input = getInput();
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
	  typeCombo.setText(""); //$NON-NLS-1$
	  if (input != null)
	  {
	    Element element = null;
	    if (input instanceof XSDElementDeclaration)
	    {
        XSDElementDeclaration xsdElem = (XSDElementDeclaration)input;
	      element = xsdElem.getElement();
        XSDTypeDefinition typeDef = xsdElem.getTypeDefinition();
        boolean isAnonymous = xsdElem.isAbstract();

        if (isAnonymous)
        {
          typeCombo.setText("**anonymous**"); //$NON-NLS-1$
        }
        else
        {
          String typeName = ""; //$NON-NLS-1$
          if (typeDef != null)
          {
            typeName = typeDef.getQName(getSchema());
            if (typeName == null)
            {
              typeName = ""; //$NON-NLS-1$
            }
            typeCombo.setText(typeName);
          }
          else
          {
            typeCombo.setText(XSDEditorPlugin.getXSDString("_UI_NO_TYPE")); //$NON-NLS-1$
          }
        }
	    }
	    else if (input instanceof XSDAttributeDeclaration)
	    {
	      element = ((XSDAttributeDeclaration)input).getElement();
	    }
      else if (input instanceof XSDAttributeUse)
      {
        XSDAttributeUse attributeUse = (XSDAttributeUse)input;
        XSDAttributeDeclaration attribute = attributeUse.getAttributeDeclaration();
        element = attribute.getElement();
      }
	    else if (input instanceof Element)
	    {
	      element = (Element)input;
	    }
//      else if (input instanceof XSDSimpleTypeDefinition)
//      {
//	      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
//	      Element simpleTypeElement = st.getElement();
//        if (st.getVariety() == XSDVariety.LIST_LITERAL)
//        {
//	        element = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.LIST_ELEMENT_TAG);
//          String result = element.getAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE);
//          if (result == null)
//          {
//            typeCombo.setText("**anonymous**");
//          }
//          else
//          {
//            typeCombo.setText(result);
//          }
//          typeLabel.setText("Item Type:");
//        }
//        else if (st.getVariety() == XSDVariety.ATOMIC_LITERAL)
//        {
//          typeLabel.setText("Base Type:");
//	        element = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.RESTRICTION_ELEMENT_TAG);
//	        if (element == null)
//	        {
//	          element = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.EXTENSION_ELEMENT_TAG);
//	          if (element == null) return;
//	        }
//
//          String result = element.getAttribute(XSDConstants.BASE_ATTRIBUTE);
//          if (result == null)
//          {
//            typeCombo.setText("");
//          }
//          else
//          {
//            typeCombo.setText(result);
//          }
//        }
//        return;
//      }
      
      typeLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_TYPE") + ":"); //$NON-NLS-1$
	    if (element != null)
	    {
        boolean isAnonymous = checkForAnonymousType(element);
        String result = element.getAttribute(XSDConstants.TYPE_ATTRIBUTE);
	      if (isAnonymous)
	      {
	        typeCombo.setText("**anonymous**"); //$NON-NLS-1$
	      }
	      if (result != null && result.equals("")) //$NON-NLS-1$
	      {
	        typeCombo.setText(XSDEditorPlugin.getXSDString("_UI_NO_TYPE")); //$NON-NLS-1$
	      }
	      else if (result != null)
	      {
	        typeCombo.setText(result);
	      }

	    }
	  }
	}

  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == button)
    {
/*        
	    Shell shell = Display.getCurrent().getActiveShell();
	    Object input = getInput();
	    Element element = ((XSDConcreteComponent)getInput()).getElement();
	    TypesDialog dialog;

//	    if (input instanceof XSDSimpleTypeDefinition)
//	    {
//	      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
//	      Element simpleTypeElement = st.getElement();
//	      if (st.getVariety() == XSDVariety.LIST_LITERAL)
//	      {
//	        Element listElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.LIST_ELEMENT_TAG);
//	        dialog = new TypesDialog(shell, listElement, XSDConstants.ITEMTYPE_ATTRIBUTE, xsdSchema);
//          dialog.showComplexTypes = false;
//        }
//        else if (st.getVariety() == XSDVariety.ATOMIC_LITERAL)
//        {
//          Element derivedByElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.RESTRICTION_ELEMENT_TAG);
//          if (derivedByElement == null)
//          {
//            derivedByElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.EXTENSION_ELEMENT_TAG);
//            if (derivedByElement == null) return;
//          }
//          if (derivedByElement != null)
//          {
//            dialog = new TypesDialog(shell, derivedByElement, XSDConstants.BASE_ATTRIBUTE, xsdSchema);
//          }
//          else
//          {
//            return;
//          }
//        }
//        else
//        {
//          dialog = new TypesDialog(shell, element, "type", xsdSchema);
//        }
//	    }
//	    else
//	    {
	      dialog = new TypesDialog(shell, element, "type", xsdSchema); //$NON-NLS-1$
//	    }

	    dialog.setBlockOnOpen(true);
	    dialog.create();
	    int result = dialog.open();
	    
	    if (result == Window.OK)
	    {
	      Object typeObject = dialog.getType();
//		    if (input instanceof XSDSimpleTypeDefinition)
//		    {
//		      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
//		      Element simpleTypeElement = st.getElement();
//	        Element listElement = (Element)domHelper.getChildNode(simpleTypeElement, XSDConstants.LIST_ELEMENT_TAG);
//
//  	      beginRecording("ItemType Change", element);
//          listElement.setAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE, (String)typeObject);
//          endRecording(element);
//		    }
	    }
*/        
        Shell shell = Display.getCurrent().getActiveShell();
        IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();
        
        Object input = getInput();
        XSDSchema schema = null;
        if (input instanceof XSDConcreteComponent) {
            schema = ((XSDConcreteComponent) input).getSchema();
        }
        
        XSDComponentSelectionProvider provider = new XSDComponentSelectionProvider(currentIFile, schema);
        XSDComponentSelectionDialog dialog = new XSDComponentSelectionDialog(shell, XSDEditorPlugin.getXSDString("_UI_LABEL_SET_TYPE"), provider);  // TODO: Externalize This
        if (input instanceof XSDAttributeDeclaration)
        {
          provider.showComplexTypes(false);
        }
        provider.setDialog(dialog);
        
        dialog.setBlockOnOpen(true);
        dialog.create();

        if (dialog.open() == Window.OK) {
            Element element = ((XSDConcreteComponent)getInput()).getElement();
            XSDSetTypeHelper helper = new XSDSetTypeHelper(currentIFile, schema);
            helper.setType(element, "type", dialog.getSelection());
        }        

//      refresh();
    }
  }

  
  
  boolean checkForAnonymousType(Element element)
  {
    /* Using Ed's model to check
     boolean isAnonymous = false;

     XSDConcreteComponent component = getXSDSchema().getCorrespondingComponent(element);
     if (component instanceof XSDElementDeclaration)
     {
     XSDElementDeclaration xsdElem = (XSDElementDeclaration)component;
     isAnonymous = xsdElem.isSetAnonymousTypeDefinition();
     }
     return isAnonymous;
     */

    boolean isAnonymous = false;

    Node aNode = getDomHelper().getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      return true;
    }
    aNode = getDomHelper().getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      isAnonymous = true;
    }
    return isAnonymous;
  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }


}
