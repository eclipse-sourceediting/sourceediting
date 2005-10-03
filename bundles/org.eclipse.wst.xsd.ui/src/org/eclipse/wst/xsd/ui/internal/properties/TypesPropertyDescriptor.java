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

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
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


public class TypesPropertyDescriptor extends PropertyDescriptor
{
  Element element;
  XSDSchema xsdSchema;
  String property;
  /**
   * @param id
   * @param displayName
   */
  public TypesPropertyDescriptor(Object id, String displayName, Element element, XSDSchema xsdSchema)
  {
    super(id, displayName);
    this.property = (String)id;
    this.element = element;
    this.xsdSchema = xsdSchema;
  }
  
  boolean showComplexTypes = true;

  public CellEditor createPropertyEditor(Composite parent)
  {
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
    // CellEditor editor = new TypesOptionsTextCellEditor(parent);
    CellEditor editor = new TypesDialogCellEditor(parent);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }

  
  public class TypesDialogCellEditor extends DialogCellEditor
  {

    /**
     * Creates a new Font dialog cell editor parented under the given control.
     * The cell editor value is <code>null</code> initially, and has no 
     * validator.
     *
     * @param parent the parent control
     */
    protected TypesDialogCellEditor(Composite parent)
    {
      super(parent);
    }

    protected Object openDialogBox(Control cellEditorWindow)
    {
	    Shell shell = Display.getCurrent().getActiveShell();
	    
	    TypesDialog dialog = new TypesDialog(shell);

	    dialog.setBlockOnOpen(true);
	    dialog.create();
	    
	    String value = (String)getValue();
	
	    int result = dialog.open();
	    
	    if (result == Window.OK)
	    {
	      value = dialog.getType();
        doSetValue(value);
        fireApplyEditorValue();
	    }
	    deactivate();
	    return null;
	  }
  }

  public class TypesDialog extends org.eclipse.jface.dialogs.Dialog implements SelectionListener
  {
    String type;
    Object typeObject;
    Table table;

    TypeSection typeSection;
    boolean showAnonymous = true;
    String previousStringType = "";
    boolean isAnonymous;
    int previousType;

    
    public TypesDialog(Shell shell)
    {
      super(shell);
    }

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
      Composite client = (Composite)super.createDialogArea(parent);
      getShell().setText(XSDEditorPlugin.getXSDString("_UI_LABEL_AVAILABLE_TYPES"));

      typeObject = null;
      
      GridLayout gl = new GridLayout(1, true);
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

      table = new Table(client,
                        SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL); 
      table.setHeaderVisible(false);
      table.setLinesVisible(true);
      
      GridData gd2 = new GridData();
      gd2.grabExcessHorizontalSpace = true;
      gd2.grabExcessVerticalSpace = true;
      gd2.horizontalAlignment = GridData.FILL;
      gd2.heightHint = 200;
      table.setLayoutData(gd2);

      TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
//      tableColumn.setImage(XSDEditorPlugin.getXSDImage("icons/XSDElement.gif"));
      tableColumn.setResizable(true);
      tableColumn.setWidth(200);
      

      // Fill table and select input type
      handleSetInput();

      return client;
    }
    
