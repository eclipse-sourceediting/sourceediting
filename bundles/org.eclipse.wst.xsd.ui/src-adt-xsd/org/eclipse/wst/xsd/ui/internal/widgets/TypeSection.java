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
package org.eclipse.wst.xsd.ui.internal.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorContextIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.xsd.XSDSchema;

public class TypeSection
{
  /**
   * Constructor for TypeSection.
   * @param parent
   */
  public TypeSection(Composite parent)
  {
  }

  protected Button  simpleType;
  protected Button  userSimpleType;
  protected Button  userComplexType;
  protected Button  noneRadio;
  protected Combo   typeList;
  protected Combo   derivedByCombo;
  protected boolean showUserComplexType = true;
  protected boolean showUserSimpleType  = true;
  protected boolean showNone            = false;
  protected boolean showDerivedBy       = false;
  protected String  derivedByChoices[]  = { "restriction", "extension" };
  public final int  NONE                = 1;
  public final int  BUILT_IN            = 2;
  public final int  SIMPLE              = 3;
  public final int  COMPLEX             = 4;

  String            sectionTitle        = XSDEditorPlugin.getXSDString("_UI_LABEL_TYPE_INFORMATION");
  String            currentObjectUuid   = "";

  /*
   * @see FlatPageSection#createClient(Composite, WidgetFactory)
   */
  public Composite createClient(Composite parent)
  {
    Composite client = new Composite(parent, SWT.NONE);
    GridLayout gl = new GridLayout(1, true);
    gl.verticalSpacing = 0;
    client.setLayout(gl);

    if (showNone)
    {
      noneRadio = ViewUtility.createRadioButton(client, XSDEditorPlugin.getXSDString("_UI_RADIO_NONE"));
      WorkbenchHelp.setHelp(noneRadio, XSDEditorContextIds.XSDE_TYPE_HELPER_NONE);
    }

    simpleType = ViewUtility.createRadioButton(client, XSDEditorPlugin.getXSDString("_UI_RADIO_BUILT_IN_SIMPLE_TYPE"));
    WorkbenchHelp.setHelp(simpleType, XSDEditorContextIds.XSDE_TYPE_HELPER_BUILT_IN);

    if (showUserSimpleType)
    {
      userSimpleType = ViewUtility.createRadioButton(client, XSDEditorPlugin.getXSDString("_UI_RADIO_USER_DEFINED_SIMPLE_TYPE"));
      WorkbenchHelp.setHelp(userSimpleType, XSDEditorContextIds.XSDE_TYPE_HELPER_USER_DEFINED_SIMPLE);
    }

    if (showUserComplexType)
    {
      userComplexType = ViewUtility.createRadioButton(client, XSDEditorPlugin.getXSDString("_UI_RADIO_USER_DEFINED_COMPLEX_TYPE"));
      WorkbenchHelp.setHelp(userComplexType, XSDEditorContextIds.XSDE_TYPE_HELPER_USER_DEFINED_COMPLEX);
    }

    //	  typeList = utility.createComboBox(client);
    //	  WorkbenchHelp.setHelp(typeList, XSDEditorContextIds.XSDE_TYPE_HELPER_TYPE);
    //    utility.createHeadingLabel(client, "Type",null);

    if (showDerivedBy)
    {
      Composite derivedByComposite = ViewUtility.createComposite(client, 2);
      ViewUtility.createLabel(derivedByComposite, XSDEditorPlugin.getXSDString("_UI_LABEL_DERIVED_BY"));
      derivedByCombo = ViewUtility.createComboBox(derivedByComposite);
      populateDerivedByCombo();
      WorkbenchHelp.setHelp(derivedByCombo, XSDEditorContextIds.XSDE_SIMPLE_CONTENT_DERIVED);
      derivedByCombo.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_DERIVED_BY"));
    }
    // Set the default selection
    if (showNone)
    {
      //		noneRadio.setSelection(true);
      //		typeList.setEnabled(false);
    }
    else
    {
      simpleType.setSelection(true);
    }
    return client;
  }

  public void setIsDerivedBy(boolean derive)
  {
    if (derive)
    {
      sectionTitle = XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE");
    }
    else
    {
      sectionTitle = XSDEditorPlugin.getXSDString("_UI_LABEL_TYPE_INFORMATION");
    }
    //	setHeaderText(sectionTitle);
  }

  /**
   * Set to true if called by Complex Type & Simple Type
   */
  public void setShowDerivedBy(boolean derive)
  {
    showDerivedBy = derive;
  }

  /**
   * Gets the derivedByField
   * @return Returns a Button
   */
  public Combo getDerivedByCombo()
  {
    return derivedByCombo;
  }

