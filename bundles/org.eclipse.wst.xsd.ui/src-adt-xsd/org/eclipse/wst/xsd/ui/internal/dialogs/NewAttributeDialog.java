/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDSchema;

public class NewAttributeDialog extends NewComponentDialog implements IComponentDialog
{
  protected XSDSchema schema;
  protected Object setObject;
  protected int typeKind;
  protected Object selection;

  public NewAttributeDialog()
  {
    super(Display.getCurrent().getActiveShell(), Messages._UI_LABEL_NEW_ATTRIBUTE, "NewAttribute");     //$NON-NLS-1$
  }

  public NewAttributeDialog(XSDSchema schema)
  {
    super(Display.getCurrent().getActiveShell(), Messages._UI_LABEL_NEW_ATTRIBUTE, "NewAttribute");     //$NON-NLS-1$
    this.schema = schema;
  }
  
  private void setup() {
    if (schema != null) {
      List usedNames = getUsedElementNames();
      setUsedNames(usedNames);
      setDefaultName(XSDCommonUIUtils.createUniqueElementName("NewAttribute", schema.getAttributeDeclarations()));
    }
  }
  
  public int createAndOpen()
  {
  setup();
    int returnCode = super.createAndOpen();
    if (returnCode == 0)
    {
      if (setObject instanceof Adapter)
      {  
        //Command command = new AddComplexTypeDefinitionCommand(getName(), schema);
      }        
    }  
    return returnCode;
  }

  public ComponentSpecification getSelectedComponent()
  {
    ComponentSpecification componentSpecification =  new ComponentSpecification(null, getName(), null);    
    componentSpecification.setMetaName(IXSDSearchConstants.ATTRIBUTE_META_NAME);
    componentSpecification.setNew(true);
    return componentSpecification;
  }

  public void setInitialSelection(ComponentSpecification componentSpecification)
  {
    // TODO Auto-generated method stub
  }
  
  private List getUsedElementNames() {
    List usedNames = new ArrayList();     
    if (schema != null ) {
      List elementsList = schema.getAttributeDeclarations();
      Iterator elements = elementsList.iterator(); 
      while (elements.hasNext()) {
        usedNames.add(((XSDAttributeDeclaration) elements.next()).getName());
      }
    }
    
    return usedNames;
  }

}
