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
package org.eclipse.wst.xsd.editor.internal.dialogs;

import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.xsd.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.adt.search.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;

public class NewTypeDialog extends NewComponentDialog implements IComponentDialog
{
  protected static int SIMPLE_TYPE = 0;
  protected static int COMPLEX_TYPE = 1;
  protected Object setObject;
  protected int typeKind;
  protected Object selection;

  public NewTypeDialog()
  {
    super(Display.getCurrent().getActiveShell(), "New Type", "NewType");    
  }
  
  public int createAndOpen()
  {
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
    componentSpecification.setMetaName(typeKind == COMPLEX_TYPE ? IXSDSearchConstants.COMPLEX_TYPE_META_NAME : IXSDSearchConstants.SIMPLE_TYPE_META_NAME);
    componentSpecification.setNew(true);
    return componentSpecification;
  }

  public void setInitialSelection(ComponentSpecification componentSpecification)
  {
    // TODO Auto-generated method stub
  }

  protected void createHeaderContent(Composite parent)
  {
    final Button complexTypeButton = new Button(parent, SWT.RADIO);
    complexTypeButton.setText("Complex Type");
    
    final Button simpleTypeButton = new Button(parent, SWT.RADIO);
    simpleTypeButton.setText("Simple Type");

    SelectionAdapter listener = new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        if (e.widget == simpleTypeButton)
        {
          typeKind = SIMPLE_TYPE;
        }
        else if (e.widget == complexTypeButton)
        {
          typeKind = COMPLEX_TYPE;
        }
      }
    };
    complexTypeButton.setSelection(true);
    typeKind = COMPLEX_TYPE;
    simpleTypeButton.addSelectionListener(listener);
    complexTypeButton.addSelectionListener(listener);
    Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData gd = new GridData(GridData.FILL_BOTH);
    separator.setLayoutData(gd);
  }

  protected String getNormalizedLocation(String location)
  {
    try
    {
      URL url = new URL(location);
      URL resolvedURL = Platform.resolve(url);
      location = resolvedURL.getPath();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return location;
  }
}