    public void widgetSelected(SelectionEvent e)
    {
      if (e.widget == typeSection.getSimpleType() && typeSection.getSimpleType().getSelection())
      {
        populateBuiltInType();
      }
      else if (e.widget == typeSection.getUserComplexType() && typeSection.getUserComplexType().getSelection())
      {
        populateUserComplexType();
      }
      else if (e.widget == typeSection.getUserSimpleType() && typeSection.getUserSimpleType().getSelection())
      {
        populateUserSimpleType();
      }

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
//      System.out.println("typeObject is " + typeObject);

//      beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_TYPE_CHANGE"), element);
//      beginRecording(XSDEditorPlugin.getXSDString("_UI_TYPE_CHANGE"), element);
//      doSetValue(typeObject);
//      applyEditorValueAndDeactivate();
//      dialog.close();

      if (!XSDDOMHelper.inputEquals(element, XSDConstants.UNION_ELEMENT_TAG, false))
      {
      if (typeObject.equals("**anonymous**"))
      {
        if (typeSection.getUserSimpleType().getSelection())
        {
          if (!previousStringType.equals("**anonymous**"))
          {
            updateElementToAnonymous(
              element,
              XSDConstants.SIMPLETYPE_ELEMENT_TAG);
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
        updateElementToNotAnonymous(element);
        //element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, typeObject.toString());
        element.setAttribute(property, typeObject.toString());
      }
      }
//      endRecording(element);

      //implement dispose();
//      table.removeAll();
//      table.dispose();
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
//        System.out.println("item " + i + " is " + item);
        item.setText(items.get(i).toString());
        item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif"));
        item.setData(items.get(i));
      }
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
      updateElementToNotAnonymous(element);
      Element childNode = null;
      if (xsdType.equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
       {
        childNode = element.getOwnerDocument().createElementNS(
            XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001,
            prefix + XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
      }
      else if (xsdType.equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG))
       {
        childNode =
        element.getOwnerDocument().createElementNS(
            XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001,
            prefix + XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      }
      element.appendChild(childNode);
    }

    boolean isSTAnonymous(Element element)
    {
      XSDDOMHelper helper = new XSDDOMHelper();
      Node aNode =
      helper.getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      if (aNode != null)
       {
        if (XSDDOMHelper
            .inputEquals(aNode, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
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
        typeDefinitionNode =
        helper.getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
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

    void updateElementToNotAnonymous(Element element)
    {
      if (element != null)
      {
	      NodeList children = element.getChildNodes();
	      if (children != null)
	       {
	        for (int i = 0; i < children.getLength(); i++)
	         {
	          Node node = (Node) children.item(i);
	          if (node instanceof Element)
	           {
	            if (node.getLocalName().equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG)
	                || node.getLocalName().equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
	             {
	              XSDDOMHelper.removeNodeAndWhitespace(node);
	              i = 0;
	            }
	          }
	        }
	      }
      }
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

  
  
  
  
  
  
  
  class TypesOptionsTextCellEditor extends OptionsTextCellEditor
  {
    boolean showAnonymous = true;
    
    public TypesOptionsTextCellEditor(Composite parent)
    {
      super(parent);
    }

    protected Control createControl(Composite parent)
    {
      isTextReadOnly = true;
      return super.createControl(parent);
    }

    Table table;
    TypeSection typeSection;

    protected void openDialog()
    {
      typeObject = null;
      dialog = new Shell(XSDEditorPlugin.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.MODELESS);
      Display display = dialog.getDisplay();
      GridLayout gl = new GridLayout(1, true);
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      gl.horizontalSpacing = 0;
      gl.verticalSpacing = 0;
      dialog.setLayout(gl);
      GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      gd.horizontalAlignment = GridData.FILL;
      gd.verticalAlignment = GridData.FILL;
      gd.horizontalIndent = 0;
      dialog.setLayoutData(gd);

      
      
      typeSection = new TypeSection(dialog);
      typeSection.setShowUserComplexType(showComplexTypes);

      typeSection.createClient(dialog);
      typeSection.getSimpleType().setSelection(false);
      typeSection.getSimpleType().addSelectionListener(this);
      typeSection.getUserSimpleType().addSelectionListener(this);
      if (showComplexTypes)
      {
        typeSection.getUserComplexType().addSelectionListener(this);
      }

      table = new Table(dialog,
                        SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL); 
      table.setHeaderVisible(false);
      table.setLinesVisible(true);
      GridData gd2 = new GridData();
      gd2.grabExcessHorizontalSpace = true;
      gd2.grabExcessVerticalSpace = true;
      gd2.horizontalAlignment = GridData.FILL;
      gd2.verticalAlignment = GridData.FILL;
      table.setLayoutData(gd2);

      // Fill table and select input type
      handleSetInput();

      TableColumn tc = new TableColumn(table, SWT.LEFT);
      tc.setImage(XSDEditorPlugin.getXSDImage("icons/XSDElement.gif"));
      tc.setResizable(false);

      int MAX_ITEMS = 23;
      Rectangle tableBounds = table.getBounds();
      tableBounds.height = Math.min(tableBounds.height, table.getItemHeight()*MAX_ITEMS);
      table.setBounds(tableBounds);
      dialog.pack();
      
      dialog.addShellListener(new ShellAdapter()
      {
        public void shellDeactivated(ShellEvent e)
        {
          cancel();
        }
      });

      Rectangle dialogBounds = dialog.getBounds();
      Point buttonLocation = getButtonAbsoluteLocation();
      dialogBounds.x = buttonLocation.x;
      dialogBounds.y = buttonLocation.y;

      if (dialogBounds.height > 200)
      {        
        dialogBounds.height = 200;
      }
      if (dialogBounds.height < 100)
      {
        dialogBounds.height = 200;
      }
      if (dialogBounds.width > 200)
      {
        dialogBounds.width = typeSection.getUserComplexType().getBounds().width + 30;
      }
      dialog.setBounds(dialogBounds);
      tc.setWidth(dialogBounds.width);

      table.addKeyListener(new KeyAdapter()
      {
        public void keyPressed(KeyEvent e)
        {
          char character = e.character;
          if (character == SWT.CR || character == SWT.LF)
            ok();
          else if (character == SWT.ESC)
            cancel();
        }
      });

      table.addMouseListener(new MouseAdapter()
      {
        public void mouseDoubleClick(MouseEvent e)
        {
          ok();
        }
        public void mouseDown(MouseEvent e)
        {
          ok();
        }
      });

      try
      {
        dialog.open();
        table.setFocus();
        table.showSelection();
        
        while (!dialog.isDisposed())
        {
          if (!display.readAndDispatch())
          {
            display.sleep();
          }
        }
      }
      finally
      {
        if (!dialog.isDisposed())
          cancel();
      }
    }
    
    public void widgetSelected(SelectionEvent e)
    {
      if (e.widget == typeSection.getSimpleType() && typeSection.getSimpleType().getSelection())
      {
        populateBuiltInType();
      }
      else if (e.widget == typeSection.getUserComplexType() && typeSection.getUserComplexType().getSelection())
      {
        populateUserComplexType();
      }
      else if (e.widget == typeSection.getUserSimpleType() && typeSection.getUserSimpleType().getSelection())
      {
        populateUserSimpleType();
      }
    }
    
    protected void cancel()
    {
      super.cancel();
      table.dispose();
    }

    protected void ok()
    {
      TableItem[] items = table.getItems();
      selection = table.getSelectionIndex();
      if (items != null && items.length > 0 && selection >= 0)
      {
        typeObject = items[selection].getData();
      }
//      System.out.println("typeObject is " + typeObject);

//      beginRecording(XSDEditorPlugin.getXSDString("_UI_ELEMENT_TYPE_CHANGE"), element);
//      beginRecording(XSDEditorPlugin.getXSDString("_UI_TYPE_CHANGE"), element);
      doSetValue(typeObject);
      applyEditorValueAndDeactivate();
      dialog.close();

      if (!XSDDOMHelper.inputEquals(element, XSDConstants.UNION_ELEMENT_TAG, false))
      {
      if (typeObject.equals("**anonymous**"))
      {
        if (typeSection.getUserSimpleType().getSelection())
        {
          if (!previousStringType.equals("**anonymous**"))
          {
            updateElementToAnonymous(
              element,
              XSDConstants.SIMPLETYPE_ELEMENT_TAG);
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
        updateElementToNotAnonymous(element);
        //element.setAttribute(XSDConstants.TYPE_ATTRIBUTE, typeObject.toString());
        element.setAttribute(property, typeObject.toString());
      }
      }
//      endRecording(element);

      //implement dispose();
      table.removeAll();
      table.dispose();
    }

    String previousStringType = "";
    boolean isAnonymous;
    int previousType;

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
//        System.out.println("item " + i + " is " + item);
        item.setText(items.get(i).toString());
        item.setImage(XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif"));
        item.setData(items.get(i));
      }
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
      updateElementToNotAnonymous(element);
      Element childNode = null;
      if (xsdType.equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
       {
        childNode = element.getOwnerDocument().createElementNS(
            XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001,
            prefix + XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
      }
      else if (xsdType.equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG))
       {
        childNode =
        element.getOwnerDocument().createElementNS(
            XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001,
            prefix + XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      }
      element.appendChild(childNode);
    }

    boolean isSTAnonymous(Element element)
    {
      XSDDOMHelper helper = new XSDDOMHelper();
      Node aNode =
      helper.getChildNode(element, XSDConstants.SIMPLETYPE_ELEMENT_TAG);
      if (aNode != null)
       {
        if (XSDDOMHelper
            .inputEquals(aNode, XSDConstants.SIMPLETYPE_ELEMENT_TAG, false))
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
        typeDefinitionNode =
        helper.getChildNode(element, XSDConstants.COMPLEXTYPE_ELEMENT_TAG);
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

    void updateElementToNotAnonymous(Element element)
    {
      NodeList children = element.getChildNodes();
      if (children != null)
       {
        for (int i = 0; i < children.getLength(); i++)
         {
          Node node = (Node) children.item(i);
          if (node instanceof Element)
           {
            if (node.getLocalName().equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG)
                || node.getLocalName().equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
             {
              XSDDOMHelper.removeNodeAndWhitespace(node);
              i = 0;
            }
          }
        }
      }
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

}