  /**
   * Gets the noneRadio.
   * @return Returns a Button
   */
  public Button getNoneRadio()
  {
    return noneRadio;
  }

  /**
   * Gets the simpleType.
   * @return Returns a Button
   */
  public Button getSimpleType()
  {
    return simpleType;
  }

  /**
   * Gets the userComplexType.
   * @return Returns a Button
   */
  public Button getUserComplexType()
  {
    return userComplexType;
  }

  /**
   * Gets the userSimpleType.
   * @return Returns a Button
   */
  public Button getUserSimpleType()
  {
    return userSimpleType;
  }

  /**
   * Gets the typeList.
   * @return Returns a CCombo
   */
  public Combo getTypeList()
  {
    return typeList;
  }

  /**
   * Populate combo box with built-in simple types
   */
  public void populateBuiltInType(XSDSchema xsdSchema)
  {
    getTypeList().removeAll();
    List items = getBuiltInTypeNamesList(xsdSchema);
    for (int i = 0; i < items.size(); i++)
    {
      getTypeList().add(items.get(i).toString());
    }
  }

  public java.util.List getBuiltInTypeNamesList(XSDSchema xsdSchema)
  {
    TypesHelper helper = new TypesHelper(xsdSchema);
    return helper.getBuiltInTypeNamesList();
  }

  /**
   * Populate combo box with user defined complex types
   */
  public void populateUserComplexType(XSDSchema xsdSchema, boolean showAnonymous)
  {
    getTypeList().removeAll();
    if (showAnonymous)
    {
      getTypeList().add(XSDEditorPlugin.getXSDString("_UI_ANONYMOUS"));
    }

    List items = getUserComplexTypeNamesList(xsdSchema);
    for (int i = 0; i < items.size(); i++)
    {
      getTypeList().add(items.get(i).toString());
    }
  }

  public java.util.List getUserComplexTypeNamesList(XSDSchema xsdSchema)
  {
    TypesHelper helper = new TypesHelper(xsdSchema);
    return helper.getUserComplexTypeNamesList();
  }

  public void populateUserSimpleType(XSDSchema xsdSchema, boolean showAnonymous)
  {
    getTypeList().removeAll();
    if (showAnonymous)
    {
      getTypeList().add(XSDEditorPlugin.getXSDString("_UI_ANONYMOUS"));
    }
    List items = getUserSimpleTypeNamesList(xsdSchema);
    for (int i = 0; i < items.size(); i++)
    {
      getTypeList().add(items.get(i).toString());
    }
  }

  /**
   * Populate combo box with user defined simple types
   */
  public void populateUserSimpleType(XSDSchema xsdSchema)
  {
    getTypeList().removeAll();
    List items = getUserSimpleTypeNamesList(xsdSchema);
    for (int i = 0; i < items.size(); i++)
    {
      getTypeList().add(items.get(i).toString());
    }
  }

  public java.util.List getUserSimpleTypeNamesList(XSDSchema xsdSchema)
  {
    TypesHelper helper = new TypesHelper(xsdSchema);
    return helper.getUserSimpleTypeNamesList();
  }

  public String getPrefix(String ns, XSDSchema xsdSchema)
  {
    TypesHelper helper = new TypesHelper(xsdSchema);
    String key = helper.getPrefix(ns, true);
    return key;
  }

  /**
   * Populate combo box with derived by choices
   */
  protected void populateDerivedByCombo()
  {
    for (int i = 0; i < derivedByChoices.length; i++)
    {
      getDerivedByCombo().add(derivedByChoices[i]);
    }
  }

  /**
   * Gets the showUserComplexType.
   * @return Returns a boolean
   */
  public boolean getShowUserComplexType()
  {
    return showUserComplexType;
  }

  /**
   * Gets the showUserSimpleType.
   * @return Returns a boolean
   */
  public boolean getShowUserSimpleType()
  {
    return showUserSimpleType;
  }

  /**
   * Gets the showNone.
   * @return Returns a boolean
   */
  public boolean getShowNone()
  {
    return showNone;
  }

  /**
   * Sets the showUserComplexType.
   * @param showUserComplexType The showUserComplexType to set
   */
  public void setShowUserComplexType(boolean showUserComplexType)
  {
    this.showUserComplexType = showUserComplexType;
  }

  /**
   * Sets the showUserSimpleType.
   * @param showUserSimpleType The showUserSimpleType to set
   */
  public void setShowUserSimpleType(boolean showUserSimpleType)
  {
    this.showUserSimpleType = showUserSimpleType;
  }

  /**
   * Sets the showNone
   * @param showUserSimpleType The showNone to set
   */
  public void setShowNone(boolean showNone)
  {
    this.showNone = showNone;
  }
}
