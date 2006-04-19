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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.wst.xsd.ui.internal.widgets.TypeSection;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;


public class SimpleContentUnionMemberTypesDialog extends Dialog implements SelectionListener
{
  XSDSimpleTypeDefinition simpleType;
  /**
   * @param parentShell
   */
  public SimpleContentUnionMemberTypesDialog(Shell parentShell, XSDSimpleTypeDefinition simpleType)
  {
    super(parentShell);
    this.simpleType = simpleType;
  }
  
  Table table;
  TypeSection typeSection;
  Button addButton, removeButton;
  org.eclipse.swt.widgets.List memberTypesList;
  
  private String result;

  protected void configureShell(Shell shell)
  {
    super.configureShell(shell);
  }

  protected void buttonPressed(int buttonId)
  {
    if (buttonId == Window.OK)
    {
      StringBuffer sb = new StringBuffer();
      int length = memberTypesList.getItemCount();
      for (int i=0 ; i < length; i++)
      {
        sb.append(memberTypesList.getItem(i));
        if (i < length - 1)
        {
          sb.append(" "); //$NON-NLS-1$
        }
      }
      result = sb.toString();
    }
    super.buttonPressed(buttonId);
  }

  public String getResult() { return result; }

  //
  // Create the controls
  //
  public Control createDialogArea(Composite parent)
  {
    Composite client = (Composite)super.createDialogArea(parent);
    getShell().setText("Union " + XSDConstants.MEMBERTYPES_ATTRIBUTE);  //$NON-NLS-1$
    
    Label instructions = new Label(client, SWT.LEFT | SWT.WRAP);
    instructions.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_SELECT_MEMBERTYPES")); //$NON-NLS-1$
    
    Composite columnsComposite = new Composite(client, SWT.NONE);
    GridLayout ccGL = new GridLayout();
    ccGL.verticalSpacing = 0;
    ccGL.horizontalSpacing = 0;
    ccGL.marginHeight = 0;
    ccGL.marginWidth = 0;
    ccGL.makeColumnsEqualWidth = true;
    ccGL.numColumns = 3;
    columnsComposite.setLayout(ccGL);
    
    GridData ccGD = new GridData();
    ccGD.grabExcessHorizontalSpace = true;
    ccGD.horizontalAlignment = GridData.FILL;
    columnsComposite.setLayoutData(ccGD);     
                           
    typeSection = new TypeSection(columnsComposite);
    typeSection.setShowUserComplexType(false);

    typeSection.createClient(columnsComposite);
    typeSection.getSimpleType().setSelection(false);
    typeSection.getSimpleType().addSelectionListener(this);
    typeSection.getUserSimpleType().addSelectionListener(this);
    
    ViewUtility.createHorizontalFiller(columnsComposite, 1);
    
    Label memberListLabel = new Label(columnsComposite, SWT.LEFT);
    memberListLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES_VALUE")); //$NON-NLS-1$
    
    Composite dataComposite = new Composite(client, SWT.NONE);
    GridLayout dcGL = new GridLayout();
    dcGL.verticalSpacing = 0;
    dcGL.marginHeight = 0;
    dcGL.marginWidth = 0;
    dcGL.numColumns = 3;
    dataComposite.setLayout(dcGL);
    
    GridData dcGD = new GridData();
    dcGD.grabExcessHorizontalSpace = true;
    dcGD.grabExcessVerticalSpace = true;
    dataComposite.setLayoutData(dcGD);
    
    table = new Table(dataComposite,
        SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER); 
    table.setHeaderVisible(false);
    table.setLinesVisible(true);
    GridData gd2 = new GridData();
    gd2.grabExcessHorizontalSpace = true;
    gd2.grabExcessVerticalSpace = true;
    gd2.horizontalAlignment = GridData.FILL;
    gd2.verticalAlignment = GridData.FILL;
    gd2.heightHint = 200;
    gd2.widthHint = 200;
    table.setLayoutData(gd2);

    // Fill table
    handleSetInput();
    table.getItemCount();

    TableColumn tc = new TableColumn(table, SWT.LEFT);
    tc.setWidth(200);
    tc.setResizable(true);
    
    Composite buttonComposite = new Composite(dataComposite, SWT.NONE);
    GridLayout bcGL = new GridLayout();
    bcGL.numColumns = 1;
    buttonComposite.setLayout(bcGL);
    addButton = new Button(buttonComposite, SWT.PUSH);
    addButton.setText(">"); //$NON-NLS-1$
    addButton.addSelectionListener(this);
    removeButton = new Button(buttonComposite, SWT.PUSH);
    removeButton.setText("<"); //$NON-NLS-1$
    removeButton.addSelectionListener(this);
    
    Composite listComposite = new Composite(dataComposite, SWT.NONE);
    GridLayout mtGL = new GridLayout();
    mtGL.numColumns = 1;
    mtGL.marginHeight = 0;
    mtGL.marginWidth = 0;
    mtGL.horizontalSpacing = 0;
    mtGL.verticalSpacing = 0;
    listComposite.setLayout(mtGL);

    GridData mtGD = new GridData();
    mtGD.grabExcessHorizontalSpace = true;
    mtGD.grabExcessVerticalSpace = true;
    mtGD.verticalAlignment = GridData.FILL;
    mtGD.horizontalAlignment = GridData.FILL;
    listComposite.setLayoutData(mtGD);
    
    memberTypesList = new org.eclipse.swt.widgets.List(listComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    GridData mtlGD = new GridData();
    mtlGD.grabExcessHorizontalSpace = true;
    mtlGD.grabExcessVerticalSpace = true;
    mtlGD.verticalAlignment = GridData.FILL;
    mtlGD.horizontalAlignment = GridData.FILL;
    mtlGD.heightHint = 200;
    mtlGD.widthHint = 200;
    memberTypesList.setLayoutData(mtlGD);
    
    initializeMemberListContent();
    return client;
  }

  private void initializeMemberListContent()
  {
//    String result = element.getAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);
//    if (result == null)
//    {
//      return;
//    }
//    StringTokenizer token = new StringTokenizer(result);
//    while (token.hasMoreTokens())
//    {
//      memberTypesList.add(token.nextToken());
//    }
    XSDSchema schema = simpleType.getSchema();
    for (Iterator i = simpleType.getMemberTypeDefinitions().iterator(); i.hasNext(); )
    {
      String name = ((XSDSimpleTypeDefinition)i.next()).getQName(schema);
      if (name != null)
      memberTypesList.add(name);
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == typeSection.getSimpleType() && typeSection.getSimpleType().getSelection())
     {
      populateBuiltInType();
    }
    else if (e.widget == typeSection.getUserSimpleType() && typeSection.getUserSimpleType().getSelection())
     {
      populateUserSimpleType(false);
    }
    else if (e.widget == addButton)
    {
      TableItem[] items = table.getItems();
      int selection = table.getSelectionIndex();
      if (items != null && items.length > 0 && selection >= 0)
      {
        String typeToAdd = items[selection].getData().toString();
        if (memberTypesList.indexOf(typeToAdd) < 0)
        {
          memberTypesList.add(items[selection].getData().toString());
        }
      }
    }
    else if (e.widget == removeButton)
    {
      String[] typesToRemove = memberTypesList.getSelection();
      for (int i=0; i < typesToRemove.length; i++)
      {
        memberTypesList.remove(typesToRemove[i]);
      }
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  public void widgetDefaultSelected(SelectionEvent e)
  {
  }
  
  public void handleSetInput()
  {
    populateBuiltInType();
  }
  
  public void populateBuiltInType()
  {
    table.removeAll();
    List items = getBuiltInTypeNamesList();
    for (int i = 0; i < items.size(); i++)
     {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(items.get(i).toString());
      item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif")); //$NON-NLS-1$
      item.setData(items.get(i));
    }
  }

  public void populateUserSimpleType(boolean showAnonymous)
  {
    table.removeAll();
    if (showAnonymous)
     {
      TableItem anonymousItem = new TableItem(table, SWT.NONE);
      anonymousItem.setText("**anonymous**"); //$NON-NLS-1$
      anonymousItem.setData("**anonymous**"); //$NON-NLS-1$
    }
    List items = getUserSimpleTypeNamesList();
    for (int i = 0; i < items.size(); i++)
     {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(items.get(i).toString());
      item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif")); //$NON-NLS-1$
      item.setData(items.get(i));
    }
  }
  
  public java.util.List getBuiltInTypeNamesList()
  {
    TypesHelper helper = new TypesHelper(simpleType.getSchema());
    return helper.getBuiltInTypeNamesList();
  }

  public java.util.List getUserSimpleTypeNamesList()
  {
    TypesHelper helper = new TypesHelper(simpleType.getSchema());
    return helper.getUserSimpleTypeNamesList();
  }

}
