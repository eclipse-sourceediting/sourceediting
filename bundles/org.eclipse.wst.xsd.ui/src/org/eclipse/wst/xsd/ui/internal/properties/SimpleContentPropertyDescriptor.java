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
package org.eclipse.wst.xsd.ui.internal.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xsd.ui.internal.properties.section.SimpleContentBaseTypeOptionsDialog;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

public class SimpleContentPropertyDescriptor extends TypesPropertyDescriptor
{
  public SimpleContentPropertyDescriptor(Object id, String displayName, Element element, XSDSchema xsdSchema)
  {
    super(id, displayName, element, xsdSchema);
  }
  
  public CellEditor createPropertyEditor(Composite parent)
  {
    // CellEditor editor = new SimpleContentBaseTypeOptionsTextCellEditor(parent);
    CellEditor editor = new SimpleContentBaseTypeDialogCellEditor(parent);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }
  
  public class SimpleContentBaseTypeDialogCellEditor extends TypesDialogCellEditor
  {

    /**
     * Creates a new Font dialog cell editor parented under the given control.
     * The cell editor value is <code>null</code> initially, and has no 
     * validator.
     *
     * @param parent the parent control
     */
    protected SimpleContentBaseTypeDialogCellEditor(Composite parent)
    {
      super(parent);
    }

    protected Object openDialogBox(Control cellEditorWindow)
    {
	    Shell shell = Display.getCurrent().getActiveShell();
	    
	    // SimpleContentBaseTypeOptionsDialog dialog = new SimpleContentBaseTypeOptionsDialog(shell);
	    SimpleContentBaseTypeOptionsDialog dialog = new SimpleContentBaseTypeOptionsDialog(shell, element, property, xsdSchema);

	    dialog.setBlockOnOpen(true);
	    dialog.create();
	    
	    String value = (String)getValue();
	
	    int result = dialog.open();
	
	    if (result == Window.OK)
	    {
	      value = dialog.getType();
	      return value;
	    }
	    deactivate();
	    return value;
	  }
  }

//  class SimpleContentBaseTypeOptionsDialog extends TypesDialog
//  {
//    public SimpleContentBaseTypeOptionsDialog(Shell shell)
//    {
//      super(shell);
//      showAnonymous = false;
//    }
//
////    protected void ok()
////    {
////      TableItem[] items = table.getItems();
////      selection = table.getSelectionIndex();
////      if (items != null && items.length > 0 && selection >= 0)
////       {
////        typeObject = items[selection].getData();
////      }
////      System.out.println("typeObject is " + typeObject);
////
////      doSetValue(typeObject);
////      applyEditorValueAndDeactivate();
////      dialog.close();
////    }
//    
//    public void handleSetInput()
//    {
//      XSDDOMHelper domHelper = new XSDDOMHelper();
//      typeSection.getSimpleType().setSelection(false);
//      typeSection.getUserSimpleType().setSelection(false);
//      typeSection.getUserComplexType().setSelection(false);
//      showAnonymous = false;
//      if (element != null)
//      {
//        String derivedBy = domHelper.getDerivedByName(element);
//        String baseType = domHelper.getBaseType(element);
//        boolean derivedByRestriction = true;
//        
//        if (XSDDOMHelper.inputEquals(element, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
//        {
//          typeSection.getSimpleType().setEnabled(false);
//          typeSection.getUserSimpleType().setEnabled(false);
//          typeSection.getUserComplexType().setSelection(true);
//
//          previousType = 3;
//        }
//        else if (XSDDOMHelper.inputEquals(element, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
//        {
//          typeSection.getSimpleType().setEnabled(false);
//          typeSection.getUserSimpleType().setEnabled(false);
//
//          if (derivedBy.equals("restriction"))
//          {
//            typeSection.getSimpleType().setEnabled(false);
//            typeSection.getUserSimpleType().setEnabled(false);
//            typeSection.getUserComplexType().setEnabled(true);
//          }
//          else if (derivedBy.equals("extension"))
//          {
//            derivedByRestriction = false;
//            typeSection.getSimpleType().setEnabled(true);
//            typeSection.getUserSimpleType().setEnabled(true);
//            typeSection.getUserComplexType().setEnabled(true);
//          }
//        }
//          
//        if (derivedBy != null)
//        {
//            if (baseType != null && !baseType.equals(""))
//            {
//              Element parent = (Element)element.getParentNode();
//              XSDConcreteComponent component = null;
//              if (parent != null)
//              {
//                component = xsdSchema.getCorrespondingComponent(parent);
//              }
//              XSDTypeDefinition baseTypeDefinition = null;
//              if (component instanceof XSDComplexTypeDefinition)
//              {
//                XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)component;
//                baseTypeDefinition = complexType.getBaseTypeDefinition();
//              }               
//
//              if (typeSection.getBuiltInTypeNamesList(xsdSchema).contains(baseType) && !derivedByRestriction)
//              {
//                typeSection.getSimpleType().setSelection(true);
//                populateBuiltInType();
//                int i = typeSection.getBuiltInTypeNamesList(xsdSchema).indexOf(baseType);
//                table.setSelection(i);
//                previousType = 1;
//              }
//              else if (baseTypeDefinition instanceof XSDSimpleTypeDefinition && !derivedByRestriction)
//              {
//                typeSection.getUserSimpleType().setSelection(true);
//                populateUserSimpleType();
//                int i = typeSection.getUserSimpleTypeNamesList(xsdSchema).indexOf(baseType);
//                if (showAnonymous)
//                 {
//                  table.setSelection(i + 1);
//                }
//                else
//                 {
//                  table.setSelection(i);
//                }
//                previousType = 2;
//              }
//              else if (baseTypeDefinition instanceof XSDComplexTypeDefinition)
//              {
//                typeSection.getUserComplexType().setSelection(true);
//                populateUserComplexType();
//                int i = typeSection.getUserComplexTypeNamesList(xsdSchema).indexOf(baseType);
//                if (showAnonymous)
//                 {
//                  table.setSelection(i + 1);
//                }
//                else
//                 {
//                  table.setSelection(i);
//                }
//                previousType = 3;
//              }
//            }
//            else
//            {
//              typeSection.getUserComplexType().setSelection(true);
//              populateUserComplexType();
//              table.setSelection(0);
//            }
//          }
//
//      }
//    }
//  }
}
