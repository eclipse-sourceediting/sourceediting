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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.wst.xsd.ui.internal.widgets.TypeSection;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class TypesDialog extends Dialog implements SelectionListener, Listener
{
  XSDSchema xsdSchema;
  String property;

  /**
   * @param parentShell
   */
  public TypesDialog(Shell parentShell, Element element, Object id, XSDSchema xsdSchema)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.RESIZE);
    this.element = element;
    this.property = (String)id;
    this.element = element;
    this.xsdSchema = xsdSchema;

    if (XSDDOMHelper.inputEquals(element, XSDConstants.ELEMENT_ELEMENT_TAG, false))
    {
      showComplexTypes = true;
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false) ||
      XSDDOMHelper.inputEquals(element, XSDConstants.LIST_ELEMENT_TAG, false) ||
      XSDDOMHelper.inputEquals(element, XSDConstants.UNION_ELEMENT_TAG, false))
    {
      showComplexTypes = false;
    }
  }

  String type;
  Object typeObject;
  Text textField;
  Table table;
  TableColumn tableColumn;
  Element element;
  
  boolean showComplexTypes = true;
  TypeSection typeSection;
  boolean showAnonymous = true;
  String previousStringType = "";
  boolean isAnonymous;
  int previousType;

  protected void configureShell(Shell shell)
  {
    super.configureShell(shell);
  }

  protected void buttonPressed(int buttonId)
  {
    if (buttonId == Dialog.OK)
    {
      type = table.getItem(table.getSelectionIndex()).getText();
      ok();
    }
    super.buttonPressed(buttonId);
  }

  public Object getTypeObject() { return typeObject; }
  public String getType() { return type; }

  //
  // Create the controls
  //
  public Control createDialogArea(Composite parent)
  {
    int tabIndex = 0;
    Composite client = (Composite)super.createDialogArea(parent);
    getShell().setText(XSDEditorPlugin.getXSDString("_UI_LABEL_AVAILABLE_TYPES"));

    typeObject = null;
    
    GridLayout gl = new GridLayout(1, true);
//    gl.marginHeight = 0;
//    gl.marginWidth = 0;
//    gl.horizontalSpacing = 0;
//    gl.verticalSpacing = 0;
    client.setLayout(gl);

    GridData gd = new GridData();
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.horizontalAlignment = GridData.FILL;
    gd.verticalAlignment = GridData.FILL;
    gd.horizontalIndent = 0;
    client.setLayoutData(gd);
    
    typeSection = new TypeSection(client);
    typeSection.setShowUserComplexType(showComplexTypes);

    typeSection.createClient(client);
    typeSection.getSimpleType().setSelection(false);
    typeSection.getSimpleType().addSelectionListener(this);
    typeSection.getUserSimpleType().addSelectionListener(this);
    if (showComplexTypes)
    {
      typeSection.getUserComplexType().addSelectionListener(this);
    }

    textField = ViewUtility.createTextField(client, 50);
    textField.addListener(SWT.Modify, this);
    ViewUtility.createVerticalFiller(client, 0);

    table = new Table(client,
                      SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL); 
    table.setHeaderVisible(false);
    table.setLinesVisible(true);
    table.addSelectionListener(this);
    
    GridData gd2 = new GridData();
    gd2.grabExcessHorizontalSpace = true;
    gd2.grabExcessVerticalSpace = true;
    gd2.horizontalAlignment = GridData.FILL;
    gd2.verticalAlignment = GridData.FILL;
    gd2.heightHint = 200;
    table.setLayoutData(gd2);
    table.addListener(SWT.Resize, this);
    
    tableColumn = new TableColumn(table, SWT.LEFT);
//    tableColumn.setImage(XSDEditorPlugin.getXSDImage("icons/XSDElement.gif"));
    tableColumn.setResizable(true);
    tableColumn.setWidth(200);

    // Fill table and select input type
    handleSetInput();

    return client;
  }
  
  public void handleEvent(Event event)
  {
  	if (event.type == SWT.Resize && event.widget == table) {
  		tableColumn.setWidth(table.getSize().x);
  	}
  	else if (event.type == SWT.Modify && event.widget == textField) {
  		List items = null;
  		boolean showAll = false;
  		String inputString = textField.getText();
  		
  		if (inputString.equals("")) {
  			showAll = true;
  		}
  		else {
  			inputString = insertString("*", ".", inputString);
  			inputString = insertString("?", ".", inputString);
  			inputString = inputString + ".*";
  		}
  		
  		try {
  			if (typeSection.getSimpleType().getSelection())
  			{
  				if (showAll) {
  					populateBuiltInType();
  				}
  				else {
  					populateBuiltInType(inputString);
  				}
  			}
  			else if (typeSection.getUserComplexType().getSelection())
  			{
  				if (showAll) {
  					populateUserComplexType();
  				}
  				else {
  					populateUserComplexType(inputString);
  				}
  			}
  			else if (typeSection.getUserSimpleType().getSelection())
  			{
  				if (showAll) {
  					populateUserSimpleType();
  				}
  				else {
  					populateUserSimpleType(inputString);
  				}
  			}
  		}
  		catch (Exception e) {
  			// Do nothing
  		}
  	}
  	
  	setEnabledState();
  }
  
  private void setEnabledState() {
  	if (table.getSelectionIndex() != -1) {
  		this.getButton(IDialogConstants.OK_ID).setEnabled(true);
  	}
  	else {
  		this.getButton(IDialogConstants.OK_ID).setEnabled(false);
  	}
  }
  
  private String insertString(String target, String newString, String string) {
  	ArrayList list = new ArrayList();
  	StringBuffer stringBuffer = new StringBuffer(string);
  	
  	int index = stringBuffer.indexOf(target);
  	while (index != -1) {
  		stringBuffer = stringBuffer.insert(index, newString);
  		index = stringBuffer.indexOf(target, index + newString.length() + target.length());
  	}
  	
  	return stringBuffer.toString();
  }
  
  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == typeSection.getSimpleType() && typeSection.getSimpleType().getSelection())
    {
    	if (textField.getText().equals("")) {
    		populateBuiltInType();
    	}
    	else {
    		populateBuiltInType(textField.getText());
    	}
    }
    else if (e.widget == typeSection.getUserComplexType() && typeSection.getUserComplexType().getSelection())
    {
    	if (textField.getText().equals("")) {
    		populateUserComplexType();
    	}
    	else {
    		populateUserComplexType(textField.getText());
    	}
    }
    else if (e.widget == typeSection.getUserSimpleType() && typeSection.getUserSimpleType().getSelection())
    {
    	if (textField.getText().equals("")) {
    		populateUserSimpleType();
    	}
    	else {
    		populateUserSimpleType(textField.getText());
    	}
    }
    setEnabledState();
  }
  
	public void widgetDefaultSelected(SelectionEvent e)
  {
  }

  protected void ok()
  {
    TableItem[] items = table.getItems();
    int selection = table.getSelectionIndex();
    if (items != null && items.length > 0 && selection >= 0)
    {
      typeObject = items[selection].getData();
    }
//    System.out.println("typeObject is " + typeObject);

//    beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_TYPE_CHANGE"), element);
//    beginRecording(XSDEditorPlugin.getXSDString("_UI_TYPE_CHANGE"), element);
//    doSetValue(typeObject);
//    applyEditorValueAndDeactivate();
//    dialog.close();

    if (!XSDDOMHelper.inputEquals(element, XSDConstants.UNION_ELEMENT_TAG, false))
    {
	    if (typeObject.equals("**anonymous**"))
	    {
	      if (typeSection.getUserSimpleType().getSelection())
	      {
	        if (!previousStringType.equals("**anonymous**"))
	        {
	          updateElementToAnonymous(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
	        }
	      }
	      else
	      {
	        if (!previousStringType.equals("**anonymous**"))
	        {
	          updateElementToAnonymous(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
	        }
	      }
	      // element.removeAttribute(XSDConstants.TYPE_ATTRIBUTE);
	      element.removeAttribute(property);
	    }
	    else
	    {
	      XSDDOMHelper.updateElementToNotAnonymous(element);
	      //element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, typeObject.toString());
	      element.setAttribute(property, typeObject.toString());
	    }
    }
//    endRecording(element);

    //implement dispose();
//    table.removeAll();
//    table.dispose();
  }

	
  public void handleSetInput()
  {
    table.removeAll();
    isAnonymous = checkForAnonymousType(element);
    // Attr attr = element.getAttributeNode(XSDConstants.TYPE_ATTRIBUTE);
    Attr attr = element.getAttributeNode(property);
    if (attr != null)
    {
      String value = attr.getValue();
      if (typeSection.getBuiltInTypeNamesList(xsdSchema).contains(value))
      {
        typeSection.getSimpleType().setSelection(true);
        populateBuiltInType();
        int i = typeSection.getBuiltInTypeNamesList(xsdSchema).indexOf(value);
        table.setSelection(i);
        previousType = 1;
      }
      else if (typeSection.getUserSimpleTypeNamesList(xsdSchema).contains(value))
      {
        typeSection.getUserSimpleType().setSelection(true);
        populateUserSimpleType();
        int i = typeSection.getUserSimpleTypeNamesList(xsdSchema).indexOf(value);
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
      else if (typeSection.getUserComplexTypeNamesList(xsdSchema).contains(value))
      {
        typeSection.getUserComplexType().setSelection(true);
        populateUserComplexType();
        int i = typeSection.getUserComplexTypeNamesList(xsdSchema).indexOf(value);
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
      else // if it is type="" for an empty list of simple types
        {
        typeSection.getUserSimpleType().setSelection(true);
        populateUserSimpleType();
        previousType = 2;
      }
    }
    else
    {
      if (isAnonymous)
      {
        if (isSTAnonymous(element))
        {
          typeSection.getUserSimpleType().setSelection(true);
          populateUserSimpleType();
          previousType = 2;
        }
        else
        {
          typeSection.getUserComplexType().setSelection(true);
          populateUserComplexType();
          previousType = 3;
        }
        table.setSelection(0); // anonymous
        //        typeSection.getTypeList().setText("**anonymous**");
      }
      else
      {
        typeSection.getSimpleType().setSelection(true);
        populateBuiltInType();
        table.setSelection(0);
        
        //        typeSection.getTypeList().setEnabled(true);
        //        typeSection.getSimpleType().setSelection(true);
        //        typeSection.populateBuiltInType(xsdSchema);
        //        typeSection.getTypeList().setText(XSDEditorPlugin.getXSDString("_UI_NO_TYPE"));
        previousType = 1;
      }
    }
    if (table.getSelection() != null && table.getSelection().length > 0)
    {
      previousStringType = (table.getSelection()[0]).getText();
    }
  }
  
  public void populateBuiltInType()
  {
    table.removeAll();
    List items = getBuiltInTypeNamesList();
    for (int i = 0; i < items.size(); i++)
    {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(items.get(i).toString());
      item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif"));
      item.setData(items.get(i));
    }
  }

  public void populateBuiltInType(String fragment)
  {
    table.removeAll();
    List items = getBuiltInTypeNamesList();
    fragment = fragment.toLowerCase();
	Pattern regex = Pattern.compile(fragment);
	
    for (int i = 0; i < items.size(); i++)
    {
    	String itemString = items.get(i).toString().toLowerCase();
    	Matcher m = regex.matcher(itemString);

    	if (itemString.startsWith(fragment) || m.matches()) {
    		TableItem item = new TableItem(table, SWT.NONE);
    		item.setText(items.get(i).toString());
    		item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif"));
    		item.setData(items.get(i));
    	}
    }
    
    table.select(0);
  }

  public void populateUserComplexType()
  {
    table.removeAll();
    if (showAnonymous)
    {
      TableItem anonymousItem = new TableItem(table, SWT.NONE);
      anonymousItem.setText("**anonymous**");
      anonymousItem.setData("**anonymous**");
    }
    List items = getUserComplexTypeNamesList();
    for (int i = 0; i < items.size(); i++)
    {
      TableItem item = new TableItem(table, SWT.NONE);
//      System.out.println("item " + i + " is " + item);
      item.setText(items.get(i).toString());
      item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif"));
      item.setData(items.get(i));
    }
  }
  
  public void populateUserComplexType(String fragment)
  {
    table.removeAll();
    fragment = fragment.toLowerCase();
    Pattern regex = java.util.regex.Pattern.compile(fragment);
    
    if (showAnonymous)
    {
    	Matcher m = regex.matcher("**anonymous**");
    	if ("**anonymous**".startsWith(fragment) || m.matches()) {
    		TableItem anonymousItem = new TableItem(table, SWT.NONE);
    		anonymousItem.setText("**anonymous**");
    		anonymousItem.setData("**anonymous**");
    	}
    }
    List items = getUserComplexTypeNamesList();
    for (int i = 0; i < items.size(); i++)
    {
    	String itemString = items.get(i).toString().toLowerCase();
    	Matcher m = regex.matcher(itemString);
    	
    	if (itemString.startsWith(fragment) || m.matches()) {
    		TableItem item = new TableItem(table, SWT.NONE);
    		item.setText(items.get(i).toString());
    		item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif"));
    		item.setData(items.get(i));
    	}
    }
    
    table.select(0);
  }

  public void populateUserSimpleType()
  {
    table.removeAll();
    if (showAnonymous)
     {
      TableItem anonymousItem = new TableItem(table, SWT.NONE);
      anonymousItem.setText("**anonymous**");
      anonymousItem.setData("**anonymous**");
    }
    List items = getUserSimpleTypeNamesList();
    for (int i = 0; i < items.size(); i++)
     {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(items.get(i).toString());
      item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif"));
      item.setData(items.get(i));
    }
  }

  public void populateUserSimpleType(String fragment)
  {
    table.removeAll();
    fragment = fragment.toLowerCase();
    Pattern regex = java.util.regex.Pattern.compile(fragment);
    
    if (showAnonymous)
     {
    	Matcher m = regex.matcher("**anonymous**");
    	if ("**anonymous**".startsWith(fragment) || m.matches())  {
    		TableItem anonymousItem = new TableItem(table, SWT.NONE);
    		anonymousItem.setText("**anonymous**");
    		anonymousItem.setData("**anonymous**");
    	}
    }
    List items = getUserSimpleTypeNamesList();
    for (int i = 0; i < items.size(); i++)
     {
    	String itemString = items.get(i).toString().toLowerCase();
    	Matcher m = regex.matcher(itemString);
    	
    	if (itemString.startsWith(fragment) || m.matches()) {
    		TableItem item = new TableItem(table, SWT.NONE);
    		item.setText(items.get(i).toString());
    		item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif"));
    		item.setData(items.get(i));
    	}
    }
    
    table.select(0);
  }
  
  boolean checkForAnonymousType(Element element)
  {
    /*
     * Using Ed's model to check boolean isAnonymous = false;
     * 
     * XSDConcreteComponent component =
     * getXSDSchema().getCorrespondingComponent(element); if (component
     * instanceof XSDElementDeclaration) { XSDElementDeclaration xsdElem =
     * (XSDElementDeclaration)component; isAnonymous =
     * xsdElem.isSetAnonymousTypeDefinition(); } return isAnonymous;
     */
    XSDDOMHelper helper = new XSDDOMHelper();
    boolean isAnonymous = false;
    Node aNode =
    helper.getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      return true;
    }
    aNode = helper.getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      isAnonymous = true;
    }
    return isAnonymous;
  }

  void updateElementToAnonymous(Element element, String xsdType)
  {
    String prefix = element.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    XSDDOMHelper.updateElementToNotAnonymous(element);
    boolean hasChildrenElements = hasElementChildren(element);
    Element childNode = null;
    if (xsdType.equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
    {
      childNode = element.getOwnerDocument().createElementNS(
          XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001,
          prefix + XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
    }
    else if (xsdType.equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG))
    {
      childNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      
      Element restrictionNode = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.RESTRICTION_ELEMENT_TAG);
      restrictionNode.setAttribute(XSDConstants.BASE_ATTRIBUTE, prefix + "string");
      childNode.appendChild(restrictionNode);      
    }
    if (childNode != null)
    {
      XSDDOMHelper helper = new XSDDOMHelper();
      Node annotationNode = helper.getChildNode(element, XSDConstants.ANNOTATION_ELEMENT_TAG);
      if (annotationNode == null)
      {
        Node firstChild = element.getFirstChild();
        element.insertBefore(childNode, firstChild);
      }
      else
      {
        Node nextSibling = annotationNode.getNextSibling();
        element.insertBefore(childNode, nextSibling);
      }
      XSDDOMHelper.formatChild(childNode);
    }
  }

  boolean isSTAnonymous(Element element)
  {
    XSDDOMHelper helper = new XSDDOMHelper();
    Node aNode = helper.getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (aNode != null)
    {
      if (XSDDOMHelper.inputEquals(aNode, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
      {
        return true;
      }
    }
    return false;
  }

  boolean isCTAnonymous(Element element)
  {
    XSDDOMHelper helper = new XSDDOMHelper();
    Node aNode = helper.getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
    if (aNode != null)
     {
      if (XSDDOMHelper.inputEquals(aNode, XSDConstants.COMPLEXTYPE_ELEMENT_TAG, false))
       {
        return true;
      }
    }
    return false;
  }

  XSDTypeDefinition getAnonymousTypeDefinition(Element element)
  {
    XSDDOMHelper helper = new XSDDOMHelper();
    Node typeDefinitionNode =
    helper.getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
    if (typeDefinitionNode == null)
     {
      typeDefinitionNode = helper.getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
    }
    if (typeDefinitionNode != null)
     {
      XSDConcreteComponent component =
      xsdSchema.getCorrespondingComponent(typeDefinitionNode);
      if (component instanceof XSDTypeDefinition)
       {
        return (XSDTypeDefinition) component;
      }
    }
    return null;
  }
  
  public java.util.List getBuiltInTypeNamesList()
  {
    TypesHelper helper = new TypesHelper(xsdSchema);
    return helper.getBuiltInTypeNamesList();
  }

  public java.util.List getUserSimpleTypeNamesList()
  {
    TypesHelper helper = new TypesHelper(xsdSchema);
    return helper.getUserSimpleTypeNamesList();
  }
  
  public java.util.List getUserComplexTypeNamesList()
  {
    TypesHelper helper = new TypesHelper(xsdSchema);
    return helper.getUserComplexTypeNamesList();
  }
  
  protected boolean hasElementChildren(Node parentNode)
  {
    boolean hasChildrenElements = false;
    if (parentNode != null && parentNode.hasChildNodes())
    {
      NodeList nodes = parentNode.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++)
      {
        if (nodes.item(i) instanceof Element)
        {
          hasChildrenElements = true;
          break;
        }
      }
    }
    return hasChildrenElements;
  }

}
