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

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class SimpleContentBaseTypeOptionsDialog extends TypesDialog
{
  /**
   * @param parentShell
   * @param element
   * @param id
   * @param xsdSchema
   */
  public SimpleContentBaseTypeOptionsDialog(Shell parentShell, Element element, Object id, XSDSchema xsdSchema)
  {
    super(parentShell, element, id, xsdSchema);
    showAnonymous = false;
  }
  
	protected void ok()
	{
		TableItem[] items = table.getItems();
		int selection = table.getSelectionIndex();
		if (items != null && items.length > 0 && selection >= 0)
		 {
		  typeObject = items[selection].getData();
		}
	}

  
  public void handleSetInput()
  {
    XSDDOMHelper domHelper = new XSDDOMHelper();
    typeSection.getSimpleType().setSelection(false);
    typeSection.getUserSimpleType().setSelection(false);
    typeSection.getUserComplexType().setSelection(false);
    showAnonymous = false;
    if (element != null)
    {
      String derivedBy = domHelper.getDerivedByName(element);
      String baseType = domHelper.getBaseType(element);
      boolean derivedByRestriction = true;
      
      if (XSDDOMHelper.inputEquals(element, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
      {
        typeSection.getSimpleType().setEnabled(false);
        typeSection.getUserSimpleType().setEnabled(false);
        typeSection.getUserComplexType().setSelection(true);

        previousType = 3;
      }
      else if (XSDDOMHelper.inputEquals(element, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
      {
        typeSection.getSimpleType().setEnabled(false);
        typeSection.getUserSimpleType().setEnabled(false);

        if (derivedBy.equals("restriction"))
        {
          typeSection.getSimpleType().setEnabled(false);
          typeSection.getUserSimpleType().setEnabled(false);
          typeSection.getUserComplexType().setEnabled(true);
        }
        else if (derivedBy.equals("extension"))
        {
          derivedByRestriction = false;
          typeSection.getSimpleType().setEnabled(true);
          typeSection.getUserSimpleType().setEnabled(true);
          typeSection.getUserComplexType().setEnabled(true);
        }
      }
        
      if (derivedBy != null)
      {
          if (baseType != null && !baseType.equals(""))
          {
            Element parent = (Element)element.getParentNode();
            XSDConcreteComponent component = null;
            if (parent != null)
            {
              component = xsdSchema.getCorrespondingComponent(parent);
            }
            XSDTypeDefinition baseTypeDefinition = null;
            if (component instanceof XSDComplexTypeDefinition)
            {
              XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)component;
              baseTypeDefinition = complexType.getBaseTypeDefinition();
            }               

            if (typeSection.getBuiltInTypeNamesList(xsdSchema).contains(baseType) && !derivedByRestriction)
            {
              typeSection.getSimpleType().setSelection(true);
              populateBuiltInType();
              int i = typeSection.getBuiltInTypeNamesList(xsdSchema).indexOf(baseType);
              table.setSelection(i);
              previousType = 1;
            }
            else if (baseTypeDefinition instanceof XSDSimpleTypeDefinition && !derivedByRestriction)
            {
              typeSection.getUserSimpleType().setSelection(true);
              populateUserSimpleType();
              int i = typeSection.getUserSimpleTypeNamesList(xsdSchema).indexOf(baseType);
              if (showAnonymous)
               {
                table.setSelection(i + 1);
              }
              else
               {
                table.setSelection(i);
              }
              previousType = 2;
            }
            else if (baseTypeDefinition instanceof XSDComplexTypeDefinition)
            {
              typeSection.getUserComplexType().setSelection(true);
              populateUserComplexType();
              int i = typeSection.getUserComplexTypeNamesList(xsdSchema).indexOf(baseType);
              if (showAnonymous)
               {
                table.setSelection(i + 1);
              }
              else
               {
                table.setSelection(i);
              }
              previousType = 3;
            }
          }
          else
          {
            typeSection.getUserComplexType().setSelection(true);
            populateUserComplexType();
            table.setSelection(0);
          }
        }

    }
  }

}
