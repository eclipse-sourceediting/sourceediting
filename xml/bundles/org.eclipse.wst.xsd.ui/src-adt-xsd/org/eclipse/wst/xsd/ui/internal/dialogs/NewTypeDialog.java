/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

public class NewTypeDialog extends NewComponentDialog implements IComponentDialog
{
  protected XSDSchema schema;
  protected static int SIMPLE_TYPE = 0;
  protected static int COMPLEX_TYPE = 1;
  protected Object setObject;
  protected int typeKind;
  protected Object selection;
  protected boolean allowComplexType = true;
  protected boolean allowAnonymousType = true;
  protected boolean anonymousType = false;
  private Button complexTypeButton;
  private Button simpleTypeButton;
  private Button anonymousTypeCheckBox;

  public NewTypeDialog()
  {
    super(Display.getCurrent().getActiveShell(), Messages._UI_LABEL_NEW_TYPE, "NewType");     //$NON-NLS-1$
  }

  public NewTypeDialog(XSDSchema schema)
  {
    super(Display.getCurrent().getActiveShell(), Messages._UI_LABEL_NEW_TYPE, "NewType");     //$NON-NLS-1$
    this.schema = schema;
  }
  
  private void setup() {
	  if (schema != null) {
		  List usedNames = getUsedTypeNames();
		  setUsedNames(usedNames);
		  setDefaultName(XSDCommonUIUtils.createUniqueElementName(name, schema.getTypeDefinitions()));
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
    ComponentSpecification componentSpecification;
    if ( anonymousType )
    	componentSpecification = new ComponentSpecification(null, null, null);
    else
    	componentSpecification =  new ComponentSpecification(null, getName(), null);    
    componentSpecification.setMetaName(typeKind == COMPLEX_TYPE ? IXSDSearchConstants.COMPLEX_TYPE_META_NAME : IXSDSearchConstants.SIMPLE_TYPE_META_NAME);
    componentSpecification.setNew(true);
    //componentSpecification.
    return componentSpecification;
  }

  public void setInitialSelection(ComponentSpecification componentSpecification)
  {
    // TODO Auto-generated method stub
  }

  protected void createHeaderContent(Composite parent)
  {
    complexTypeButton = new Button(parent, SWT.RADIO);
    complexTypeButton.setText(Messages._UI_LABEL_COMPLEX_TYPE);
    complexTypeButton.setEnabled(allowComplexType);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(complexTypeButton, XSDEditorCSHelpIds.NEWTYPE_COMPLEXTYPE);
    
    simpleTypeButton = new Button(parent, SWT.RADIO);
    simpleTypeButton.setText(Messages._UI_LABEL_SIMPLE_TYPE);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(simpleTypeButton, XSDEditorCSHelpIds.NEWTYPE_SIMPLETYPE);
    
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
        else if (allowAnonymousType && e.widget == anonymousTypeCheckBox)
        {
          if (anonymousTypeCheckBox.getSelection() == true)
          {
        	  nameField.setEnabled(false);
        	  anonymousType = true;
          }
          else
          {
        	  nameField.setEnabled(true);
        	  anonymousType = false;
          }
        }
      }
    };
    if (allowComplexType)
    {
      complexTypeButton.setSelection(true);
      typeKind = COMPLEX_TYPE;
    }
    else
    {
      simpleTypeButton.setSelection(true);
      typeKind = SIMPLE_TYPE;
    }

    simpleTypeButton.addSelectionListener(listener);
    complexTypeButton.addSelectionListener(listener);
    
    Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData gd = new GridData(GridData.FILL_BOTH);
    separator.setLayoutData(gd);

    if (allowAnonymousType )
    {
      anonymousTypeCheckBox = new Button(parent, SWT.CHECK);
      anonymousTypeCheckBox.setText(Messages._UI_LABEL_CREATE_ANON_TYPE);
    }    

    if (anonymousTypeCheckBox != null)
      anonymousTypeCheckBox.addSelectionListener(listener);    
  }

  /**
   * This method will be removed in the next WTP release.
   * @deprecated
   */
  protected String getNormalizedLocation(String location)
  {
    try
    {
      URL url = new URL(location);
      URL resolvedURL = FileLocator.resolve(url);
      location = resolvedURL.getPath();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return location;
  }

  public void allowComplexType(boolean value)
  {
    this.allowComplexType= value;
  }
  
  public void allowAnonymousType(boolean value)
  {
	this.allowAnonymousType = value;
  }
  
  private List getUsedTypeNames() {
	  List usedNames = new ArrayList();

	  if (schema != null) {
		  List typesList = schema.getTypeDefinitions();
		  Iterator types = typesList.iterator();
		  while (types.hasNext()) {
			  usedNames.add(((XSDTypeDefinition) types.next()).getName());
		  }
	  }
	  
	  return usedNames;
  }
}
