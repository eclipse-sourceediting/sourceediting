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
package org.eclipse.wst.xsd.ui.common.properties.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.PageBook;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.common.commands.AddAppInfoAttributeCommand;
import org.eclipse.wst.xsd.ui.common.commands.AddAppInfoCommand;
import org.eclipse.wst.xsd.ui.common.commands.AddAppInfoElementCommand;
import org.eclipse.wst.xsd.ui.common.commands.RemoveAppInfoAttrCommand;
import org.eclipse.wst.xsd.ui.common.commands.RemoveAppInfoElementCommand;
import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.AddApplicationInfoDialog;
import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.ApplicationInformationPropertiesRegistry;
import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.ApplicationInformationTableTreeViewer;
import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.SpecificationForAppinfoSchema;
import org.eclipse.wst.xsd.ui.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ApplicationInfoSection extends AbstractSection
{
  protected static final Image DEFAULT_ELEMENT_ICON = XSDEditorPlugin.getXSDImage("icons/XSDElement.gif");
  protected static final Image DEFAULT_ATTR_ICON = XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif");
  protected ApplicationInformationTableTreeViewer tableTree;
  protected TableViewer extensibleElementsTable;
  protected Label extensibilityElementsLabel, contentLabel;
  protected ISelectionChangedListener elementSelectionChangedListener;

  private Text simpleText;
  private Composite page, pageBook1, pageBook2;
  private Button textRadioButton, structureRadioButton;
  private Button addButton, removeButton;
  private PageBook pageBook;

  /**
   * 
   */
  public ApplicationInfoSection()
  {
    super();
  }

  public void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    composite.setLayout(gridLayout);

    GridData gridData = new GridData();

    page = getWidgetFactory().createComposite(composite);
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    page.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    page.setLayoutData(gridData);

    pageBook = new PageBook(page, SWT.FLAT);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    pageBook.setLayoutData(gridData);

    pageBook2 = getWidgetFactory().createComposite(pageBook, SWT.FLAT);

    gridLayout = new GridLayout();
    gridLayout.marginHeight = 2;
    gridLayout.marginWidth = 2;
    gridLayout.numColumns = 1;
    pageBook2.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    pageBook2.setLayoutData(gridData);

    SashForm sashForm = new SashForm(pageBook2, SWT.HORIZONTAL);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    sashForm.setLayoutData(gridData);

    Composite leftContent = getWidgetFactory().createComposite(sashForm, SWT.FLAT);
    gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    leftContent.setLayout(gridLayout);

    extensibilityElementsLabel = getWidgetFactory().createLabel(leftContent, "Application Information Items");
    extensibleElementsTable = new TableViewer(leftContent, SWT.FLAT | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.LINE_SOLID);
    gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    extensibleElementsTable.getTable().setLayout(gridLayout);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    extensibleElementsTable.getTable().setLayoutData(gridData);
    extensibleElementsTable.setContentProvider(new ElementTableContentProvider());
    extensibleElementsTable.setLabelProvider(new ElementTableLabelProvider());
    elementSelectionChangedListener = new ElementSelectionChangedListener();
    extensibleElementsTable.addSelectionChangedListener(elementSelectionChangedListener);
    extensibleElementsTable.getTable().addMouseTrackListener(new MouseTrackAdapter()
    {
      public void mouseHover(org.eclipse.swt.events.MouseEvent e)
      {
        ISelection selection = extensibleElementsTable.getSelection();
        if (selection instanceof StructuredSelection)
        {
          Object obj = ((StructuredSelection) selection).getFirstElement();
          if (obj instanceof Element)
          {
            Element element = (Element) obj;
            ApplicationInformationPropertiesRegistry registry = XSDEditorPlugin.getDefault().getApplicationInformationPropertiesRegistry();
            // ApplicationSpecificSchemaProperties[] properties =
            // registry.getAllApplicationSpecificSchemaProperties();
            // ApplicationSpecificSchemaProperties[] properties =
            // (ApplicationSpecificSchemaProperties[])
            // registry.getAllApplicationSpecificSchemaProperties().toArray(new
            // ApplicationSpecificSchemaProperties[0]);
            List properties = registry.getAllApplicationSpecificSchemaProperties();

            int length = properties.size();
            for (int i = 0; i < length; i++)
            {
              SpecificationForAppinfoSchema current = (SpecificationForAppinfoSchema) properties.get(i);
              if (current.getNamespaceURI().equals(element.getNamespaceURI()))
              {
                extensibleElementsTable.getTable().setToolTipText(current.getDescription());
                break;
              }
            }
          }
        }
      };

    });

    Composite rightContent = getWidgetFactory().createComposite(sashForm, SWT.FLAT);

    contentLabel = getWidgetFactory().createLabel(rightContent, "Content");

    Composite testComp = getWidgetFactory().createComposite(rightContent, SWT.FLAT);

    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.marginLeft = 0;
    gridLayout.marginRight = 0;
    gridLayout.numColumns = 1;
    gridLayout.marginHeight = 3;
    gridLayout.marginWidth = 3;
    rightContent.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    rightContent.setLayoutData(gridData);

    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginLeft = 0;
    gridLayout.marginRight = 0;
    gridLayout.marginBottom = 0;
    gridLayout.marginHeight = 3;
    gridLayout.marginWidth = 3;
    gridLayout.numColumns = 1;
    testComp.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    testComp.setLayoutData(gridData);

    createElementContentWidget(testComp);

    int[] weights = { 30, 70 };
    sashForm.setWeights(weights);

    Composite buttonComposite = getWidgetFactory().createComposite(pageBook2, SWT.FLAT);
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 2;
    gridLayout.makeColumnsEqualWidth = true;
    buttonComposite.setLayout(gridLayout);
    addButton = getWidgetFactory().createButton(buttonComposite, "Add...", SWT.FLAT);
    addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    addButton.addSelectionListener(this);
    addButton.setToolTipText("Add Application Specific Information");
    removeButton = getWidgetFactory().createButton(buttonComposite, "Remove", SWT.FLAT);
    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    removeButton.addSelectionListener(this);
    removeButton.setToolTipText("Remove Application Specific Information");

    pageBook.showPage(pageBook2);
  }

  protected void createElementContentWidget(Composite parent)
  {
    tableTree = new ApplicationInformationTableTreeViewer(parent);
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;

    tableTree.getControl().setLayoutData(gridData);
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    setListenerEnabled(false);
    if (input != null)
    {
      tableTree.setInput(null);
      tableTree.getTree().removeAll();
      extensibleElementsTable.getTable().removeAll();
      
      extensibleElementsTable.setInput(input);

      if (extensibleElementsTable.getTable().getSelectionCount() == 0)
      {
        Object o = extensibleElementsTable.getElementAt(0);
        if (o != null)
        {
          extensibleElementsTable.setSelection(new StructuredSelection(o));
          if (o instanceof Element)
          {
            tableTree.setInput(((Element) o).getParentNode());
          }
        }
        tableTree.refresh();
      }

    }
    setListenerEnabled(true);

  }

  public Composite getPage()
  {
    return page;
  }

  public void widgetSelected(SelectionEvent event)
  {
    if (event.widget == addButton)
    {
        ApplicationInformationPropertiesRegistry registry = XSDEditorPlugin.getDefault().getApplicationInformationPropertiesRegistry();
      AddApplicationInfoDialog dialog = new AddApplicationInfoDialog(composite.getShell(), registry);

      List properties = registry.getAllApplicationSpecificSchemaProperties();

      dialog.setInput(properties);
      dialog.setBlockOnOpen(true);

      if (dialog.open() == Window.OK)
      {
        Object[] result = dialog.getResult();
        if (result != null)
        {
          SpecificationForAppinfoSchema appInfoSchemaSpec = (SpecificationForAppinfoSchema) result[1];
          if (input instanceof XSDConcreteComponent)
          {
            AddAppInfoCommand addAppInfo = null;
            if (result[0] instanceof XSDElementDeclaration)
            {
              XSDElementDeclaration element = (XSDElementDeclaration) result[0];
              addAppInfo = new AddAppInfoElementCommand("Add AppInfo Element", (XSDConcreteComponent) input, element);
            }
            else if (result[0] instanceof XSDAttributeDeclaration)
            {
              XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) result[0];
              addAppInfo = new AddAppInfoAttributeCommand("Add AppInfo Attribute", (XSDConcreteComponent) input, attribute);
            }
            else
              return;

            addAppInfo.setSchemaProperties(appInfoSchemaSpec);
            if (getCommandStack() != null)
            {
              getCommandStack().execute(addAppInfo);
            }
          }
        }
        extensibleElementsTable.refresh();
        refresh();
      }

    }
    else if (event.widget == removeButton)
    {
      ISelection selection = extensibleElementsTable.getSelection();
      
      if (selection instanceof StructuredSelection)
      {
    	Command command = null;
        Object o = ((StructuredSelection) selection).getFirstElement();
        if (o instanceof Element)
        {
            XSDAnnotation xsdAnnotation = (XSDAnnotation) extensibleElementsTable.getInput();
        	Node appInfoElement = ((Element) o).getParentNode();
        	command = new RemoveAppInfoElementCommand("Remove AppInfo Element",
        			xsdAnnotation, appInfoElement);
        }
        else if (o instanceof Attr){
        	Element hostElement = ((Attr) o).getOwnerElement();
        	command = new RemoveAppInfoAttrCommand("Remove AppInfo Attribute",
        			hostElement, (Attr) o);
        }
        else 
        	return;
        if (getCommandStack() != null)
        {
        	getCommandStack().execute(command);
        	extensibleElementsTable.setInput(input);
        	extensibleElementsTable.refresh();
        	
        	if (extensibleElementsTable.getTable().getItemCount() > 0)
        	{
        		Object object = extensibleElementsTable.getElementAt(0);
        		if (object != null)
        		{
        			extensibleElementsTable.setSelection(new StructuredSelection(object));
        		}
        	}
        	else
        	{
        		tableTree.setInput(null);
        	}
        }
      }
    }
    else if (event.widget == extensibleElementsTable.getTable())
    {

    }
  }

  public void widgetDefaultSelected(SelectionEvent event)
  {

  }

  public boolean shouldUseExtraSpace()
  {
    return true;
  }

  public void dispose()
  {

  }

  static class ElementTableContentProvider implements IStructuredContentProvider
  {
    protected String facet;

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public java.lang.Object[] getElements(java.lang.Object inputElement)
    {
      if (inputElement instanceof XSDConcreteComponent)
      {
        XSDConcreteComponent component = (XSDConcreteComponent) inputElement;
        List elementsAndAttributes = new ArrayList();
        
        /** Construct elements list */
        XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(component, false);
        if (xsdAnnotation != null)
        {
          List appInfoList = xsdAnnotation.getApplicationInformation();
          Element appInfoElement = null;
          if (appInfoList.size() > 0)
          {
            appInfoElement = (Element) appInfoList.get(0);
          }
          if (appInfoElement != null)
          {            
            for (Iterator it = appInfoList.iterator(); it.hasNext();)
            {
              Object obj = it.next();
              if (obj instanceof Element)
              {
                Element appInfo = (Element) obj;
                NodeList nodeList = appInfo.getChildNodes();
                int length = nodeList.getLength();
                for (int i = 0; i < length; i++)
                {
                  Node node = nodeList.item(i);
                  if (node instanceof Element)
                  {
                    elementsAndAttributes.add(node);
                  }
                }
              }
            }
          }
        }
        
        /** Construct attributes list */
        NamedNodeMap attributes = component.getElement().getAttributes();
        if ( attributes != null ){
          //String defaultNamespace = (String)component.getSchema().getQNamePrefixToNamespaceMap().get("");          
          int length = attributes.getLength();
          for (int i = 0; i < length; i++){
            Node oneAttribute = attributes.item(i);
            if (!isXmlnsAttribute(oneAttribute))
            {  
              String namespace = oneAttribute.getNamespaceURI();        
              boolean isExtension = true;
              if (namespace == null && oneAttribute.getPrefix() == null) 
              {
                isExtension = false;
              }  
              else if (!XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(namespace))
              {
                isExtension = true;
              }           
              if (isExtension)
              {  
                elementsAndAttributes.add(oneAttribute);
              }    
            }
          }
        }
        return elementsAndAttributes.toArray();
      }
      return Collections.EMPTY_LIST.toArray();
    }
    
    private static boolean isXmlnsAttribute(Node attribute)
    {
      String prefix = attribute.getPrefix();
      if (prefix != null)
      {
        // this handle the xmlns:foo="blah" case
        return "xmlns".equals(prefix);
      }
      else
      {
        // this handles the xmlns="blah" case
        return "xmlns".equals(attribute.getNodeName());
      }  
    }

    public void dispose()
    {

    }
  }

  static class ElementTableLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    public Image getColumnImage(Object element, int columnIndex)
    {
      ApplicationInformationPropertiesRegistry registry = XSDEditorPlugin.getDefault().getApplicationInformationPropertiesRegistry();
      if (element instanceof Element)
      {
        Element domElement = (Element) element;
        ILabelProvider lp = registry.getLabelProvider(domElement);
        if (lp != null)
        {
          Image img = lp.getImage(domElement);
          if (img != null)
            return img;
        }
        return DEFAULT_ELEMENT_ICON;
      }
      if (element instanceof Attr)
    	return DEFAULT_ATTR_ICON;
      return null;
    }

    public String getColumnText(Object input, int columnIndex)
    {
      ApplicationInformationPropertiesRegistry registry = XSDEditorPlugin.getDefault().getApplicationInformationPropertiesRegistry();

      if (input instanceof Element)
      {
        Element domElement = (Element) input;
        return domElement.getLocalName();
      }
      if ( input instanceof Attr){
        return ((Attr) input).getLocalName();
      }
      return "";
    }
  }

  Element selectedElement;

  class ElementSelectionChangedListener implements ISelectionChangedListener
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
      ISelection selection = event.getSelection();
      if (selection instanceof StructuredSelection)
      {
        Object obj = ((StructuredSelection) selection).getFirstElement();
        if (obj instanceof Element)
        {
          selectedElement = (Element) obj;
          tableTree.setInput(selectedElement.getParentNode());
          tableTree.setASIElement(selectedElement);
          tableTree.setCommandStack(getCommandStack());
          contentLabel.setText("Structure of " + selectedElement.getLocalName());
          contentLabel.getParent().layout();
        }
      }
    }

  }

}
