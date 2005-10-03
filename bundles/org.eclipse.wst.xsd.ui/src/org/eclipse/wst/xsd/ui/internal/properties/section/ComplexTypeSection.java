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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.SetBaseTypeAction;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xml.XMLComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDSetTypeHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class ComplexTypeSection extends AbstractSection
{
  private String derivedByChoicesComboValues[] =
  {
        "",
        XSDConstants.RESTRICTION_ELEMENT_TAG,
        XSDConstants.EXTENSION_ELEMENT_TAG
  };

  /**
   * 
   */
  public ComplexTypeSection()
  {
    super();
  }

	Text baseTypeCombo;
	CCombo derivedByCombo;
	Button button;

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite =	getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		String textBaseType = XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE_WITH_COLON");
		String textDerivedType = XSDEditorPlugin.getXSDString("_UI_LABEL_DERIVED_BY");
		GC gc = new GC(parent);
		int xoffset = Math.max(100, gc.textExtent(textBaseType).x + 20); // adds 20 due to borders
		xoffset = Math.max(xoffset, gc.textExtent(textDerivedType).x + 20); // adds 20 due to borders
		gc.dispose();

		baseTypeCombo = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		CLabel baseTypeLabel = getWidgetFactory().createCLabel(composite, textBaseType); //$NON-NLS-1$ 

    button = getWidgetFactory().createButton(composite, "", SWT.PUSH);
    button.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif")); //$NON-NLS-1$
    baseTypeCombo.setEditable(false);
    baseTypeCombo.addListener(SWT.Modify, this);

		data = new FormData();
		data.left = new FormAttachment(0, xoffset);
    data.right = new FormAttachment(button, 0);
		baseTypeCombo.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(baseTypeCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(button, 0, SWT.CENTER);
		baseTypeLabel.setLayoutData(data);

    button.addSelectionListener(this);
		data = new FormData();
    data.left = new FormAttachment(100, -rightMarginSpace + 2);
		data.right = new FormAttachment(100,0);
    data.top = new FormAttachment(baseTypeCombo, 0, SWT.CENTER);
		button.setLayoutData(data);
		
		derivedByCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(0, xoffset);
		data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(baseTypeCombo, +ITabbedPropertyConstants.VSPACE);
		derivedByCombo.setLayoutData(data);
		derivedByCombo.setItems(derivedByChoicesComboValues);
		derivedByCombo.addSelectionListener(this);

		CLabel derivedByLabel = getWidgetFactory().createCLabel(composite, textDerivedType); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(derivedByCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(derivedByCombo, 0, SWT.CENTER);
		derivedByLabel.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
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
	  baseTypeCombo.setText(""); //$NON-NLS-1$
	  
    if (input instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)input;
      Element element = complexType.getElement();
      Element contentModelElement = getDomHelper().getContentModelFromParent(element);    
      String baseType = getDomHelper().getBaseType(contentModelElement);
    
      derivedByCombo.setText(getDomHelper().getDerivedByName(contentModelElement));

      if (baseType != null)
      {
        baseTypeCombo.setText(baseType);
      }
    }	  
	  
	  setListenerEnabled(true);
	}
	
  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
    XSDComplexTypeDefinition xsdComplexType = (XSDComplexTypeDefinition)getInput();
    Element ctElement = xsdComplexType.getElement();
    if (e.widget == button)
    {
	    Shell shell = Display.getCurrent().getActiveShell();
	    Element element = null;
	    if (xsdComplexType.getContent() != null)
	    {
  	    element = ((XSDComplexTypeDefinition)getInput()).getContent().getElement();
	    }
	    
//	    SimpleContentBaseTypeOptionsDialog dialog = new SimpleContentBaseTypeOptionsDialog(shell, element, BASE_TYPE_ID, ((XSDConcreteComponent)getInput()).getSchema());
//	    dialog.setBlockOnOpen(true);
//	    dialog.create();
//	    int result = dialog.open();
        
        IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();
        
        Object input = getInput();
        XSDSchema schema = null;
        if (input instanceof XSDConcreteComponent) {
            schema = ((XSDConcreteComponent) input).getSchema();
        }
        
        XSDComponentSelectionProvider provider = new XSDComponentSelectionProvider(currentIFile, schema);
        XSDComponentSelectionDialog dialog = new XSDComponentSelectionDialog(shell, XSDEditorPlugin.getXSDString("_UI_LABEL_SET_TYPE"), provider);
        provider.setDialog(dialog);
        dialog.setBlockOnOpen(true);
        dialog.create();
        int result = dialog.open();
   
	    if (result == Window.OK)
	    {
            XMLComponentSpecification spec = dialog.getSelection();            
            XSDSetTypeHelper helper = new XSDSetTypeHelper(currentIFile, schema);
            helper.addImportIfNecessary(element, spec);
            
            String typeString = helper.getPrefixedTypeName(spec);
            String derivedBy = getDomHelper().getDerivedByName(element);
            SetBaseTypeAction setBaseTypeAction = new SetBaseTypeAction(XSDEditorPlugin.getXSDString("_UI_LABEL_SET_BASE_TYPE")); //$NON-NLS-1$
            setBaseTypeAction.setXSDSchema(xsdSchema);
            setBaseTypeAction.setComplexTypeElement(ctElement);
            setBaseTypeAction.setType(typeString);
            setBaseTypeAction.setDerivedBy(derivedBy);
            setBaseTypeAction.performAction();

	    }

//      refresh();
    }
    else if (e.widget == derivedByCombo)
    {
      Element contentModelElement = getDomHelper().getContentModelFromParent(ctElement);
      String baseType = getDomHelper().getBaseType(contentModelElement);
      beginRecording(XSDEditorPlugin.getXSDString("_UI_DERIVEDBY_CHANGE"), ctElement); //$NON-NLS-1$
      if (contentModelElement != null)
      {
        getDomHelper().changeDerivedByType(contentModelElement, derivedByCombo.getText(), baseType);
      }
      endRecording(ctElement);
    }
  }


  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return false;
  }

}
